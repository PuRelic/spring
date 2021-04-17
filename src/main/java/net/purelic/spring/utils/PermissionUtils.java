package net.purelic.spring.utils;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.profile.Rank;

import java.util.Arrays;
import java.util.List;

public class PermissionUtils {

    private static boolean hasRank(ProxiedPlayer player, Rank... ranks) {
        List<Rank> playerRanks = ProfileManager.getProfile(player).getRanks();

        for (Rank rank : ranks) {
            if (playerRanks.contains(rank)) return true;
        }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isAdmin(ProxiedPlayer player) {
        return hasRank(player, Rank.ADMIN);
    }

    public static boolean isStaff(ProxiedPlayer player) {
        return hasRank(player, Arrays.copyOf(Rank.getStaffRanks().toArray(), Rank.getStaffRanks().size(), Rank[].class));
    }

    public static boolean isDonator(ProxiedPlayer player) {
        return hasRank(player, Rank.PREMIUM, Rank.CREATOR) || isStaff(player);
    }

}
