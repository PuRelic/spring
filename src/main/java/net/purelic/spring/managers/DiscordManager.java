package net.purelic.spring.managers;

import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.purelic.spring.Spring;
import net.purelic.spring.discord.Role;
import net.purelic.spring.profile.DiscordProfile;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.profile.Rank;
import net.purelic.spring.punishment.PunishmentType;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.utils.*;

import java.awt.*;
import java.util.UUID;

public class DiscordManager {

    private static final String AVATAR_URL = "https://purelic.net/siteicon.png";
    private static final String BASE_URL = "https://discordapp.com/api/webhooks/";

    private static String alertsWebhook;
    private static String supportWebhook;
    private static String reportsWebhook;
    private static String guardianWebhook;

    public static void loadDiscordWebhooks(Configuration config) {
        alertsWebhook = config.getString("discord.alerts_webhook");
        supportWebhook = config.getString("discord.support_webhook");
        reportsWebhook = config.getString("discord.reports_webhook");
        guardianWebhook = config.getString("discord.guardian_webhook");
        startTasks();
    }

    private static DiscordWebhook getWebhook(String webhook) {
        DiscordWebhook discordWebhook = new DiscordWebhook(BASE_URL + webhook);
        discordWebhook.setAvatarUrl(AVATAR_URL);
        discordWebhook.setUsername("PuRelic");
        return discordWebhook;
    }

    public static void sendServerNotification(GameServer server) {
        int online = Spring.getPlugin().getProxy().getOnlineCount();

        if (online <= 1) return;

        DiscordWebhook webhook = getWebhook(alertsWebhook);
        webhook.setContent("<@&" + Role.LOOKING_TO_PLAY + ">");
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setColor(Color.GREEN)
            .setDescription("A new server is now online!")
            .addField("Server", "/server " + server.getName(), false)
            .addField("Players Online", "" + online, false)
        );
        webhook.execute();
    }

    public static void sendSupportNotification(ProxiedPlayer player, String request) {
        GameServer server = ServerUtils.getGameServer(player);
        String serverType = server == null ? "n/a" : server.getType().getName();

        DiscordWebhook webhook = getWebhook(supportWebhook);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setColor(Color.MAGENTA)
            .addField("Request", request, false)
            .addField("Server", ServerUtils.getServerName(player), false)
            .addField("Server Type", serverType, false)
            .addField("Staff Online", "" + ServerUtils.getStaffOnline(), false)
            .setAuthor(player.getName(), "https://purelic.net/players/" + player.getName(), "https://crafatar.com/renders/head/" + player.getUniqueId().toString() + "?size=128&overlay")
        );
        webhook.execute();
    }

    public static void sendAppeal(ProxiedPlayer staff, String punishedName, UUID punishedId, String reason, PunishmentType type) {
        DiscordWebhook webhook = getWebhook(reportsWebhook);
        webhook.setAvatarUrl("https://purelic.net/siteicon.png");
        webhook.setUsername("Guardian");
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setColor(Color.GREEN)
            .addField("Type", "Appeal", false)
            .addField("Reason", reason, false)
            .addField("Severity", type.getName(), false)
            .addField("Server", ServerUtils.getServerName(staff), false)
            .setFooter("Appealed by " + staff.getName(), "https://crafatar.com/renders/head/" + staff.getUniqueId().toString() + "?size=128&overlay")
            .setAuthor(punishedName, "https://purelic.net/players/" + punishedName, "https://crafatar.com/renders/head/" + punishedId.toString() + "?size=128&overlay")
        );
        webhook.execute();
    }

    public static void sendPunishment(ProxiedPlayer punisher, Profile profile, String reason, PunishmentType type) {
        if (punisher == null) { // automatic punishment, player will always be online
            sendPunishment(profile.getPlayer(), reason, type);
            return;
        }

        String punishedName = profile.getName();
        String punishedId = profile.getId().toString();

        DiscordWebhook webhook = getWebhook(reportsWebhook);
        webhook.setAvatarUrl("https://purelic.net/siteicon.png");
        webhook.setUsername("Guardian");
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setColor(Color.RED)
            .addField("Type", "Punishment", false)
            .addField("Reason", reason, false)
            .addField("Severity", type.getName(), false)
            .addField("Server", ServerUtils.getServerName(punisher), false)
            .setFooter("Punished by " + punisher.getName(), "https://crafatar.com/renders/head/" + punisher.getUniqueId().toString() + "?size=128&overlay")
            .setAuthor(punishedName, "https://purelic.net/players/" + punishedName, "https://crafatar.com/renders/head/" + punishedId + "?size=128&overlay")
        );
        webhook.execute();
    }

    public static void sendPunishment(ProxiedPlayer punished, String reason, PunishmentType type) {
        DiscordWebhook webhook = getWebhook(reportsWebhook);
        webhook.setAvatarUrl("https://purelic.net/siteicon.png");
        webhook.setUsername("Guardian");
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setColor(Color.RED)
            .addField("Type", "Punishment", false)
            .addField("Reason", reason, false)
            .addField("Severity", type.getName(), false)
            .addField("Server", ServerUtils.getServerName(punished), false)
            .setFooter("Automatic Punishment", "https://purelic.net/siteicon.png")
            .setAuthor(punished.getName(), "https://purelic.net/players/" + punished.getName(), "https://crafatar.com/renders/head/" + punished.getUniqueId().toString() + "?size=128&overlay")
        );
        webhook.execute();
    }

    public static void sendReport(ProxiedPlayer reporter, ProxiedPlayer reported, String reason) {
        DiscordWebhook webhook = getWebhook(reportsWebhook);
        webhook.setAvatarUrl("https://purelic.net/siteicon.png");
        webhook.setUsername("Guardian");
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setColor(Color.ORANGE)
            .addField("Nickname", NickUtils.isNicked(reported) ? NickUtils.getNick(reported) : "N/A", false)
            .addField("Type", "Report", false)
            .addField("Reason", reason, false)
            .addField("Server", ServerUtils.getServerName(reported), false)
            .addField("Ping", reported.getPing() + "", false)
            .setFooter("Reported by " + reporter.getName(), "https://crafatar.com/renders/head/" + reporter.getUniqueId().toString() + "?size=128&overlay")
            .setAuthor(reported.getName(), "https://purelic.net/players/" + reported.getName(), "https://crafatar.com/renders/head/" + reported.getUniqueId().toString() + "?size=128&overlay")
        );
        webhook.execute();
    }

    private static void startTasks() {
        TaskUtils.runTimer(DiscordUtils::updateOnlineCount, 5); // refresh every 5 seconds
        TaskUtils.runTimer(DiscordUtils::updateMemberCount, 600); // refresh every 10 minutes
    }

    public static DiscordProfile getProfile(User user) {
        return new DiscordProfile(DatabaseUtils.getDiscordDoc(user));
    }

    public static UUID getLinkedId(User user) {
        String playerId = (String) DatabaseUtils.getDiscordDoc(user).get("player_uuid");
        return playerId == null ? null : UUID.fromString(playerId);
    }

    public static void syncRoles(ProxiedPlayer player, User user) {
        syncRoles(ProfileManager.getProfile(player), user);
    }

    public static void syncRoles(UUID playerId, User user) {
        syncRoles(ProfileManager.getProfile(playerId), user);
    }

    private static void syncRoles(Profile profile, User user) {
        for (Rank rank : Rank.values()) {
            if (!rank.hasDiscordRole()) continue;

            if (profile.hasRank(rank)) {
                DiscordUtils.addRole(user, rank.getDiscordRole()).queue();
            } else {
                DiscordUtils.removeRole(user, rank.getDiscordRole()).queue();
            }
        }
    }

}
