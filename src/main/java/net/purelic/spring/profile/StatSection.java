package net.purelic.spring.profile;

import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.utils.ItemAction;
import net.purelic.spring.utils.Protocol;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum StatSection {

    TOTAL("Total", 10, ItemType.PAPER),
    RANKED("League", 12, ItemType.QUARTZ),
    BED_WARS("Bed Wars", 14, ItemType.RED_BED, StatType.BEDS),
    DEATHMATCH("Deathmatch", 16, ItemType.STONE_SWORD),
    HEAD_HUNTER("Head Hunter", 29, ItemType.SKELETON_SKULL, StatType.HEADS_COLLECTED, StatType.HEADS_STOLEN, StatType.HEADS_RECOVERED),
    KING_OF_THE_HILL("King of the Hill", 31, ItemType.GOLDEN_HELMET),
    CAPTURE_THE_FLAG("Capture the Flag", 33, ItemType.WHITE_BANNER, StatType.FLAGS),
    ;

    private final StatType[] defaultStats = new StatType[] { StatType.GAMES_PLAYED, StatType.WINS, StatType.KILLS, StatType.DEATHS };
    private final String key;
    private final String name;
    private final int slot;
    private final ItemType itemType;
    private final List<StatType> statTypes;

    StatSection(String name, int slot, ItemType itemType, StatType... statTypes) {
        this.key = this.name().toLowerCase();
        this.name = name;
        this.slot = slot;
        this.itemType = itemType;
        this.statTypes = Arrays.asList(statTypes);
    }

    public String getKey() {
        return this.key;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack toItem(ProxiedPlayer viewer, ProxiedPlayer statsPlayer) {
        Map<String, Object> stats = this.getStats(statsPlayer);

        ItemStack item = new ItemStack(this.useLegacyItem(viewer) ? ItemType.OAK_SIGN : this.itemType);
        item.setDisplayName("" + ChatColor.AQUA + ChatColor.BOLD + this.name + " Stats");

        List<String> lore = this.getDefaultStats(stats);
        if (statTypes.size() > 0) lore.add("");
        this.statTypes.forEach(statType -> lore.add(this.getFormattedValue(stats, statType)));
        item.setLore(lore);

        ItemAction.STATS.apply(item);
        return item;
    }

    private boolean useLegacyItem(ProxiedPlayer player) {
        return this == CAPTURE_THE_FLAG && Protocol.isLegacy(player);
    }

    private String getFormattedValue(Map<String, Object> stats, StatType statType) {
        return this.getFormattedValue(statType, this.getIntValue(stats, statType));
    }

    private String getFormattedValue(StatType statType, int value) {
        return ChatColor.GRAY + statType.getName() + ": " + statType.getColor() + value;
    }

    private String getFormattedValue(StatType statType, double value) {
        return this.getFormattedValue(statType.getName(), statType.getColor(), value);
    }

    private String getFormattedValue(String label, double value) {
        return this.getFormattedValue(label, ChatColor.AQUA, value);
    }

    private String getFormattedValue(String label, ChatColor color, double value) {
        return ChatColor.GRAY + label + ": " + color + new DecimalFormat("0.0").format(value);
    }

    private int getIntValue(Map<String, Object> stats, StatType statType) {
        return ((Long) stats.getOrDefault(statType.getKey(), 0L)).intValue();
    }

    private int getDoubleValue(Map<String, Object> stats, StatType statType) {
        return ((Double) stats.getOrDefault(statType.getKey(), 0D)).intValue();
    }

    private Map<String, Object> getStats(ProxiedPlayer player) {
        Profile profile = ProfileManager.getProfile(player);
        if (this == TOTAL) return profile.getStats(this);
        else return profile.getTotalStats(this);
    }

    private List<String> getDefaultStats(Map<String, Object> stats) {
        List<String> lore = new ArrayList<>();

        int wins = this.getIntValue(stats, StatType.WINS);
        double losses = Math.max(this.getIntValue(stats, StatType.LOSSES), 1);
        double wlr = wins / losses;

        int kills = this.getIntValue(stats, StatType.KILLS);
        int finalKills = this.getIntValue(stats, StatType.FINAL_KILLS);
        double deaths = Math.max(this.getIntValue(stats, StatType.DEATHS), 1);
        double finalDeaths = Math.max(this.getIntValue(stats, StatType.FINAL_DEATHS), 1);
        int assists = this.getIntValue(stats, StatType.ASSISTS);
        double kd = kills / deaths;
        double fkd = finalKills / finalDeaths;
        double kda = (kills + assists) / deaths;

        int damageDealt = this.getDoubleValue(stats, StatType.DAMAGE_DEALT);
        double damageReceived = Math.max(this.getDoubleValue(stats, StatType.DAMAGE_RECEIVED), 1);
        double nd = damageDealt / damageReceived;

        int hit = this.getIntValue(stats, StatType.ARROWS_HIT);
        int shot = Math.max(this.getIntValue(stats, StatType.ARROWS_SHOT), 1);
        double accuracy = (hit / (double) shot) * 100;

        lore.add(this.getFormattedValue(stats, StatType.GAMES_PLAYED));
        lore.add(this.getFormattedValue(stats, StatType.WINS) + "  " + this.getFormattedValue("WLR", wlr));
        lore.add("");
        lore.add(this.getFormattedValue(StatType.KILLS, kills) + "  " + this.getFormattedValue("KDR", kd));
        lore.add(this.getFormattedValue(StatType.ASSISTS, assists) + "  " + this.getFormattedValue("KDAR", kda));
        lore.add(this.getFormattedValue(StatType.FINAL_KILLS, finalKills) + "  " + this.getFormattedValue("FKDR", fkd));
        lore.add("");
        lore.add(this.getFormattedValue(StatType.DAMAGE_DEALT, damageDealt) + "  " + this.getFormattedValue("NDR", nd));
        lore.add(this.getFormattedValue("Bow Accuracy", accuracy) + "%");

        return lore;
    }

}
