package net.purelic.spring.server;

import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.config.Configuration;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.ItemAction;

import java.util.List;

public enum ServerType {

    CUSTOM_GAMES("custom_games", "custom_games_beta", "Custom Games", ItemType.IRON_SWORD, "Play private custom games with friends!", ItemAction.VIEW_PLAYLISTS, false),
    GAME_DEVELOPMENT("game_development", "game_development_beta", "Game Development", ItemType.BRICK, "Create your own maps and game modes!", ItemAction.CREATE, false);

    private final String key;
    private final String betaKey;
    private final String name;
    private final ItemType itemType;
    private final String description;
    private final ItemAction action;
    private final boolean premium;

    private int snapshotId;
    private int betaSnapshotId;

    ServerType(String key, String betaKey, String name, ItemType itemType, String description, ItemAction action, boolean premium) {
        this.key = key;
        this.betaKey = betaKey;
        this.name = name;
        this.itemType = itemType;
        this.description = description;
        this.action = action;
        this.premium = premium;
    }

    public String getName() {
        return this.name;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

    public int getSnapshotId() {
        return this.snapshotId;
    }

    public int getBetaSnapshotId() {
        return this.betaSnapshotId;
    }

    public boolean isPremium() {
        return this.premium;
    }

    public void setSnapshotId(Configuration config) {
        this.snapshotId = config.getInt("snapshots." + this.key);
        this.betaSnapshotId = config.getInt("snapshots." + this.betaKey);
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(this.itemType);
        item.setDisplayName(new ComponentBuilder(this.name).color(ChatColor.AQUA).bold(true).create());
        List<String> lore = ChatUtils.wrap(this.description);

        if (this.premium) {
            lore.add("");
            lore.add(ChatColor.GREEN + "◊ Premium Only");
        }

        item.setLore(lore);
        this.action.apply(item, this.name());
        return item;
    }

}
