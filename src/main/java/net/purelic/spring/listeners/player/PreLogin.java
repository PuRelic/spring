package net.purelic.spring.listeners.player;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.Spring;
import net.purelic.spring.managers.SettingsManager;
import net.purelic.spring.profile.Rank;
import net.purelic.spring.utils.DatabaseUtils;

import java.util.ArrayList;
import java.util.List;

public class PreLogin implements Listener {

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        String playerName = event.getConnection().getName();

        if (SettingsManager.isMaxPlayerEnforced()
            && Spring.getPlugin().getProxy().getOnlineCount() >= SettingsManager.getMaxPlayers()
            && !this.canBypass(playerName, true)) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent(SettingsManager.getServerFullMessage()));
        } else if (SettingsManager.isMaintenanceMode() && !this.canBypass(playerName, false)) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent(SettingsManager.getMotdMaintenance()));
        }
    }

    @SuppressWarnings({"unchecked", "BooleanMethodIsAlwaysInverted"})
    private boolean canBypass(String playerName, boolean includePremium) {
        QueryDocumentSnapshot doc = DatabaseUtils.getPlayerDoc(playerName);

        if (doc == null) return false;

        return Rank.parseRanks((List<Object>) doc.getData().getOrDefault(Rank.PATH, new ArrayList<>()))
            .stream().anyMatch(rank -> rank.isStaff() || (includePremium && (rank == Rank.CREATOR || rank == Rank.PREMIUM)));
    }

}
