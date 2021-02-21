package net.purelic.spring.league;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.purelic.commons.Commons;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.utils.ItemAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Leaderboard {

    private static final int LIMIT = 10;

    private final Playlist playlist;
    private final String season;
    private final String id;
    private final List<LeaderboardEntry> entries;
    private DocumentReference docRef;

    public Leaderboard(Playlist playlist, String season) {
        this.playlist = playlist;
        this.season = season;
        this.id = season + "__" + playlist.getId();
        this.entries = new ArrayList<>();
        this.docRef = null;
        this.refresh();
    }

    @SuppressWarnings({ "unchecked", "ConstantConditions" })
    public void refresh() {
        if (this.docRef != null && !this.isServerOnline()) return;

        if (this.docRef == null) {
            this.docRef = Commons.getFirestore().collection("leaderboards").document(this.id);
        }

        ApiFuture<DocumentSnapshot> future = this.docRef.get();

        try {
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                Map<String, Object> data = document.getData();
                List<Map<String, Object>> leaders = (List<Map<String, Object>>) data.getOrDefault("leaders", new ArrayList<>());
                this.entries.clear();
                leaders.stream().map(LeaderboardEntry::new).forEach(this.entries::add);
                if (this.entries.size() > LIMIT) this.entries.subList(LIMIT, this.entries.size()).clear();
            }
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Failed to refresh leaderboard " + this.id);
            e.printStackTrace();
        }
    }

    private boolean isServerOnline() {
        return ServerManager.getPublicServers(this.playlist).size() > 0;
    }

    public ItemStack toItem() {
        if (this.entries.isEmpty()) return null;

        List<String> lore = new ArrayList<>();

        lore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "Refreshes every 5 minutes");
        lore.add("");

        for (int i = 0; i < this.entries.size(); i++) {
            lore.add(ChatColor.AQUA + "#" + (i + 1) + " " + this.entries.get(i).toString());
        }

        lore.add("");
        lore.add(ChatColor.AQUA + "purelic.net/leaderboards");

        ItemStack item = new ItemStack(ItemType.OAK_SIGN);
        item.setDisplayName(new ComponentBuilder(this.playlist.getName() + " Leaderboard").color(ChatColor.AQUA).bold(true).create());
        item.setLore(lore);
        ItemAction.LEADERBOARD.apply(item);
        return item;
    }

}
