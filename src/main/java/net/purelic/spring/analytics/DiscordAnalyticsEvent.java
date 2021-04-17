package net.purelic.spring.analytics;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.managers.DiscordManager;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public abstract class DiscordAnalyticsEvent {

    private final String name;
    private final String userId;
    private final UUID playerId;
    protected final Map<String, Object> properties;

    public DiscordAnalyticsEvent(String name, Member member) {
        this(name, member.getUser());
    }

    public DiscordAnalyticsEvent(String name, User user) {
        this.name = name;
        this.userId = user.getId();
        this.playerId = DiscordManager.getLinkedId(user);
        this.properties = new LinkedHashMap<>();

        // set default properties
        this.properties.put("member_id", this.userId);
        this.properties.put("member_tag", user.getAsTag());
        this.properties.put("member_name", user.getName());
        this.properties.put("member_discriminator", user.getDiscriminator());
    }

    public String getName() {
        return this.name;
    }

    public String getUserId() {
        return this.userId;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public boolean hasLinkedId() {
        return this.playerId != null;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void track() {
        Analytics.track(this);
    }

}
