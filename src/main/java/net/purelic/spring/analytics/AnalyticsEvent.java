package net.purelic.spring.analytics;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AnalyticsEvent {

    private final String name;
    private final ProxiedPlayer player;
    private final UUID playerId;
    protected final Map<String, Object> properties;

    public AnalyticsEvent(String name, ProxiedPlayer player) {
        this.name = name;
        this.player = player;
        this.playerId = player.getUniqueId();
        this.properties = new LinkedHashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

}
