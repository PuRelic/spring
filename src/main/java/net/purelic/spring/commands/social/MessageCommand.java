package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.bungee.arguments.PlayerArgument;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.analytics.events.PrivateMessageSentEvent;
import net.purelic.spring.commands.ProxyCommand;

import java.util.HashMap;
import java.util.Map;

public class MessageCommand implements ProxyCommand {

    protected static Map<ProxiedPlayer, ProxiedPlayer> messages = new HashMap<>();

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("msg", "tell")
            .senderType(ProxiedPlayer.class)
            .argument(PlayerArgument.of("player"))
            .argument(StringArgument.greedy("message"))
            .handler(c -> {
                ProxiedPlayer sender = (ProxiedPlayer) c.getSender();
                ProxiedPlayer recipient = c.get("player");
                String message = c.get("message");

                messages.put(sender, recipient);
                messages.put(recipient, sender);
                sendPM(sender, recipient, message);
            });
    }

    protected static void sendPM(ProxiedPlayer sender, ProxiedPlayer recipient, String message) {
        boolean sameServer = sender.getServer().getInfo() == recipient.getServer().getInfo();

        if (sameServer) {
            Spring.sendPluginMessage(
                sender,
                "PrivateMessage",
                sender.getUniqueId().toString(),
                recipient.getUniqueId().toString(),
                message);
        } else {
            sendFancyPM(sender, recipient, message, true);
            sendFancyPM(sender, recipient, message, false);
        }

        new PrivateMessageSentEvent(sender, recipient, message, sameServer).track();
    }

    private static void sendFancyPM(ProxiedPlayer sender, ProxiedPlayer recipient, String message, boolean isSender) {
        TextComponent pm = new TextComponent(ChatColor.GRAY + "(" + (isSender ? "To " : "From "));
        pm.addExtra(isSender ? getMessageName(recipient) : getMessageName(sender));
        pm.addExtra(ChatColor.GRAY + "): " + ChatColor.RESET + message);
        (isSender ? sender : recipient).sendMessage(pm);
    }

    @SuppressWarnings("deprecation")
    private static TextComponent getMessageName(ProxiedPlayer player) {
        TextComponent component = new TextComponent(ChatColor.DARK_AQUA + player.getName());
        ComponentBuilder hover = new ComponentBuilder(ChatColor.GRAY + "Server: " + ChatColor.DARK_AQUA + player.getServer().getInfo().getName());
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()));
        return component;
    }

}
