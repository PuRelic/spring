package net.purelic.spring.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.ArrayUtils;

public class CommandUtils {

    private static BaseComponent[] errorPrefix;
    private static BaseComponent[] alertPrefix;
    private static BaseComponent[] successPrefix;

    @SuppressWarnings("deprecation")
    private static BaseComponent[] getPrefix(String hover, ChatColor primary, ChatColor secondary) {
        return
                new ComponentBuilder("[").color(secondary).bold(true)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).color(primary).create()))
                        .append("!", ComponentBuilder.FormatRetention.ALL).color(primary)
                        .append("] ", ComponentBuilder.FormatRetention.ALL).color(secondary)
                        .create();
    }

    private static BaseComponent[] getErrorPrefix() {
        if (errorPrefix == null) errorPrefix = getPrefix("Error", ChatColor.RED, ChatColor.DARK_RED);
        return errorPrefix;
    }

    private static BaseComponent[] getAlertPrefix() {
        if (alertPrefix == null) alertPrefix = getPrefix("Alert", ChatColor.YELLOW, ChatColor.GOLD);
        return alertPrefix;
    }

    private static BaseComponent[] getSuccessPrefix() {
        if (successPrefix == null) successPrefix = getPrefix("Success", ChatColor.GREEN, ChatColor.DARK_GREEN);
        return successPrefix;
    }

    public static void sendErrorMessage(ProxiedPlayer player, String message) {
        TextComponent errorMessage = new TextComponent(message);
        errorMessage.setColor(ChatColor.RED);
        sendErrorMessage(player, errorMessage);
    }

    public static void sendErrorMessage(ProxiedPlayer player, BaseComponent... messages) {
        player.sendMessage(ArrayUtils.addAll(getErrorPrefix(), messages));
    }

    public static void sendNoServerMessage(ProxiedPlayer player, String target) {
        TextComponent errorMessage = new TextComponent("Could not find server \"" + target + "\"");
        errorMessage.setColor(ChatColor.RED);
        sendErrorMessage(player, errorMessage);
    }

    public static void sendNoPermissionMessage(ProxiedPlayer player) {
        TextComponent errorMessage = new TextComponent("You don't have permission to use this command!");
        errorMessage.setColor(ChatColor.RED);
        sendErrorMessage(player, errorMessage);
    }

    public static void sendAlertMessage(ProxiedPlayer player, String message) {
        sendAlertMessage(player, new TextComponent(message));
    }

    public static void sendAlertMessage(ProxiedPlayer player, BaseComponent... messages) {
        player.sendMessage(ArrayUtils.addAll(getAlertPrefix(), messages));
    }

    public static void sendSuccessMessage(ProxiedPlayer player, String message) {
        TextComponent successMessage = new TextComponent(message);
        successMessage.setColor(ChatColor.GREEN);
        sendSuccessMessage(player, successMessage);
    }

    public static void sendSuccessMessage(ProxiedPlayer player, BaseComponent... messages) {
        player.sendMessage(ArrayUtils.addAll(getSuccessPrefix(), messages));
    }

}
