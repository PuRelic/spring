package net.purelic.spring.profile.match;

import net.md_5.bungee.api.ChatColor;

import java.util.Map;

public class MatchTeam {

    private final String id;
    private final ChatColor color;
    private final String name;
    private final int score;

    public MatchTeam(Map<String, Object> data) {
        this.id = (String) data.get("team_id");
        this.color = ChatColor.of(((String) data.get("team_color")).toUpperCase());
        this.name = (String) data.get("team_name");
        this.score = ((Long) data.get("score")).intValue();
    }

    public String getId() {
        return this.id;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public String getName() {
        return this.color + this.name + ChatColor.RESET;
    }

    public int getScore() {
        return this.score;
    }

    public String toString(boolean bold, boolean showScore, boolean winner) {
        String result = "" + (bold ? ChatColor.BOLD : "") + this.color + this.name + ChatColor.RESET;
        if (showScore) result += ChatColor.DARK_GRAY + " - " + ChatColor.GRAY;
        if (winner) result += ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + "Winner" + ChatColor.DARK_GRAY + ")";
        return result;
    }

}
