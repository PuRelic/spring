package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.bungee.arguments.PlayerArgument;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.CommandUtils;

import java.util.Optional;

public class PingCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("ping")
            .senderType(ProxiedPlayer.class)
            .argument(PlayerArgument.optional("player"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                Optional<ProxiedPlayer> targetArg = c.getOptional("player");

                if (targetArg.isPresent()) {
                    ProxiedPlayer target = targetArg.get();
                    CommandUtils.sendSuccessMessage(player, "Pong! " + ChatColor.GRAY + "(" + target.getName() + " has a ping of " + target.getPing() + "ms)");
                } else {
                    CommandUtils.sendSuccessMessage(player, "Pong! " + ChatColor.GRAY + "(You have a ping of " + player.getPing() + "ms)");
                }
            });
    }

}
