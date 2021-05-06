package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.PlayerArgument;
import net.purelic.spring.utils.CommandUtils;

public class FindCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("find")
            .senderType(ProxiedPlayer.class)
            .argument(PlayerArgument.of("player"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                ProxiedPlayer target = c.get("player");
                sendFoundMessage(player, target);
            });
    }

    public static void sendFoundMessage(ProxiedPlayer player, ProxiedPlayer target) {
        CommandUtils.sendAlertMessage(player,
            ChatColor.AQUA + target.getName() + ChatColor.RESET + " is currently playing on server " +
                ChatColor.AQUA + target.getServer().getInfo().getName());
    }

}
