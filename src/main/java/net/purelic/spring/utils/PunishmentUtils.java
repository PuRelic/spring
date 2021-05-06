package net.purelic.spring.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.events.PlayerBanEvent;
import net.purelic.spring.events.PlayerKickEvent;
import net.purelic.spring.events.PlayerPunishEvent;
import net.purelic.spring.events.PlayerWarnEvent;
import net.purelic.spring.managers.DiscordManager;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.punishment.BanUnit;
import net.purelic.spring.punishment.PunishmentType;

import java.util.UUID;

public class PunishmentUtils {

    private static final UUID PURELIC_UUID = UUID.fromString("57014d5f-1d26-4986-832b-a0e7a4e41088");

    public static void autoBan(ProxiedPlayer player, String reason) {
        punishPlayer(null, ProfileManager.getProfile(player), reason, PunishmentType.PERMA_BAN);
    }

    public static void punishPlayer(ProxiedPlayer player, Profile profile, String reason) {
        punishPlayer(player, profile, reason, null);
    }

    public static void punishPlayer(ProxiedPlayer player, Profile profile, String reason, PunishmentType type) {
        punishPlayer(player, profile, reason, type, 0, null);
    }

    public static void punishPlayer(ProxiedPlayer punisher, Profile profile, String reason, PunishmentType type, int duration, BanUnit unit) {
        if (PermissionUtils.isStaff(profile.getId())) {
            if (punisher != null) CommandUtils.sendErrorMessage(punisher, "You cannot punish other staff members!");
            return;
        }

        // Get the next punishment severity if one isn't set
        if (type == null) {
            type = profile.getNextPunishmentSeverity();

            // default temp ban is 7 days
            if (type == PunishmentType.TEMP_BAN) {
                duration = 7;
                unit = BanUnit.DAY;
            }
        }

        // Call generic punishment event
        Spring.callEvent(new PlayerPunishEvent(profile, punisher == null ? PURELIC_UUID : punisher.getUniqueId(), reason, type, duration, unit));

        // Call punishment specific events if player is online
        if (profile.isOnline()) {
            ProxiedPlayer player = profile.getPlayer();

            if (type == PunishmentType.WARN) Spring.callEvent(new PlayerWarnEvent(player, reason, true));
            else if (type == PunishmentType.KICK) Spring.callEvent(new PlayerKickEvent(player, reason));
            else Spring.callEvent(new PlayerBanEvent(player, reason, type, duration, unit));
        }

        // Broadcast to Discord
        DiscordManager.sendPunishment(punisher, profile, reason, type);

        // Send success message
        if (punisher != null) {
            CommandUtils.sendSuccessMessage(punisher,
                new ComponentBuilder("You successfully punished ").color(ChatColor.GREEN)
                    .append(profile.getName()).color(ChatColor.DARK_AQUA)
                    .append("!").color(ChatColor.GREEN)
                    .append(" (" + type.getPastTense() + ")").color(ChatColor.GRAY)
                    .create());
        }
    }

}
