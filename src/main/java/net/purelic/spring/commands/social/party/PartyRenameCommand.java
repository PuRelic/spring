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
import net.purelic.spring.utils.PermissionUtils;

public class PartyRenameCommand implements CustomCommand {

    private final String regex = "[a-zA-Z ]*";

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("party", "p")
                .literal("rename")
                .senderType(ProxiedPlayer.class)
                .argument(StringArgument.greedy("name"))
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    String name = c.get("name");
                    Party party = PartyManager.getParty(player);

                    if (party == null) {
                        CommandUtils.sendErrorMessage(player, "You aren't currently in a party!");
                        return;
                    }

                    if (player != party.getLeader()) {
                        CommandUtils.sendErrorMessage(player, "Only party leaders can rename the party!");
                        return;
                    }

                    if (!PermissionUtils.isDonator(player)) {
                        CommandUtils.sendErrorMessage(player, "Only premium players can set custom party names!");
                        return;
                    }

                    if (name.length() < 3 || name.length() > 16) {
                        CommandUtils.sendErrorMessage(player, "Party names must be between 3 and 16 characters longer!");
                        return;
                    }

                    if (!name.matches(this.regex)) {
                        CommandUtils.sendErrorMessage(player, "Party names can only contain alphanumeric characters and spaces!");
                        return;
                    }

                    party.setName(name);
                    party.sendMessage("The party has been renamed to \"" + name + "\"!");
                });
    }

}
