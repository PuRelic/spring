package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.managers.DiscordManager;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.CommandUtils;

public class SupportCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("support", "assist", "helpop")
            .senderType(ProxiedPlayer.class)
            .argument(StringArgument.greedy("request"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                String request = c.get("request");
                ChatUtils.broadcastRequest(player, request);
                DiscordManager.sendSupportNotification(player, request);
                CommandUtils.sendSuccessMessage(player, "Your support request has been sent to the staff team!");
            });
    }

}
