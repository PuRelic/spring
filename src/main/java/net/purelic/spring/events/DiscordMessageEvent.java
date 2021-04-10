package net.purelic.spring.events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.md_5.bungee.api.plugin.Event;

public class DiscordMessageEvent extends Event {

    private final GuildMessageReceivedEvent messageEvent;

    public DiscordMessageEvent(GuildMessageReceivedEvent event) {
        this.messageEvent = event;
    }

    public GuildMessageReceivedEvent getMessageEvent() {
        return this.messageEvent;
    }

}
