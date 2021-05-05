package net.purelic.spring.commands.staff;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.PunishmentUtils;
import net.purelic.spring.punishment.PunishmentType;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.PermissionUtils;

public class BanCommand extends PunishmentUtils implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("pban")
            .senderType(ProxiedPlayer.class)
            .argument(StringArgument.of("player"))
            .argument(StringArgument.greedy("reason"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                String target = c.get("player");
                String reason = c.get("reason");

                if (!PermissionUtils.isStaff(player)) {
                    CommandUtils.sendNoPermissionMessage(player);
                    return;
                }

                PunishmentUtils.punishPlayer(player, target, reason, PunishmentType.PERMA_BAN);
            });
    }

}
