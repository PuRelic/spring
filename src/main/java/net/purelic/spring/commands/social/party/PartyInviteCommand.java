package net.purelic.spring.commands.social.party;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.bungee.arguments.PlayerArgument;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;
import net.purelic.spring.managers.PartyManager;
import net.purelic.spring.party.Party;
import net.purelic.spring.utils.CommandUtils;

public class PartyInviteCommand implements CustomCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("party", "p")
                .literal("invite")
                .senderType(ProxiedPlayer.class)
                .argument(PlayerArgument.of("player"))
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    ProxiedPlayer target = c.get("player");
                    Party party = PartyManager.getParty(player);

                    if (target == player) {
                        CommandUtils.sendErrorMessage(player, "You can't invite yourself to a party!");
                        return;
                    }

                    // Create a new party if they aren't currently in one
                    if (party == null) {
                        party = PartyManager.createParty(player);
                    }

                    // Check if they're the party leader
                    if (player != party.getLeader()) {
                        CommandUtils.sendErrorMessage(player, "Only party leaders can invite players!");
                        return;
                    }

                    // Check if target is already in a party
                    if (PartyManager.hasParty(target)) {
                        CommandUtils.sendErrorMessage(player, "That player is already in a party!");
                        return;
                    }

                    // Check if they've already been invited to this party
                    if (PartyManager.hasInvite(target, party)) {
                        CommandUtils.sendErrorMessage(player, "You've already sent a party invite to this player!");
                        return;
                    }

                    // Create and send the party invite
                    PartyManager.createInvite(party, player, target).send();
                });
    }

}
