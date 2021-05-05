package net.purelic.spring.listeners.player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.managers.AltManager;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.punishment.Punishment;
import net.purelic.spring.punishment.PunishmentType;
import net.purelic.spring.utils.ChatUtils;
import org.apache.commons.lang.WordUtils;

public class Login implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        Profile profile = ProfileManager.getProfile(event.getConnection().getUniqueId(), true);

        for (Punishment punishment : profile.getPunishments()) {
            PunishmentType type = punishment.getType();
            boolean banned = type == PunishmentType.PERMA_BAN || (type == PunishmentType.TEMP_BAN && !punishment.isExpired());

            if (banned && !punishment.isAppealed()) {
                event.setCancelled(true);
                event.setCancelReason(this.getBannedMessage(punishment));
                return;
            }
        }

        if (AltManager.track(event.getConnection())) {
            event.setCancelled(true);
            event.setCancelReason(this.getBanEvasionMessage());
        }
    }

    private TextComponent getBannedMessage(Punishment punishment) {
        PunishmentType type = punishment.getType();

        return new TextComponent(
            ChatColor.RED + "" + ChatColor.BOLD + WordUtils.capitalizeFully(type.getPastTense()) + "!\n\n" + ChatColor.RESET +
                ChatColor.RED + punishment.getReason() + "\n\n" +
                ChatColor.GRAY + (type == PunishmentType.PERMA_BAN
                ? "" : "\n\nYou will be unbanned " + ChatUtils.format(punishment.getExpirationTimestamp().toDate()) + "\n\n") +
                ChatColor.WHITE + "Please read the rules at " + ChatColor.AQUA + "purelic.net/rules\n" +
                ChatColor.WHITE + "or appeal your ban at " + ChatColor.AQUA + "purelic.net/appeal"
        );
    }

    private TextComponent getBanEvasionMessage() {
        return new TextComponent(
            ChatColor.RED + "" + ChatColor.BOLD + "Disconnected\n\n" + ChatColor.RESET +
                ChatColor.RED + "Ban Evasion\n\n" +
                ChatColor.WHITE + "Please read the rules at " + ChatColor.AQUA + "purelic.net/rules\n" +
                ChatColor.WHITE + "or appeal your ban at " + ChatColor.AQUA + "purelic.net/appeal"
        );
    }

}
