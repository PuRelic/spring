package net.purelic.spring.analytics.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.analytics.AnalyticsEvent;
import net.purelic.spring.punishment.Punishment;

import java.util.UUID;

public class PunishmentAppealedEvent extends AnalyticsEvent {

    public PunishmentAppealedEvent(UUID playerId, Punishment punishment) {
        super("Punishment Appealed", playerId);

        ProxiedPlayer punished = Spring.getPlayer(playerId);
        ProxiedPlayer punisher = Spring.getPlayer(punishment.getPunisher());
        ProxiedPlayer appellant = Spring.getPlayer(punishment.getAppealedBy());

        if (punished != null) this.properties.put("player_name", punished.getName());
        if (punisher != null) this.properties.put("punisher_name", punisher.getName());
        if (appellant != null) this.properties.put("appellant_name", appellant.getName());

        this.properties.put("punisher_id", punishment.getPunisher().toString());
        this.properties.put("punishment_id", punishment.getPunishmentId());
        this.properties.put("reason", punishment.getReason());
        this.properties.put("type", punishment.getType().name());
        this.properties.put("type_name", punishment.getType().getName());
        this.properties.put("online", punishment.hasSeen());
        this.properties.put("stale", punishment.isStale());
        this.properties.put("expired", punishment.isExpired());
        this.properties.put("appealed", punishment.getAppealedTimestamp().toDate());
        this.properties.put("appellant_id", punishment.getAppealedBy().toString());
        this.properties.put("appeal_time", punishment.getAppealedTimestamp().getSeconds() - punishment.getTimestamp().getSeconds());
        this.properties.put("punished", punishment.getTimestamp().toDate());

        if (punishment.hasExpirationTimestamp()) {
            this.properties.put("expiration", punishment.getExpirationTimestamp().toDate());
            this.properties.put("duration", punishment.getExpirationTimestamp().getSeconds() - punishment.getTimestamp().getSeconds());
        }
    }

}
