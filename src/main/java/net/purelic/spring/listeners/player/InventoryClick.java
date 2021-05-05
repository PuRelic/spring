package net.purelic.spring.listeners.player;

import de.exceptionflug.protocolize.api.ClickType;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.event.InventoryClickEvent;
import de.exceptionflug.protocolize.items.ItemStack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.analytics.Analytics;
import net.purelic.spring.commands.parsers.Permission;
import net.purelic.spring.managers.*;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.punishment.Punishment;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.server.ServerType;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.Fetcher;
import net.purelic.spring.utils.ItemAction;
import net.purelic.spring.utils.ServerUtils;
import net.querz.nbt.tag.CompoundTag;

import java.util.UUID;

public class InventoryClick implements Listener {

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onInventoryClick(InventoryClickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ItemStack item = event.getClickedItem();
        ClickType clickType = event.getClickType();

        if (item != null) {
            CompoundTag tag = (CompoundTag) item.getNBTTag();
            if (!tag.getBoolean("spring")) return;

            event.setCancelled(true);
            InventoryModule.closeAllInventories(player);

            ItemAction action = ItemAction.valueOf(tag.getString("action"));
            String value = tag.getString("value");

            Playlist playlist = PlaylistManager.getPlaylist(value);

            switch (action) {
                case CREATE:
                    ServerType serverType = ServerType.valueOf(value);

                    if (serverType.isPremium()
                        && Permission.notPremium(player,
                        serverType.getName() + " servers are only available for Premium players!")) {
                        break;
                    }

                    CommandUtils.sendAlertMessage(player, "Creating your private " + serverType.getName() + " server! We'll notify you when it's ready.");
                    ServerManager.createPrivateSerer(player, serverType);
                    break;
                case JOIN:
                    ServerManager.getGameServers().get(value).connect(player);
                    break;
                case STOP:
                    ServerManager.removeServer(value);
                    break;
                case BETA:
                    if (Permission.notPremium(player,
                        "Enabling Beta Features is only available for Premium players!")) {
                        break;
                    }

                    ProfileManager.getProfile(player).toggleBetaFeatures();
                    InventoryManager.openPrivateServerInv(player);
                    break;
                case VIEW_PLAYLISTS:
                    PlaylistManager.openSelectorInventory(player);
                    break;
                case SELECT_PLAYLIST:
                    CommandUtils.sendAlertMessage(player, "Creating your private " + ServerType.CUSTOM_GAMES.getName() + " server! We'll notify you when it's ready.");
                    ServerManager.createPrivateSerer(player, playlist);
                    break;
                case QUEUE:
                    CommandUtils.sendSuccessMessage(player, "You were added to the queue for " + playlist.getName() + "!");
                    ServerManager.addToQueue(player, playlist);
                    break;
                case BROWSE_PRIVATE:
                    InventoryManager.openPrivateServerSelector(player);
                    break;
                case BROWSE_PUBLIC:
                    InventoryManager.openServerSelector(player);
                    break;
                case SELECT_PUBLIC:
                    int servers = ServerManager.getPublicServers(playlist, true).size();

                    if (servers == 1) ServerUtils.quickJoin(player, playlist);
                    else InventoryManager.openPublicServerSelector(player, playlist, false);
                    break;
                case QUICK_JOIN:
                    ServerUtils.quickJoin(player, playlist);
                    break;
                case SELECT_LEAGUE:
                    if (clickType == ClickType.LEFT_CLICK) {
                        LeagueManager.joinQueue(player, playlist);
                    } else {
                        InventoryManager.openPublicServerSelector(player, playlist, true);
                    }
                    break;
                case BROWSE_LEAGUE:
                    InventoryManager.openLeagueSelector(player);
                    break;
                case LEADERBOARD:
                    String lbUrl = Analytics.urlBuilder(player, "https://purelic.net/leaderboards", "league_gui");
                    player.sendMessage(new ComponentBuilder("View the rest of the leaderboard online")
                        .append(" » ").color(ChatColor.GRAY)
                        .append("purelic.net/leaderboards").color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Open").create()))
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, lbUrl))
                        .create());
                    break;
                case STATS:
                    String profileUrl = Analytics.urlBuilder(player, "https://purelic.net/players/" + player.getName(), "stats_gui");
                    player.sendMessage(new ComponentBuilder("View your full stats online:")
                        .append(" » ").color(ChatColor.GRAY)
                        .append("purelic.net/players/" + player.getName()).color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Open").create()))
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, profileUrl))
                        .create());
                    break;
                case MATCH:
                    String truncated = value.length() > 15 ? value.substring(0, 10) + "..." : "";
                    String matchUrl = Analytics.urlBuilder(player, "https://purelic.net/matches/" + value, "matches_gui");
                    player.sendMessage(new ComponentBuilder("View the full match online:")
                        .append(" » ").color(ChatColor.GRAY)
                        .append("purelic.net/matches/" + truncated).color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Open").create()))
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, matchUrl))
                        .create());
                    break;
                case PRIVATE_SERVER:
                    InventoryManager.openPrivateServerInv(player);
                    break;
                case APPEAL:
                    UUID punishedId = UUID.fromString(tag.getString("punished_uuid"));

                    Profile profile = ProfileManager.getProfile(punishedId);
                    Punishment punishment = profile.getPunishment(value);
                    punishment.appeal(player);

                    CommandUtils.sendSuccessMessage(player, "Successfully appealed " + Fetcher.getNameOf(punishedId) +
                        "'s " + punishment.getType().getName().toLowerCase() + "!");
                    DiscordManager.sendAppeal(player, Fetcher.getNameOf(punishedId), punishedId, punishment.getReason(), punishment.getType());
                    break;
            }
        }
    }

}
