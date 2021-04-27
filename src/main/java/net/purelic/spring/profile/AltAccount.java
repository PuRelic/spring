package net.purelic.spring.profile;

import com.google.cloud.Timestamp;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.utils.ChatUtils;

import java.util.HashMap;
import java.util.Map;

public class AltAccount {

    private final String uuid;
    private String name;
    private final Timestamp firstSeen;
    private final Timestamp lastSeen;
    private final long totalLogins;

    public AltAccount(Map<String, Object> data) {
        this.uuid = (String) data.get("uuid");
        this.name = (String) data.get("name");
        this.firstSeen = (Timestamp) data.get("first_seen");
        this.lastSeen = (Timestamp) data.get("last_seen");
        this.totalLogins = (long) data.get("total_logins");
    }

    public AltAccount(ProxiedPlayer player) {
        this.uuid = player.getUniqueId().toString();
        this.name = player.getName();
        this.firstSeen = Timestamp.now();
        this.lastSeen = this.firstSeen;
        this.totalLogins = 1;
    }

    public String getId() {
        return this.uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> toData(boolean update) {
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", this.uuid);
        data.put("name", this.name);
        data.put("first_seen", this.firstSeen);
        data.put("last_seen", update ? Timestamp.now() : this.lastSeen);
        data.put("total_logins", update ? this.totalLogins + 1 : this.totalLogins);
        return data;
    }

    @SuppressWarnings("deprecation")
    public BaseComponent[] toComponent() {
        return new ComponentBuilder(ChatUtils.BULLET).color(ChatColor.GRAY)
            .append(this.name).color(ChatColor.DARK_AQUA)
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(this.uuid + "\n").color(ChatColor.GRAY)
                    .append("Name: ").color(ChatColor.GRAY).append(this.name + "\n").color(ChatColor.DARK_AQUA)
                    .append("First Seen: ").color(ChatColor.GRAY).append(ChatUtils.format(this.firstSeen) + "\n").color(ChatColor.DARK_AQUA)
                    .append("Last Seen: ").color(ChatColor.GRAY).append(ChatUtils.format(this.lastSeen) + "\n").color(ChatColor.DARK_AQUA)
                    .append("Total Logins: ").color(ChatColor.GRAY).append("" + this.totalLogins).color(ChatColor.DARK_AQUA)
                    .create()))
            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, this.uuid))
            .append(" " + ChatUtils.ARROW + " ").color(ChatColor.GRAY)
            .append("Seen " + ChatUtils.format(this.lastSeen) + " (" + this.totalLogins + " Logins)").color(ChatColor.WHITE)
            .create();
    }

}
