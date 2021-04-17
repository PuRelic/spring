package net.purelic.spring.analytics.events;

import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.analytics.DiscordAnalyticsEvent;

public class DiscordUnlinkedEvent extends DiscordAnalyticsEvent {

    public DiscordUnlinkedEvent(User user) {
        super("Discord Unlinked", user);
    }

}
