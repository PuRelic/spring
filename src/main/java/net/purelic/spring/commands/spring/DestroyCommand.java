package net.purelic.spring.commands.spring;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.bungee.arguments.ServerArgument;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.Permission;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.utils.CommandUtils;

public class DestroyCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("spring")
            .literal("destroy")
            .senderType(ProxiedPlayer.class)
            .permission(Permission.isAdmin())
            .argument(ServerArgument.of("server"))
            .handler(c -> {
                String server = ((ServerInfo) c.get("server")).getName();
                ServerManager.removeServer(server);
                CommandUtils.sendSuccessMessage((ProxiedPlayer) c.getSender(), "Server \"" + server + "\" was destroyed!");
            });
    }

}
