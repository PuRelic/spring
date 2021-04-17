package net.purelic.spring.events;

import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.plugin.Event;

import java.util.concurrent.TimeUnit;

public class DiscordTempMuteEvent extends Event {

    private final User user;
    private final int duration;
    private final TimeUnit timeUnit;

    public DiscordTempMuteEvent(User user, int duration, TimeUnit timeUnit) {
        this.user = user;
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    public User getUser() {
        return this.user;
    }

    public int getDuration() {
        return this.duration;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

}
