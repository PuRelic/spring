package net.purelic.spring.league;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.LeagueManager;
import net.purelic.spring.party.Party;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.utils.Protocol;

import java.util.*;

public class LeagueTeam {

    private final Playlist playlist;
    private final Set<ProxiedPlayer> players;
    private final String name;
    private final int rating;

    public LeagueTeam(Playlist playlist, Party party) {
        this.playlist = playlist;
        this.players = new HashSet<>(party.getMembers());
        this.name = party.hasCustomName() ? party.getName() : null;
        this.rating = LeagueManager.getAvgRating(this.playlist, this.players);
    }

    public Playlist getPlaylist() {
        return this.playlist;
    }

    public Set<ProxiedPlayer> getPlayers() {
        return this.players;
    }

    public int getRating() {
        return this.rating;
    }

    public void sendMessage(String message, boolean legacy) {
        if (legacy) {
            this.players.forEach(player -> player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message)));
        } else {
            this.players.stream().filter(player -> !Protocol.isLegacy(player)).forEach(player -> player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message)));
        }
    }

    public void sendLegacyMessage(String message) {
        this.players.stream().filter(Protocol::isLegacy)
            .forEach(player -> player.sendMessage(new TextComponent(message)));
    }

    public boolean isOnline() {
        return this.players.stream().allMatch(Connection::isConnected);
    }

    public Map<String, Object> toData() {
        Map<String, Object> data = new HashMap<>();

        if (this.name != null) data.put("name", this.name);

        List<String> players = new ArrayList<>();
        this.players.forEach(player -> players.add(player.getUniqueId().toString()));
        data.put("players", players);

        return data;
    }

}
