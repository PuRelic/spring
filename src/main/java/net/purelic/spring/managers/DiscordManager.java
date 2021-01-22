package net.purelic.spring.managers;

import net.md_5.bungee.config.Configuration;
import net.purelic.spring.Spring;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.utils.DiscordWebhook;

import java.awt.*;
import java.io.IOException;

public class DiscordManager {

    private static final String AVATAR_URL = "https://purelic.net/siteicon.png";
    private static final String BASE_URL = "https://discordapp.com/api/webhooks/";

    private static String serversWebhook;

    public static void loadDiscordWebhooks(Configuration config) {
        serversWebhook = config.getString("discord_webhooks.servers");
    }

    public static void sendServerNotification(GameServer server) {
        try {
            DiscordWebhook webhook = new DiscordWebhook(BASE_URL + serversWebhook);
            webhook.setAvatarUrl(AVATAR_URL);
            webhook.setUsername("PuRelic");
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

}
