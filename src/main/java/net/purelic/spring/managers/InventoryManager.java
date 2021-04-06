package net.purelic.spring.managers;

import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.items.ItemStack;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.purelic.spring.league.LeagueRank;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.profile.match.Match;
import net.purelic.spring.profile.stats.StatSection;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.server.PublicServer;
import net.purelic.spring.server.ServerType;
import net.purelic.spring.utils.ItemUtils;
import net.purelic.spring.utils.PermissionUtils;
import net.purelic.spring.utils.ServerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryManager {

    private static int selectorRows;
    private static int rankedSelectorRows;

    public static void loadServerSelector(Configuration config) {
        selectorRows = config.getInt("server_selector.rows", 3);
        rankedSelectorRows = config.getInt("server_selector.ranked_selector_rows", 3);
    }

    public static void openMainSelector(ProxiedPlayer player) {
        Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(3), new TextComponent("Select an option:"));
        inventory.setItem(10, ItemUtils.getPublicServerItem());
        inventory.setItem(12, ItemUtils.getLeagueServerItem());
        inventory.setItem(16, ItemUtils.getPrivateServerItem());

        // private servers
        List<GameServer> privateServers = ServerManager.getPrivateServers(!PermissionUtils.isStaff(player));

        if (privateServers.size() > 0) {
            inventory.setItem(14, ItemUtils.getPrivateServerItem(privateServers));
        }

        InventoryModule.sendInventory(player, inventory);
    }

    public static void openServerSelector(ProxiedPlayer player) {
        Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(selectorRows), new TextComponent("Select a playlist:"));
        ServerManager.getPublicServerTypes().values().stream().filter(server -> !server.isRanked()).forEach(server -> inventory.setItem(server.getSlot(), server.toItem()));
        InventoryModule.sendInventory(player, inventory);
    }

    public static void openLeagueSelector(ProxiedPlayer player) {
        Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(rankedSelectorRows), new TextComponent("Select a playlist:"));

        inventory.setItem(4, LeagueManager.getCurrentSeason().toItem());

        ServerManager.getPublicServerTypes().values().stream().filter(PublicServer::isRanked).forEach(server -> {
            Playlist playlist = server.getPlaylist();
            inventory.setItem(server.getSlot(), server.toItem());
            inventory.setItem(server.getSlot() + 9, LeaderboardManager.getLeaderboard(playlist).toItem());
            inventory.setItem(server.getSlot() - 9, LeagueRank.toItem(player, playlist));
        });

        InventoryModule.sendInventory(player, inventory);
    }

    public static void openPrivateServerSelector(ProxiedPlayer player) {
        List<GameServer> servers = ServerManager.getPrivateServers(!PermissionUtils.isStaff(player));

        int rows = Math.max((servers.size() / 9) + (servers.size() % 9 == 0 ? 0 : 1), 1);
        Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(rows), new TextComponent("Select a server:"));

        List<ItemStack> items = new ArrayList<>();
        servers.forEach(server -> items.add(server.getItem()));
        inventory.setItems(items);

        InventoryModule.sendInventory(player, inventory);
    }

    public static void openPublicServerSelector(ProxiedPlayer player, Playlist playlist, boolean ranked) {
        List<GameServer> servers = ServerUtils.getSortedPublicServers(playlist).stream().filter(server -> server.isRanked() == ranked).collect(Collectors.toList());

        int rows = Math.max((servers.size() / 9) + (servers.size() % 9 == 0 ? 0 : 1), 1);
        Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(rows), new TextComponent("Select a server:"));

        List<ItemStack> items = new ArrayList<>();
        servers.forEach(server -> items.add(server.getItem()));
        inventory.setItems(items);

        InventoryModule.sendInventory(player, inventory);
    }

    public static void openPrivateServerInv(ProxiedPlayer player) {
        GameServer server = ServerUtils.getPrivateServerById(player.getUniqueId().toString());

        if (server == null) openSelectorInv(player);
        else openControlsInv(player, server);
    }

    private static void openSelectorInv(final ProxiedPlayer player) {
        final Inventory inventory = new Inventory(InventoryType.GENERIC_9X1, new TextComponent("Choose a server type:"));

        // Add the server type items
        int i = 0;
        for (ServerType serverType : ServerType.values()) {
            inventory.setItem(i, serverType.getItem());
            i++;
        }

        inventory.setItem(8, ItemUtils.getBetaItem(player));

        InventoryModule.sendInventory(player, inventory);
    }

    private static void openControlsInv(ProxiedPlayer player, GameServer server) {
        String name = server.getName();
        Inventory inventory = new Inventory(InventoryType.GENERIC_9X1, new TextComponent("Choose an option:"));
        inventory.setItem(0, server.isOnline() ? ItemUtils.getJoinServerItem(name) : ItemUtils.getStartingServerItem());
        inventory.setItem(1, ItemUtils.getStopServerItem(name));
        inventory.setItem(8, server.getItem());
        InventoryModule.sendInventory(player, inventory);
    }

    public static void openStatsMenu(ProxiedPlayer viewer) {
        openStatsMenu(viewer, viewer);
    }

    public static void openStatsMenu(ProxiedPlayer viewer, ProxiedPlayer statsPlayer) {
        ProfileManager.reloadProfile(statsPlayer);
        Inventory inventory = new Inventory(InventoryType.GENERIC_9X5, new TextComponent(statsPlayer.getName() + "'s Stats"));
        Arrays.asList(StatSection.values()).forEach(section -> inventory.setItem(section.getSlot(), section.toItem(viewer, statsPlayer)));
        InventoryModule.sendInventory(viewer, inventory);
    }

    public static void openMatchesMenu(ProxiedPlayer viewer) {
        openMatchesMenu(viewer, viewer);
    }

    public static void openMatchesMenu(ProxiedPlayer viewer, ProxiedPlayer statsPlayer) {
        ProfileManager.reloadProfile(statsPlayer);
        Profile profile = ProfileManager.getProfile(statsPlayer);
        Inventory inventory = new Inventory(InventoryType.GENERIC_9X5, new TextComponent(statsPlayer.getName() + "'s Recent Matches"));

        int row = 1;
        int offset = 1;

        for (Map<String, Object> matchData : profile.getMatches()) {
            if (offset == 8) {
                row++;
                offset = 1;
            }

            Match match = new Match(matchData);
            inventory.setItem((row * 9) + offset, match.toItem());
            offset++;
        }

        InventoryModule.sendInventory(viewer, inventory);
    }

}
