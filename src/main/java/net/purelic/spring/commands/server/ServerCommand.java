package net.purelic.spring.commands.server;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.server.ServerStatus;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.ServerUtils;

import java.util.Optional;

public class ServerCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("server")
                .senderType(ProxiedPlayer.class)
                .argument(StringArgument.optional("server", StringArgument.StringMode.GREEDY))
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    Optional<String> serverArg = c.getOptional("server");

                    if (!serverArg.isPresent()) {
                        ServerInfo server = player.getServer().getInfo();
                        String name = server.getName();

                        CommandUtils.sendAlertMessage(
                                player,
                                new ComponentBuilder("You are currently connected to ")
                                    .append(name).color(ChatColor.AQUA)
                                    .append(" (" + server.getPlayers().size() + " Online)").color(ChatColor.GRAY)
                                    .create());
                        return;
                    }

                    String name = serverArg.get();
                    GameServer server = ServerUtils.getGameServerByName(name, false);

                    if (server == null) {
                        CommandUtils.sendNoServerMessage(player, name);
                        return;
                    }

                    name = server.getName();

                    if (!server.isOnline()) {
                        CommandUtils.sendAlertMessage(
                                player,
                                new ComponentBuilder("Server ").append(name).color(ChatColor.AQUA).append(" is still starting up").reset().create());
                        return;
                    }

                    if (player.getServer().getInfo().getName().equals(name)) {
                        CommandUtils.sendErrorMessage(player, "You are already connected to " + name + "!");
                        return;
                    }

                    if (server.isPrivate() && server.isLocked() && !server.getId().equals(player.getUniqueId().toString())) {
                        CommandUtils.sendErrorMessage(player, "This server has not been opened yet!");
                        return;
                    }

                    if (server.getStatus() == ServerStatus.RESTARTING) {
                        CommandUtils.sendErrorMessage(player, "This server is currently restarting!");
                        return;
                    }

                    CommandUtils.sendAlertMessage(
                            player,
                            new ComponentBuilder("Sending you to ").append(name).color(ChatColor.AQUA).append("...").color(ChatColor.WHITE).create());
                    server.connect(player);
                });
    }

}
