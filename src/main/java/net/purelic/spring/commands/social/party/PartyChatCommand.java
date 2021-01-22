package net.purelic.spring.commands.social.party;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.managers.PartyManager;
import net.purelic.spring.party.Party;
import net.purelic.spring.utils.CommandUtils;

public class PartyChatCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("pchat", "pc")
                .senderType(ProxiedPlayer.class)
                .argument(StringArgument.greedy("message"))
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    String message = c.get("message");
                    Party party = PartyManager.getParty(player);

                    if (party == null) {
                        CommandUtils.sendErrorMessage(player, "You aren't currently in a party!");
                        return;
                    }

                    party.sendMessage(player, message);
                });
    }

}
