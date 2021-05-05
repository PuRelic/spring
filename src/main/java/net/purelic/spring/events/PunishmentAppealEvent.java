package net.purelic.spring.events;

import net.md_5.bungee.api.plugin.Event;
import net.purelic.spring.punishment.Punishment;

import java.util.UUID;

public class PunishmentAppealEvent extends Event {

    private final UUID playerId;
    private final Punishment punishment;

    public PunishmentAppealEvent(UUID playerId, Punishment punishment) {
        this.playerId = playerId;
        this.punishment = punishment;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public Punishment getPunishment() {
        return this.punishment;
    }

}
