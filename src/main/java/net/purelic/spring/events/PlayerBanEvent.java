package net.purelic.spring.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.purelic.spring.punishment.BanUnit;
import net.purelic.spring.punishment.PunishmentType;

public class PlayerBanEvent extends Event {

    private final ProxiedPlayer player;
    private final String reason;
    private final PunishmentType type;
    private final int duration;
    private final BanUnit unit;

    public PlayerBanEvent(ProxiedPlayer player, String reason, PunishmentType type, int duration, BanUnit unit) {
        this.player = player;
        this.reason = reason;
        this.type = type;
        this.duration = duration;
        this.unit = unit;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public String getReason() {
        return this.reason;
    }

    public PunishmentType getType() {
        return this.type;
    }

    public int getDuration() {
        return this.duration;
    }

    public BanUnit getUnit() {
        return this.unit;
    }

}
