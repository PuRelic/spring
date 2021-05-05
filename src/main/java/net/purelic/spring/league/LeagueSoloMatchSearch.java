package net.purelic.spring.league;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.LeagueManager;
import net.purelic.spring.server.Playlist;

import java.util.List;

public class LeagueSoloMatchSearch implements Runnable {

    private final Playlist playlist;
    private int elapsed;

    public LeagueSoloMatchSearch(Playlist playlist) {
        this.playlist = playlist;
        this.elapsed = 0;
    }

    @Override
    public void run() {
        LeagueManager.cleanSoloQueue(this.playlist);

        String message = "Waiting for players to queue..." + ChatColor.GRAY + " - " + ChatColor.GREEN + this.elapsed + "s";
        List<ProxiedPlayer> queued = LeagueManager.getSoloQueue(this.playlist);

        if (this.elapsed % 10 == 0) {
            // this.team.sendLegacyMessage(message);
        }

        // this.team.sendMessage(message, false);
        this.elapsed++;
    }

}
