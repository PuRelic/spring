package net.purelic.spring.commands;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.InventoryManager;

public class LeagueCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("league")
                .senderType(ProxiedPlayer.class)
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    InventoryManager.openLeagueSelector(player);
                });
    }

}
