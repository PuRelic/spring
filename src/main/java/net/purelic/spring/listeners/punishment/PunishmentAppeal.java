package net.purelic.spring.listeners.punishment;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.events.PunishmentAppealEvent;
import net.purelic.spring.punishment.PunishmentType;
import net.purelic.spring.utils.DatabaseUtils;

import java.util.UUID;

public class PunishmentAppeal implements Listener {

    @EventHandler
    public void onPunishmentAppeal(PunishmentAppealEvent event) {
        UUID playerId = event.getPlayerId();
        PunishmentType type = event.getPunishment().getType();

        if (type == PunishmentType.PERMA_BAN || type == PunishmentType.TEMP_BAN) {
            DatabaseUtils.unbanIp(playerId);
        }
    }

}
