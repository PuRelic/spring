package net.purelic.spring.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.analytics.Analytics;
import net.purelic.spring.utils.Protocol;

public class PostLogin implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Protocol protocol = Protocol.getProtocol(player);

        if (protocol.value() != Protocol.MINECRAFT_1_8.value()) {
            ComponentBuilder legacyWarning =
                    new ComponentBuilder("WARNING  ").color(ChatColor.YELLOW).bold(true)
                        .append("PuRelic is optimized for 1.8 - some features might not be supported on " + protocol.getFullLabel()).reset();
            player.sendMessage(legacyWarning.create());
        }

        ComponentBuilder discordMessage =
                new ComponentBuilder("Join our discord community for updates and more info:\n").color(ChatColor.WHITE).bold(true)
                    .append(" » ").reset().color(ChatColor.GRAY)
                    .append("discord.gg/mZc2PhrYAv").color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Open").create()))
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/mZc2PhrYAv"));
        player.sendMessage(discordMessage.create());

        Analytics.startSession(player);
    }

}
