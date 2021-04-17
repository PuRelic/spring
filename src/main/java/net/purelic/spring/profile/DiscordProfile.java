package net.purelic.spring.profile;

import com.google.cloud.Timestamp;
import net.dv8tion.jda.api.entities.User;
import net.purelic.commons.Commons;
import net.purelic.spring.managers.ProfileManager;

import java.util.Map;
import java.util.UUID;

public class DiscordProfile {

    private final Timestamp joined;
    private final long referrals;
    private final String playerId;
    private final Timestamp verified;
    private final String referringInvite;
    private final String referringUser;

    public DiscordProfile(Map<String, Object> data) {
        this.joined = (Timestamp) data.get("joined");
        this.referrals = (long) data.get("referrals");
        this.playerId = (String) data.get("player_uuid");
        this.verified = (Timestamp) data.get("verified_at");
        this.referringInvite = (String) data.get("referring_invite");
        this.referringUser = (String) data.get("referring_user");
    }

    public Timestamp getJoined() {
        return this.joined;
    }

    public long getReferrals() {
        return this.referrals;
    }

    public boolean isVerified() {
        return this.verified != null && this.playerId != null;
    }

    public Timestamp getVerifiedTimestamp() {
        return this.verified;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public Profile getPlayerProfile() {
        return ProfileManager.getProfile(UUID.fromString(this.playerId));
    }

    public boolean wasReferred() {
        return this.referringInvite != null && this.referringUser != null;
    }

    public String getReferringUserId() {
        return this.referringUser;
    }

    public String getReferringInvite() {
        return this.referringInvite;
    }

    public User getReferringUser() {
        return Commons.getDiscordBot().getUserById(this.referringUser);
    }

}
