package net.purelic.spring.commands.staff;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.Permission;
import net.purelic.spring.commands.parsers.ProfileArgument;
import net.purelic.spring.punishment.PunishmentType;
import net.purelic.spring.utils.PunishmentUtils;

public class BanCommand extends PunishmentUtils implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("pban")
            .senderType(ProxiedPlayer.class)
            // .permission(Permission.isStaff())
            .argument(ProfileArgument.of("player"))
            .argument(StringArgument.greedy("reason"))
            .handler(c -> {
                if (!Permission.isStaff(c)) return;
                
                PunishmentUtils.punishPlayer(
                (ProxiedPlayer) c.getSender(),
                c.get("player"),
                c.get("reason"),
                PunishmentType.PERMA_BAN
            );});
    }

}
