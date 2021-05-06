package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.ProfileArgument;
import net.purelic.spring.managers.InventoryManager;
import net.purelic.spring.profile.Profile;

import java.util.Optional;

public class StatsCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("stats")
            .senderType(ProxiedPlayer.class)
            .argument(ProfileArgument.optional("player"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                Optional<Profile> profileArg = c.getOptional("player");

                if (profileArg.isPresent()) {
                    Profile profile = profileArg.get();
                    if (profile.isOnline()) InventoryManager.openStatsMenu(player, profile.getPlayer());
                    else InventoryManager.openStatsMenu(player, profile);
                } else {
                    InventoryManager.openStatsMenu(player);
                }
            });
    }

}
