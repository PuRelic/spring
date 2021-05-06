package net.purelic.spring.commands.spring;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.commons.Commons;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.Permission;
import net.purelic.spring.utils.CommandUtils;

public class RebootCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("spring")
            .senderType(ProxiedPlayer.class)
            .permission(Permission.isAdmin())
            .literal("reboot")
            .handler(c -> {
                Commons.rebootDiscordBot();
                CommandUtils.sendSuccessMessage((ProxiedPlayer) c.getSender(), "Reggie Bot is now rebooting!");
            });
    }

}
