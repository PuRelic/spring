package net.purelic.spring.commands;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.utils.CommandUtils;

public class HubCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("hub")
                .senderType(ProxiedPlayer.class)
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();

                    if (player.getServer().getInfo().getName().equalsIgnoreCase("Hub")) {
                        CommandUtils.sendAlertMessage(
                                player,
                                new ComponentBuilder("You are already connected to the ").append("Hub").color(ChatColor.AQUA).create());
                        return;
                    }

                    CommandUtils.sendSuccessMessage(
                            player,
                            new ComponentBuilder("Sending you to the ").color(ChatColor.GREEN).append("Hub").color(ChatColor.AQUA).append("!").color(ChatColor.GREEN).create());

                    ServerInfo target = ProxyServer.getInstance().getServerInfo("Hub");
                    player.connect(target);
                });
    }

}
