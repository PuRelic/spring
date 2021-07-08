package net.purelic.spring.managers;

import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
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
import net.purelic.spring.utils.*;

import java.util.Arrays;
import java.util.Collections;
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

    public static void openSelector(ProxiedPlayer player) {
        GameServer current = ServerUtils.getGameServer(player);

        if (current == null) { // in hub
            openMainSelector(player);
        } else if (current.isPrivate()) {
            openPrivateServerSelector(player);
        } else if (current.isRanked()) {
            openLeagueSelector(player);
        } else {
            openPublicServerSelector(player, current.getPlaylist(), current.isRanked());
        }
    }

    public static void openMainSelector(ProxiedPlayer player) {
        Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(3), new TextComponent("Select an option:"));
        inventory.setItem(11, ItemUtils.getPublicServerItem());
        // inventory.setItem(12, ItemUtils.getLeagueServerItem());
        inventory.setItem(15, ItemUtils.getPrivateServerItem());

        // private servers
        List<GameServer> privateServers = ServerManager.getPrivateServers(!PermissionUtils.isStaff(player));

        if (privateServers.size() > 0) {
            inventory.setItem(13, ItemUtils.getPrivateServerItem(privateServers));
        }

        InventoryModule.sendInventory(player, inventory);
    }

    public static void openServerSelector(ProxiedPlayer player) {
        Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(selectorRows), new TextComponent("Select a playlist:"));
        ServerManager.getPublicServerTypes().values().stream().filter(server -> !server.isRanked()).forEach(server -> inventory.setItem(server.getSlot(), server.toItem(player)));
        inventory.setItem(8, ItemUtils.getMainSelectorItem());
        InventoryModule.sendInventory(player, inventory);
    }

    public static void openLeagueSelector(ProxiedPlayer player) {
        Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(rankedSelectorRows), new TextComponent("Select a playlist:"));

        inventory.setItem(4, LeagueManager.getCurrentSeason().toItem());

        ServerManager.getPublicServerTypes().values().stream().filter(PublicServer::isRanked).forEach(server -> {
            Playlist playlist = server.getPlaylist();
            inventory.setItem(server.getSlot(), server.toItem(player));
            inventory.setItem(server.getSlot() + 9, LeaderboardManager.getLeaderboard(playlist).toItem());
            inventory.setItem(server.getSlot() - 9, LeagueRank.toItem(player, playlist));
        });

        InventoryModule.sendInventory(player, inventory);
    }

    public static void openPrivateServerSelector(ProxiedPlayer player) {
        List<GameServer> servers = ServerUtils.getSortedPrivateServers(false);

        ItemStack quickJoin = new ItemStack(ItemType.EMERALD);
        quickJoin.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD + "Quick Join");
        quickJoin.setLore(Collections.singletonList(
            ChatColor.WHITE + "Auto-join the largest, non-full server!"
        ));
        ItemAction.QUICK_JOIN_CUSTOM.apply(quickJoin);

        openServerBrowser(player, "Custom Game Servers", quickJoin, ItemUtils.getPrivateServerItem(servers), servers);
    }

    public static void openPublicServerSelector(ProxiedPlayer player, Playlist playlist, boolean ranked) {
        List<GameServer> servers = ServerUtils.getSortedPublicServers(playlist).stream().filter(server -> server.isRanked() == ranked).collect(Collectors.toList());

        ItemStack quickJoin = new ItemStack(ItemType.EMERALD);
        quickJoin.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD + "Quick Join");
        quickJoin.setLore(Collections.singletonList(
            ChatColor.WHITE + "Auto-join the largest, non-full server!"
        ));
        ItemAction.QUICK_JOIN.apply(quickJoin, playlist.getName());

        openServerBrowser(player, playlist.getName() + " Servers", quickJoin, ServerManager.getPublicServer(playlist).toItem(player), servers);
    }

    public static void openServerBrowser(ProxiedPlayer player, String title, ItemStack quickJoinItem, ItemStack playlistItem, List<GameServer> servers) {
        Inventory inventory = new Inventory(InventoryType.GENERIC_9X5, new TextComponent(title));

        // add the servers to the middle of the inventory
        int row = 1;
        int offset = 1;

        for (GameServer server : servers) {
            if (offset == 8) {
                row++;
                offset = 1;
            }

            inventory.setItem((row * 9) + offset, server.getItem());
            offset++;
        }

        inventory.setItem(3, quickJoinItem);
        inventory.setItem(4, playlistItem);
        inventory.setItem(5, ItemUtils.getMainSelectorItem());

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

        inventory.setItem(7, ItemUtils.getBetaItem(player));
        inventory.setItem(8, ItemUtils.getMainSelectorItem());

        InventoryModule.sendInventory(player, inventory);
    }

    private static void openControlsInv(ProxiedPlayer player, GameServer server) {
        String name = server.getName();
        Inventory inventory = new Inventory(InventoryType.GENERIC_9X1, new TextComponent("Choose an option:"));
        inventory.setItem(0, server.isOnline() ? ItemUtils.getJoinServerItem(name) : ItemUtils.getStartingServerItem());
        inventory.setItem(1, ItemUtils.getStopServerItem(name));
        inventory.setItem(7, server.getItem());
        inventory.setItem(8, ItemUtils.getMainSelectorItem());
        InventoryModule.sendInventory(player, inventory);
    }

    public static void openStatsMenu(ProxiedPlayer viewer) {
        openStatsMenu(viewer, viewer);
    }

    public static void openStatsMenu(ProxiedPlayer viewer, ProxiedPlayer player) {
        Inventory inventory = new Inventory(InventoryType.GENERIC_9X5, new TextComponent(NickUtils.getDisplayName(player, viewer) + "'s Stats"));
        Arrays.asList(StatSection.values()).forEach(section -> inventory.setItem(section.getSlot(), section.toItem(viewer, player)));
        InventoryModule.sendInventory(viewer, inventory);
    }

    public static void openMatchesMenu(ProxiedPlayer viewer) {
        openMatchesMenu(viewer, viewer);
    }

    public static void openMatchesMenu(ProxiedPlayer viewer, ProxiedPlayer player) {
        Profile profile = ProfileManager.getProfile(player);

        Inventory inventory = new Inventory(InventoryType.GENERIC_9X5, new TextComponent(NickUtils.getDisplayName(player, viewer) + "'s Recent Matches"));

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
