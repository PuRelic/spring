package net.purelic.spring.server;

import de.exceptionflug.protocolize.items.ItemFlag;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.purelic.spring.utils.ItemAction;

import java.util.Collections;
import java.util.Map;

public class Playlist {

    private final String name;
    private final String id;
    private final String description;
    private final ItemType itemType;
    private final ItemStack itemStack;
    private final boolean archived;

    public Playlist(Map<String, Object> data) {
        this.name = (String) data.get("name");
        this.id = this.name.replaceAll(" ", "_").toLowerCase();
        this.description = (String) data.get("description");
        this.itemType = ItemType.valueOf((String) data.get("item"));
        this.itemStack = this.getItem();
        this.archived = (boolean) data.getOrDefault("archived", false);
    }

    private ItemStack getItem() {
        ItemStack item = new ItemStack(this.itemType);
        item.setDisplayName(new ComponentBuilder(this.name).color(ChatColor.AQUA).bold(true).create());
        item.setLore(Collections.singletonList(ChatColor.WHITE + this.description));
        item.setFlag(ItemFlag.HIDE_ATTRIBUTES, true);
        ItemAction.SELECT_PLAYLIST.apply(item, this.name);
        return item;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

    public ItemStack toItem() {
        return this.itemStack;
    }

    public boolean isArchived() {
        return this.archived;
    }

}
