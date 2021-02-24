package net.purelic.spring.listeners.server;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.purelic.spring.Spring;

public class ServerKick implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerKick(ServerKickEvent e) {
        ServerInfo hub = Spring.getPlugin().getProxy().getServerInfo("Hub");

        if (hub == null) {
            System.out.println("Unable to find the specified fallback server!");
        } else {
            String reason = BaseComponent.toLegacyText(e.getKickReasonComponent());

            if ((e.getState() == ServerKickEvent.State.CONNECTED && !reason.contains("rules"))
                || (e.getState() == ServerKickEvent.State.CONNECTING)) {
                if (e.getCancelServer() != hub || reason.contains("closed") || reason.contains("shutdown") || reason.contains("You were") || reason.contains("whitelist")) {
                    e.setCancelServer(hub);
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(new TextComponent(reason));
                }
            }
        }
    }

}
