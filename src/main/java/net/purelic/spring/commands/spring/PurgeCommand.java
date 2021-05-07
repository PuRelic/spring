package net.purelic.spring.commands.spring;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.Permission;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.utils.CommandUtils;

import java.util.ArrayList;

public class PurgeCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("spring")
            .literal("purge")
            // .permission(Permission.isAdmin())
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                if (!Permission.isAdmin(c)) return;

                int servers = ServerManager.getGameServers().size();
                new ArrayList<>(ServerManager.getGameServers().values()).forEach(ServerManager::removeServer);
                CommandUtils.sendSuccessMessage((ProxiedPlayer) c.getSender(), "Purged " + servers + " server(s)!");
            });
    }

}
