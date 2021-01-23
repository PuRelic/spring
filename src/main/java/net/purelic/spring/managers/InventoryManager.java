package net.purelic.spring.managers;

import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.purelic.spring.league.LeagueRank;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.server.PublicServer;
import net.purelic.spring.server.ServerType;
import net.purelic.spring.utils.*;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryManager {

    private static int selectorRows;
    private static int rankedSelectorRows;
    private static int privateServerItemSlot;

    public static void loadServerSelector(Configuration config) {
        selectorRows = config.getInt("server_selector.rows", 3);
        rankedSelectorRows = config.getInt("server_selector.ranked_selector_rows", 3);
        privateServerItemSlot = config.getInt("server_selector.private_server_item_slot", 4);
    }

    public static void openMainSelector(ProxiedPlayer player) {
        ItemStack publicServers = new ItemStack(ItemType.IRON_CHESTPLATE);
        publicServers.setDisplayName(new ComponentBuilder("Public Servers").color(ChatColor.AQUA).bold(true).create());
        publicServers.setLore(ChatUtils.wrap("Play a variety unique game modes on community built maps!"));
        ItemAction.BROWSE_PUBLIC.apply(publicServers);

        ItemStack leagueServers = new ItemStack(ItemType.QUARTZ);
        leagueServers.setDisplayName(new ComponentBuilder("League" + ChatColor.RESET + ChatColor.GRAY + " (/league)").color(ChatColor.AQUA).bold(true).create());
        leagueServers.setLore(ChatUtils.wrap("Show off your skill and play in competitive, ranked matches!"));
        ItemAction.BROWSE_LEAGUE.apply(leagueServers);

        ItemStack privateServer = new ItemStack(ItemType.TRIPWIRE_HOOK);
        privateServer.setDisplayName(new ComponentBuilder("Private Server" + ChatColor.RESET + ChatColor.GRAY + " (/ps)").color(ChatColor.AQUA).bold(true).create());
        privateServer.setLore(ChatUtils.wrap("Host custom games or create your own maps!"));
        ItemAction.PRIVATE_SERVER.apply(privateServer);

        Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(3), new TextComponent("Select an option:"));
        inventory.setItem(10, publicServers);
        inventory.setItem(12, leagueServers);
        inventory.setItem(16, privateServer);

        List<GameServer> privateServers = ServerManager.getPrivateServers(!PermissionUtils.isStaff(player));

        if (privateServers.size() > 0) {
            inventory.setItem(14, getPrivateServerItem(privateServers));
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

    private static ItemStack getPrivateServerItem(List<GameServer> servers) {
        ItemStack skull = new ItemStack(ItemType.PLAYER_HEAD);
        skull.setDisplayName(new ComponentBuilder("Custom Games").color(ChatColor.AQUA).bold(true).create());
        skull.setLore(Collections.singletonList(ChatColor.WHITE + "Browse " + ChatColor.AQUA + servers.size() + ChatColor.WHITE + " server" + (servers.size() == 1 ? "" : "s")));
        ItemAction.BROWSE_PRIVATE.apply(skull);
        return skull;
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

        if (server == null) {
            openSelectorInv(player);
        } else {
            if (server.isOnline()) {
                openControlsInv(player, server);
            } else {
                CommandUtils.sendAlertMessage(player, "Please wait, your server is still starting up...");
            }
        }
    }

    private static void openSelectorInv(final ProxiedPlayer player) {
        final Inventory inventory = new Inventory(InventoryType.GENERIC_9X1, new TextComponent("Choose a server type:"));

        // Add the server type items
        int i = 0;
        for (ServerType serverType : ServerType.values()) {
            inventory.setItem(i, serverType.getItem());
            i++;
        }

        Profile profile = ProfileManager.getProfile(player);

        // Add the playlist selector item
        Playlist playlist = profile.getPlaylist();
        ItemStack playlistItem = new ItemStack(playlist.getItemType());
        playlistItem.setDisplayName(new ComponentBuilder("Playlist Selector").color(ChatColor.WHITE).bold(true).create());
        playlistItem.setLore(Arrays.asList(
                ChatColor.GRAY + "Selected: " + ChatColor.AQUA + playlist.getName(),
                "",
                ChatColor.WHITE + "Playlists decide what maps and game",
                ChatColor.WHITE + "modes your server starts with.",
                "",
                ChatColor.GREEN + "◊ Premium Only"
        ));
        ItemAction.VIEW_PLAYLISTS.apply(playlistItem);

        inventory.setItem(7, playlistItem);

        // Add the beta features item
        boolean beta = profile.hasBetaFeatures();

        ItemStack betaItem = new ItemStack(beta ? ItemType.LIME_DYE : ItemType.GRAY_DYE);
        betaItem.setDisplayName(new ComponentBuilder("Beta Features").color(ChatColor.WHITE).bold(true).create());
        betaItem.setLore(Arrays.asList(
                (beta ? ChatColor.ITALIC + "" + ChatColor.GREEN + "Enabled" : ChatColor.ITALIC + "" + ChatColor.RED + "Disabled"),
                "",
                ChatColor.WHITE + "Enabling beta features gives you access",
                ChatColor.WHITE + "to updates early, but might introduce",
                ChatColor.WHITE + "new bugs or stability issues.",
                "",
                ChatColor.WHITE + "Please report bugs and",
                ChatColor.WHITE + "feedback in Discord.",
                "",
                ChatColor.GREEN + "◊ Premium Only"
        ));
        ItemAction.BETA.apply(betaItem);

        inventory.setItem(8, betaItem);

        InventoryModule.sendInventory(player, inventory);
    }

    private static void openControlsInv(ProxiedPlayer player, GameServer server) {
        String name = server.getName();
        Inventory inventory = new Inventory(InventoryType.GENERIC_9X1, new TextComponent("Choose an option:"));

        ItemStack joinItem = new ItemStack(ItemType.LIME_WOOL);
        joinItem.setDisplayName(new ComponentBuilder("Join Server").color(ChatColor.GREEN).bold(true).create());
        ItemAction.JOIN.apply(joinItem, name);

        ItemStack stopItem = new ItemStack(ItemType.RED_WOOL);
        stopItem.setDisplayName(new ComponentBuilder("Force Shutdown").color(ChatColor.RED).bold(true).create());
        stopItem.setLore(Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "WARNING",
                "",
                ChatColor.WHITE + "Please join your server and use",
                ChatColor.WHITE + "/shutdown if possible. One use this",
                ChatColor.WHITE + "if you can't connect to your server.",
                "",
                ChatColor.WHITE + "Forcing a shutdown from here could",
                ChatColor.WHITE + "corrupt your custom maps."
        ));
        ItemAction.STOP.apply(stopItem, name);

        inventory.setItem(0, joinItem);
        inventory.setItem(1, stopItem);
        inventory.setItem(8, server.getItem());

        InventoryModule.sendInventory(player, inventory);
    }

}
