package net.purelic.spring.listeners.discord;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.discord.Channel;
import net.purelic.spring.discord.Role;
import net.purelic.spring.events.DiscordMessageEvent;
import net.purelic.spring.utils.DiscordUtils;

import java.util.Arrays;
import java.util.List;

public class AttachmentOnlyChannels implements Listener {

    private static final List<String> CHANNELS = Arrays.asList(
        Channel.BUILDS,
        Channel.CLIPS,
        Channel.SCREENSHOTS
    );

    @EventHandler
    public void onDiscordMessage(DiscordMessageEvent event) {
        if (CHANNELS.contains(event.getChannelId())
            && !event.hasAttachments()
            && !event.hasEmbeds()
            && !DiscordUtils.hasRole(event.getSender(), Role.staff())
        ) {
            event.getMessage().delete().queue();
        }
    }

}
