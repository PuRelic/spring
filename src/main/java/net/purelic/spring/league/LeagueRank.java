package net.purelic.spring.league;

import net.md_5.bungee.api.ChatColor;

public enum LeagueRank {

    IRON("Iron", '❖', ChatColor.DARK_GRAY, "0 - 500 ELO"),
    GOLD("Gold", '❖', ChatColor.YELLOW, "500 - 1000 ELO"),
    DIAMOND("Diamond", '❖', ChatColor.AQUA, "1000 - 1500 ELO"),
    EMERALD("Emerald", '❖', ChatColor.GREEN, "1500 - 2000 ELO"),
    QUARTZ("Quartz", '❖', ChatColor.WHITE, "2000+ ELO"),
    ;

    private final String name;
    private final String flair;
    private final ChatColor color;
    private final String description;

    LeagueRank(String name, Character flair, ChatColor color, String description) {
        this.name = name;
        this.flair = flair.toString();
        this.color = color;
        this.description = description;
    }

    public String getFlair() {
        return this.color + this.flair + ChatColor.RESET;
    }

    public String getName() {
        return this.color + this.name + ChatColor.RESET;
    }

    public static LeagueRank getRank(long rating) {
        if (rating < 500) return IRON;
        else if (rating < 1000) return GOLD;
        else if (rating < 1500) return DIAMOND;
        else if (rating < 2000) return EMERALD;
        else return QUARTZ;
    }

}
