package net.purelic.spring.analytics.events;

import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.analytics.DiscordAnalyticsEvent;
import net.purelic.spring.utils.DatabaseUtils;

public class DiscordJoinedEvent extends DiscordAnalyticsEvent {

    public DiscordJoinedEvent(Member member, Invite invite, boolean firstJoin) {
        super("Discord Joined", member);

        this.properties.put("first_join", firstJoin);

        if (invite != null) {
            User inviter = invite.getInviter();
            Invite.Channel channel = invite.getChannel();

            this.properties.put("invite_code", invite.getCode());
            this.properties.put("invite_uses", invite.getUses());
            this.properties.put("invite_max_uses", invite.getMaxUses());
            this.properties.put("invite_max_age", invite.getMaxAge());
            this.properties.put("invite_created_at", DatabaseUtils.timestampOf(invite.getTimeCreated()));

            if (inviter != null) {
                this.properties.put("invite_inviter_id", inviter.getId());
                this.properties.put("invite_inviter_tag", inviter.getAsTag());
            }

            if (channel != null) {
                this.properties.put("invite_channel_id", channel.getId());
                this.properties.put("invite_channel_name", channel.getName());
            }
        }
    }

}
