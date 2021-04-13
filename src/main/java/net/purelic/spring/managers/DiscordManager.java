package net.purelic.spring.managers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.purelic.spring.Spring;
import net.purelic.spring.discord.Role;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.utils.DiscordUtils;
import net.purelic.spring.utils.DiscordWebhook;
import net.purelic.spring.utils.ServerUtils;
import net.purelic.spring.utils.TaskUtils;

import java.awt.*;
import java.io.IOException;

public class DiscordManager {

    private static final String AVATAR_URL = "https://purelic.net/siteicon.png";
    private static final String BASE_URL = "https://discordapp.com/api/webhooks/";

    private static String alertsWebhook;
    private static String supportWebhook;

    public static void loadDiscordWebhooks(Configuration config) {
        alertsWebhook = config.getString("discord.alerts_webhook");
        supportWebhook = config.getString("discord.support_webhook");
        startTasks();
    }

    private static DiscordWebhook getWebhook(String webhook) {
        DiscordWebhook discordWebhook = new DiscordWebhook(BASE_URL + webhook);
        discordWebhook.setAvatarUrl(AVATAR_URL);
        discordWebhook.setUsername("PuRelic");
        return discordWebhook;
    }

    public static void sendServerNotification(GameServer server) {
        try {
            DiscordWebhook webhook = getWebhook(alertsWebhook);
            webhook.setContent("<@&" + Role.LOOKING_TO_PLAY + ">");
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setColor(Color.GREEN)
                    .setDescription("A new server is now online!")
                    .addField("Server", "/server " + server.getName(), false)
                    .addField("Players Online", "" + Spring.getPlugin().getProxy().getOnlineCount(), false)
            );
            webhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendSupportNotification(ProxiedPlayer player, String request) {
        try {
            GameServer server = ServerUtils.getGameServer(player);
            String serverType = server == null ? "n/a" : server.getType().getName();

            DiscordWebhook webhook = getWebhook(supportWebhook);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setColor(Color.MAGENTA)
                .addField("Request", request, false)
                .addField("Server", player.getServer().getInfo().getName(), false)
                .addField("Server Type", serverType, false)
                .addField("Staff Online", "" + ServerUtils.getStaffOnline(), false)
                .setAuthor(player.getName(), "https://purelic.net/players/" + player.getName(), "https://crafatar.com/renders/head/" + player.getUniqueId().toString() + "?size=128&overlay")
            );
            webhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startTasks() {
        TaskUtils.runTimer(DiscordUtils::updateOnlineCount, 5); // refresh every 5 seconds
        TaskUtils.runTimer(DiscordUtils::updateMemberCount, 600); // refresh every 10 minutes
    }

}
