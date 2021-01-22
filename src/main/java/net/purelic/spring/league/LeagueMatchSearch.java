package net.purelic.spring.league;

import net.md_5.bungee.api.ChatColor;
import net.purelic.spring.managers.LeagueManager;

public class LeagueMatchSearch implements Runnable {

    private static final int MAX_WAIT = 60;
    private static final int INCREMENT_INTERVAL = 5;
    private static final int INCREMENT_AMOUNT = 50;

    private final LeagueTeam team;
    private int max;
    private int min;
    private int elapsed;

    public LeagueMatchSearch(LeagueTeam team) {
        this.team = team;
        this.max = team.getRating();
        this.min = this.max;
        this.elapsed = 0;
    }

    @Override
    public void run() {
        if (!LeagueManager.isQueued(this.team)) {
            return;
        }

        if (!this.team.isOnline()) {
            this.team.sendMessage(ChatColor.RED + "Teammate disconnected! Removing from queue...", true);
            LeagueManager.removeFromQueue(this.team);
            return;
        }

        if (this.elapsed == MAX_WAIT) {
            LeagueTeam match = LeagueManager.findClosest(this.team);

            if (match != null) {
                LeagueManager.createMatch(this.team, match);
            } else {
                this.team.sendMessage(ChatColor.RED + "Failed to find a match! Please try rejoining the queue.", true);
                LeagueManager.removeFromQueue(this.team);
            }

            return;
        }

        if (this.elapsed % INCREMENT_INTERVAL == 0) {
            this.max += INCREMENT_AMOUNT;
            this.min -= INCREMENT_AMOUNT;
            this.min = Math.max(0, this.min);
        }

        LeagueTeam match = LeagueManager.findMatch(this.team, this.min, this.max);

        if (match != null) {
            LeagueManager.createMatch(this.team, match);
            return;
        }

        String message = "Searching for a match between " + ChatColor.AQUA + this.min + ChatColor.RESET + " - " + ChatColor.AQUA + this.max + ChatColor.RESET + " ELO " + ChatColor.GRAY + "- " + ChatColor.GREEN + (MAX_WAIT - this.elapsed) + "s";

        if (this.elapsed % 10 == 0) {
            this.team.sendLegacyMessage(message);
        }

        this.team.sendMessage(message, false);
        this.elapsed++;
    }

}
