package net.purelic.spring.commands.staff;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.PunishmentUtils;
import net.purelic.spring.punishment.BanUnit;
import net.purelic.spring.punishment.PunishmentType;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.PermissionUtils;

public class TempBanCommand extends PunishmentUtils implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("tempban")
            .senderType(ProxiedPlayer.class)
            .argument(StringArgument.of("player"))
            .argument(IntegerArgument.of("duration"))
            .argument(EnumArgument.of(BanUnit.class, "unit"))
            .argument(StringArgument.greedy("reason"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                String target = c.get("player");
                int duration = c.get("duration");
                BanUnit unit = c.get("unit");
                String reason = c.get("reason");

                if (!PermissionUtils.isStaff(player)) {
                    CommandUtils.sendNoPermissionMessage(player);
                    return;
                }

                PunishmentUtils.punishPlayer(player, target, reason, PunishmentType.TEMP_BAN, duration, unit);
            });
    }

}
