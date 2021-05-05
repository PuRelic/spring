package net.purelic.spring.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.profile.Rank;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("all")
public class PermissionUtils {

    private static boolean hasRank(ProxiedPlayer player, Rank... ranks) {
        return hasRank(player.getUniqueId(), ranks);
    }

    private static boolean hasRank(UUID uuid, Rank... ranks) {
        return hasRank(ProfileManager.getProfile(uuid), ranks);
    }

    private static boolean hasRank(Profile profile, Rank... ranks) {
        List<Rank> playerRanks = profile.getRanks();

        for (Rank rank : ranks) {
            if (playerRanks.contains(rank)) return true;
        }

        return false;
    }

    public static boolean isAdmin(ProxiedPlayer player) {
        return isAdmin(player.getUniqueId());
    }

    public static boolean isAdmin(UUID uuid) {
        return hasRank(uuid, Rank.ADMIN, Rank.DEVELOPER);
    }

    public static boolean isStaff(ProxiedPlayer player) {
        return isStaff(player.getUniqueId());
    }

    public static boolean isStaff(UUID uuid) {
        return hasRank(uuid, Arrays.copyOf(Rank.getStaffRanks().toArray(), Rank.getStaffRanks().size(), Rank[].class));
    }

    public static boolean isDonator(ProxiedPlayer player) {
        return isDonator(player.getUniqueId());
    }

    public static boolean isDonator(UUID uuid) {
        return hasRank(uuid, Rank.PREMIUM, Rank.CREATOR) || isStaff(uuid);
    }

}
