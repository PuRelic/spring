package net.purelic.spring.managers;

import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.purelic.commons.Commons;
import net.purelic.spring.Spring;
import net.purelic.spring.server.*;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.ServerUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ServerManager {

    private static final Map<String, PublicServer> PUBLIC_SERVERS = new HashMap<>();
    private static final Map<ProxiedPlayer, PublicServer> QUEUED = new HashMap<>();
    private static final Map<String, GameServer> GAME_SERVERS = new HashMap<>();
    private static final Map<UUID, String> LAST_SERVERS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void loadPublicServers(Configuration config) {
        clearQueues();
        PUBLIC_SERVERS.clear();

        config.getList("servers")
                .forEach(data -> {
                    PublicServer server = new PublicServer((Map<String, Object>) data);
                    PUBLIC_SERVERS.put(server.getPlaylist().getName(), server);
                });
    }

    public static void loadServerCache() {
        Commons.getServerCache().forEach((name, data) -> {
            try {
                GAME_SERVERS.put(name, new GameServer(name, data));
            } catch (RequestUnsuccessfulException | DigitalOceanException e) {
                e.printStackTrace();
            }
        });
        Commons.getServerCache().clear();
    }

    public static Map<String, PublicServer> getPublicServerTypes() {
        return PUBLIC_SERVERS;
    }

    public static PublicServer getPublicServer(Playlist playlist) {
        return PUBLIC_SERVERS.get(playlist.getName());
    }

    public static Map<String, GameServer> getGameServers() {
        return GAME_SERVERS;
    }

    public static List<GameServer> getGameServers(PublicServer server) {
        return getPublicServers(true).stream()
                .filter(gs -> gs.getPlaylist().getName().equals(server.getPlaylist().getName()))
                .collect(Collectors.toList());
    }

    public static void addToQueue(ProxiedPlayer player, Playlist playlist) {
        removeFromQueue(player);
        PublicServer server = getPublicServer(playlist);
        server.addToQueue(player);
        QUEUED.put(player, server);
    }

    public static void removeFromQueue(ProxiedPlayer player) {
        if (!QUEUED.containsKey(player)) return;
        PublicServer server = QUEUED.get(player);
        server.removeFromQueue(player);
        QUEUED.remove(player);
    }

    public static void clearQueues() {
        QUEUED.keySet().forEach(player ->
                CommandUtils.sendAlertMessage(player, "Playlists have been updated and you were removed from the queue. Please re-queue"));
        QUEUED.forEach((player, server) -> server.setStarting(false));
        QUEUED.clear();
    }

    public static List<GameServer> getPublicServers(boolean visibleOnly) {
        return GAME_SERVERS.values().stream()
                .filter(server -> !server.isPrivate() && (!visibleOnly || server.isVisible()))
                .collect(Collectors.toList());
    }

    public static List<GameServer> getPublicServers(Playlist playlist, boolean visibleOnly) {
        return getPublicServers(visibleOnly).stream()
                .filter(server -> server.getPlaylist().getName().equals(playlist.getName()))
                .collect(Collectors.toList());
    }

    public static List<GameServer> getPrivateServers(boolean visibleOnly) {
       return GAME_SERVERS.values().stream()
                .filter(server -> server.isPrivate() && (!visibleOnly || server.isVisible()))
                .collect(Collectors.toList());
    }

    public static void clearQueue(Playlist playlist) {
        PublicServer server = getPublicServer(playlist);
        server.getQueued().forEach(QUEUED::remove);
        server.setStarting(false);
    }

    public static void createPublicServer(PublicServer server) {
        addServer(new GameServer(server));
    }

    public static void createPublicServer(GameServer server) {
        addServer(new GameServer(getPublicServer(server.getPlaylist())));
    }

    public static void createPrivateSerer(ProxiedPlayer player, ServerType type) {
        addServer(new GameServer(player, type));
    }

    public static void createPrivateSerer(ProxiedPlayer player, Playlist playlist) {
        addServer(new GameServer(player, ServerType.CUSTOM_GAMES, playlist));
    }

    private static void addServer(GameServer server) {
        GAME_SERVERS.put(server.getName(), server);
        server.create();
        DocumentManager.addServerDoc(server);
    }

    public static void removeServer(String name) {
        removeServer(GAME_SERVERS.get(name));
    }

    @SuppressWarnings("deprecation")
    public static void removeServer(GameServer server) {
        if (server == null) return;

        String name = server.getName();
        GAME_SERVERS.remove(name);

        if (!server.isCreated()) return;

        for (ProxiedPlayer player : server.getPlayers()) {
            ServerInfo hub = ProxyServer.getInstance().getServerInfo("hub");
            player.connect(hub);
            player.sendMessage(new TextComponent("Server closed. Sent to the hub"));
        }

        Spring.getPlugin().getProxy().getServers().remove(name);
        server.destroy();
        DocumentManager.removeServerDoc(server.getId());

        System.out.println((server.isPrivate() ? "Private" : "Public") + " server destroyed (" + name + ")");
    }

    public static GameServer getLastServer(ProxiedPlayer player) {
        String serverName = LAST_SERVERS.get(player.getUniqueId());
        return serverName == null ? null : ServerUtils.getGameServerByName(serverName, true);
    }

    public static void setLastServer(ProxiedPlayer player) {
        LAST_SERVERS.put(player.getUniqueId(), player.getServer().getInfo().getName());
    }

}