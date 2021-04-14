package net.purelic.spring.listeners.server;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.analytics.events.ProxyPingedEvent;
import net.purelic.spring.managers.SettingsManager;

public class ProxyPing implements Listener {

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getResponse();
        ping.setDescriptionComponent(this.getMotd());

        ServerPing.Players players = ping.getPlayers();
        players.setMax(SettingsManager.isMaintenanceMode() ? 0 : SettingsManager.getMaxPlayers());

        event.setResponse(ping);

        new ProxyPingedEvent(event).track();
    }

    private BaseComponent getMotd() {
        return new TextComponent(
            ChatColor.translateAlternateColorCodes('&', SettingsManager.getMotdHeader()) +
                "\n" + ChatColor.RESET +
                (SettingsManager.isMaintenanceMode() ?
                    SettingsManager.getMotdMaintenance()
                    : SettingsManager.getMotdFooter())
        );
    }

}
