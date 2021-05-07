package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.PlayerArgument;
import net.purelic.spring.managers.InventoryManager;

import java.util.Optional;

public class StatsCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("stats")
            .senderType(ProxiedPlayer.class)
            .argument(PlayerArgument.optional("player"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                Optional<ProxiedPlayer> playerArg = c.getOptional("player");

                if (playerArg.isPresent()) {
                    InventoryManager.openStatsMenu(player, playerArg.get());
                } else {
                    InventoryManager.openStatsMenu(player);
                }
            });
    }

}
