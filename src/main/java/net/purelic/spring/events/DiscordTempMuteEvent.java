package net.purelic.spring.events;

import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.plugin.Event;

import java.util.concurrent.TimeUnit;

public class DiscordTempMuteEvent extends Event {

    private final User user;
    private final int duration;
    private final TimeUnit timeUnit;
    private final String timeUnitName;
    private final String message;
    private final boolean automatic;
    private final String muteLength;

    public DiscordTempMuteEvent(User user, int duration, TimeUnit timeUnit) {
        this(user, duration, timeUnit, null, false);
    }

    public DiscordTempMuteEvent(User user, int duration, TimeUnit timeUnit, String message) {
        this(user, duration, timeUnit, message, false);
    }

    private DiscordTempMuteEvent(User user, int duration, TimeUnit timeUnit, String message, boolean automatic) {
        this.user = user;
        this.duration = duration;
        this.timeUnit = timeUnit;
        this.timeUnitName = timeUnit.name().toLowerCase();
        this.message = message;
        this.automatic = automatic;
        this.muteLength = duration + " " + (duration == 1 ?
            this.timeUnitName.substring(0, this.timeUnitName.length() - 1) : this.timeUnitName);
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

    public String getTimeUnitName() {
        return this.timeUnitName;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isAutomatic() {
        return this.automatic;
    }

    public String getMuteLength() {
        return this.muteLength;
    }

}
