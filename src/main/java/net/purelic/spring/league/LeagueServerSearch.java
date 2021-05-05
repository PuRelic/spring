package net.purelic.spring.league;

import net.md_5.bungee.api.ChatColor;
import net.purelic.spring.managers.LeagueManager;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.utils.TaskUtils;

public class LeagueServerSearch implements Runnable {

    private static final int MAX_WAIT = 120;

    private final Playlist playlist;
    private final LeagueMatch match;
    private int elapsed;

    public LeagueServerSearch(Playlist playlist, LeagueMatch match) {
        this.playlist = playlist;
        this.match = match;
        this.elapsed = 0;
    }

    @Override
    public void run() {
        if (!LeagueManager.isQueued(this.match)) {
            return;
        }

        if (!this.match.getBlueTeam().isOnline()) {
            this.match.getBlueTeam().sendMessage("Teammate disconnected! Removing from queue", true);
            this.match.getRedTeam().sendMessage(ChatColor.RED + "Match disconnected! Placing you back in queue...", true);
            LeagueManager.removeFromQueue(this.match);
            this.requeue(this.match.getRedTeam());
            return;
        }

        if (!this.match.getRedTeam().isOnline()) {
            this.match.getRedTeam().sendMessage(ChatColor.RED + "Teammate disconnected! Removing from queue...", true);
            this.match.getBlueTeam().sendMessage(ChatColor.RED + "Match disconnected! Placing you back in queue...", true);
            LeagueManager.removeFromQueue(this.match);
            this.requeue(this.match.getBlueTeam());
            return;
        }

        if (this.elapsed == MAX_WAIT) {
            this.match.sendMessage(ChatColor.RED + "Failed to find a server! Please try rejoining the queue.", true);
            LeagueManager.removeFromQueue(this.match);
            return;
        }

        GameServer server = LeagueManager.findServer(this.playlist);

        if (server != null) {
            LeagueManager.startMatch(this.match, server);
            return;
        }

        String message = ChatColor.GREEN + "Match found!" + ChatColor.RESET + " Searching for an open server... " + ChatColor.GRAY + "- " + ChatColor.GREEN + (MAX_WAIT - this.elapsed) + "s";

        if (this.elapsed % 10 == 0) {
            this.match.sendLegacyMessage(message);
        }

        this.match.sendMessage(message, false);
        this.elapsed++;
    }

    private void requeue(LeagueTeam team) {
        TaskUtils.scheduleTask(() -> {
            LeagueManager.joinTeamQueue(team);
        }, 2);
    }

}
