package net.purelic.spring;

import cloud.commandframework.CommandTree;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.JDAGuildSender;
import cloud.commandframework.jda.JDAPrivateSender;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.leangen.geantyref.TypeToken;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.purelic.commons.Commons;
import net.purelic.spring.analytics.Analytics;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.discord.*;
import net.purelic.spring.commands.league.LeaveCommand;
import net.purelic.spring.commands.parsers.*;
import net.purelic.spring.commands.server.*;
import net.purelic.spring.commands.social.*;
import net.purelic.spring.commands.social.party.*;
import net.purelic.spring.commands.spring.CreateCommand;
import net.purelic.spring.commands.spring.DestroyCommand;
import net.purelic.spring.commands.spring.PurgeCommand;
import net.purelic.spring.commands.spring.ReloadCommand;
import net.purelic.spring.commands.staff.*;
import net.purelic.spring.listeners.discord.*;
import net.purelic.spring.listeners.party.PartyJoin;
import net.purelic.spring.listeners.party.PartyLeave;
import net.purelic.spring.listeners.player.*;
import net.purelic.spring.listeners.punishment.*;
import net.purelic.spring.listeners.server.ProxyPing;
import net.purelic.spring.listeners.server.ServerConnected;
import net.purelic.spring.listeners.server.ServerKick;
import net.purelic.spring.listeners.server.SpringPluginMessage;
import net.purelic.spring.managers.*;
import net.purelic.spring.server.ServerType;
import net.purelic.spring.utils.DiscordUtils;
import net.purelic.spring.utils.TaskUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Spring extends Plugin {

    private static Spring plugin;

    private Configuration config;
    private BungeeCommandManager<CommandSender> bungeeCmdMgr;
    private JDA4CommandManager<DiscordUser> jdaCmdMgr;

    @Override
    public void onEnable() {
        plugin = this;

        this.getProxy().registerChannel("purelic:spring");
        this.reloadConfig();
        this.registerBungeeCommandManager();
        this.registerJDACommandManager();
        this.registerCommands();
        this.registerListeners();

        ProfileManager.loadProfileCache();
        ServerManager.loadServerCache();
        DocumentManager.loadDocuments();
        PartyManager.loadPartyCache();
        Analytics.loadSessionCache();
    }

    @Override
    public void onDisable() {
        ServerManager.clearQueues();
        DocumentManager.clearDocs();
        ProfileManager.getProfiles().forEach((uuid, profile) -> Commons.getPlayerCache().put(uuid, profile.getData()));
        ServerManager.getGameServers().values().forEach(server -> Commons.getServerCache().put(server.getName(), server.getData()));
        PartyManager.cacheParties();
        LeagueManager.clearQueues();
        Analytics.cacheSessions();
        unregisterDiscordListeners();
    }

    public static Spring getPlugin() {
        return plugin;
    }

    public void reloadConfig() {
        this.config = this.getConfig();
        Arrays.stream(ServerType.values()).forEach(type -> type.setSnapshotId(this.config));
        PlaylistManager.loadPlaylists(this.config);
        ServerManager.loadPublicServers(this.config);
        InventoryManager.loadServerSelector(this.config);
        SettingsManager.loadSettings(this.config);
        DiscordManager.loadDiscordWebhooks(this.config);
        LeagueManager.reloadSeason();
        CensoredWordFilter.updateFilter(this.config);
    }

    public Configuration getConfig() {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void registerListeners() {
        // Discord
        this.registerListener(new AttachmentOnlyChannels());
        this.registerListener(new CensoredWordFilter());
        this.registerListener(new DiscordTempMute());
        this.registerListener(new GuildMessageReceived());
        this.registerListener(new InviteTracker());
        this.registerListener(new LinkChannelFilter());
        this.registerListener(new ReactionRoles());

        // Party
        this.registerListener(new PartyJoin());
        this.registerListener(new PartyLeave());

        // Player
        this.registerListener(new Chat());
        this.registerListener(new InventoryClick());
        this.registerListener(new Login());
        this.registerListener(new PlayerDisconnect());
        this.registerListener(new PostLogin());
        this.registerListener(new PreLogin());

        // Server
        this.registerListener(new ProxyPing());
        this.registerListener(new ServerKick());
        this.registerListener(new ServerConnected());
        this.registerListener(new SpringPluginMessage());

        // Punishment
        this.registerListener(new PlayerBan());
        this.registerListener(new PlayerKick());
        this.registerListener(new PlayerPunish());
        this.registerListener(new PlayerWarn());
        this.registerListener(new PunishmentAppeal());
    }

    private void registerListener(ListenerAdapter listener) {
        Commons.getDiscordBot().addEventListener(listener);
    }

    private void registerListener(Listener listener) {
        this.getProxy().getPluginManager().registerListener(this, listener);
    }

    private void registerCommands() {
        // Discord
        this.registerCommand(new DiscordVerifyCommand());
        this.registerCommand(new EmbedCommand());
        this.registerCommand(new EmbedEditCommand());
        this.registerCommand(new GiveawayCommand());
        this.registerCommand(new MuteCommand());
        this.registerCommand(new NukeCommand());
        this.registerCommand(new ProxyVerifyCommand());
        this.registerCommand(new ReactCommand());
        this.registerCommand(new SpeakCommand());
        this.registerCommand(new TempMuteCommand());
        this.registerCommand(new TopReferrersCommand());
        this.registerCommand(new UnlinkCommand());
        this.registerCommand(new UnmuteCommand());
        this.registerCommand(new WhoIsCommand());

        // League
        this.registerCommand(new LeaveCommand());

        // Server
        this.registerCommand(new HubCommand());
        this.registerCommand(new LeagueCommand());
        this.registerCommand(new PrivateServerCommand());
        this.registerCommand(new RejoinCommand());
        this.registerCommand(new ServerCommand());
        this.registerCommand(new ServersCommand());
        this.registerCommand(new StaffCommand());

        // Party
        this.registerCommand(new PartyAcceptCommand());
        this.registerCommand(new PartyChatCommand());
        this.registerCommand(new PartyCreateCommand());
        this.registerCommand(new PartyDenyCommand());
        this.registerCommand(new PartyDisbandCommand());
        this.registerCommand(new PartyHelpCommand());
        this.registerCommand(new PartyInviteCommand());
        this.registerCommand(new PartyKickCommand());
        this.registerCommand(new PartyLeaveCommand());
        this.registerCommand(new PartyListCommand());
        this.registerCommand(new PartyPromoteCommand());
        this.registerCommand(new PartyRenameCommand());
        this.registerCommand(new PartyWarpCommand());

        // Social
        this.registerCommand(new BroadcastCommand());
        this.registerCommand(new DiscordInviteCommand());
        this.registerCommand(new DocsCommand());
        this.registerCommand(new HelpCommand());
        this.registerCommand(new MatchesCommand());
        this.registerCommand(new MessageCommand());
        this.registerCommand(new PingCommand());
        this.registerCommand(new PlayersCommand());
        this.registerCommand(new ReplyCommand());
        this.registerCommand(new SeenCommand());
        this.registerCommand(new StaffChatCommand());
        this.registerCommand(new StatsCommand());
        this.registerCommand(new SupportCommand());
        this.registerCommand(new WebsiteCommand());

        // Spring
        this.registerCommand(new CreateCommand());
        this.registerCommand(new DestroyCommand());
        this.registerCommand(new PurgeCommand());
        this.registerCommand(new ReloadCommand());

        // Staff
        this.registerCommand(new AltsCommand());
        this.registerCommand(new AppealCommand());
        this.registerCommand(new BanCommand());
        this.registerCommand(new KickCommand());
        this.registerCommand(new LookupCommand());
        this.registerCommand(new PunishCommand());
        this.registerCommand(new ReportCommand());
        this.registerCommand(new TempBanCommand());
        this.registerCommand(new WarnCommand());
    }

    private void registerBungeeCommandManager() {
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
            AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build();

        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();

        try {
            this.bungeeCmdMgr = new BungeeCommandManager<>(
                this,
                executionCoordinatorFunction,
                mapperFunction,
                mapperFunction
            );
        } catch (final Exception e) {
            System.out.println("Failed to register Bungee Command Manager");
            e.printStackTrace();
        }

        // Create case insensitive suggestions
        this.bungeeCmdMgr.setCommandSuggestionProcessor((context, strings) -> {
            String input;

            if (context.getInputQueue().isEmpty()) {
                input = "";
            } else {
                input = context.getInputQueue().peek();
            }

            input = input.toLowerCase();
            List<String> suggestions = new LinkedList<>();

            for (String suggestion : strings) {
                if (suggestion == null) continue;

                if (suggestion.toLowerCase().startsWith(input)) {
                    suggestions.add(suggestion);
                }
            }

            return suggestions;
        });
    }

    private void registerJDACommandManager() {
        try {
            this.jdaCmdMgr = new JDA4CommandManager<>(
                Commons.getDiscordBot(),
                message -> "!", // command prefix
                DiscordUtils::hasRole, // permission check function
                AsynchronousCommandExecutionCoordinator.simpleCoordinator(),
                sender -> {
                    MessageReceivedEvent event = sender.getEvent().orElse(null);

                    if (sender instanceof JDAPrivateSender) {
                        JDAPrivateSender jdaPrivateSender = (JDAPrivateSender) sender;
                        return new PrivateUser(event, jdaPrivateSender.getUser(), jdaPrivateSender.getPrivateChannel());
                    }

                    if (sender instanceof JDAGuildSender) {
                        JDAGuildSender jdaGuildSender = (JDAGuildSender) sender;
                        return new GuildUser(event, jdaGuildSender.getMember(), jdaGuildSender.getTextChannel());
                    }

                    return new WebhookUser(event, sender.getUser(), sender.getChannel());
                },
                user -> {
                    MessageReceivedEvent event = user.getEvent().orElse(null);

                    if (user instanceof PrivateUser) {
                        PrivateUser privateUser = (PrivateUser) user;
                        return new JDAPrivateSender(event, privateUser.getUser(), privateUser.getPrivateChannel());
                    }

                    if (user instanceof GuildUser) {
                        GuildUser guildUser = (GuildUser) user;
                        return new JDAGuildSender(event, guildUser.getMember(), guildUser.getTextChannel());
                    }

                    throw new UnsupportedOperationException();
                }
            );
        } catch (Exception e) {
            System.out.println("Failed to register JDA Command Manager");
            e.printStackTrace();
        }

        // Register custom parsers
        this.jdaCmdMgr.getParserRegistry().registerParserSupplier(TypeToken.get(RoleArgument.class), parserParameters ->
            new RoleArgument.MessageParser<>(
                new HashSet<>(Arrays.asList(RoleArgument.ParserMode.values()))
            ));

        this.jdaCmdMgr.getParserRegistry().registerParserSupplier(TypeToken.get(ChannelArgument.class), parserParameters ->
            new ChannelArgument.MessageParser<>(
                new HashSet<>(Arrays.asList(ChannelArgument.ParserMode.values()))
            ));
    }

    private void registerCommand(ProxyCommand command) {
        this.bungeeCmdMgr.command(command.getCommandBuilder(this.bungeeCmdMgr).build());
    }

    private void registerCommand(DiscordCommand command) {
        this.jdaCmdMgr.command(command.getCommandBuilder(this.jdaCmdMgr).build());
    }

    private void unregisterDiscordListeners() {
        Commons.getDiscordBot().getRegisteredListeners().forEach(Spring::unregisterDiscordListener);
    }

    private static void unregisterDiscordListener(Object listener) {
        Commons.getDiscordBot().removeEventListener(listener);
    }

    public static void sendPluginMessage(ProxiedPlayer player, String subChannel, String... data) {
        Server server = player.getServer();
        if (server == null) return;
        sendPluginMessage(player.getServer().getInfo(), subChannel, data);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void sendPluginMessage(ServerInfo server, String subChannel, String... data) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();

        if (networkPlayers == null || networkPlayers.isEmpty()) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        for (String s : data) out.writeUTF(s);

        server.sendData("purelic:spring", out.toByteArray());
    }

    public static void callEvent(Event event) {
        TaskUtils.runAsync(() -> getPlugin().getProxy().getPluginManager().callEvent(event));
    }

    public static ProxiedPlayer getPlayer(UUID uuid) {
        return getPlugin().getProxy().getPlayer(uuid);
    }

    public static ProxiedPlayer getPlayer(String name) {
        return getPlugin().getProxy().getPlayer(name);
    }

    public static boolean isOnline(UUID uuid) {
        return getPlayer(uuid) != null;
    }

    public static boolean isOnline(String name) {
        return getPlayer(name) != null;
    }

}
