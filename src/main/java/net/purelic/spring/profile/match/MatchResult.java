package net.purelic.spring.profile.match;

import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;

public enum MatchResult {

    WIN("Won", ChatColor.GREEN, ItemType.LIME_WOOL),
    LOSS("Lost", ChatColor.RED, ItemType.RED_WOOL),
    DRAW("Tied", ChatColor.YELLOW, ItemType.YELLOW_WOOL),
    ;

    private final String name;
    private final ChatColor color;
    private final ItemType itemType;

    MatchResult(String name, ChatColor color, ItemType itemType) {
        this.name = name;
        this.color = color;
        this.itemType = itemType;
    }

    public String getName() {
        return this.color + this.name;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

}
