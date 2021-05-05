package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.Permission;
import net.purelic.spring.utils.PermissionUtils;
import org.apache.commons.lang3.ArrayUtils;

public class StaffChatCommand implements ProxyCommand {

    private static final ChatColor PRIMARY_COLOR = ChatColor.DARK_RED;
    private static final ChatColor SECONDARY_COLOR = ChatColor.WHITE;
    private static final String PREFIX = "Staff";

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("sc")
            .senderType(ProxiedPlayer.class)
            .permission(Permission.isStaff())
            .argument(StringArgument.greedy("message"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                String message = player.getName() + ": " + c.get("message");

                ProxyServer.getInstance().getPlayers().stream()
                    .filter(PermissionUtils::isStaff)
                    .forEach(staff -> this.sendStaffMessage(staff, message));
            });
    }

    private BaseComponent[] getPrefix() {
        return
            new ComponentBuilder("[").color(SECONDARY_COLOR)
                .append(PREFIX).color(PRIMARY_COLOR)
                .append("]").color(SECONDARY_COLOR)
                .append(" ").reset()
                .create();
    }

    private void sendStaffMessage(ProxiedPlayer player, String message) {
        sendStaffMessage(player, new TextComponent(message));
    }

    private void sendStaffMessage(ProxiedPlayer player, BaseComponent... messages) {
        player.sendMessage(ArrayUtils.addAll(this.getPrefix(), messages));
    }

}
