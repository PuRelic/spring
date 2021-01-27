package net.purelic.spring.listeners;

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
import net.purelic.spring.managers.*;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.server.ServerType;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.ItemAction;
import net.purelic.spring.utils.PermissionUtils;
import net.purelic.spring.utils.ServerUtils;
import net.querz.nbt.tag.CompoundTag;

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

                    if (serverType.isPremium() && !PermissionUtils.isDonator(player)) {
                        CommandUtils.sendErrorMessage(player, serverType.getName() + " servers are only available for Premium players. Consider donating at purelic.net/donate");
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
                    if (!PermissionUtils.isDonator(player)) {
                        CommandUtils.sendErrorMessage(player, "Enabling Beta Features is only available for Premium players. Consider donating at purelic.net/donate");
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
                    if (clickType == ClickType.LEFT_CLICK) {
                        ServerUtils.quickJoin(player, playlist);
                    } else {
                        InventoryManager.openPublicServerSelector(player, playlist, false);
                    }
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
                    player.sendMessage(new ComponentBuilder("View the rest of the leaderboard online")
                            .append(" » ").color(ChatColor.GRAY)
                            .append("purelic.net/leaderboards").color(ChatColor.AQUA)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Open").create()))
                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://purelic.net/leaderboards"))
                            .create());
                    break;
                case STATS:
                    player.sendMessage(new ComponentBuilder("View your full stats online:")
                        .append(" » ").color(ChatColor.GRAY)
                        .append("purelic.net/players/" + player.getName()).color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Open").create()))
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://purelic.net/players/" + player.getName()))
                        .create());
                    break;
                case MATCH:
                    String truncated = value.length() > 15 ? value.substring(0, 10) + "..." : "";
                    player.sendMessage(new ComponentBuilder("View the full match online:")
                        .append(" » ").color(ChatColor.GRAY)
                        .append("purelic.net/matches/" + truncated).color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Open").create()))
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://purelic.net/matches/" + value))
                        .create());
                    break;
                case PRIVATE_SERVER:
                    InventoryManager.openPrivateServerInv(player);
                    break;
            }
        }
    }

}
