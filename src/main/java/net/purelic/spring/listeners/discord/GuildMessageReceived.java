package net.purelic.spring.listeners.discord;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.purelic.spring.Spring;
import net.purelic.spring.events.DiscordMessageEvent;
import org.jetbrains.annotations.NotNull;

public class GuildMessageReceived extends ListenerAdapter {

    // We call a custom discord message event to add helper functions and filter out webhook and bot messages

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.isWebhookMessage() || event.getAuthor().isBot()) return;
        Spring.callEvent(new DiscordMessageEvent(event));
    }

}
