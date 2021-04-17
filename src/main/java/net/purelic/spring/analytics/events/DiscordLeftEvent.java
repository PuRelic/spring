package net.purelic.spring.analytics.events;

import net.dv8tion.jda.api.entities.Member;
import net.purelic.spring.analytics.DiscordAnalyticsEvent;

public class DiscordLeftEvent extends DiscordAnalyticsEvent {

    public DiscordLeftEvent(Member member) {
        super("Discord Left", member);
    }

}
