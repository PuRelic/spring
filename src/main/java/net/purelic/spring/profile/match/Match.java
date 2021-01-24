package net.purelic.spring.profile.match;

import com.google.cloud.Timestamp;
import de.exceptionflug.protocolize.items.ItemStack;
import net.md_5.bungee.api.ChatColor;
import net.purelic.spring.utils.ItemAction;
import org.ocpsoft.prettytime.PrettyTime;

import java.time.LocalTime;
import java.util.*;

public class Match {

    private static final PrettyTime PT = new PrettyTime();

    private final String id;
    private final MatchResult result;
    private final Timestamp started;
    private final int length;
    private final GameType gameType;
    private final TeamType teamType;
    private final String playlist;
    private final String map;
    private final String gameMode;
    private final String place;
    private final boolean showScores;
    private final String teamId;
    private final List<MatchTeam> teams;

    @SuppressWarnings("unchecked")
    public Match(Map<String, Object> data) {
        this.id = (String) data.get("match_id");
        this.result = MatchResult.valueOf(((String) data.get("match_result")).toUpperCase());
        this.started = (Timestamp) data.get("started");
        this.length = ((Long) data.get("length")).intValue();
        this.gameType = GameType.valueOf(((String) data.get("game_type")).toUpperCase());
        this.teamType = TeamType.valueOf(((String) data.get("team_type")).toUpperCase());
        this.playlist = (String) data.get("playlist");
        this.map = (String) data.get("map");
        this.gameMode = (String) data.get("game_mode");
        this.place = data.get("place") + (String) data.get("place_suffix");
        this.showScores = (boolean) data.get("show_scores");
        this.teamId = (String) data.get("team");
        this.teams = new ArrayList<>();

        List<Map<String, Object>> scores = (List<Map<String, Object>>) data.getOrDefault("scores", new ArrayList<>());
        scores.forEach(score -> this.teams.add(new MatchTeam(score)));
        this.teams.sort(Comparator.comparingInt(MatchTeam::getScore));
        Collections.reverse(this.teams);
    }

    public ItemStack toItem() {
        ItemStack item = new ItemStack(this.result.getItemType());
        item.setDisplayName("" + ChatColor.BOLD + ChatColor.YELLOW + this.gameMode + ChatColor.WHITE + " on " + ChatColor.GOLD + this.map);
        List<String> lore = new ArrayList<>();
        lore.add(this.result.getName() + " " + PT.format(this.started.toDate()));
        lore.add(ChatColor.GRAY + "Length: " + (" " + LocalTime.ofSecondOfDay(this.length).toString()).replaceFirst(" 00:", ""));
        lore.add("");
        lore.add(ChatColor.GRAY + "Playlist: " + ChatColor.AQUA + this.playlist);
        lore.add(ChatColor.GRAY + "Game Type: " + ChatColor.AQUA + this.gameType.toString());

        if (this.teamType == TeamType.SOLO) {
            lore.add(ChatColor.GRAY + "Place: " + ChatColor.AQUA + this.place + " Place");
        } else {
            lore.add("");
            lore.add(ChatColor.GRAY + (this.showScores ? "Scores" : "Teams") + ":");

            for (MatchTeam team : this.teams) {
                boolean onTeam = this.teamId.equals(team.getId());
                String value = team.getName();
                if (this.showScores) value += ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + team.getScore();
                if (onTeam) value += ChatColor.GRAY + " (You)";
                lore.add(value);
            }
        }

        lore.add("");
        lore.add(ChatColor.WHITE + "Click to view online");
        item.setLore(lore);
        ItemAction.MATCH.apply(item, this.id);
        return item;
    }

    private String indexToPlace(int index) {
        int place = index + 1;
        return place + this.getPlaceSuffix(place);
    }

    private String getPlaceSuffix(int place) {
        if (place == 1 || (place % 10 == 1 && place > 20)) return "st";
        else if (place == 2 || (place % 10 == 2 && place > 20)) return "nd";
        else if (place == 3 || (place % 10 == 3 && place > 20)) return "rd";
        else return "th";
    }

}
