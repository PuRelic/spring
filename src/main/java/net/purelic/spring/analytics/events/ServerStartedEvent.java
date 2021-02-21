package net.purelic.spring.analytics.events;

import com.google.cloud.Timestamp;
import net.purelic.spring.analytics.AnalyticsEvent;
import net.purelic.spring.server.GameServer;

public class ServerStartedEvent extends AnalyticsEvent {

    public ServerStartedEvent(GameServer server) {
        super("Server Started");

        // set event properties
        this.properties.put("server_id", server.getId());
        this.properties.put("server_name", server.getName());
        this.properties.put("size", server.getSize().name());
        this.properties.put("size_name", server.getSize().getName());
        this.properties.put("size_slug", server.getSize().getSlug());
        this.properties.put("region", server.getRegion().name());
        this.properties.put("region_name", server.getRegion().getName());
        this.properties.put("region_slug", server.getRegion().getSlug());
        this.properties.put("snapshot_id", server.getSnapshotId());
        this.properties.put("private", server.isPrivate());
        this.properties.put("ranked", server.isRanked());
        this.properties.put("beta", server.isBeta());
        this.properties.put("type", server.getType().name());
        this.properties.put("type_name", server.getType().getName());
        this.properties.put("premium", server.getType().isPremium());
        if (server.getPlaylist() != null) this.properties.put("playlist", server.getPlaylist());
        this.properties.put("max_players", server.getMaxPlayers());
        this.properties.put("max_party", server.getMaxParty());
        this.properties.put("min_party", server.getMinParty());
        this.properties.put("ip_address", server.getIp());
        this.properties.put("start_time", Timestamp.now().getSeconds() - server.getCreatedAt().getSeconds());
    }

}
