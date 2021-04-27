package net.purelic.spring.server;

import de.exceptionflug.protocolize.items.ItemFlag;
import de.exceptionflug.protocolize.items.ItemStack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.PlaylistManager;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.ItemAction;
import net.purelic.spring.utils.ServerUtils;

import java.util.*;

public class PublicServer {

    private final Playlist playlist;
    private final int slot;
    private final int queue;
    private final int maxPlayers;
    private final int overflow;
    private final int scaleThreshold;
    private final int maxServers;
    private final int minParty;
    private final int maxParty;
    private final boolean ranked;
    private final String serverName;
    private final ServerSize serverSize;
    private final Set<ProxiedPlayer> queued;
    private boolean starting;

    public PublicServer(Map<String, Object> data) {
        this.playlist = PlaylistManager.getPlaylist((String) data.get("playlist"));
        this.slot = (int) data.get("slot");
        this.queue = (int) data.get("queue");
        this.maxPlayers = (int) data.get("max_players");
        this.overflow = (int) data.get("overflow");
        this.scaleThreshold = (int) data.get("scale_threshold");
        this.maxServers = (int) data.get("max_servers");
        this.minParty = (int) data.getOrDefault("min_party", 0);
        this.maxParty = (int) data.getOrDefault("max_party", 0);
        this.ranked = (boolean) data.getOrDefault("ranked", false);
        this.serverName = (String) data.getOrDefault("server_name", this.playlist.getName());
        this.serverSize = ServerSize.valueOf((String) data.getOrDefault("server_size", ServerSize.BASIC.name()));
        this.queued = new HashSet<>();
        this.starting = false;
    }

    public Playlist getPlaylist() {
        return this.playlist;
    }

    public int getSlot() {
        return this.slot;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getOverflow() {
        return this.overflow;
    }

    public int getMaxServers() {
        return this.maxServers;
    }

    public int getScaleThreshold() {
        return this.scaleThreshold;
    }

    public int getMinParty() {
        return this.minParty;
    }

    public int getMaxParty() {
        return this.maxParty;
    }

    public boolean isRanked() {
        return this.ranked;
    }

    public String getServerName() {
        return this.serverName;
    }

    public ServerSize getServerSize() {
        return this.serverSize;
    }

    public Set<ProxiedPlayer> getQueued() {
        return this.queued;
    }

    public void addToQueue(ProxiedPlayer player) {
        this.queued.add(player);

        if (this.queued.size() >= this.queue) {
            String message = this.playlist.getName() + " will be opening shortly! We'll notify you when it's ready.";

            if (!this.starting) {
                this.queued.forEach(queued -> CommandUtils.sendAlertMessage(queued, message));
                ServerManager.createPublicServer(this);
                this.starting = true;
            } else {
                CommandUtils.sendAlertMessage(player, message);
            }
        }
    }

    public void removeFromQueue(ProxiedPlayer player) {
        this.queued.remove(player);
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
        if (!starting) this.queued.clear();
    }

    public ItemStack toItem(ProxiedPlayer player) {
        List<GameServer> servers = ServerManager.getGameServers(this);
        int totalServers = servers.size();

        ItemStack item = new ItemStack(this.playlist.getItemType());
        item.setDisplayName(new ComponentBuilder(this.playlist.getName()).color(ChatColor.AQUA).bold(true).create());

        if ((totalServers == 0 || this.starting) && !this.isRanked()) {
            if (this.starting) {
                item.setLore(Arrays.asList(
                    ChatColor.WHITE + this.playlist.getDescription(),
                    "",
                    ChatColor.GREEN + "Server starting...",
                    "",
                    this.queued.contains(player) ? ChatColor.GREEN + "You are queued!" : ChatColor.WHITE + "Click to Join Queue"
                ));
            } else {
                item.setLore(Arrays.asList(
                    ChatColor.WHITE + this.playlist.getDescription(),
                    "",
                    ChatColor.AQUA + "" + this.queued.size() + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + this.queue + " Queued",
                    "",
                    this.queued.contains(player) ? ChatColor.GREEN + "You are queued!" : ChatColor.WHITE + "Click to Join Queue"
                ));
            }

            ItemAction.QUEUE.apply(item, this.playlist.getName());
        } else {
            int totalPlayers = ServerUtils.totalPlaying(servers);
            String browse = ChatColor.GRAY + "R-Click to Browse " + ChatColor.AQUA + totalServers + ChatColor.GRAY + " Server" + (totalServers == 1 ? "" : "s");

            if (this.isRanked()) {
                item.setLore(Arrays.asList(
                    ChatColor.WHITE + this.playlist.getDescription(),
                    "",
                    ChatColor.AQUA + "" + totalPlayers + ChatColor.GRAY + " Playing",
                    ChatColor.GRAY + "Party Required: " + ChatColor.AQUA + ServerUtils.getPartyString(this),
                    "",
                    ChatColor.WHITE + "L-Click to Join Queue",
                    servers.size() > 0 ? browse : ""
                ));

                ItemAction.SELECT_LEAGUE.apply(item, this.playlist.getName());
            } else {
                item.setLore(Arrays.asList(
                    ChatColor.WHITE + this.playlist.getDescription(),
                    "",
                    ChatColor.AQUA + "" + totalPlayers + ChatColor.GRAY + " Playing"
                ));

                ItemAction.SELECT_PUBLIC.apply(item, this.playlist.getName());
            }
        }

        item.setFlag(ItemFlag.HIDE_ATTRIBUTES, true);
        return item;
    }

}
