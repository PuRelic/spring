package net.purelic.spring;

import cloud.commandframework.CommandTree;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.purelic.commons.Commons;
import net.purelic.spring.commands.*;
import net.purelic.spring.commands.social.party.*;
import net.purelic.spring.commands.social.FindCommand;
import net.purelic.spring.commands.social.MessageCommand;
import net.purelic.spring.commands.social.ReplyCommand;
import net.purelic.spring.commands.spring.DestroyCommand;
import net.purelic.spring.commands.spring.PurgeCommand;
import net.purelic.spring.commands.spring.ReloadCommand;
import net.purelic.spring.listeners.*;
import net.purelic.spring.listeners.party.PartyJoin;
import net.purelic.spring.listeners.party.PartyLeave;
import net.purelic.spring.managers.*;
import net.purelic.spring.server.ServerType;
import net.purelic.spring.utils.TaskUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

public class Spring extends Plugin {

    private static Spring plugin;

    private BungeeCommandManager<CommandSender> cmdMgr;

    @Override
    public void onEnable() {
        plugin = this;

        this.getProxy().registerChannel("purelic:spring");
        this.registerListeners();
        this.registerCommands();
        this.reloadConfig();

        ProfileManager.loadProfileCache();
        ServerManager.loadServerCache();
        DocumentManager.loadDocuments();
        PartyManager.loadPartyCache();
    }

    @Override
    public void onDisable() {
        ServerManager.clearQueues();
        DocumentManager.clearDocs();
        ProfileManager.getProfiles().forEach((uuid, profile) -> Commons.getPlayerCache().put(uuid, profile.getData()));
        ServerManager.getGameServers().values().forEach(server -> Commons.getServerCache().put(server.getName(), server.getData()));
        PartyManager.cacheParties();
        LeagueManager.clearQueues();
    }

    public static Spring getPlugin() {
        return Spring.plugin;
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
        // Party
        this.registerListener(new PartyJoin());
        this.registerListener(new PartyLeave());

        this.registerListener(new InventoryClick());
        this.registerListener(new PlayerDisconnect());
        this.registerListener(new PostLogin());
        this.registerListener(new PreLogin());
        this.registerListener(new ProxyPing());
        this.registerListener(new ServerKick());
        this.registerListener(new ServerSwitch());
        this.registerListener(new SpringPluginMessage());
    }

    private void registerListener(Listener listener) {
        this.getProxy().getPluginManager().registerListener(this, listener);
    }

    private void registerCommands() {
        this.registerCommandManager();

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

        // Spring
        this.registerCommand(new DestroyCommand());
        this.registerCommand(new PurgeCommand());
        this.registerCommand(new ReloadCommand());

        this.registerCommand(new BroadcastCommand());
        this.registerCommand(new FindCommand());
        this.registerCommand(new HubCommand());
        this.registerCommand(new LeagueCommand());
        this.registerCommand(new MessageCommand());
        this.registerCommand(new PingCommand());
        this.registerCommand(new PrivateServerCommand());
        this.registerCommand(new ReplyCommand());
        this.registerCommand(new ServerCommand());
        this.registerCommand(new ServersCommand());
    }

    private void registerCommandManager() {
        try {
            final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                    AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build();
            this.cmdMgr = new BungeeCommandManager<>(
                    this,
                    executionCoordinatorFunction,
                    Function.identity(),
                    Function.identity()
            );
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void registerCommand(CustomCommand customCommand) {
        this.cmdMgr.command(customCommand.getCommandBuilder(this.cmdMgr).build());
    }

    public static void sendPluginMessage(ProxiedPlayer player, String subChannel, String... data) {
        Spring.sendPluginMessage(player.getServer().getInfo(), subChannel, data);
    }

    public static void sendPluginMessage(ServerInfo server, String subChannel, String... data) {
        Collection<ProxiedPlayer> networkPlayers = ProxyServer.getInstance().getPlayers();

        if (networkPlayers == null || networkPlayers.isEmpty()) return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        for (String s : data) out.writeUTF(s);

        server.sendData("purelic:spring", out.toByteArray());
    }

    public static void callEvent(Event event) {
        TaskUtils.runAsync(() -> Spring.getPlugin().getProxy().getPluginManager().callEvent(event));
    }

    public static ProxiedPlayer getPlayer(UUID uuid) {
        return Spring.getPlugin().getProxy().getPlayer(uuid);
    }

}
