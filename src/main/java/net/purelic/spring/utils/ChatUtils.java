package net.purelic.spring.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatUtils {

    public static final String BULLET = " \u2022 "; // •
    public static final String ARROW = "\u00BB"; // »

    public static ComponentBuilder getHeader(String header) {
        return getHeader(header, false);
    }

    public static ComponentBuilder getHeader(String header, boolean bold) {
        return getHeader(header, bold, ChatColor.AQUA, ChatColor.WHITE);
    }

    public static ComponentBuilder getHeader(String header, ChatColor primary, ChatColor secondary) {
        return getHeader(header, false, primary, secondary);
    }

    public static ComponentBuilder getHeader(String header, boolean bold, ChatColor primary, ChatColor secondary) {
        return getHeader(header, bold, primary, secondary, false);
    }

    public static ComponentBuilder getHeader(String header, boolean bold, ChatColor primary, ChatColor secondary, boolean padding) {
        return new ComponentBuilder((padding ? "\n" : "") + "                    ").color(secondary).strikethrough(true)
                .append("   " +  header + "   ").reset().color(primary).bold(bold)
                .append("                    " + (padding ? "\n" : "")).reset().color(secondary).strikethrough(true);
    }

    public static void broadcastMessage(String message) {
        ComponentBuilder builder = new ComponentBuilder("\n")
            .append("BROADCAST  ").color(ChatColor.RED).bold(true)
            .append(message).reset()
            .append("\n ");

        for (ProxiedPlayer player : Spring.getPlugin().getProxy().getPlayers()) sendMessage(player, builder);
    }

    public static List<String> wrap(String text) {
        return wrap(text, ChatColor.WHITE);
    }

    public static List<String> wrap(String text, ChatColor color) {
        return wrap(text, 30, color);
    }

    public static List<String> wrap(String text, int length, ChatColor color) {
        String wrapped = WordUtils.wrap(text, length, "%%" + color, true);
        return new ArrayList<>(Arrays.asList(wrapped.split("%%")));
    }

    public static void sendMessage(ProxiedPlayer player, ComponentBuilder builder) {
        sendMessage(player, new TextComponent(builder.create()));
    }

    public static void sendMessage(ProxiedPlayer player, String message) {
        sendMessage(player, new TextComponent(message));
    }

    public static void sendMessage(ProxiedPlayer player, BaseComponent[] message) {
        sendMessage(player, new TextComponent(message));
    }

    @SuppressWarnings("deprecation")
    public static void sendMessage(ProxiedPlayer player, TextComponent message) {
        if (Protocol.isLegacy(player)) player.sendMessages(message.toLegacyText().split("\n"));
        else player.sendMessage(message);
    }

}