package net.purelic.spring.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.List;

public class ChatUtils {

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
        for (ProxiedPlayer player : Spring.getPlugin().getProxy().getPlayers()) {
            player.sendMessage(
                    new ComponentBuilder("\nBROADCAST  ")
                                .color(ChatColor.RED)
                                .bold(true)
                            .append(message).reset()
                            .append("\n")
                            .create());
        }
    }

    public static List<String> wrap(String text) {
        return wrap(text, ChatColor.WHITE);
    }

    public static List<String> wrap(String text, ChatColor color) {
        return wrap(text, 30, color);
    }

    public static List<String> wrap(String text, int length, ChatColor color) {
        String wrapped = WordUtils.wrap(text, length, "%%" + color, true);
        return Arrays.asList(wrapped.split("%%"));
    }

}