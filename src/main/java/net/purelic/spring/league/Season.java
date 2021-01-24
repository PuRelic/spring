package net.purelic.spring.league;

import com.google.cloud.Timestamp;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Season {

    private static final PrettyTime PT = new PrettyTime();

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
        lore.add(ChatColor.GRAY + "Started " + PT.format(this.started.toDate()));
        lore.add(ChatColor.GRAY + "Ends " + PT.format(this.ended.toDate()));
        lore.add("");
        lore.add(ChatColor.GRAY + "Ranks:");
        Arrays.stream(LeagueRank.values()).forEach(rank -> lore.add(rank.toString()));
        item.setLore(lore);
        return item;
    }

}
