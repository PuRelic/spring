package net.purelic.spring.managers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class SettingsManager {

    private static boolean maintenanceMode;
    private static String motdHeader;
    private static String motdFooter;
    private static String motdMaintenance;
    private static int maxPlayers;
    private static boolean maxPlayerEnforced;
    private static String serverFullMessage;

    public static void loadSettings(Configuration config) {
        maintenanceMode = config.getBoolean("maintenance");
        motdHeader = translateColorCodes(config.getString("motd.header"));
        motdFooter = translateColorCodes(config.getString("motd.footer"));
        motdMaintenance = translateColorCodes(config.getString("motd.maintenance"));
        maxPlayers = config.getInt("players.max");
        maxPlayerEnforced = config.getBoolean("players.max_enforced");
        serverFullMessage = translateColorCodes(config.getString("players.full_message"));
    }

    private static String translateColorCodes(String value) {
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public static boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    public static String getMotdHeader() {
        return motdHeader;
    }

    public static String getMotdFooter() {
        return motdFooter;
    }

    public static String getMotdMaintenance() {
        return motdMaintenance;
    }

    public static int getMaxPlayers() {
        return maxPlayers;
    }

    public static boolean isMaxPlayerEnforced() {
        return maxPlayerEnforced;
    }

    public static String getServerFullMessage() {
        return serverFullMessage;
    }

}
