package net.purelic.spring.server;

import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.config.Configuration;
import net.purelic.spring.utils.ItemAction;

import java.util.Arrays;
import java.util.Collections;

public enum ServerType {

    CUSTOM_GAMES("custom_games", "custom_games_beta", "Custom Games", ItemType.IRON_SWORD, "Play private custom games with friends!"),
    GAME_DEVELOPMENT("game_development", "game_development_beta", "Game Development", ItemType.BRICK, "Create your own maps and game modes!");

    private final String key;
    private final String betaKey;
    private final String name;
    private final ItemType itemType;
    private final String description;

    private int snapshotId;
    private int betaSnapshotId;

    ServerType(String key, String betaKey, String name, ItemType itemType, String description) {
        this.key = key;
        this.betaKey = betaKey;
        this.name = name;
        this.itemType = itemType;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

    public String getDescription() {
        return this.description;
    }

    public int getSnapshotId() {
        return this.snapshotId;
    }

    public int getBetaSnapshotId() {
        return this.betaSnapshotId;
    }

    public void setSnapshotId(Configuration config) {
        this.snapshotId = config.getInt("snapshots." + this.key);
        this.betaSnapshotId = config.getInt("snapshots." + this.betaKey);
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(this.itemType);
        item.setDisplayName(new ComponentBuilder(this.name).color(ChatColor.AQUA).bold(true).create());

        // TODO replace with a premium flag
        if (this != GAME_DEVELOPMENT) {
            item.setLore(Arrays.asList(ChatColor.WHITE + "" + ChatColor.ITALIC + this.description, "", ChatColor.GREEN + "◊ Premium Only"));
        } else {
            item.setLore(Collections.singletonList(ChatColor.WHITE + "" + ChatColor.ITALIC + this.description));
        }

        ItemAction.CREATE.apply(item, this.name());
        return item;
    }

}
