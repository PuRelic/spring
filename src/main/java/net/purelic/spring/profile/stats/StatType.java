package net.purelic.spring.profile.stats;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.text.WordUtils;

public enum StatType {

    KILLS(ChatColor.GREEN),
    DEATHS,
    ASSISTS(ChatColor.GREEN),
    FINAL_KILLS(ChatColor.GREEN),
    FINAL_DEATHS,
    BEDS(ChatColor.YELLOW),
    WINS(ChatColor.GREEN),
    LOSSES,
    WIN_STREAK(ChatColor.GREEN),
    GAMES_PLAYED,
    HEADS_COLLECTED(ChatColor.YELLOW),
    HEADS_STOLEN(ChatColor.YELLOW),
    HEADS_RECOVERED(ChatColor.YELLOW),
    DAMAGE_DEALT(ChatColor.GREEN),
    DAMAGE_RECEIVED,
    ARROWS_HIT,
    ARROWS_SHOT,
    FLAGS(ChatColor.YELLOW),
    ;

    private final String key;
    private final String name;
    private final ChatColor color;

    StatType() {
        this(ChatColor.AQUA);
    }

    StatType(ChatColor color) {
        this.key = this.name().toLowerCase();
        this.name = WordUtils.capitalizeFully(this.key.replaceAll("_", " "));
        this.color = color;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public ChatColor getColor() {
        return this.color;
    }

}
