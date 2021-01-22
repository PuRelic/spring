package net.purelic.spring.commands.spring;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.PermissionUtils;

public class ReloadCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("spring")
                .senderType(ProxiedPlayer.class)
                .literal("reload")
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();

                    if (!PermissionUtils.isAdmin(player)) {
                        CommandUtils.sendNoPermissionMessage(player);
                        return;
                    }

                    Spring.getPlugin().reloadConfig();
                    CommandUtils.sendSuccessMessage(player, "Spring config reloaded!");
                });
    }

}
