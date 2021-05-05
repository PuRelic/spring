package net.purelic.spring.managers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.commons.Commons;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.utils.DatabaseUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {

    private static final Map<UUID, Profile> PROFILES = new HashMap<>();

    public static Map<UUID, Profile> getProfiles() {
        return PROFILES;
    }

    public static void reloadProfile(ProxiedPlayer player) {
        reloadProfile(player.getUniqueId());
    }

    public static void reloadProfile(UUID uuid) {
        removeProfile(uuid);
        loadProfile(uuid);
    }

    public static Profile getProfile(ProxiedPlayer player) {
        return getProfile(player.getUniqueId());
    }

    public static Profile getProfile(UUID uuid) {
        return getProfile(uuid, false);
    }

    public static Profile getProfile(UUID uuid, boolean reload) {
        if (reload) reloadProfile(uuid);
        else if (!PROFILES.containsKey(uuid)) loadProfile(uuid);

        return PROFILES.get(uuid);
    }

    public static void removeProfile(ProxiedPlayer player) {
        removeProfile(player.getUniqueId());
    }

    public static void removeProfile(UUID uuid) {
        PROFILES.remove(uuid);
    }

    private static void loadProfile(UUID uuid) {
        if (PROFILES.containsKey(uuid)) return;
        Profile profile = new Profile(uuid, DatabaseUtils.getPlayerDoc(uuid));
        PROFILES.put(uuid, profile);
    }

    public static void loadProfileCache() {
        Commons.getPlayerCache().forEach((uuid, data) -> PROFILES.put(uuid, new Profile(uuid, data)));
        Commons.getPlayerCache().clear();
    }

}
