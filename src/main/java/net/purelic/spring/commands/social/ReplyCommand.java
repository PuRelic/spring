package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.CommandUtils;

public class ReplyCommand extends MessageCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("r")
            .senderType(ProxiedPlayer.class)
            .argument(StringArgument.greedy("message"))
            .handler(c -> {
                ProxiedPlayer sender = (ProxiedPlayer) c.getSender();
                ProxiedPlayer recipient = messages.get(sender);
                String message = c.get("message");

                if (recipient == null || !recipient.isConnected()) {
                    CommandUtils.sendErrorMessage(sender, "You have no one to reply to!");
                    return;
                }

                sendPM(sender, recipient, message);
            });
    }

}
