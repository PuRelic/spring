package net.purelic.spring.events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.md_5.bungee.api.plugin.Event;

public class DiscordMessageEvent extends Event {

    private final Message message;
    private final User sender;
    private final TextChannel channel;
    private final String channelId;
    private final boolean hasAttachments;
    private final boolean hasEmbeds;

    public DiscordMessageEvent(GuildMessageReceivedEvent event) {
        this.message = event.getMessage();
        this.sender = event.getAuthor();
        this.channel = event.getChannel();
        this.channelId = this.channel.getId();
        this.hasAttachments = this.message.getAttachments().size() > 0;
        this.hasEmbeds = this.message.getEmbeds().size() > 0;
    }

    public Message getMessage() {
        return this.message;
    }

    public User getSender() {
        return this.sender;
    }

    public TextChannel getChannel() {
        return this.channel;
    }

    public String getChannelId() {
        return this.channelId;
    }

    public boolean hasAttachments() {
        return this.hasAttachments;
    }

    public boolean hasEmbeds() {
        return this.hasEmbeds;
    }

}
