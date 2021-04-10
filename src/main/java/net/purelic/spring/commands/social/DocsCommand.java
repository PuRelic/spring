package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;

public class DocsCommand implements ProxyCommand {

    @SuppressWarnings("deprecation")
    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("docs")
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                player.sendMessage(
                    new ComponentBuilder("Visit PuRelic's Documentation").color(ChatColor.WHITE).bold(true)
                        .append(" » ").reset().color(ChatColor.GRAY)
                        .append("purelic.net/docs").color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Open").create()))
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://purelic.net/docs"))
                        .create()
                );
            });
    }

}
