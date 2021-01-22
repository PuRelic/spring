package net.purelic.spring.server;

import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.text.WordUtils;

public enum ServerStatus {

    RESTARTING(ChatColor.RED, ItemType.RED_WOOL),
    STARTING(ChatColor.YELLOW, ItemType.YELLOW_WOOL),
    STARTED(ChatColor.GREEN, ItemType.LIME_WOOL),
    ;

    private final ChatColor color;
    private final ItemType itemType;

    ServerStatus(ChatColor color, ItemType itemType) {
        this.color = color;
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

    @Override
    public String toString() {
        return this.color + WordUtils.capitalizeFully(name());
    }

}
