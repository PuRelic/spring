package net.purelic.spring.commands;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.PermissionUtils;

public class BroadcastCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("broadcast", "bc")
                .senderType(ProxiedPlayer.class)
                .argument(StringArgument.greedy("message"))
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    String message = c.get("message");

                    if (!PermissionUtils.isAdmin(player)) {
                        CommandUtils.sendNoPermissionMessage(player);
                        return;
                    }

                    ChatUtils.broadcastMessage(message);
                });
    }

}
