package net.purelic.spring.analytics.events;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.analytics.DiscordAnalyticsEvent;
import net.purelic.spring.utils.DatabaseUtils;

public class DiscordInviteCreatedEvent extends DiscordAnalyticsEvent {

    public DiscordInviteCreatedEvent(Invite invite, User inviter) {
        super("Discord Invite Created", inviter);

        Invite.Channel channel = invite.getChannel();

        this.properties.put("code", invite.getCode());
        this.properties.put("uses", invite.getUses());
        this.properties.put("max_uses", invite.getMaxUses());
        this.properties.put("max_age", invite.getMaxAge());
        this.properties.put("created_at", DatabaseUtils.timestampOf(invite.getTimeCreated()));

        if (channel != null) {
            this.properties.put("channel_id", channel.getId());
            this.properties.put("channel_name", channel.getName());
        }
    }

}
