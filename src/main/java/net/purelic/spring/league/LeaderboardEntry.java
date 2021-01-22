package net.purelic.spring.league;

import net.md_5.bungee.api.ChatColor;

import java.util.Map;

public class LeaderboardEntry {

    private final String name;
    private final long value;

    public LeaderboardEntry(Map<String, Object> data) {
        this((String) data.get("name"), (long) data.get("value"));
    }

    private LeaderboardEntry(String name, long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public long getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        LeagueRank rank = LeagueRank.getRank(this.value);
        return ChatColor.RESET + this.name + ChatColor.GRAY + " - " +
                rank.getFlair() + " " + rank.getName() +
                ChatColor.DARK_GRAY + " (" + ChatColor.GRAY +
                this.value + ChatColor.DARK_GRAY + ")";
    }

}
