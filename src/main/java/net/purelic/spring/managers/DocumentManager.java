package net.purelic.spring.managers;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.ListenerRegistration;
import net.purelic.commons.Commons;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.server.ServerStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DocumentManager {

    private static final Map<String, DocumentReference> DOC_REFS = new HashMap<>();
    private static final Map<DocumentReference, ListenerRegistration> LISTENERS = new HashMap<>();

    public static void loadDocuments() {
        Commons.getFirestore().collection("servers").listDocuments().forEach(documentReference -> {
            try {
                addServerDoc(documentReference);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static Map<DocumentReference, ListenerRegistration> getListeners() {
        return LISTENERS;
    }

    public static void addServerDoc(GameServer server) {
        DocumentReference docRef = Commons.getFirestore().collection("servers").document(server.getId());

        Map<String, Object> docData = new HashMap<>();
        docData.put("id", server.getId());
        docData.put("name", server.getName());
        docData.put("size", server.getSize().getName());
        docData.put("region", server.getRegion().getName());
        docData.put("type", server.getType().getName());
        docData.put("private", server.isPrivate());
        docData.put("beta", server.isBeta());
        docData.put("max_players", server.getMaxPlayers());
        docData.put("players_online", 0);
        docData.put("online", server.isOnline());
        docData.put("locked", server.isLocked());
        docData.put("whitelisted", server.isWhitelisted());
        docData.put("shutdown", false);
        docData.put("ranked", server.isRanked());

        if (server.getPlaylist() != null) {
            docData.put("playlist", server.getPlaylist().getName());
            docData.put("status", server.getStatus().name());
            docData.put("map", null);
            docData.put("game_mode", null);
        }

        docRef.set(docData);

        addServerDoc(server, docRef);
    }

    private static void addServerDoc(DocumentReference docRef) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot doc = future.get();
        GameServer server = ServerManager.getGameServers().get(doc.getString("name"));
        addServerDoc(server, docRef);
    }

    @SuppressWarnings("ConstantConditions")
    public static void addServerDoc(GameServer server, DocumentReference docRef) {
        DOC_REFS.put(server.getId(), docRef);

        ListenerRegistration registration = docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                System.err.println("Listen failed: " + e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Map<String, Object> data = snapshot.getData();
                boolean shutdown = (boolean) data.get("shutdown");
                boolean online = (boolean) data.get("online");
                boolean locked = (boolean) data.get("locked");
                boolean whitelisted = (boolean) data.get("whitelisted");

                if (shutdown) {
                    DocumentReference ref = snapshot.getReference();
                    LISTENERS.get(ref).remove();
                    LISTENERS.remove(ref);
                    ServerManager.removeServer(server);
                    return;
                }

                String status = (String) data.get("status");

                if (status != null) {
                    String map = (String) data.get("map");
                    String gameMode = (String) data.get("game_mode");
                    server.setStatus(ServerStatus.valueOf(status), map, gameMode);
                }

                server.setOnline(online);
                server.setLocked(locked);
                server.setWhitelisted(whitelisted);
            }
        });

        LISTENERS.put(docRef, registration);
    }

    public static void removeServerDoc(String id) {
        DocumentReference docRef = DOC_REFS.get(id);
        LISTENERS.get(docRef).remove();
        LISTENERS.remove(docRef);
        DOC_REFS.remove(id);
        docRef.delete();
    }

    public static void clearDocs() {
        DOC_REFS.clear();
        LISTENERS.values().forEach(ListenerRegistration::remove);
        LISTENERS.clear();
    }

    public static DocumentReference getServerDoc(GameServer server) {
        return DOC_REFS.get(server.getId());
    }

}
