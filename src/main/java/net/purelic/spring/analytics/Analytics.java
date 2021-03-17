package net.purelic.spring.analytics;

import com.google.cloud.Timestamp;
import com.google.common.collect.ImmutableMap;
import com.rudderstack.sdk.java.messages.IdentifyMessage;
import com.rudderstack.sdk.java.messages.TrackMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.commons.Commons;
import net.purelic.spring.analytics.events.PlayerConnectedEvent;
import net.purelic.spring.analytics.events.PlayerDisconnectedEvent;
import net.purelic.spring.managers.ProfileManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Analytics {

    private static Map<UUID, UUID> sessions = new HashMap<>();
    private static Map<UUID, Timestamp> sessionStarts = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void loadSessionCache() {
        sessions = (Map<UUID, UUID>) Commons.getGeneralCache().getOrDefault("session_ids", new HashMap<>());
        sessionStarts = (Map<UUID, Timestamp>) Commons.getGeneralCache().getOrDefault("session_starts", new HashMap<>());
    }

    public static void cacheSessions() {
        Commons.getGeneralCache().put("session_ids", sessions);
        Commons.getGeneralCache().put("session_starts", sessionStarts);
    }

    public static void startSession(ProxiedPlayer player) {
        UUID sessionId = UUID.randomUUID();

        sessions.put(player.getUniqueId(), sessionId);
        sessionStarts.put(sessionId, Timestamp.now());

        identify(player);
        track(new PlayerConnectedEvent(player));
        ProfileManager.getProfile(player).setSessionId(sessionId);
    }

    public static void endSession(ProxiedPlayer player) {
        UUID playerId = player.getUniqueId();
        UUID sessionId = sessions.get(playerId);

        Timestamp sessionStarted = sessionStarts.get(sessionId);
        Timestamp sessionEnded = Timestamp.now();

        // Players that disconnect before fully connecting can sometimes cause NPEs
        if (sessionId == null || sessionStarted == null || player.getServer() == null) return;

        long playtime = sessionEnded.getSeconds() - sessionStarted.getSeconds();

        track(new PlayerDisconnectedEvent(player, playtime));
        ProfileManager.getProfile(player).setSessionId(null);

        sessions.remove(playerId);
        sessionStarts.remove(sessionId);
    }

    @SuppressWarnings("deprecation")
    public static void identify(ProxiedPlayer player) {
        Commons.getAnalytics().enqueue(IdentifyMessage.builder()
            .timestamp(Timestamp.now().toDate())
            .userId(player.getUniqueId().toString())
            .traits(ImmutableMap.<String, Object>builder()
                .put("name", player.getName())
                .build()
            )
            .context(ImmutableMap.<String, Object>builder().put("ip", player.getAddress().getAddress().getHostAddress()).build())
        );
    }

    @SuppressWarnings("deprecation")
    public static void track(AnalyticsEvent event) {
        TrackMessage.Builder track = TrackMessage.builder(event.getName())
            .timestamp(Timestamp.now().toDate())
            .userId(event.getPlayerId().toString());

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(event.getPlayerId());

        if (player != null) {
            track.context(ImmutableMap.<String, Object>builder().put("ip", player.getAddress().getAddress().getHostAddress()).build());
        }

        Map<String, Object> properties = event.getProperties();

        if (sessions.containsKey(event.getPlayerId())) {
            properties.put("session_id", sessions.get(event.getPlayerId()).toString());
        }

        Commons.getAnalytics().enqueue(track.properties(properties));
    }

    public static void track(String event, String ip, Map<String, Object> properties) {
        Commons.getAnalytics().enqueue(
            TrackMessage.builder(event)
                .timestamp(Timestamp.now().toDate())
                .anonymousId(ip)
                .properties(properties)
                .context(ImmutableMap.<String, Object>builder().put("ip", ip).build())
        );
    }

    public static String urlBuilder(ProxiedPlayer player, String url, String content, String... utms) {
        StringBuilder urlBuilder = new StringBuilder(url)
            .append("?uuid=").append(player.getUniqueId().toString())
            .append("&utm_source=server")
            .append("&utm_medium=chat")
            .append("&utm_content=").append(content);

        for (String utm : utms) {
            urlBuilder.append("&").append(utm);
        }

        return urlBuilder.toString();
    }

}
