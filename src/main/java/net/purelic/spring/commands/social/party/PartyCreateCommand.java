package net.purelic.spring.commands.social.party;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.managers.PartyManager;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.PermissionUtils;

import java.util.Optional;

public class PartyCreateCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("party", "p")
                .literal("create")
                .senderType(ProxiedPlayer.class)
                .argument(StringArgument.optional("name", StringArgument.StringMode.GREEDY))
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    Optional<String> nameArg = c.getOptional("name");

                    if (PartyManager.hasParty(player)) {
                        CommandUtils.sendErrorMessage(player, "You're currently in a party!");
                        return;
                    }

                    if (nameArg.isPresent() && !PermissionUtils.isDonator(player)) {
                        CommandUtils.sendErrorMessage(player, "Only premium players can set custom party names!");
                        return;
                    }

                    String name = nameArg.orElse(player.getName());
                    PartyManager.createParty(player, name);
                });
    }

}
