package net.purelic.spring.league;

import com.google.cloud.Timestamp;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.purelic.spring.utils.ChatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Season {

    private final String id;
    private final String name;
    private final Timestamp started;
    private final Timestamp ended;

    public Season(String id, Map<String, Object> data) {
        this.id = id;
        this.name = (String) data.getOrDefault("name", "Beta Season");
        this.started = (Timestamp) data.getOrDefault("started", null);
        this.ended = (Timestamp) data.getOrDefault("ended", null);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Timestamp getStarted() {
        return this.started;
    }

    public Timestamp getEnded() {
        return this.ended;
    }

    public ItemStack toItem() {
        ItemStack item = new ItemStack(ItemType.PAPER);
        item.setDisplayName("" + ChatColor.AQUA + ChatColor.BOLD + this.name);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Started " + ChatUtils.format(this.started));
        lore.add(ChatColor.GRAY + "Ends " + ChatUtils.format(this.ended));
        lore.add("");
        lore.add(ChatColor.GRAY + "Ranks:");
        Arrays.stream(LeagueRank.values()).forEach(rank -> lore.add(rank.toString()));
        item.setLore(lore);
        return item;
    }

}
