package net.purelic.spring.league;

import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.utils.ItemAction;

import java.util.ArrayList;
import java.util.List;

public enum LeagueRank {

    IRON("Iron", '❖', ChatColor.DARK_GRAY, ItemType.IRON_INGOT, "0 - 500 ELO"),
    GOLD("Gold", '❖', ChatColor.YELLOW, ItemType.GOLD_INGOT, "500 - 1000 ELO"),
    DIAMOND("Diamond", '❖', ChatColor.AQUA, ItemType.DIAMOND, "1000 - 1500 ELO"),
    EMERALD("Emerald", '❖', ChatColor.GREEN, ItemType.EMERALD, "1500 - 2000 ELO"),
    QUARTZ("Quartz", '❖', ChatColor.WHITE, ItemType.QUARTZ, "2000+ ELO"),
    ;

    private final String name;
    private final String flair;
    private final ChatColor color;
    private final ItemType itemType;
    private final String description;
    private LeagueRank nextRank;

    static {
        IRON.nextRank = GOLD;
        GOLD.nextRank = DIAMOND;
        DIAMOND.nextRank = EMERALD;
        EMERALD.nextRank = QUARTZ;
    }

    LeagueRank(String name, Character flair, ChatColor color, ItemType itemType, String description) {
        this.name = name;
        this.flair = flair.toString();
        this.color = color;
        this.itemType = itemType;
        this.description = description;
    }

    public String getFlair() {
        return this.color + this.flair + ChatColor.RESET;
    }

    public String getName() {
        return this.color + this.name + ChatColor.RESET;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

    public String getDescription() {
        return this.description;
    }

    public LeagueRank getNextRank() {
        return this.nextRank;
    }

    @Override
    public String toString() {
        return this.getFlair() + " " + this.getName() + ChatColor.DARK_GRAY + " (" + ChatColor.GRAY + this.description + ChatColor.DARK_GRAY + ")";
    }

    public static ItemStack toItem(ProxiedPlayer player, Playlist playlist) {
        int rating = ProfileManager.getProfile(player).getRating(playlist);
        LeagueRank rank = getRank(rating);
        LeagueRank next = rank.getNextRank();
        ItemStack item = new ItemStack(rank.getItemType());
        item.setDisplayName(new ComponentBuilder(playlist.getName() + " Rank").color(ChatColor.AQUA).bold(true).create());

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Rating: " + ChatColor.WHITE + rating);
        lore.add(ChatColor.GRAY + "Rank: " + rank.getFlair() + " " + rank.getName());
        if (next != null) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Next: " + next.getFlair() + " " + next.getName() + "");
            lore.add(ChatColor.GRAY + next.getDescription());
        }
        lore.add("");
        lore.add(ChatColor.RESET + "View your full stats online:");
        lore.add(ChatColor.AQUA + "purelic.net/players/" + player.getName());
        item.setLore(lore);

        ItemAction.STATS.apply(item);
        return item;
    }

    public static LeagueRank getRank(int rating) {
        if (rating < 500) return IRON;
        else if (rating < 1000) return GOLD;
        else if (rating < 1500) return DIAMOND;
        else if (rating < 2000) return EMERALD;
        else return QUARTZ;
    }

}
