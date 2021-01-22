package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.bungee.arguments.PlayerArgument;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.utils.CommandUtils;

public class FindCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("find")
                .senderType(ProxiedPlayer.class)
                .argument(PlayerArgument.of("player"))
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    ProxiedPlayer target = c.get("player");
                    CommandUtils.sendAlertMessage(player,
                            target.getDisplayName() + " is currently playing on server " +
                                    ChatColor.AQUA + target.getServer().getInfo().getName());
                });
    }

}
