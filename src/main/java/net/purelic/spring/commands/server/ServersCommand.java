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

public class ServersCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("servers")
            .senderType(ProxiedPlayer.class)
            .flag(mgr.flagBuilder("list").withAliases("l"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                boolean list = c.flags().isPresent("list");

                if (list) {
                    ComponentBuilder message = ChatUtils.getHeader("Servers");

                    for (ServerInfo server : ProxyServer.getInstance().getServersCopy().values()) {
                        message.append("\n").reset().append(this.getServerDetails(server));
                    }

                    ChatUtils.sendMessage(player, message);
                } else {
                    InventoryManager.openMainSelector(player);
                }
            });
    }

    @SuppressWarnings("deprecation")
    private BaseComponent[] getServerDetails(ServerInfo server) {
        String name = server.getName();
        boolean hub = name.equals("Hub");
        int online = server.getPlayers().size();

        return new ComponentBuilder(ChatUtils.BULLET).color(ChatColor.GRAY)
            .append(name).color(ChatColor.AQUA)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("Connect to ").color(ChatColor.GRAY)
                        .append(name).color(ChatColor.AQUA)
                        .create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, hub ? "/hub" : "/server " + name))
            .append(" " + ChatUtils.ARROW + " ").color(ChatColor.GRAY)
            .append(online + " Online").color(ChatColor.WHITE)
            .create();
    }

}
