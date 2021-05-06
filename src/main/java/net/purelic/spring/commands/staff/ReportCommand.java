package net.purelic.spring.commands.staff;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.analytics.events.PlayerReportedEvent;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.PlayerArgument;
import net.purelic.spring.managers.DiscordManager;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.PermissionUtils;
import net.purelic.spring.utils.ServerUtils;

public class ReportCommand implements ProxyCommand {

    @Override
    @SuppressWarnings("deprecation")
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("report")
            .senderType(ProxiedPlayer.class)
            .argument(PlayerArgument.of("player"))
            .argument(StringArgument.greedy("reason"))
            .handler(c -> {
                ProxiedPlayer sender = (ProxiedPlayer) c.getSender();
                ProxiedPlayer reported = c.get("player");
                String reason = c.get("reason");

                if (reported == sender) {
                    CommandUtils.sendErrorMessage(sender, "You can't report yourself! If you need assistance please use /support");
                    return;
                }

                CommandUtils.sendSuccessMessage(sender, "Online and offline staff have received your report!");

                Spring.sendPluginMessage(
                    reported,
                    "ReportMessage",
                    sender.getUniqueId().toString(),
                    reported.getUniqueId().toString(),
                    reason);

                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (!PermissionUtils.isStaff(player) || ServerUtils.sameServer(reported, player)) continue;

                    String serverName = ServerUtils.getServerName(reported);
                    boolean inHub = ServerUtils.inHub(reported);

                    ChatUtils.sendMessage(player,
                        new ComponentBuilder(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + serverName + ChatColor.DARK_AQUA + "]")
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Connect").create()))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, inHub ? "/hub" : "/server " + serverName)).create()[0],
                        new ComponentBuilder(" GUARDIAN  ").color(ChatColor.RED).bold(true).create()[0],
                        new TextComponent(ChatColor.DARK_AQUA + sender.getName()),
                        new ComponentBuilder(" " + ChatUtils.ARROW + " ").color(ChatColor.GRAY).create()[0],
                        new TextComponent(ChatColor.DARK_AQUA + reported.getName()),
                        new ComponentBuilder(" " + ChatUtils.ARROW + " ").color(ChatColor.GRAY).create()[0],
                        new ComponentBuilder(reason).create()[0]);
                }

                DiscordManager.sendReport(sender, reported, reason);
                new PlayerReportedEvent(reported, sender, reason).track();
            });
    }

}
