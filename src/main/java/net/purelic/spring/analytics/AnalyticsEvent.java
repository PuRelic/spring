package net.purelic.spring.analytics;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AnalyticsEvent {

    private static final UUID FALLBACK_UUID = UUID.fromString("57014d5f-1d26-4986-832b-a0e7a4e41088"); // PuRelic

    private final String name;
    private final UUID playerId;
    protected final Map<String, Object> properties;

    public AnalyticsEvent(String name) {
        this(name, FALLBACK_UUID);
    }

    public AnalyticsEvent(String name, ProxiedPlayer player) {
        this(name, player == null ? FALLBACK_UUID : player.getUniqueId());
        if (player != null) this.properties.put("player_name", player.getName());
    }

    public AnalyticsEvent(String name, UUID playerId) {
        this.name = name;
        this.playerId = playerId;
        this.properties = new LinkedHashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void track() {
        Analytics.track(this);
    }

}
