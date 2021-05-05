package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.Permission;
import net.purelic.spring.utils.ChatUtils;

public class BroadcastCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("broadcast", "bc")
            .senderType(ProxiedPlayer.class)
            .permission(Permission.isStaff())
            .argument(StringArgument.greedy("message"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                String message = c.get("message");

                ChatUtils.broadcastMessage(message);
            });
    }

}
