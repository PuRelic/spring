package net.purelic.spring.utils;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import net.purelic.commons.Commons;
import net.purelic.spring.league.Season;
import net.purelic.spring.managers.DocumentManager;
import net.purelic.spring.server.GameServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class DatabaseUtils {

    public static void setServerIp(GameServer server) {
        String id = server.getId();
        String ip = server.getIp();

        Map<String, Object> data = new HashMap<>();
        data.put("ip", ip);
        data.put("port", 25565);
        data.put("droplet_id", server.getDropletId());
        data.put("snapshot_id", server.getSnapshotId());

        Commons.getFirestore().collection("server_ips")
                .document(id)
                .set(data, SetOptions.merge());
    }

    public static void removeServerDoc(GameServer server) {
        String id = server.getId();
        Commons.getFirestore().collection("servers").document(id).delete();
        Commons.getFirestore().collection("server_ips").document(id).delete();
        DocumentManager.removeServerDoc(id);
    }

    public static Map<String, Object> getPlayerDoc(UUID uuid) {
        try {
            DocumentReference docRef = Commons.getFirestore().collection("players").document(uuid.toString());
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) return document.getData();
            else return new HashMap<>();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Error loading profile for player " + uuid.toString());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static QueryDocumentSnapshot getPlayerDoc(String name) {
        try {
            Query query = Commons.getFirestore().collection("players").whereEqualTo("name_lower", name.toLowerCase());
            ApiFuture<QuerySnapshot> future = query.get();
            QuerySnapshot snapshot = future.get();

            if (!snapshot.isEmpty()) return snapshot.getDocuments().get(0);
            else return null;
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Error loading profile for player " + name);
            e.printStackTrace();
            return null;
        }
    }

    public static void updatePlayerDoc(UUID uuid, String field, Object value) {
        Commons.getFirestore().collection("players")
                .document(uuid.toString())
                .update(field, value);
    }

    @SuppressWarnings("unchecked")
    public static Season getCurrentSeason() {
        try {
            DocumentReference docRef = Commons.getFirestore().collection("league").document("seasons");
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists() && document.getData() != null) {
                String current = (String) document.getData().getOrDefault("current", "beta");
                Map<String, Object> data = (Map<String, Object>) document.getData().getOrDefault(current, new HashMap<>());
                return new Season(current, data);
            } else {
                return null;
            }
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Error fetching current league season!");
            e.printStackTrace();
            return null;
        }
    }

}
