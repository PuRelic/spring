package net.purelic.spring.analytics.events;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.purelic.spring.analytics.Analytics;
import net.purelic.spring.utils.Protocol;

import java.util.HashMap;
import java.util.Map;

public class ProxyPingedEvent {

    private static final String NAME = "Proxy Pinged";
    private final String anonymousId;
    private final Map<String, Object> properties;

    @SuppressWarnings("deprecation")
    public ProxyPingedEvent(ProxyPingEvent proxyPingEvent) {
        PendingConnection connection = proxyPingEvent.getConnection();
        Protocol protocol = Protocol.getProtocol(connection.getVersion());
        ServerPing ping = proxyPingEvent.getResponse();
        ServerPing.Players players = ping.getPlayers();

        this.anonymousId = connection.getAddress().getAddress().getHostAddress(); // ip address
        this.properties = new HashMap<>();

        // set event properties
        this.properties.put("version_protocol", protocol.value());
        this.properties.put("version_full", protocol.getFullLabel());
        this.properties.put("version_clean", protocol.getLabel());
        this.properties.put("motd", ping.getDescription());
        this.properties.put("max_players", players.getMax());
        this.properties.put("players_online", players.getOnline());
    }

    public void track() {
        Analytics.track(NAME, this.anonymousId, this.properties);
    }

}
