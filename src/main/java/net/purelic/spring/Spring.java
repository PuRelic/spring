package net.purelic.spring;

import cloud.commandframework.CommandTree;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.JDACommandSender;
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
import net.purelic.spring.commands.discord.TempMuteCommand;
import net.purelic.spring.commands.league.LeaveCommand;
import net.purelic.spring.commands.parsers.RoleArgument;
import net.purelic.spring.commands.server.*;
import net.purelic.spring.commands.social.*;
import net.purelic.spring.commands.social.party.*;
import net.purelic.spring.commands.spring.DestroyCommand;
import net.purelic.spring.commands.spring.PurgeCommand;
import net.purelic.spring.commands.spring.ReloadCommand;
import net.purelic.spring.listeners.discord.GuildMessageReceived;
import net.purelic.spring.listeners.party.PartyJoin;
import net.purelic.spring.listeners.party.PartyLeave;
import net.purelic.spring.listeners.player.Chat;
import net.purelic.spring.listeners.player.InventoryClick;
import net.purelic.spring.listeners.server.*;
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

    private BungeeCommandManager<CommandSender> bungeeCmdMgr;
    private JDA4CommandManager<JDACommandSender> jdaCmdMgr;
    private List<net.purelic.spring.commands.DiscordCommand> discordCommands;

    @Override
    public void onEnable() {
        plugin = this;

        this.getProxy().registerChannel("purelic:spring");
        this.reloadConfig();
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
        Configuration config = this.getConfig();
        Arrays.stream(ServerType.values()).forEach(type -> type.setSnapshotId(config));
        PlaylistManager.loadPlaylists(config);
        ServerManager.loadPublicServers(config);
        InventoryManager.loadServerSelector(config);
        SettingsManager.loadSettings(config);
        DiscordManager.loadDiscordWebhooks(config);
        LeagueManager.reloadSeason();
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
        this.registerListener(new GuildMessageReceived());

        // Party
        this.registerListener(new PartyJoin());
        this.registerListener(new PartyLeave());

        // Player
        this.registerListener(new Chat());
        this.registerListener(new InventoryClick());

        // Server
        this.registerListener(new PlayerDisconnect());
        this.registerListener(new PostLogin());
        this.registerListener(new PreLogin());
        this.registerListener(new ProxyPing());
        this.registerListener(new ServerKick());
        this.registerListener(new ServerConnected());
        this.registerListener(new SpringPluginMessage());
    }

    private void registerListener(ListenerAdapter listener) {
        Commons.getDiscordBot().addEventListener(listener);
    }

    private void registerListener(Listener listener) {
        this.getProxy().getPluginManager().registerListener(this, listener);
    }

    private void registerCommands() {
        this.registerCommandManagers();

        // Discord
        this.registerCommand(new TempMuteCommand());

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
        this.registerCommand(new FindCommand());
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
        this.registerCommand(new DestroyCommand());
        this.registerCommand(new PurgeCommand());
        this.registerCommand(new ReloadCommand());
    }

    private void registerCommandManagers() {
        try {
            final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build();

            this.bungeeCmdMgr = new BungeeCommandManager<>(
                this,
                executionCoordinatorFunction,
                Function.identity(),
                Function.identity()
            );

            this.jdaCmdMgr = new JDA4CommandManager<>(
                Commons.getDiscordBot(),
                message -> "!", // command prefix
                DiscordUtils::hasRole, // permission check function
                AsynchronousCommandExecutionCoordinator.simpleCoordinator(),
                sender -> {
                    MessageReceivedEvent event = sender.getEvent().orElse(null);

                    if (sender instanceof JDAPrivateSender) {
                        return new JDAPrivateSender(event, sender.getUser(), ((JDAPrivateSender) sender).getPrivateChannel());
                    }

                    if (sender instanceof JDAGuildSender) {
                        JDAGuildSender jdaGuildSender = (JDAGuildSender) sender;
                        return new JDAGuildSender(event, jdaGuildSender.getMember(), jdaGuildSender.getTextChannel());
                    }

                    throw new UnsupportedOperationException();
                },
                user -> {
                    MessageReceivedEvent event = user.getEvent().orElse(null);

                    if (user instanceof JDAPrivateSender) {
                        JDAPrivateSender privateUser = (JDAPrivateSender) user;
                        return new JDAPrivateSender(event, privateUser.getUser(), privateUser.getPrivateChannel());
                    }

                    if (user instanceof JDAGuildSender) {
                        JDAGuildSender guildUser = (JDAGuildSender) user;
                        return new JDAGuildSender(event, guildUser.getMember(), guildUser.getTextChannel());
                    }

                    throw new UnsupportedOperationException();
                }
            );

            // Register custom parsers
            this.jdaCmdMgr.getParserRegistry().registerParserSupplier(TypeToken.get(RoleArgument.class), parserParameters ->
                new RoleArgument.MessageParser<>(
                    new HashSet<>(Arrays.asList(RoleArgument.ParserMode.values()))
                ));
        } catch (final Exception e) {
            e.printStackTrace();
        }
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

}
