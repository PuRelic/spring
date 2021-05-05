package net.purelic.spring.profile;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.FieldValue;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.LeagueManager;
import net.purelic.spring.party.Party;
import net.purelic.spring.profile.stats.StatSection;
import net.purelic.spring.punishment.Punishment;
import net.purelic.spring.punishment.PunishmentType;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.DatabaseUtils;

import java.util.*;

@SuppressWarnings("unchecked")
public class Profile {

    private static final Long STARTING_ELO = 250L;

    private final UUID uuid;
    private final String name;
    private final List<Rank> ranks;
    private final Map<String, Object> stats;
    private final List<Map<String, Object>> matches;
    private final List<Map<String, Object>> punishmentsData;
    private final Timestamp joined;
    private boolean betaFeatures;
    private boolean discordLinked;

    // punishments
    private final List<Punishment> punishments;
    private final Map<String, Punishment> punishmentMap;

    public Profile(UUID uuid, Map<String, Object> data) {
        this.uuid = uuid;
        this.name = (String) data.get("name");
        this.ranks = Rank.parseRanks((List<Object>) data.getOrDefault(Rank.PATH, new ArrayList<>()));
        this.stats = (Map<String, Object>) data.getOrDefault("stats", new HashMap<>());
        this.matches = (List<Map<String, Object>>) data.getOrDefault("recent_matches", new ArrayList<>());
        this.joined = (Timestamp) data.getOrDefault("joined", Timestamp.now());
        this.betaFeatures = (boolean) this.getPreference(Preference.BETA_FEATURES, data, false);
        this.discordLinked = (boolean) data.getOrDefault("discord_linked", false);
        this.punishmentsData = (List<Map<String, Object>>) data.getOrDefault("punishments", new ArrayList<>());
        this.punishments = new ArrayList<>();
        this.punishmentMap = new HashMap<>();
        this.setPunishments(this.punishmentsData);
    }

    public UUID getId() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public List<Rank> getRanks() {
        return this.ranks;
    }

    public boolean hasRank(Rank... ranks) {
        for (Rank rank : ranks) {
            if (this.ranks.contains(rank)) return true;
        }

        return false;
    }

    public boolean hasBetaFeatures() {
        return this.betaFeatures;
    }

    public void toggleBetaFeatures() {
        this.betaFeatures = !this.betaFeatures;
        DatabaseUtils.updatePlayerDoc(this.uuid, Preference.BETA_FEATURES.getFullPath(), this.betaFeatures);
    }

    private Object getPreference(Preference preference, Map<String, Object> data, Object defaultValue) {
        Map<String, Object> preferences = (Map<String, Object>) data.getOrDefault(Preference.PATH, new HashMap<>());
        return preferences.getOrDefault(preference.getKey(), defaultValue);
    }

    public void setParty(Party party) {
        if (party == null) DatabaseUtils.updatePlayerDoc(this.uuid, "party_id", FieldValue.delete());
        else DatabaseUtils.updatePlayerDoc(this.uuid, "party_id", party.getId());
    }

    public Map<String, Object> getStats() {
        return this.stats;
    }

    public Map<String, Object> getStats(StatSection section) {
        return (Map<String, Object>) this.stats.getOrDefault(section.getKey(), new HashMap<>());
    }

    public Map<String, Object> getTotalStats(StatSection section) {
        return (Map<String, Object>) this.getStats(section).getOrDefault("total", new HashMap<>());
    }

    public List<Map<String, Object>> getMatches() {
        return this.matches;
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        List<String> ranks = new ArrayList<>();
        this.ranks.forEach(rank -> ranks.add(rank.getName()));
        data.put(Rank.PATH, ranks);

        Map<String, Object> preferences = new HashMap<>();
        preferences.put(Preference.BETA_FEATURES.getKey(), this.betaFeatures);
        data.put(Preference.PATH, preferences);

        data.put("name", this.name);
        data.put("stats", this.stats);
        data.put("recent_matches", this.matches);
        data.put("punishments", this.punishmentsData);
        data.put("joined", this.joined);
        data.put("beta_features", this.betaFeatures);
        data.put("discord_linked", this.discordLinked);

        return data;
    }

    public Map<String, Object> getRankedStats(Playlist pl) {
        Map<String, Object> ranked = (Map<String, Object>) this.stats.getOrDefault("ranked", new HashMap<>());
        Map<String, Object> season = (Map<String, Object>) ranked.getOrDefault(LeagueManager.getCurrentSeason().getId(), new HashMap<>());
        return (Map<String, Object>) season.getOrDefault(pl.getId(), new HashMap<>());
    }

    public int getRating(Playlist playlist) {
        Long rating = (Long) this.getRankedStats(playlist).getOrDefault("rating", STARTING_ELO);
        return rating.intValue();
    }

    public int getWinStreak(Playlist playlist) {
        Long winStreak = (Long) this.getRankedStats(playlist).getOrDefault("win_streak", 0L);
        return winStreak.intValue();
    }

    public void setSessionId(UUID sessionId) {
        if (sessionId == null) DatabaseUtils.updatePlayerDoc(this.uuid, "session_id", FieldValue.delete());
        else DatabaseUtils.updatePlayerDoc(this.uuid, "session_id", sessionId.toString());
    }

    public Timestamp getJoined() {
        return this.joined;
    }

    public boolean hasDiscordLinked() {
        return this.discordLinked;
    }

    public void setDiscordLinked(boolean discordLinked) {
        this.discordLinked = discordLinked;
        DatabaseUtils.updatePlayerDoc(this.uuid, "discord_linked", this.discordLinked);
    }

    public void setPunishments(List<Map<String, Object>> punishments) {
        for (Map<String, Object> punishmentData : punishments) {
            Punishment punishment = new Punishment(this, punishmentData);
            this.punishments.add(punishment);
            this.punishmentMap.put(punishment.getPunishmentId(), punishment);
        }
    }

    public void addPunishment(Punishment punishment) {
        this.punishments.add(punishment);
        this.punishmentMap.put(punishment.getPunishmentId(), punishment);
    }

    public void appealPunishment(ProxiedPlayer appellant, String id) {
        Punishment punishment = this.getPunishment(id);

        if (punishment == null) {
            CommandUtils.sendErrorMessage(appellant, "Could not find a punishment with that id!");
        } else {
            punishment.appeal(appellant.getUniqueId());
        }
    }

    public List<Punishment> getPunishments() {
        return this.punishments;
    }

    public Punishment getPunishment(String id) {
        return this.punishmentMap.get(id);
    }

    public void updatePunishments() {
        List<Map<String, Object>> punishments = new ArrayList<>();

        for (Punishment punishment : this.punishments) {
            punishments.add(punishment.toData());
        }

        Map<String, Object> values = new HashMap<>();
        values.put("punishments", punishments);

        DatabaseUtils.updatePlayerDoc(this.uuid, values);
    }

    public PunishmentType getNextPunishmentSeverity() {
        PunishmentType type = PunishmentType.WARN;

        for (Punishment punishment : this.getPunishments()) {
            if (punishment.isAppealed() || punishment.isStale()) continue;

            switch (punishment.getType()) {
                case PERMA_BAN:
                case TEMP_BAN:
                    type = PunishmentType.PERMA_BAN;
                    break;
                case KICK:
                    type = PunishmentType.TEMP_BAN;
                    break;
                case WARN:
                    type = PunishmentType.KICK;
                    break;
            }
        }

        return type;
    }

}
