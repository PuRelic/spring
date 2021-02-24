package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.utils.CommandUtils;

public class PlayersCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("players", "online")
                .senderType(ProxiedPlayer.class)
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    int online = ProxyServer.getInstance().getOnlineCount();

                    if (online == 1) {
                        CommandUtils.sendAlertMessage(player, "Forever alone :(");
                    } else {
                        CommandUtils.sendAlertMessage(player, "There are currently " + ChatColor.AQUA + online + ChatColor.RESET + " players online");
                    }
                });
    }

}
