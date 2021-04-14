package net.purelic.spring.commands.league;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.league.LeagueTeam;
import net.purelic.spring.managers.LeagueManager;
import net.purelic.spring.utils.CommandUtils;

public class LeaveCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("leave")
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                LeagueTeam team = LeagueManager.getTeam(player);

                if (team == null) {
                    CommandUtils.sendErrorMessage(player, "You aren't currently in a league queue!");
                    return;
                }

                String message = player.getName() + " has removed your team from the queue " + ChatColor.GRAY + " (/leave)";

                team.getPlayers().forEach(pl -> CommandUtils.sendAlertMessage(pl, message));
                LeagueManager.removeFromQueue(team);
            });
    }

}
