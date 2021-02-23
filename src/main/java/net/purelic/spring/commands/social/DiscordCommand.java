package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import cloud.commandframework.bungee.arguments.PlayerArgument;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.CustomCommand;

import java.util.Optional;

public class DiscordCommand implements CustomCommand {

    @SuppressWarnings("deprecation")
    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("discord")
                .senderType(ProxiedPlayer.class)
                .argument(PlayerArgument.optional("player"))
                .handler(c -> {
                    ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                    Optional<ProxiedPlayer> targetArg = c.getOptional("player");

                    if (targetArg.isPresent()) {
                        targetArg.get().sendMessage(
                            new ComponentBuilder(player.getName() + " invited you to the PuRelic Discord").color(ChatColor.WHITE).bold(true)
                                .append(" » ").reset().color(ChatColor.GRAY)
                                .append("purelic.net/discord").color(ChatColor.AQUA)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Join").create()))
                                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://purelic.net/discord"))
                                .create()
                        );
                    } else {
                        sendDiscordMessage(player);
                    }
                });
    }

    @SuppressWarnings("deprecation")
    public static void sendDiscordMessage(ProxiedPlayer player) {
        player.sendMessage(
            new ComponentBuilder("Join the PuRelic Discord").color(ChatColor.WHITE).bold(true)
                .append(" » ").reset().color(ChatColor.GRAY)
                .append("purelic.net/discord").color(ChatColor.AQUA)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Join").create()))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://purelic.net/discord"))
                .create()
        );
    }

}