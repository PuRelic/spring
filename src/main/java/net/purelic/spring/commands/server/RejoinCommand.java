package net.purelic.spring.commands.server;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.utils.CommandUtils;

public class RejoinCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("rejoin")
                .senderType(ProxiedPlayer.class)
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    GameServer lastServer = ServerManager.getLastServer(player);

                    if (lastServer == null) {
                        CommandUtils.sendErrorMessage(player, "Could not find the last server you were on!");
                        return;
                    }

                    ComponentBuilder message = new ComponentBuilder("Sending you to ").color(ChatColor.GREEN)
                        .append(lastServer.getName()).color(ChatColor.AQUA)
                        .append("...").color(ChatColor.GREEN);

                    CommandUtils.sendSuccessMessage(player, message.create());
                    lastServer.connect(player);
                });
    }

}
