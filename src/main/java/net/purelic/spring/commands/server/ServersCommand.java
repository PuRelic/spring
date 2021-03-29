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
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.ServerUtils;

public class ServersCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("servers", "s")
            .senderType(ProxiedPlayer.class)
            .flag(mgr.flagBuilder("list").withAliases("l"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                boolean list = c.flags().isPresent("list");

                if (list) {
                    ComponentBuilder message = ChatUtils.getHeader("Servers");

                    for (ServerInfo server : ProxyServer.getInstance().getServersCopy().values()) {
                        message.append("\n").reset().append(ServerUtils.getServerDetails(server, false));
                    }

                    ChatUtils.sendMessage(player, message);
                } else {
                    InventoryManager.openMainSelector(player);
                }
            });
    }

}
