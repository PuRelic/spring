package net.purelic.spring.commands.social.party;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.managers.PartyManager;
import net.purelic.spring.party.Party;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.NickUtils;
import net.purelic.spring.utils.PartyUtils;

public class PartyListCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("party", "p")
            .literal("list")
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                Party party = PartyManager.getParty(player);

                if (party == null) {
                    CommandUtils.sendErrorMessage(player, "You aren't currently in a party!");
                    return;
                }

                String message = "Party Members (" + ChatColor.AQUA + party.getMembers().size() + ChatColor.RESET + "): ";
                String separator = ChatColor.GRAY + ", " + ChatColor.RESET;
                boolean first = true;

                for (ProxiedPlayer member : party.getMembers()) {
                    message += (first ? "" : separator) + NickUtils.getDisplayName(member, player);
                    if (first) first = false;
                }

                PartyUtils.sendPartyMessage(player, message);
            });
    }

}
