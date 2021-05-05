package net.purelic.spring.utils;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;

import java.util.Map;
import java.util.UUID;

public class Fetcher {

    public static String getNameOf(UUID uuid) {
        ProxiedPlayer online = Spring.getPlayer(uuid);

        if (online != null) {
            return online.getName();
        }

        Map<String, Object> data = DatabaseUtils.getPlayerDoc(uuid);

        if (data.isEmpty()) return null;
        else return (String) data.getOrDefault("name", null);
    }

    public static UUID getUUIDOf(String name) {
        ProxiedPlayer online = Spring.getPlayer(name);

        if (online != null) {
            return online.getUniqueId();
        }

        QueryDocumentSnapshot doc = DatabaseUtils.getPlayerDoc(name);

        if (doc == null) return null;
        else return UUID.fromString(doc.getId());
    }

}
