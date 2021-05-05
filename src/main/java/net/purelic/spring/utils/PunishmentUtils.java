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
        punishPlayer(null, player.getUniqueId().toString(), reason, PunishmentType.PERMA_BAN);
    }

    public static void punishPlayer(ProxiedPlayer player, String target, String reason) {
        punishPlayer(player, target, reason, null);
    }

    public static void punishPlayer(ProxiedPlayer player, String target, String reason, PunishmentType type) {
        punishPlayer(player, target, reason, type, 0, null);
    }

    public static void punishPlayer(ProxiedPlayer player, String target, String reason, PunishmentType type, int duration, BanUnit unit) {
        ProxiedPlayer targetPlayer = Spring.getPlayer(target);
        boolean targetOnline = targetPlayer != null;

        // Automated punishments (no player/punisher)
        if (player == null && targetPlayer != null) {
            // Ignore automatic punishments if it's for a staff member
            if (PermissionUtils.isStaff(targetPlayer)) {
                return;
            }

            Profile targetProfile = ProfileManager.getProfile(targetPlayer);

            // Get the next punishment severity if one isn't set
            if (type == null) {
                type = targetProfile.getNextPunishmentSeverity();

                if (type == PunishmentType.TEMP_BAN) {
                    duration = 7;
                    unit = BanUnit.DAY;
                }
            }

            // Call generic punishment event
            Spring.callEvent(new PlayerPunishEvent(targetPlayer, targetProfile, PURELIC_UUID, reason, type, duration, unit));

            // Call punishment specific events
            if (type == PunishmentType.WARN) Spring.callEvent(new PlayerWarnEvent(targetPlayer, reason, true));
            else if (type == PunishmentType.KICK) Spring.callEvent(new PlayerKickEvent(targetPlayer, reason));
            else Spring.callEvent(new PlayerBanEvent(targetPlayer, reason, type, duration, unit));

            // Broadcast to Discord
            DiscordManager.sendPunishment(targetPlayer, reason, type);
        } else if (!targetOnline && player != null) { // Issuing punishment to an offline player
            UUID targetId = Fetcher.getUUIDOf(target);

            // Make sure the target uuid is valid
            if (targetId == null) {
                CommandUtils.sendNoPlayerMessage(player, target);
                return;
            }

            // Disallow punishing other staff
            if (PermissionUtils.isStaff(targetId)) {
                CommandUtils.sendErrorMessage(player, "You cannot punish other staff members!");
                return;
            }

            Profile profile = ProfileManager.getProfile(targetId);

            // Get the next punishment severity if one isn't set
            if (type == null) {
                type = profile.getNextPunishmentSeverity();

                if (type == PunishmentType.TEMP_BAN) {
                    duration = 7;
                    unit = BanUnit.DAY;
                }
            }

            // Call generic punishment event
            Spring.callEvent(new PlayerPunishEvent(targetId, player, reason, type, duration, unit));

            // Broadcast to Discord
            DiscordManager.sendPunishment(player, Fetcher.getNameOf(targetId), targetId, reason, type);

            // Send success message
            CommandUtils.sendSuccessMessage(player,
                new ComponentBuilder("You successfully punished ").color(ChatColor.GREEN)
                    .append(profile.getName()).color(ChatColor.DARK_AQUA)
                    .append("!").color(ChatColor.GREEN)
                    .append(" (" + type.getPastTense() + ")").color(ChatColor.GRAY)
                    .create());
        } else { // punishing an online player
            if (player == null) return; // shouldn't happen


            // Disallow punishing other staff Mmembers
            if (PermissionUtils.isStaff(targetPlayer)) {
                CommandUtils.sendErrorMessage(player, "You cannot punish other staff members!");
                return;
            }

            Profile profile = ProfileManager.getProfile(targetPlayer);

            // Get the next punishment severity if one isn't set
            if (type == null) {
                type = profile.getNextPunishmentSeverity();
                if (type == PunishmentType.TEMP_BAN) {
                    duration = 7;
                    unit = BanUnit.DAY;
                }
            }

            // Call generic punishment event
            Spring.callEvent(new PlayerPunishEvent(targetPlayer, profile, player, reason, type, duration, unit));

            // Call punishment specific events
            if (type == PunishmentType.WARN) Spring.callEvent(new PlayerWarnEvent(targetPlayer, reason, true));
            else if (type == PunishmentType.KICK) Spring.callEvent(new PlayerKickEvent(targetPlayer, reason));
            else Spring.callEvent(new PlayerBanEvent(targetPlayer, reason, type, duration, unit));

            // Broadcast to Discord
            DiscordManager.sendPunishment(player, targetPlayer.getName(), targetPlayer.getUniqueId(), reason, type);
        }
    }

}
