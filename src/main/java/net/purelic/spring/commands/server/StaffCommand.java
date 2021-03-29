package net.purelic.spring.commands.server;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.managers.InventoryManager;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.PermissionUtils;
import net.purelic.spring.utils.ServerUtils;

public class StaffCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("staff")
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();

                ComponentBuilder message = ChatUtils.getHeader("Staff");

                for (ServerInfo server : ProxyServer.getInstance().getServersCopy().values()) {
                    message.append("\n").reset().append(ServerUtils.getServerDetails(server, true));
                }

                ChatUtils.sendMessage(player, message);
            });
    }

}
