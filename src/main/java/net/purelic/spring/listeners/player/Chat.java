package net.purelic.spring.listeners.player;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.analytics.events.ChatSentEvent;
import net.purelic.spring.utils.PunishmentUtils;

public class Chat implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
            new ChatSentEvent(sender, event).track();

            if (event.getMessage().toLowerCase().contains("zenhax")) {
                PunishmentUtils.autoBan(sender, "Ban Evasion");
            }
        }
    }

}
