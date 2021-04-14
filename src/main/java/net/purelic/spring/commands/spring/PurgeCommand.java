package net.purelic.spring.commands.spring;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.PermissionUtils;

import java.util.ArrayList;

public class PurgeCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("spring")
            .literal("purge")
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();

                if (!PermissionUtils.isAdmin(player)) {
                    CommandUtils.sendNoPermissionMessage(player);
                    return;
                }

                int servers = ServerManager.getGameServers().size();
                new ArrayList<>(ServerManager.getGameServers().values()).forEach(ServerManager::removeServer);
                CommandUtils.sendSuccessMessage(player, "Purged " + servers + " server(s)!");
            });
    }

}
