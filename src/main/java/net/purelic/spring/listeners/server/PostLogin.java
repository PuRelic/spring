package net.purelic.spring.listeners.server;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.analytics.Analytics;
import net.purelic.spring.commands.social.DiscordCommand;
import net.purelic.spring.utils.Protocol;

public class PostLogin implements Listener {

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

        DiscordCommand.sendDiscordMessage(player);

        Analytics.startSession(player);
    }

}
