package net.purelic.spring.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PlayerWarnEvent extends Event {

    private final ProxiedPlayer player;
    private final String reason;
    private final boolean seen;

    public PlayerWarnEvent(ProxiedPlayer player, String reason, boolean seen) {
        this.player = player;
        this.reason = reason;
        this.seen = seen;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public String getReason() {
        return this.reason;
    }

    public boolean hasSeen() {
        return this.seen;
    }

}
