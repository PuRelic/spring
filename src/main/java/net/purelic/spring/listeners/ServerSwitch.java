package net.purelic.spring.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.Spring;

public class ServerSwitch implements Listener {

    @EventHandler
    public void onServerSwitch(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (player.getServer() == null) return;

        Spring.sendPluginMessage(
                player,
                "QuitMessage",
                player.getUniqueId().toString(),
                event.getServer().getInfo().getName());
    }

}
