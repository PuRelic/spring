package net.purelic.spring.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.PartyManager;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.party.Party;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.server.PublicServer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ServerUtils {

    public static ServerInfo getHub() {
        return ProxyServer.getInstance().getServerInfo("Hub");
    }

    public static void sendToHub(ProxiedPlayer player) {
        ServerInfo current = player.getServer().getInfo();
        ServerInfo hub = getHub();
        if (current != hub) player.connect(hub);
    }

    public static boolean inHub(ProxiedPlayer player) {
        return player.getServer().getInfo() == getHub();
    }

    public static boolean sameServer(ProxiedPlayer player, ProxiedPlayer other) {
        return getServerName(player).equals(getServerName(other));
    }

    @SuppressWarnings("deprecation")
    public static String getIP(PendingConnection connection) {
        return connection.getAddress().getAddress().getHostAddress();
    }

    @SuppressWarnings("deprecation")
    public static String getIP(ProxiedPlayer player) {
        return player.getAddress().getAddress().getHostAddress();
    }

    public static String getValidName(String name) {
        return !ServerManager.getGameServers().containsKey(name) ? name : ServerUtils.getValidName(name, 2);
    }

    public static String getValidName(String name, int i) {
        if (ServerManager.getGameServers().containsKey(name + i)) {
            return ServerUtils.getValidName(name, i + 1);
        }

        return name + i;
    }

    public static GameServer getPrivateServerById(String id) {
        return ServerManager.getPrivateServers(false).stream()
            .filter(s -> s.getId().equals(id))
            .findFirst().orElse(null);
    }

    public static int totalPlaying(List<GameServer> servers) {
        return servers.stream()
            .filter(gs -> gs.getServerInfo() != null)
            .mapToInt(gs -> gs.getServerInfo().getPlayers().size()).sum();
    }

    public static boolean isServerFull(GameServer server, ProxiedPlayer player) {
        if (PermissionUtils.isStaff(player)) return false; // always allow staff
        if (server.isRanked() && server.isRankedPlayer(player)) return false; // always allow ranked players
        if (server.getId().equals(player.getUniqueId().toString())) return false; // always allow the server owner

        int maxPlayersWithOverflow = server.isPrivate() ? (int) (server.getMaxPlayers() * 1.5) : server.getMaxPlayers() + ServerManager.getPublicServer(server.getPlaylist()).getOverflow();
        boolean isFull = server.getPlayersOnline() >= server.getMaxPlayers();
        boolean isOverflow = server.getPlayersOnline() >= maxPlayersWithOverflow;

        return isOverflow || (!PermissionUtils.isDonator(player) && isFull);
    }

    public static boolean allServersFull(Playlist playlist) {
        int scaleThreshold = ServerManager.getPublicServer(playlist).getScaleThreshold();
        return ServerManager.getPublicServers(playlist, false).stream().noneMatch(server -> server.getPlayersOnline() < scaleThreshold);
    }

    public static boolean isMaxServers(Playlist playlist) {
        int maxServers = ServerManager.getPublicServer(playlist).getMaxServers();
        return ServerManager.getPublicServers(playlist, false).size() >= maxServers;
    }

    public static List<GameServer> getSortedPublicServers(Playlist playlist) {
        List<GameServer> servers = ServerManager.getPublicServers(playlist, true);
        servers.sort(Comparator.comparingInt(GameServer::getPlayersOnline)); // sort by player count
        Collections.reverse(servers); // most players to least players
        return servers;
    }

    public static void quickJoin(ProxiedPlayer player, Playlist playlist) {
        List<GameServer> servers = getSortedPublicServers(playlist);
        Optional<GameServer> result = servers.stream().filter(server -> !server.isFull()).findFirst(); // first server not full

        if (result.isPresent()) {
            result.get().connect(player); // first non-full server sorted by players desc
        } else {
            servers.get(0).connect(player); // all servers full
        }
    }

    public static GameServer getGameServer(ProxiedPlayer player) {
        return getGameServerByName(player.getServer().getInfo().getName(), true);
    }

    public static GameServer getGameServerByName(String name, boolean exactMatch) {
        for (GameServer gameServer : ServerManager.getGameServers().values()) {
            String serverName = gameServer.getName();

            if (serverName.equalsIgnoreCase(name) || (!exactMatch && serverName.toLowerCase().startsWith(name.toLowerCase()))) {
                return gameServer;
            }
        }

        return null;
    }

    public static boolean meetsPartyRequirements(ProxiedPlayer player, GameServer server) {
        Party party = PartyManager.getParty(player);
        int min = server.getMinParty();
        int max = server.getMaxParty();

        if (min > 0 && max == min) {
            if (party == null || party.size() != min) {
                CommandUtils.sendErrorMessage(player, "You must be in a party of " + min + " players!");
                return false;
            }
        }

        if (min > 0) {
            if (party == null || party.size() < min) {
                CommandUtils.sendErrorMessage(player, "You must be in a party of at least " + min + " players!");
                return false;
            }
        }

        if (max > 0) {
            if (party == null || party.size() > max) {
                CommandUtils.sendErrorMessage(player, "You must be in a party with no more than " + max + " players!");
                return false;
            }
        }

        return true;
    }

    public static String getPartyString(PublicServer server) {
        int min = server.getMinParty();
        int max = server.getMaxParty();

        if (min > 0 && max == min) {
            return min + " Players";
        } else if (min > 0) {
            if (max == 0) return "Min. " + min + " Players";
            else return min + " - " + max + " Players";
        } else if (max > 0) {
            if (min == 0) return "Max " + max + " Players";
            else return min + " - " + max + " Players";
        } else {
            return "Any Size";
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isRankedPlayer(ProxiedPlayer player) {
        return ServerManager.getGameServers().values().stream().anyMatch(server -> server.isRankedPlayer(player));
    }

    @SuppressWarnings("deprecation")
    public static BaseComponent[] getServerDetails(ServerInfo server, boolean staffOnly) {
        String name = server.getName();
        boolean hub = name.equals("Hub");
        long online = staffOnly ?
            server.getPlayers().stream().filter(PermissionUtils::isStaff).count() :
            server.getPlayers().size();

        if (online == 0 && staffOnly) return new BaseComponent[]{};

        return new ComponentBuilder(ChatUtils.BULLET).color(ChatColor.GRAY)
            .append(name).color(ChatColor.AQUA)
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Connect to ").color(ChatColor.GRAY)
                    .append(name).color(ChatColor.AQUA)
                    .create()))
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, hub ? "/hub" : "/server " + name))
            .append(" " + ChatUtils.ARROW + " ").color(ChatColor.GRAY)
            .append(online + (staffOnly ? " Staff" : " Online")).color(ChatColor.WHITE)
            .create();
    }

    public static int getStaffOnline() {
        return (int) ProxyServer.getInstance().getPlayers().stream().filter(PermissionUtils::isStaff).count();
    }

    public static String getServerName(ProxiedPlayer player) {
        return player.getServer().getInfo().getName();
    }

}
