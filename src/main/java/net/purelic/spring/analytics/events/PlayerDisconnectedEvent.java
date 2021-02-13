package net.purelic.spring.analytics.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.analytics.AnalyticsEvent;

public class PlayerDisconnectedEvent extends AnalyticsEvent {

    public PlayerDisconnectedEvent(ProxiedPlayer player, long playtime) {
        super("Player Disconnected", player);

        // set event properties
        this.properties.put("server", player.getServer().getInfo().getName());
        this.properties.put("playtime", playtime);
    }

}
