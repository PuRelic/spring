package net.purelic.spring.commands.social.party;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.bungee.arguments.PlayerArgument;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.managers.PartyManager;
import net.purelic.spring.party.Party;
import net.purelic.spring.party.PartyInvite;
import net.purelic.spring.utils.CommandUtils;

public class PartyAcceptCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("party", "p")
                .literal("accept")
                .senderType(ProxiedPlayer.class)
                .argument(PlayerArgument.of("player"))
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    ProxiedPlayer target = c.get("player");
                    Party party = PartyManager.getParty(target);

                    if (PartyManager.hasParty(player)) {
                        CommandUtils.sendErrorMessage(player, "You're already in a party!");
                        return;
                    }

                    if (party == null) {
                        CommandUtils.sendErrorMessage(player, "This party no longer exists!");
                        return;
                    }

                    PartyInvite invite = PartyManager.getInvite(player, party);

                    if (invite == null) {
                        CommandUtils.sendErrorMessage(player, "This invite has expired or doesn't exist!");
                        return;
                    }

                    invite.accept();
                });
    }

}
