package net.purelic.spring.server;

import com.google.cloud.Timestamp;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.pojo.*;
import de.exceptionflug.protocolize.items.ItemFlag;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.purelic.commons.Commons;
import net.purelic.spring.Spring;
import net.purelic.spring.analytics.events.ServerCreatedEvent;
import net.purelic.spring.analytics.events.ServerDestroyedEvent;
import net.purelic.spring.analytics.events.ServerStartedEvent;
import net.purelic.spring.league.LeagueMatch;
import net.purelic.spring.managers.*;
import net.purelic.spring.party.Party;
import net.purelic.spring.utils.*;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameServer {

    private final Spring plugin;
    private final ProxyServer proxy;
    private final String id;
    private final String name;
    private final Timestamp createdAt;
    private final ServerSize size;
    private final ServerRegion region;
    private final ServerType type;
    private final int snapshotId;
    private final boolean isPrivate;
    private final boolean beta;
    private final Playlist playlist;
    private final int maxPlayers;
    private final int minParty;
    private final int maxParty;
    private final boolean ranked;
    private final List<String> rankedPlayers;

    private boolean created; // if the droplet was created
    private boolean online; // if the server came online
    private boolean locked; // if the server owner has joined yet (if private)
    private boolean whitelisted; // if the server is whitelisted

    private ServerStatus status;
    private String map;
    private String gameMode;

    private Droplet droplet;
    private ScheduledTask task;
    private int dropletId;
    private String ip;
    private ServerInfo serverInfo;

    public GameServer(ProxiedPlayer player, ServerType type) {
        this(player, type, null);
    }

    public GameServer(ProxiedPlayer player, ServerType type, Playlist playlist) {
        this(
                player.getUniqueId().toString(),
                player.getName(),
                PermissionUtils.isDonator(player) ? ServerSize.BASIC : ServerSize.LITE,
                type,
                playlist,
                type == ServerType.GAME_DEVELOPMENT ?
                    (PermissionUtils.isDonator(player) ? 10 : 4) :
                    (PermissionUtils.isDonator(player) ? 20 : 8),
                0,
                0,
                false,
                true,
                ProfileManager.getProfile(player).hasBetaFeatures()
        );
    }

    public GameServer(PublicServer server) {
        this(
                UUID.randomUUID().toString(),
                server.isRanked() ? "League" : server.getPlaylist().getName().replaceAll(" ", ""),
                server.isRanked() ? ServerSize.BASIC : ServerSize.PREMIUM,
                ServerType.CUSTOM_GAMES,
                server.getPlaylist(),
                server.getMaxPlayers(),
                server.getMinParty(),
                server.getMaxParty(),
                server.isRanked(),
                false,
                false
        );
    }

    @SuppressWarnings("unchecked")
    public GameServer(String name, Map<String, Object> data) throws RequestUnsuccessfulException, DigitalOceanException {
        this.plugin = Spring.getPlugin();
        this.proxy = this.plugin.getProxy();
        this.id = (String) data.get("id");
        this.name = name;
        this.createdAt = (Timestamp) data.get("created_at");
        this.size = ServerSize.valueOf((String) data.get("size"));
        this.region = ServerRegion.valueOf((String) data.get("region"));
        this.type = ServerType.valueOf((String) data.get("type"));
        this.beta = (boolean) data.get("beta");
        this.isPrivate = (boolean) data.get("private");
        this.playlist = PlaylistManager.getPlaylist((String) data.get("playlist"));
        this.maxPlayers = (int) data.get("max_players");
        this.minParty = (int) data.get("min_party");
        this.maxParty = (int) data.get("max_party");
        this.ranked = (boolean) data.get("ranked");
        this.rankedPlayers = (List<String>) data.get("ranked_players");

        this.created = (boolean) data.get("created");
        this.online = (boolean) data.get("online");
        this.locked = (boolean) data.get("locked");
        this.whitelisted = (boolean) data.get("whitelisted");

        this.status = ServerStatus.valueOf((String) data.get("status"));
        this.map = (String) data.get("map");
        this.gameMode = (String) data.get("game_mode");

        this.task = null;
        this.dropletId = (int) data.get("droplet_id");
        this.snapshotId = (int) data.get("snapshot_id");
        this.ip = (String) data.get("ip");
        this.serverInfo = this.proxy.getServerInfo(this.name);
        this.droplet = Commons.getDigitalOcean().getDropletInfo(this.dropletId);
    }

    private GameServer(String id, String name, ServerSize size, ServerType type, Playlist playlist, int maxPlayers, int minParty, int maxParty, boolean ranked, boolean isPrivate, boolean beta) {
        this.plugin = Spring.getPlugin();
        this.proxy = this.plugin.getProxy();
        this.id = id;
        this.name = isPrivate ? ServerUtils.getValidName(name) : ServerUtils.getValidName(name, 1);
        this.createdAt = Timestamp.now();
        this.size = size;
        this.region = ServerRegion.NYC;
        this.type = type;
        this.snapshotId = beta ? type.getBetaSnapshotId() : type.getSnapshotId();
        this.isPrivate = isPrivate;
        this.beta = beta;
        this.playlist = type == ServerType.GAME_DEVELOPMENT ? null : playlist;
        this.maxPlayers = maxPlayers;
        this.minParty = minParty;
        this.maxParty = maxParty;
        this.ranked = ranked;
        this.rankedPlayers = new ArrayList<>();

        this.created = false;
        this.online = false;
        this.locked = isPrivate;
        this.whitelisted = isPrivate;

        this.status = ServerStatus.RESTARTING;
        this.map = null;
        this.gameMode = null;

        this.setDroplet();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public ServerSize getSize() {
        return this.size;
    }

    public ServerRegion getRegion() {
        return this.region;
    }

    public ServerType getType() {
        return this.type;
    }

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public boolean isBeta() {
        return this.beta;
    }

    public Playlist getPlaylist() {
        return this.playlist;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
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

    @SuppressWarnings("unchecked")
    public void setRankedPlayers(LeagueMatch match) {
        List<Map<String, Object>> data = match.toData();
        DocumentManager.getServerDoc(this).update("ranked_players", data);
        data.forEach(team -> this.rankedPlayers.addAll((List<String>) team.getOrDefault("players", new ArrayList<>())));
    }

    private void clearRankedPlayers() {
        this.rankedPlayers.clear();
    }

    public Set<ProxiedPlayer> getRankedPlayers() {
        Set<ProxiedPlayer> players = new HashSet<>();
        this.rankedPlayers.forEach(id -> players.add(Spring.getPlayer(UUID.fromString(id))));
        return players.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public boolean isRankedPlayer(ProxiedPlayer player) {
        return this.rankedPlayers.contains(player.getUniqueId().toString());
    }

    public Collection<ProxiedPlayer> getPlayers() {
        return this.serverInfo.getPlayers();
    }

    public int getPlayersOnline() {
        if (this.serverInfo == null) return 0;
        return this.getPlayers().size();
    }

    public boolean isFull() {
        return this.getPlayersOnline() >= this.maxPlayers;
    }

    public boolean isCreated() {
        return this.created;
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        if (this.online) return;

        this.online = online;

        if (online) {
            new ServerStartedEvent(this).track();
        }

        if (this.isPrivate && online) {
            ProxiedPlayer player = this.proxy.getPlayer(UUID.fromString(this.id));
            if (player != null) CommandUtils.sendSuccessMessage(player, "Your private server is now online!");
        } else if (!this.isPrivate
                && online
                && ServerManager.getPublicServers(this.playlist).size() == 1
                && !this.ranked) {
            ChatUtils.broadcastMessage("A " + this.playlist.getName() + " server is now open! Join now with" + ChatColor.AQUA + " /server " + this.name);
            ServerManager.getPublicServer(this.playlist).getQueued().stream().filter(player -> player.getServer().getInfo().getName().equals("Hub")).forEach(this::connect);
            ServerManager.clearQueue(this.playlist);
            DiscordManager.sendServerNotification(this);
        }
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isWhitelisted() {
        return this.whitelisted;
    }

    public void setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
    }

    public void setStatus(ServerStatus status, String map, String gameMode) {
        ServerStatus current = this.status;

        this.status = status;
        this.map = map;
        this.gameMode = gameMode;

        if (current != ServerStatus.STARTED && status == ServerStatus.STARTED && this.isRanked()) {
            this.getRankedPlayers().forEach(this::connect);
        }

        if (current == ServerStatus.STARTED && status != ServerStatus.STARTED && this.isRanked()) {
            this.getPlayers().forEach(ServerUtils::sendToHub);
            this.clearRankedPlayers();
        }
    }

    public ServerStatus getStatus() {
        return this.status;
    }

    public boolean isVisible() {
        return this.created && this.online && !this.locked && !this.whitelisted;
    }

    public String getIp() {
        return this.ip;
    }

    public int getDropletId() {
        return this.dropletId;
    }

    public int getSnapshotId() {
        return this.snapshotId;
    }

    public ServerInfo getServerInfo() {
        return this.serverInfo;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(this.isPrivate ? ItemType.PLAYER_HEAD : this.status.getItemType());

        if (this.isPrivate) {
            item.setSkullOwner(this.name);
        }

        item.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + this.name + (this.isVisible() ? "" : "" + ChatColor.RESET + ChatColor.GRAY + " (Hidden)"));
        item.setLore(Arrays.asList(
                ChatColor.WHITE + "" + ChatColor.ITALIC + this.type.getName() + (this.beta ? ChatColor.GRAY + " (Beta)" : ""),
                ChatColor.AQUA + "" + this.getPlayersOnline() + ChatColor.DARK_GRAY + "/" + ChatColor.DARK_AQUA + this.maxPlayers + ChatColor.GRAY + " Players",
                "",
                ChatColor.GRAY + "Playlist: " + (this.playlist == null ? "N/A" : ChatColor.AQUA + this.playlist.getName()),
                ChatColor.GRAY + "Status: " + (this.playlist == null ? "N/A" : this.status.toString()),
                ChatColor.GRAY + "Map: " + (this.map == null ? "N/A" : ChatColor.YELLOW + this.map),
                ChatColor.GRAY + "Game Mode: " + (this.gameMode == null ? "N/A" : ChatColor.GOLD + this.gameMode),
                "",
                ChatColor.ITALIC + "" + ChatColor.GRAY + "Server Info",
                ChatColor.GRAY + "Region: " + ChatColor.DARK_AQUA + this.region.getName(),
                ChatColor.GRAY + "Hardware: " + ChatColor.DARK_AQUA + this.size.getName()
        ));

        item.setFlag(ItemFlag.HIDE_ATTRIBUTES, true);
        ItemAction.JOIN.apply(item, this.name);

        return item;
    }

    @SuppressWarnings("deprecation")
    public TextComponent getTextComponent() {
        TextComponent component = new TextComponent(
                ChatColor.GRAY + " • " + ChatColor.AQUA + this.name + ChatColor.GRAY + " » " +
                ChatColor.AQUA + this.getPlayersOnline() + ChatColor.DARK_GRAY + "/" + ChatColor.DARK_AQUA + this.maxPlayers + ChatColor.GRAY + " Players"
        );

        ComponentBuilder hover = new ComponentBuilder(ChatColor.GRAY + "Connect to " + ChatColor.AQUA + this.name);

        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + this.name));

        return component;
    }

    private void setDroplet() {
        Droplet droplet = new Droplet();
        droplet.setName(this.name.replaceAll("_", "-"));
        droplet.setSize(new Size(this.size.getSlug()).getSlug());
        droplet.setRegion(new Region(this.region.getSlug()));
        droplet.setImage(new Image(this.snapshotId));
        droplet.setKeys(Collections.singletonList(new Key(28618976)));
        droplet.setTags(Collections.singletonList(this.id));
        droplet.setEnableBackup(false);
        droplet.setEnableIpv6(false);
        droplet.setEnablePrivateNetworking(false);
        this.droplet = droplet;
    }

    public void create() {
        try {
            Commons.getDigitalOcean().createDroplet(this.droplet);
            AtomicInteger attempts = new AtomicInteger();

            this.task = this.proxy.getScheduler().schedule(this.plugin, () -> {
                attempts.getAndIncrement();

                if (attempts.get() >= 60) {
                    System.out.println("Attempt to start server has failed! (" + this.name + ")");
                    ServerManager.removeServer(this);
                    this.cancel();
                } else {
                    try {
                        List<Droplet> droplets = Commons.getDigitalOcean().getAvailableDropletsByTagName(this.id, 1, 1).getDroplets();

                        if (droplets.size() > 0) {
                            Droplet droplet = droplets.get(0);
                            String hostname = "";

                            for (Network network : droplet.getNetworks().getVersion4Networks()) {
                                if (network.getType().equals("public")) {
                                    hostname = network.getIpAddress();
                                    break;
                                }
                            }

                            if (hostname.isEmpty()) return;

                            InetSocketAddress address;

                            try {
                                address = new InetSocketAddress(hostname, 25565);
                            } catch (IllegalArgumentException e) {
                                return;
                            }

                            this.ip = hostname;
                            this.dropletId = droplet.getId();

                            this.addServer(address);

                            System.out.println("Server successfully created after " + attempts.get() + " attempt(s)! (" + this.name + ")");
                            new ServerCreatedEvent(this, attempts.get()).track();
                            this.cancel();
                        }
                    } catch (DigitalOceanException | RequestUnsuccessfulException e) {
                        System.out.println("There was an error setting up the droplet! (" + this.name + ")");
                        e.printStackTrace();
                        ServerManager.removeServer(this);
                        this.cancel();
                    }
                }
            }, 1, 1, TimeUnit.SECONDS); // wait 1 second and retry every 1 second up to 30 times
        } catch (Exception e) {
            System.out.println("There was an error setting up the server! (" + this.name + ")");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void addServer(InetSocketAddress address) {
        ServerInfo serverInfo = this.proxy.constructServerInfo(
                this.name,
                address,
                "", // motd
                false);

        this.plugin.getProxy().getServers().put(this.name, serverInfo);
        this.serverInfo = serverInfo;
        this.created = true;
        DatabaseUtils.setServerIp(this);
    }

    public void destroy() {
        if (this.created) {
            try {
                DatabaseUtils.removeServerIp(this);
                Commons.getDigitalOcean().deleteDroplet(this.dropletId);
                ProxiedPlayer player = Spring.getPlugin().getProxy().getPlayer(this.name);
                if (player != null) CommandUtils.sendAlertMessage(player, "Your private server has shutdown");
                new ServerDestroyedEvent(this).track();
            } catch (DigitalOceanException | RequestUnsuccessfulException e) {
                System.out.println("There was an error destroying server! (" + this.name + " - " + this.dropletId + ")");
                e.printStackTrace();
            }
        }
    }

    private void cancel() {
        this.task.cancel();
    }

    public void connect(ProxiedPlayer player) {
        if (this.serverInfo == player.getServer().getInfo()) return;

        if (this.status == ServerStatus.RESTARTING) {
            CommandUtils.sendErrorMessage(player, "This server is currently restarting!");
            return;
        }

        if (this.status != ServerStatus.STARTED && this.ranked) {
            CommandUtils.sendErrorMessage(player, "You can only join ranked servers that have an active match!");
            return;
        }

        if (ServerUtils.isServerFull(this, player)) {
            CommandUtils.sendErrorMessage(player, "This server is full!");
            return;
        }

        // if (!ServerUtils.meetsPartyRequirements(player, this)) return;

        // get party info
        Party party = PartyManager.getParty(player);
        boolean fromHub = player.getServer().getInfo().getName().equals("Hub");

        // connect player
        player.connect(this.serverInfo);

        // scale server
        if (!this.isPrivate
                && ServerUtils.allServersFull(this.playlist)
                && !ServerUtils.isMaxServers(this.playlist)) {
            ServerManager.createPublicServer(this);
        }

        if (party != null
                && party.isLeader(player)
                && fromHub) {
            TaskUtils.scheduleTask(() -> party.warp(this), 1L);
        }

    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);
        data.put("name", this.name);
        data.put("created_at", this.createdAt);
        data.put("size", this.size.name());
        data.put("region", this.region.name());
        data.put("type", this.type.name());
        data.put("max_players", this.maxPlayers);
        data.put("private", this.isPrivate);
        data.put("beta", this.beta);
        data.put("droplet_id", this.dropletId);
        data.put("snapshot_id", this.snapshotId);
        data.put("ip", this.ip);
        data.put("created", this.created);
        data.put("online", this.online);
        data.put("locked", this.locked);
        data.put("whitelisted", this.whitelisted);
        data.put("status", this.status.name());
        data.put("map", this.map);
        data.put("game_mode", this.gameMode);
        data.put("min_party", this.minParty);
        data.put("max_party", this.maxParty);
        data.put("ranked", this.ranked);
        data.put("ranked_players", this.rankedPlayers);
        if (this.playlist != null) data.put("playlist", this.playlist.getName());
        return data;
    }

}
