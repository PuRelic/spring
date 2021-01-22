package net.purelic.spring.managers;

import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.purelic.spring.server.Playlist;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlaylistManager {

    private static final Map<String, Playlist> PLAYLISTS = new LinkedHashMap<>();

    public static void loadPlaylists(Configuration config) {
        PLAYLISTS.clear();
        config.getList("playlists")
                .forEach(data -> {
                    Playlist playlist = new Playlist((Map<String, Object>) data);
                    PLAYLISTS.put(playlist.getName(), playlist);
                });
    }

    public static Playlist getDefaultPlaylist() {
        return PLAYLISTS.values().stream().findFirst().orElse(null);
    }

    public static Playlist getPlaylist(String name) {
        return PLAYLISTS.get(name);
    }

    public static boolean isAvailable(Playlist playlist) {
        if (playlist == null) return false;
        Playlist refreshed = getPlaylist(playlist.getName());
        return refreshed != null && !refreshed.isArchived();
    }

    public static void openSelectorInventory(ProxiedPlayer player) {
        List<Playlist> playlists = PLAYLISTS.values().stream()
                .filter(playlist -> !playlist.isArchived())
                .collect(Collectors.toList());

        int rows = Math.max((playlists.size() / 9) + (playlists.size() % 9 == 0 ? 0 : 1), 1);
        Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(rows), new TextComponent("Choose a playlist:"));

        int i = 0;
        for (Playlist playlist : playlists) {
            inventory.setItem(i, playlist.toItem());
            i++;
        }

        InventoryModule.sendInventory(player, inventory);
    }

}
