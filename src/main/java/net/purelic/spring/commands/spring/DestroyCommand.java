package net.purelic.spring.commands.spring;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.PermissionUtils;

public class DestroyCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("spring")
                .literal("destroy")
                .senderType(ProxiedPlayer.class)
                .argument(StringArgument.of("server"))
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    String server = c.get("server");

                    if (!PermissionUtils.isAdmin(player)) {
                        CommandUtils.sendNoPermissionMessage(player);
                        return;
                    }

                    ServerManager.removeServer(server);
                    CommandUtils.sendSuccessMessage(player, "Server \"" + server + "\" was destroyed!");
                });
    }

}
