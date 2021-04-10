package net.purelic.spring.commands.server;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.ServerUtils;

public class StaffCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("staff")
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();

                boolean staffOnline = false;
                ComponentBuilder message = ChatUtils.getHeader("Staff");

                for (ServerInfo server : ProxyServer.getInstance().getServersCopy().values()) {
                    BaseComponent[] details = ServerUtils.getServerDetails(server, true);
                    if (details.length == 0) continue;
                    message.append("\n").reset().append(details);
                    staffOnline = true;
                }

                if (!staffOnline) {
                    message.append("\n").reset().append(" " + ChatUtils.BULLET + " There is currently no staff online.").color(ChatColor.GRAY);
                }

                ChatUtils.sendMessage(player, message);
            });
    }

}
