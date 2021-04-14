package net.purelic.spring.managers;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.purelic.spring.league.Leaderboard;
import net.purelic.spring.league.Season;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.server.PublicServer;
import net.purelic.spring.utils.TaskUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LeaderboardManager {

    private static final Map<Playlist, Leaderboard> LEADERBOARDS = new HashMap<>();
    private static final int REFRESH_INTERVAL = 600; // 5 minutes
    private static ScheduledTask refreshTask = null;

    public static void reloadLeaderboards(Season season) {
        LEADERBOARDS.clear();
        getRankedPlaylists().forEach(playlist -> LEADERBOARDS.put(playlist, new Leaderboard(playlist, season.getId())));
        startRefreshTask();
    }

    private static Set<Playlist> getRankedPlaylists() {
        Set<Playlist> playlists = new HashSet<>();
        ServerManager.getPublicServerTypes()
            .values().stream()
            .filter(PublicServer::isRanked)
            .forEach(server -> playlists.add(server.getPlaylist()));
        return playlists;
    }

    private static void startRefreshTask() {
        if (refreshTask == null) {
            refreshTask = TaskUtils.runTimer(() -> LEADERBOARDS.values().forEach(Leaderboard::refresh), REFRESH_INTERVAL);
        }
    }

    public static Leaderboard getLeaderboard(Playlist playlist) {
        return LEADERBOARDS.get(playlist);
    }

}
