package net.purelic.spring.profile;

import com.google.cloud.firestore.FieldValue;
import net.purelic.spring.managers.LeagueManager;
import net.purelic.spring.managers.PlaylistManager;
import net.purelic.spring.party.Party;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.utils.DatabaseUtils;

import java.util.*;

public class Profile {

    private static final Long STARTING_ELO = 250L;

    private final UUID uuid;
    private final Set<Rank> ranks;
    private final Map<String, Object> stats;
    private boolean betaFeatures;
    private Playlist playlist;

    @SuppressWarnings("unchecked")
    public Profile(UUID uuid, Map<String, Object> data) {
        this.uuid = uuid;
        this.ranks = Rank.parseRanks((List<Object>) data.getOrDefault(Rank.PATH, new ArrayList<>()));
        this.stats = (Map<String, Object>) data.getOrDefault("stats", new HashMap<>());
        this.betaFeatures = (boolean) this.getPreference(Preference.BETA_FEATURES, data, false);
        this.playlist = this.getPlaylist(data);
    }

    public Set<Rank> getRanks() {
        return this.ranks;
    }

    public boolean hasBetaFeatures() {
        return this.betaFeatures;
    }

    public void toggleBetaFeatures() {
        this.betaFeatures = !this.betaFeatures;
        DatabaseUtils.updatePlayerDoc(this.uuid, Preference.BETA_FEATURES.getFullPath(), this.betaFeatures);
    }

    public Playlist getPlaylist() {
        if (!PlaylistManager.isAvailable(this.playlist)) this.playlist = PlaylistManager.getDefaultPlaylist();
        return this.playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        DatabaseUtils.updatePlayerDoc(this.uuid, Preference.PLAYLIST.getFullPath(), playlist.getName());
    }

    private Playlist getPlaylist(Map<String, Object> data) {
        String playlistValue = (String) this.getPreference(Preference.PLAYLIST, data, PlaylistManager.getDefaultPlaylist().getName());
        Playlist playlist = PlaylistManager.getPlaylist(playlistValue);
        return playlist == null || playlist.isArchived() ? PlaylistManager.getDefaultPlaylist() : playlist;
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    public Map<String, Object> getStats(StatSection section) {
        return (Map<String, Object>) this.stats.getOrDefault(section.getKey(), new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getTotalStats(StatSection section) {
        return (Map<String, Object>) this.getStats(section).getOrDefault("total", new HashMap<>());
    }

    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();

        List<String> ranks = new ArrayList<>();
        this.ranks.forEach(rank -> ranks.add(rank.getName()));
        data.put(Rank.PATH, ranks);

        Map<String, Object> preferences = new HashMap<>();
        preferences.put(Preference.BETA_FEATURES.getKey(), this.betaFeatures);
        preferences.put(Preference.PLAYLIST.getKey(), this.playlist.getName());
        data.put(Preference.PATH, preferences);

        data.put("stats", this.stats);

        return data;
    }

    @SuppressWarnings("unchecked")
    public int getRating(Playlist pl) {
        Map<String, Object> ranked = (Map<String, Object>) this.stats.getOrDefault("ranked", new HashMap<>());
        Map<String, Object> season = (Map<String, Object>) ranked.getOrDefault(LeagueManager.getCurrentSeason().getId(), new HashMap<>());
        Map<String, Object> playlist = (Map<String, Object>) season.getOrDefault(pl.getId(), new HashMap<>());
        Long rating = (Long) playlist.getOrDefault("rating", STARTING_ELO);
        return rating.intValue();
    }

}
