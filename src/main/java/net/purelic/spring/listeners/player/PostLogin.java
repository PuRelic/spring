package net.purelic.spring.listeners.player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.analytics.Analytics;
import net.purelic.spring.commands.social.DiscordInviteCommand;
import net.purelic.spring.managers.AltManager;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.utils.Protocol;
import net.purelic.spring.utils.TaskUtils;

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

        if (!ProfileManager.getProfile(player).hasDiscordLinked()) DiscordInviteCommand.sendDiscordMessage(player);
        Analytics.startSession(player);

        TaskUtils.runAsync(() -> AltManager.track(player));
    }

}
