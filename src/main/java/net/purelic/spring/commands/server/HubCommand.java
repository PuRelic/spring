package net.purelic.spring.commands.server;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.ServerUtils;

public class HubCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("hub")
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();

                if (ServerUtils.inHub(player)) {
                    CommandUtils.sendAlertMessage(
                        player,
                        new ComponentBuilder("You are already connected to the ").append("Hub").color(ChatColor.AQUA).create());
                    return;
                }

                CommandUtils.sendSuccessMessage(
                    player,
                    new ComponentBuilder("Sending you to the ").color(ChatColor.GREEN).append("Hub").color(ChatColor.AQUA).append("!").color(ChatColor.GREEN).create());

                ServerUtils.sendToHub(player);
            });
    }

}
