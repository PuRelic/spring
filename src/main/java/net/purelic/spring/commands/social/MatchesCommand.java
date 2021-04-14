package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.bungee.arguments.PlayerArgument;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.managers.InventoryManager;

import java.util.Optional;

public class MatchesCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("matches")
            .senderType(ProxiedPlayer.class)
            .argument(PlayerArgument.optional("player"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                Optional<ProxiedPlayer> targetArg = c.getOptional("player");

                if (targetArg.isPresent()) {
                    ProxiedPlayer target = targetArg.get();
                    InventoryManager.openMatchesMenu(player, target);
                } else {
                    InventoryManager.openMatchesMenu(player);
                }
            });
    }

}
