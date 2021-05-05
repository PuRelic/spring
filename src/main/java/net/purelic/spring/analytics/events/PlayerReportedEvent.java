package net.purelic.spring.analytics.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.analytics.AnalyticsEvent;

public class PlayerReportedEvent extends AnalyticsEvent {

    public PlayerReportedEvent(ProxiedPlayer player, ProxiedPlayer reporter, String reason) {
        super("Player Reported", player);
        this.properties.put("reason", reason);
        this.properties.put("automatic", false);
        this.properties.put("reporter_id", reporter.getUniqueId().toString());
        this.properties.put("reporter_name", reporter.getName());
        this.properties.put("ping", player.getPing());
    }

    public PlayerReportedEvent(ProxiedPlayer player, String reason) {
        super("Player Reported", player);
        this.properties.put("reporter_id", "57014d5f-1d26-4986-832b-a0e7a4e41088");
        this.properties.put("reporter_name", "PuRelic");
        this.properties.put("reason", reason);
        this.properties.put("automatic", true);
        this.properties.put("ping", player.getPing());
    }

}
