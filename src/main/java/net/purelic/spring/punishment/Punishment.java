package net.purelic.spring.punishment;

import com.google.cloud.Timestamp;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.analytics.events.PunishmentAppealedEvent;
import net.purelic.spring.events.PunishmentAppealEvent;
import net.purelic.spring.profile.Profile;

import java.util.*;

public class Punishment {

    private final Profile profile;
    private final String id;
    private final UUID punisher;
    private final String reason;
    private final Timestamp timestamp;
    private final PunishmentType type;
    private boolean seen;
    private final Timestamp expirationTimestamp;
    private Timestamp appealedTimestamp;
    private UUID appealedBy;

    public Punishment(Profile profile, Map<String, Object> docData) {
        this.profile = profile;
        this.id = (String) docData.get("id");
        Object punisherObj = docData.get("punisher");
        this.punisher = punisherObj == null ? null : UUID.fromString((String) punisherObj);
        this.reason = (String) docData.get("reason");
        this.timestamp = (Timestamp) docData.get("timestamp");
        this.type = PunishmentType.fromString((String) docData.get("type"));
        this.seen = (boolean) docData.get("seen");
        this.expirationTimestamp = (Timestamp) docData.get("expiration_timestamp");
        Object appealedByObj = docData.get("appealed_by");
        this.appealedBy = appealedByObj == null ? null : UUID.fromString((String) appealedByObj);
        this.appealedTimestamp = (Timestamp) docData.get("appealed_timestamp");
    }

    public Punishment(Profile profile, String id, UUID punisher, String reason, PunishmentType type, int duration, BanUnit unit, boolean seen) {
        this.profile = profile;
        this.id = id;
        this.punisher = punisher;
        this.reason = reason;
        this.timestamp = Timestamp.now();
        this.type = type;
        this.seen = seen;
        this.appealedTimestamp = null;
        this.appealedBy = null;
        if (unit != null) {
            Date today = Timestamp.now().toDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            cal.add(unit.getUnit(), duration);
            this.expirationTimestamp = Timestamp.of(cal.getTime());
        } else {
            this.expirationTimestamp = null;
        }
    }

    public String getPunishmentId() {
        return this.id;
    }

    public UUID getPunisher() {
        return this.punisher;
    }

    public String getReason() {
        return this.reason;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public PunishmentType getType() {
        return this.type;
    }

    public boolean hasSeen() {
        return this.seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
        this.profile.updatePunishments();
    }

    public Timestamp getExpirationTimestamp() {
        return this.expirationTimestamp;
    }

    public boolean hasExpirationTimestamp() {
        return this.expirationTimestamp != null;
    }

    public boolean isExpired() {
        if (!hasExpirationTimestamp()) return true;
        return this.expirationTimestamp.toDate().before(new Date());
    }

    public Timestamp getAppealedTimestamp() {
        return this.appealedTimestamp;
    }

    public UUID getAppealedBy() {
        return this.appealedBy;
    }

    public boolean isAppealed() {
        return this.appealedTimestamp != null;
    }

    public void appeal(ProxiedPlayer player) {
        this.appeal(player.getUniqueId());
    }

    public void appeal(UUID appealedBy) {
        this.appealedBy = appealedBy;
        this.appealedTimestamp = Timestamp.now();
        this.profile.updatePunishments();
        Spring.callEvent(new PunishmentAppealEvent(this.profile.getId(), this));
        new PunishmentAppealedEvent(this.profile.getId(), this).track();
    }

    public boolean isStale() {
        Date today = Timestamp.now().toDate();
        Calendar cal = Calendar.getInstance();

        if (this.hasExpirationTimestamp()) {
            cal.setTime(this.expirationTimestamp.toDate());
            cal.add(Calendar.MONTH, 6);
        } else {
            cal.setTime(today);
            cal.add(Calendar.MONTH, 3);
        }

        return today.after(cal.getTime());
    }

    public Map<String, Object> toData() {
        Map<String, Object> docData = new HashMap<>();

        docData.put("id", this.id);
        docData.put("punisher", this.punisher.toString());
        docData.put("reason", this.reason);
        docData.put("timestamp", this.timestamp);
        docData.put("type", this.type.getName());
        docData.put("seen", this.seen);

        if (this.hasExpirationTimestamp()) {
            docData.put("expiration_timestamp", this.expirationTimestamp);
        }

        if (this.isAppealed()) {
            docData.put("appealed_timestamp", this.appealedTimestamp);
            docData.put("appealed_by", this.appealedBy.toString());
        }

        return docData;
    }

}
