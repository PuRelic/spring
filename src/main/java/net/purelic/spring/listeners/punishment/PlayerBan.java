package net.purelic.spring.listeners.punishment;

import com.google.cloud.Timestamp;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.events.PlayerBanEvent;
import net.purelic.spring.punishment.BanUnit;
import net.purelic.spring.punishment.PunishmentType;
import net.purelic.spring.utils.ChatUtils;
import org.apache.commons.lang.WordUtils;

import java.util.Calendar;
import java.util.Date;

public class PlayerBan implements Listener {

    @EventHandler
    @SuppressWarnings("all")
    public void onPlayerBan(PlayerBanEvent event) {
        PunishmentType type = event.getType();
        BanUnit unit = event.getUnit();
        Timestamp expirationTimestamp;

        if (unit != null) {
            Date today = Timestamp.now().toDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            cal.add(unit.getUnit(), event.getDuration());
            expirationTimestamp = Timestamp.of(cal.getTime());
        } else {
            expirationTimestamp = null;
        }

        event.getPlayer().disconnect(new TextComponent(
            ChatColor.RED + "" + ChatColor.BOLD + WordUtils.capitalizeFully(type.getPastTense()) + "!\n\n" + ChatColor.RESET +
                ChatColor.RED + event.getReason() + "\n\n" +
                ChatColor.GRAY + (type == PunishmentType.PERMA_BAN || expirationTimestamp == null ?
                    "" : "\n\nYou will be unbanned " + ChatUtils.format(expirationTimestamp.toDate()) + "\n\n") +
                ChatColor.WHITE + "Please read the rules at " + ChatColor.AQUA + "purelic.net/rules\n" +
                ChatColor.WHITE + "or appeal your ban at " + ChatColor.AQUA + "purelic.net/appeal"
        ));
    }

}
