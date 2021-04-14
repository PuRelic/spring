package net.purelic.spring.commands.social.party;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.managers.PartyManager;
import net.purelic.spring.party.Party;
import net.purelic.spring.utils.CommandUtils;

public class PartyDisbandCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("party", "p")
            .literal("disband")
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                Party party = PartyManager.getParty(player);

                if (party == null) {
                    CommandUtils.sendErrorMessage(player, "You aren't currently in a party!");
                    return;
                }

                if (player != party.getLeader()) {
                    CommandUtils.sendErrorMessage(player, "Only party leaders can disband the party!");
                    return;
                }

                party.disband();
            });
    }

}
