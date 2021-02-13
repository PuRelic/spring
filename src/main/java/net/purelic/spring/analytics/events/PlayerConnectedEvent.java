package net.purelic.spring.analytics.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.analytics.AnalyticsEvent;
import net.purelic.spring.utils.Protocol;

public class PlayerConnectedEvent extends AnalyticsEvent {

    @SuppressWarnings("deprecation")
    public PlayerConnectedEvent(ProxiedPlayer player) {
        super("Player Connected", player);

        // set event properties
        Protocol protocol = Protocol.getProtocol(player);
        this.properties.put("version_protocol", protocol.value());
        this.properties.put("version_full", protocol.getFullLabel());
        this.properties.put("version_clean", protocol.getLabel());
        this.properties.put("ping", player.getPing());
        this.properties.put("ip_address", player.getAddress().getAddress().getHostAddress());
    }

}
