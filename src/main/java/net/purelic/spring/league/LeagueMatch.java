package net.purelic.spring.league;

import net.purelic.spring.server.GameServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeagueMatch {

    private final LeagueTeam blueTeam;
    private final LeagueTeam redTeam;

    public LeagueMatch(LeagueTeam blue, LeagueTeam red) {
        this.blueTeam = blue;
        this.redTeam = red;
    }

    public LeagueTeam getBlueTeam() {
        return this.blueTeam;
    }

    public LeagueTeam getRedTeam() {
        return this.redTeam;
    }

    public void sendMessage(String message, boolean legacy) {
        this.blueTeam.sendMessage(message, legacy);
        this.redTeam.sendMessage(message, legacy);
    }

    public void sendLegacyMessage(String message) {
        this.blueTeam.sendLegacyMessage(message);
        this.redTeam.sendLegacyMessage(message);
    }

    public List<Map<String, Object>> toData() {
        List<Map<String, Object>> data = new ArrayList<>();

        Map<String, Object> blue = this.blueTeam.toData();
        blue.put("id", "BLUE");
        data.add(blue);

        Map<String, Object> red = this.redTeam.toData();
        red.put("id", "RED");
        data.add(red);

        return data;
    }

    public void connect(GameServer server) {
        this.blueTeam.getPlayers().forEach(server::connect);
        this.redTeam.getPlayers().forEach(server::connect);
    }

}
