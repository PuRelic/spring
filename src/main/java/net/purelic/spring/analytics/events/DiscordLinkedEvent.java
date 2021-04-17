package net.purelic.spring.analytics.events;

import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.analytics.DiscordAnalyticsEvent;

public class DiscordLinkedEvent extends DiscordAnalyticsEvent {

    public DiscordLinkedEvent(User user) {
        super("Discord Linked", user);
    }

}
