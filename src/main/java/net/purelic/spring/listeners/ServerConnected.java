package net.purelic.spring.listeners;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.Spring;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.managers.ServerManager;

public class ServerConnected implements Listener {

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();

        ServerInfo to = event.getServer().getInfo();
        boolean toHub = to.getName().equals("Hub");

        if (toHub) {
            ProfileManager.reloadProfile(player);
        }

        if (player.getServer() == null) return;

        ServerManager.setLastServer(player);

        Spring.sendPluginMessage(
                player,
                "QuitMessage",
                player.getUniqueId().toString(),
                to.getName());
    }

}
