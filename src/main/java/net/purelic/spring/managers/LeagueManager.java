package net.purelic.spring.managers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.purelic.spring.league.*;
import net.purelic.spring.party.Party;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.server.PublicServer;
import net.purelic.spring.server.ServerStatus;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.DatabaseUtils;
import net.purelic.spring.utils.TaskUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class LeagueManager {

    private static final Map<LeagueTeam, ScheduledTask> QUEUE = new HashMap<>();
    private static final Map<LeagueMatch, ScheduledTask> MATCHES = new HashMap<>();
    private static Season currentSeason;

    public static void reloadSeason() {
        currentSeason = DatabaseUtils.getCurrentSeason();
        LeaderboardManager.reloadLeaderboards(currentSeason);
    }

    public static Season getCurrentSeason() {
        return currentSeason;
    }

    public static void clearQueues() {
        QUEUE.forEach((team, task) -> {
            team.sendMessage(ChatColor.RED + "Playlists updated - queues cleared! Please rejoin.", true);
            task.cancel();
        });

        MATCHES.forEach((match, task) -> {
            match.sendMessage(ChatColor.RED + "Playlists updated - queues cleared! Please rejoin.", true);
            task.cancel();
        });

        QUEUE.clear();
        MATCHES.clear();
    }

    public static void joinQueue(ProxiedPlayer player, Playlist playlist) {
        Party party = PartyManager.getParty(player);

        if (party == null) {
            CommandUtils.sendErrorMessage(player, "You must be in a party to play league! Use /party create to start a party");
            return;
        }

        if (!party.isLeader(player)) {
            CommandUtils.sendErrorMessage(player, "Only the party leader can join a league queue!");
            return;
        }

        PublicServer server = ServerManager.getPublicServer(playlist);

        if (party.size() != server.getMinParty()) {
            CommandUtils.sendErrorMessage(player, "You must have an exact party size of " + server.getMinParty() + " Players to join this queue!");
            return;
        }

        if (anyQueued(party)) {
            CommandUtils.sendErrorMessage(player, "You or someone in your party is already queued!");
            return;
        }

        party.getMembers().forEach(member -> CommandUtils.sendSuccessMessage(member, "Joining " + playlist.getName() + " queue... Use /leave to leave the queue at any time"));

        // if no servers are online, create a server on first successful queue join
        if (ServerManager.getGameServers().values().stream().noneMatch(gs -> gs.getPlaylist() == playlist)) {
            ServerManager.createPublicServer(server);
        }

        LeagueTeam team = new LeagueTeam(playlist, party);
        joinQueue(team);
    }

    private static boolean anyQueued(Party party) {
        return party.getMembers().stream().anyMatch(LeagueManager::isQueued);
    }

    public static boolean isQueued(ProxiedPlayer player) {
        return QUEUE.keySet().stream().anyMatch(team -> team.getPlayers().contains(player))
            || MATCHES.keySet().stream().anyMatch(match -> match.getBlueTeam().getPlayers().contains(player))
            || MATCHES.keySet().stream().anyMatch(match -> match.getRedTeam().getPlayers().contains(player));
    }

    public static LeagueTeam getTeam(ProxiedPlayer player) {
        LeagueTeam queued = QUEUE.keySet().stream().filter(team -> team.getPlayers().contains(player)).findFirst().orElse(null);
        LeagueTeam blueTeam = MATCHES.keySet().stream().filter(match -> match.getBlueTeam().getPlayers().contains(player)).findFirst().map(LeagueMatch::getBlueTeam).orElse(null);
        LeagueTeam redTeam = MATCHES.keySet().stream().filter(match -> match.getRedTeam().getPlayers().contains(player)).findFirst().map(LeagueMatch::getRedTeam).orElse(null);

        if (queued != null) return queued;
        else if (blueTeam != null) return blueTeam;
        else return redTeam;
    }

    public static void joinQueue(LeagueTeam team) {
        LeagueMatchSearch search = new LeagueMatchSearch(team);
        ScheduledTask task = TaskUtils.runTimer(search, 1L);
        QUEUE.put(team, task);
    }

    public static void removeFromQueue(LeagueTeam team) {
        QUEUE.get(team).cancel();
        QUEUE.remove(team);
    }

    public static void removeFromQueue(LeagueMatch match) {
        MATCHES.get(match).cancel();
        MATCHES.remove(match);
    }

    public static LeagueTeam findMatch(LeagueTeam team, int min, int max) {
        LeagueTeam closest = findClosest(team);
        if (closest == null) return null;
        int rating = closest.getRating();
        return rating > max || rating < min ? null : closest;
    }

    public static LeagueTeam findClosest(LeagueTeam team) {
        Playlist playlist = team.getPlaylist();
        int rating = team.getRating();
        LeagueTeam closest = null;
        int tempDiff = Integer.MAX_VALUE;

        for (LeagueTeam queued : QUEUE.keySet()) {
            if (queued == team || queued.getPlaylist() != playlist) continue;

            int diff = Math.abs(rating - queued.getRating());

            if (diff < tempDiff) {
                tempDiff = diff;
                closest = queued;
            }
        }

        return closest;
    }

    public static GameServer findServer(Playlist playlist) {
        return ServerManager.getPublicServers(playlist, true)
            .stream().filter(server -> server.getStatus() == ServerStatus.STARTING)
            .findFirst().orElse(null);
    }

    public static boolean isQueued(LeagueTeam team) {
        return QUEUE.containsKey(team);
    }

    public static boolean isQueued(LeagueMatch match) {
        return MATCHES.containsKey(match);
    }

    public static void createMatch(LeagueTeam blue, LeagueTeam red) {
        removeFromQueue(blue);
        removeFromQueue(red);

        LeagueMatch match = new LeagueMatch(blue, red);
        LeagueServerSearch search = new LeagueServerSearch(blue.getPlaylist(), match);
        ScheduledTask task = TaskUtils.runTimer(search, 1L);
        MATCHES.put(match, task);
    }

    public static void startMatch(LeagueMatch match, GameServer server) {
        removeFromQueue(match);
        match.sendMessage(ChatColor.GREEN + "Server found! Teleporting teams...", true);
        server.setRankedPlayers(match);
    }

    public static int getAvgRating(Playlist pl, Set<ProxiedPlayer> players) {
        AtomicInteger total = new AtomicInteger();
        players.forEach(player -> total.addAndGet(ProfileManager.getProfile(player).getRating(pl)));
        return total.get() / players.size();
    }

}
