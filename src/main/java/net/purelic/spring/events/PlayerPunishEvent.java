package net.purelic.spring.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.punishment.BanUnit;
import net.purelic.spring.punishment.PunishmentType;

import java.util.UUID;

public class PlayerPunishEvent extends Event {

    private final UUID playerId;
    private final Profile profile;
    private final UUID punisherId;
    private final String reason;
    private final PunishmentType type;
    private final int duration;
    private final BanUnit unit;
    private final boolean isOnline;

    public PlayerPunishEvent(UUID playerId, ProxiedPlayer punisher, String reason, PunishmentType type, int duration, BanUnit unit) {
        this(playerId, punisher.getUniqueId(), reason, type, duration, unit);
    }

    public PlayerPunishEvent(UUID playerId, UUID punisher, String reason, PunishmentType type, int duration, BanUnit unit) {
        this.playerId = playerId;
        this.profile = ProfileManager.getProfile(playerId);
        this.punisherId = punisher;
        this.reason = reason;
        this.type = type;
        this.duration = duration;
        this.unit = unit;
        this.isOnline = false;
    }

    public PlayerPunishEvent(ProxiedPlayer player, Profile profile, ProxiedPlayer punisher, String reason, PunishmentType type, int duration, BanUnit unit) {
        this(player, profile, punisher.getUniqueId(), reason, type, duration, unit);
    }

    public PlayerPunishEvent(ProxiedPlayer player, Profile profile, UUID punisher, String reason, PunishmentType type, int duration, BanUnit unit) {
        this.playerId = player.getUniqueId();
        this.profile = profile;
        this.punisherId = punisher;
        this.reason = reason;
        this.type = type;
        this.duration = duration;
        this.unit = unit;
        this.isOnline = true;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public UUID getPunisherId() {
        return this.punisherId;
    }

    public String getReason() {
        return this.reason;
    }

    public PunishmentType getType() {
        return this.type;
    }

    public int getDuration() {
        return this.duration;
    }

    public BanUnit getUnit() {
        return unit;
    }

    public boolean isOnline() {
        return this.isOnline;
    }

}
