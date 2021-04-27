package net.purelic.spring.utils;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.purelic.commons.Commons;
import net.purelic.spring.league.Season;
import net.purelic.spring.managers.DocumentManager;
import net.purelic.spring.server.GameServer;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class DatabaseUtils {

    private static final Firestore FIRESTORE = Commons.getFirestore();

    public static void setServerIp(GameServer server) {
        String id = server.getId();
        String ip = server.getIp();

        Map<String, Object> data = new HashMap<>();
        data.put("ip", ip);
        data.put("port", 25565);
        data.put("droplet_id", server.getDropletId());
        data.put("snapshot_id", server.getSnapshotId());

        FIRESTORE.collection("server_ips")
            .document(id)
            .set(data, SetOptions.merge());
    }

    public static void removeServerDoc(GameServer server) {
        String id = server.getId();
        FIRESTORE.collection("servers").document(id).delete();
        FIRESTORE.collection("server_ips").document(id).delete();
        DocumentManager.removeServerDoc(id);
    }

    public static Map<String, Object> getPlayerDoc(UUID uuid) {
        return getDocumentData("players", uuid.toString());
    }

    public static QueryDocumentSnapshot getPlayerDoc(String name) {
        try {
            Query query = FIRESTORE.collection("players").whereEqualTo("name_lower", name.toLowerCase());
            ApiFuture<QuerySnapshot> future = query.get();
            QuerySnapshot snapshot = future.get();

            if (!snapshot.isEmpty()) return snapshot.getDocuments().get(0);
            else return null;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> getDiscordDoc(User user) {
        return getDocumentData("discord_users", user.getId());
    }

    public static Map<String, Object> getIPDoc(String ip) {
        return getDocumentData("player_ips", ip);
    }

    private static Map<String, Object> getDocumentData(String collection, String id) {
        try {
            DocumentReference docRef = FIRESTORE.collection(collection).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) return document.getData();
            else return new HashMap<>();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static boolean createDiscordDoc(Member member) {
        try {
            DocumentReference docRef = FIRESTORE.collection("discord_users").document(member.getId());
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return false;
            } else {
                if (member.hasTimeJoined()) {
                    createDiscordDoc(docRef, member);
                } else { // time joined not cached need to retrieve member by id
                    DiscordUtils.getGuild().retrieveMemberById(member.getIdLong()).queue(
                        retrieved -> createDiscordDoc(docRef, retrieved)
                    );
                }

                return true;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void createDiscordDoc(DocumentReference docRef, Member member) {
        Timestamp joined = timestampOf(member.getTimeJoined());

        Map<String, Object> data = new HashMap<>();
        data.put("joined", joined);
        data.put("referrals", 0);

        docRef.set(data);
    }

    public static void createIPDoc(String ip, Map<String, Object> data) {
        FIRESTORE.collection("player_ips").document(ip).set(data);
    }

    public static void updateIPDoc(String ip, Map<String, Object> data) {
        updateDocument("player_ips", ip, data);
    }

    public static void updatePlayerDoc(UUID uuid, String field, Object value) {
        updateDocument("players", uuid.toString(), field, value);
    }

    public static void updateDiscordDoc(String id, String field, Object value) {
        updateDocument("discord_users", id, field, value);
    }

    public static void updateDiscordDoc(String id, Map<String, Object> values) {
        updateDocument("discord_users", id, values);
    }

    private static void updateDocument(String collection, String id, String field, Object value) {
        FIRESTORE.collection(collection)
            .document(id)
            .update(field, value);
    }

    private static void updateDocument(String collection, String id, Map<String, Object> values) {
        FIRESTORE.collection(collection)
            .document(id)
            .update(values);
    }

    @SuppressWarnings("unchecked")
    public static Season getCurrentSeason() {
        try {
            DocumentReference docRef = FIRESTORE.collection("league").document("seasons");
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

    public static Timestamp timestampOf(OffsetDateTime offsetDateTime) {
        return Timestamp.of(new Date(offsetDateTime.toInstant().toEpochMilli()));
    }

}
