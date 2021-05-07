package net.purelic.spring.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.profile.Profile;

public class NickUtils {

    public static boolean isNicked(ProxiedPlayer player) {
        return isNicked(ProfileManager.getProfile(player));
    }

    public static boolean isNicked(Profile profile) {
        return profile.isNicked();
    }

    public static String getNick(ProxiedPlayer player) {
        return isNicked(player) ? ProfileManager.getProfile(player).getNick() : player.getName();
    }

    public static String getName(ProxiedPlayer player) {
        return isNicked(player) ? player.getName() : getNick(player);
    }

    public static String getName(Profile profile) {
        return profile.isNicked() ? profile.getName() : profile.getNick();
    }

    public static String getDisplayName(ProxiedPlayer player, ProxiedPlayer viewer) {
        if (PermissionUtils.isStaff(viewer)) { // TODO friends could also see their real name crossed out
            return isNicked(player) ? ChatColor.STRIKETHROUGH + player.getName() + ChatColor.RESET : player.getName();
        } else {
            return isNicked(player) ? ProfileManager.getProfile(player).getNick() : player.getName();
        }
    }

    public static ProxiedPlayer getNickedPlayer(String nick) {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (getNick(player).equalsIgnoreCase(nick)) return player;
        }

        return null;
    }

}
