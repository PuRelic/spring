package net.purelic.spring.utils;

import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.punishment.Punishment;
import net.purelic.spring.punishment.PunishmentType;
import net.purelic.spring.server.GameServer;
import net.querz.nbt.tag.CompoundTag;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemUtils {

    public static ItemStack getPublicServerItem() {
        ItemStack item = new ItemStack(ItemType.IRON_CHESTPLATE);
        item.setDisplayName(new ComponentBuilder("Casual Games").color(ChatColor.AQUA).bold(true).create());

        List<String> lore = getServerCategoryLore(
            ServerManager.getPublicServers(true),
            "Play a variety unique game modes on community built maps!");
        item.setLore(lore);

        ItemAction.BROWSE_PUBLIC.apply(item);
        return item;
    }

    public static ItemStack getLeagueServerItem() {
        ItemStack item = new ItemStack(ItemType.QUARTZ);
        item.setDisplayName(new ComponentBuilder("League" + ChatColor.RESET + ChatColor.GRAY + " (/league)").color(ChatColor.AQUA).bold(true).create());

        List<String> lore = getServerCategoryLore(
            ServerManager.getRankedServers(true),
            "Show off your skill and play in competitive, ranked matches!");
        item.setLore(lore);

        ItemAction.BROWSE_LEAGUE.apply(item);

        return item;
    }

    public static ItemStack getPrivateServerItem() {
        ItemStack item = new ItemStack(ItemType.TRIPWIRE_HOOK);
        item.setDisplayName(new ComponentBuilder("Private Server" + ChatColor.RESET + ChatColor.GRAY + " (/ps)").color(ChatColor.AQUA).bold(true).create());
        item.setLore(ChatUtils.wrap("Host custom games or create your own maps!"));
        ItemAction.PRIVATE_SERVER.apply(item);
        return item;
    }

    private static List<String> getServerCategoryLore(List<GameServer> servers, String description) {
        int playing = ServerUtils.totalPlaying(servers);
        List<String> lore = ChatUtils.wrap(description);

        if (playing > 0) {
            lore.add("");
            lore.add("" + ChatColor.AQUA + playing + ChatColor.WHITE + " playing");
        }

        return lore;
    }

    public static ItemStack getPrivateServerItem(List<GameServer> servers) {
        int playing = ServerUtils.totalPlaying(servers);
        List<String> lore = ChatUtils.wrap(ChatColor.WHITE + "Browse " + ChatColor.AQUA + servers.size() + ChatColor.WHITE + " private server" + (servers.size() == 1 ? "" : "s"));

        if (playing > 0) {
            lore.add("");
            lore.add("" + ChatColor.AQUA + playing + ChatColor.WHITE + " playing");
        }

        ItemStack skull = new ItemStack(ItemType.PLAYER_HEAD);
        skull.setDisplayName(new ComponentBuilder("Custom Games").color(ChatColor.AQUA).bold(true).create());
        skull.setLore(lore);
        ItemAction.BROWSE_PRIVATE.apply(skull);
        return skull;
    }

    public static ItemStack getBetaItem(ProxiedPlayer player) {
        Profile profile = ProfileManager.getProfile(player);
        boolean beta = profile.hasBetaFeatures();

        ItemStack item = new ItemStack(beta ? ItemType.LIME_DYE : ItemType.GRAY_DYE);
        item.setDisplayName(new ComponentBuilder("Beta Features").color(ChatColor.WHITE).bold(true).create());
        item.setLore(Arrays.asList(
            (beta ? ChatColor.ITALIC + "" + ChatColor.GREEN + "Enabled" : ChatColor.ITALIC + "" + ChatColor.RED + "Disabled"),
            "",
            ChatColor.WHITE + "Enabling beta features gives you access",
            ChatColor.WHITE + "to updates early, but might introduce",
            ChatColor.WHITE + "new bugs or stability issues.",
            "",
            ChatColor.WHITE + "Please report bugs and",
            ChatColor.WHITE + "feedback in Discord.",
            "",
            ChatColor.GREEN + "◊ Premium Only"
        ));

        ItemAction.BETA.apply(item);

        return item;
    }

    public static ItemStack getJoinServerItem(String serverName) {
        ItemStack item = new ItemStack(ItemType.LIME_WOOL);
        item.setDisplayName(new ComponentBuilder("Join Server").color(ChatColor.GREEN).bold(true).create());
        ItemAction.JOIN.apply(item, serverName);
        return item;
    }

    public static ItemStack getStartingServerItem() {
        ItemStack item = new ItemStack(ItemType.YELLOW_WOOL);
        item.setDisplayName(new ComponentBuilder("Server Starting...").color(ChatColor.YELLOW).bold(true).create());
        item.setLore(Arrays.asList(
            ChatColor.WHITE + "You will be notified when",
            ChatColor.WHITE + "your server is ready!"
        ));

        ItemAction.NOTHING.apply(item);

        return item;
    }

    public static ItemStack getStopServerItem(String serverName) {
        ItemStack item = new ItemStack(ItemType.RED_WOOL);
        item.setDisplayName(new ComponentBuilder("Force Shutdown").color(ChatColor.RED).bold(true).create());
        item.setLore(Arrays.asList(
            ChatColor.RED + "" + ChatColor.BOLD + "WARNING",
            "",
            ChatColor.WHITE + "Please join your server and use",
            ChatColor.WHITE + "/shutdown if possible. Only use this",
            ChatColor.WHITE + "if you can't connect to your server."
        ));

        ItemAction.STOP.apply(item, serverName);

        return item;
    }

    public static ItemStack getPunishmentItem(Punishment punishment, UUID punishedId) {
        PunishmentType type = punishment.getType();

        String expiration = !punishment.hasExpirationTimestamp() ? "" :
            ChatColor.DARK_AQUA + (punishment.isExpired() ? "Expired " : "Expires ") +
                ChatUtils.format(punishment.getExpirationTimestamp().toDate());

        List<String> lore = Arrays.asList(
            ChatColor.WHITE + punishment.getReason(),
            "",
            ChatColor.DARK_AQUA + "Punished by " + Fetcher.getNameOf(punishment.getPunisher()),
            ChatColor.DARK_AQUA + "Received " + ChatUtils.format(punishment.getTimestamp().toDate()),
            expiration
        );

        ItemStack item = new ItemStack(type.getMaterial());
        item.setDisplayName("" + ChatColor.RESET + getPunishmentColor(punishment) + ChatColor.BOLD + type.getName());
        item.setLore(lore);

        CompoundTag itemTag = (CompoundTag) item.getNBTTag();
        itemTag.putString("punished_uuid", punishedId.toString());
        ItemAction.APPEAL.apply(item, punishment.getPunishmentId());

        return item;
    }

    public static ChatColor getPunishmentColor(Punishment punishment) {
        if (punishment.isAppealed()) return ChatColor.GREEN;
        if (punishment.getType() == PunishmentType.PERMA_BAN) return ChatColor.RED;
        if (punishment.isStale()) return ChatColor.GRAY;
        if (punishment.isExpired()) return ChatColor.YELLOW;
        return ChatColor.RED;
    }

}
