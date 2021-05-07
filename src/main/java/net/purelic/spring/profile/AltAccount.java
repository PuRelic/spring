package net.purelic.spring.profile;

import com.google.cloud.Timestamp;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.purelic.spring.Spring;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.ServerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AltAccount {

    private final UUID uuid;
    private String name;
    private Timestamp firstSeen;
    private Timestamp lastSeen;
    private long totalLogins;

    public AltAccount(String uuid, Map<String, Object> data) {
        this.uuid = UUID.fromString(uuid);
        this.name = (String) data.get("name");
        this.firstSeen = (Timestamp) data.get("first_seen");
        this.lastSeen = (Timestamp) data.get("last_seen");
        this.totalLogins = (long) data.get("total_logins");
    }

    public AltAccount(String uuid, String name) {
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.firstSeen = Timestamp.now();
        this.lastSeen = this.firstSeen;
        this.totalLogins = 1;
    }

    public UUID getId() {
        return this.uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getFirstSeen() {
        return this.firstSeen;
    }

    public Timestamp getLastSeen() {
        return this.lastSeen;
    }

    public long getTotalLogins() {
        return this.totalLogins;
    }

    public Map<String, Object> toData(boolean update) {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", this.uuid.toString());
        data.put("name", this.name);
        data.put("first_seen", this.firstSeen);
        data.put("last_seen", update ? Timestamp.now() : this.lastSeen);
        data.put("total_logins", update ? this.totalLogins + 1 : this.totalLogins);
        return data;
    }

    public AltAccount merge(AltAccount altAccount) {
        if (this.firstSeen.getSeconds() > altAccount.getFirstSeen().getSeconds()) {
            this.firstSeen = altAccount.getFirstSeen();
        }

        if (this.lastSeen.getSeconds() < altAccount.getLastSeen().getSeconds()) {
            this.lastSeen = altAccount.getLastSeen();
        }

        this.totalLogins += altAccount.getTotalLogins();

        return this;
    }

    @SuppressWarnings("deprecation")
    public BaseComponent[] toComponent() {
        boolean online = Spring.isOnline(this.uuid);
        String status = online ?
            "Online " + ChatColor.AQUA + ServerUtils.getServerName(Spring.getPlayer(this.uuid)) :
            "Seen " + ChatUtils.format(this.lastSeen);

        return new ComponentBuilder(ChatUtils.BULLET).color(ChatColor.GRAY)
            .append(this.name).color(online ? ChatColor.AQUA : ChatColor.DARK_AQUA)
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(this.uuid + "\n").color(ChatColor.GRAY)
                    .append("Name: ").color(ChatColor.GRAY).append(this.name + "\n").color(ChatColor.DARK_AQUA)
                    .append("First Seen: ").color(ChatColor.GRAY).append(ChatUtils.format(this.firstSeen) + "\n").color(ChatColor.DARK_AQUA)
                    .append("Last Seen: ").color(ChatColor.GRAY).append(ChatUtils.format(this.lastSeen) + "\n").color(ChatColor.DARK_AQUA)
                    .append("Total Logins: ").color(ChatColor.GRAY).append("" + this.totalLogins).color(ChatColor.DARK_AQUA)
                    .create()))
            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, this.uuid.toString()))
            .append(" " + ChatUtils.ARROW + " ").color(ChatColor.GRAY)
            .append(ChatColor.WHITE + status + ChatColor.GRAY + " (" + this.totalLogins + " Logins)")
            .create();
    }

}
