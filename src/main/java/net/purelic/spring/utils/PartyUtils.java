package net.purelic.spring.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.ArrayUtils;

public class PartyUtils {

    private static final ChatColor PRIMARY_COLOR = ChatColor.GOLD;
    private static final ChatColor SECONDARY_COLOR = ChatColor.WHITE;

    private static BaseComponent[] getPrefix(String prefix) {
        return
            new ComponentBuilder("[").color(SECONDARY_COLOR)
                .append(prefix).color(PRIMARY_COLOR)
                .append("]").color(SECONDARY_COLOR)
                .append(" ").reset()
                .create();
    }

    public static void sendPartyMessage(ProxiedPlayer player, String message) {
        sendPartyMessage(player, new TextComponent(message));
    }

    public static void sendPartyMessage(ProxiedPlayer player, String prefix, String message) {
        sendPartyMessage(player, prefix, new TextComponent(message));
    }

    public static void sendPartyMessage(ProxiedPlayer player, BaseComponent... messages) {
        sendPartyMessage(player, "Party", messages);
    }

    public static void sendPartyMessage(ProxiedPlayer player, String prefix, BaseComponent... messages) {
        player.sendMessage(ArrayUtils.addAll(getPrefix(prefix), messages));
    }

}
