package net.purelic.spring.listeners.punishment;

import com.google.cloud.firestore.FieldValue;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.Spring;
import net.purelic.spring.analytics.events.PlayerPunishedEvent;
import net.purelic.spring.events.PlayerPunishEvent;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.punishment.Punishment;
import net.purelic.spring.punishment.PunishmentType;
import net.purelic.spring.utils.DatabaseUtils;

import java.util.UUID;

public class PlayerPunish implements Listener {

    @EventHandler
    public void onPlayerPunished(PlayerPunishEvent event) {
        UUID playerId = event.getPlayerId();
        Profile profile = event.getProfile();

        Punishment punishment = new Punishment(
            profile,
            UUID.randomUUID().toString(),
            event.getPunisherId(),
            event.getReason(),
            event.getType(),
            event.getDuration(),
            event.getUnit(),
            event.isOnline()
        );

        profile.addPunishment(punishment);
        DatabaseUtils.updatePlayerDoc(profile.getId(), "punishments", FieldValue.arrayUnion(punishment.toData()));
        new PlayerPunishedEvent(playerId, punishment).track();

        // ip ban if perma or temp ban
        if (event.getType() == PunishmentType.PERMA_BAN || event.getType() == PunishmentType.TEMP_BAN) {
            DatabaseUtils.banIp(playerId, punishment.getExpirationTimestamp());
        }

        // broadcast the punishment to the server if the punished player is online
        ProxiedPlayer punished = Spring.getPlayer(playerId);

        if (punished != null) {
            Spring.sendPluginMessage(
                punished,
                "PunishMessage",
                event.getPunisherId().toString(),
                event.getType().getPastTense(),
                event.getPlayerId().toString(),
                event.getReason());
        }
    }

}
