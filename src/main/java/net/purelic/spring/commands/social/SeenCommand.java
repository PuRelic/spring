package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.ProfileArgument;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.NickUtils;
import net.purelic.spring.utils.ServerUtils;

public class SeenCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("seen", "find")
            .senderType(ProxiedPlayer.class)
            .argument(ProfileArgument.of("player"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                Profile profile = c.get("player");

                if (profile.isOnline()) {
                    this.sendFoundMessage(player, profile.getPlayer());
                } else {
                    CommandUtils.sendAlertMessage(player,
                        ChatColor.DARK_AQUA + profile.getName() + ChatColor.RESET + " was last seen " + ChatUtils.format(profile.getLastSeen()));
                }
            });
    }

    private void sendFoundMessage(ProxiedPlayer player, ProxiedPlayer target) {
        CommandUtils.sendAlertMessage(player,
            ChatColor.AQUA + NickUtils.getDisplayName(target, player) + ChatColor.RESET + " is currently playing on server " +
                ChatColor.AQUA + ServerUtils.getServerName(target));
    }

}
