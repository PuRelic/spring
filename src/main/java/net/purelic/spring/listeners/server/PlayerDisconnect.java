package net.purelic.spring.listeners.server;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.Spring;
import net.purelic.spring.analytics.Analytics;
import net.purelic.spring.managers.PartyManager;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.managers.ServerManager;

public class PlayerDisconnect implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ProfileManager.removeProfile(player);
        ServerManager.removeFromQueue(player);
        ServerManager.setLastServer(player);
        PartyManager.removeMember(player);
        Analytics.endSession(player);

        Spring.sendPluginMessage(
                player,
                "QuitMessage",
                player.getUniqueId().toString(),
                "");
    }

}
