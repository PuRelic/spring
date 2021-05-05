package net.purelic.spring.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

public class PlayerKickEvent extends Event {

    private final ProxiedPlayer player;
    private final String reason;

    public PlayerKickEvent(ProxiedPlayer player, String reason) {
        this.player = player;
        this.reason = reason;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public String getReason() {
        return this.reason;
    }

}
