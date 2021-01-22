package net.purelic.spring.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.Spring;
import net.purelic.spring.managers.InventoryManager;
import net.purelic.spring.managers.ServerManager;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class SpringPluginMessage implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("purelic:spring") || !(event.getSender() instanceof Server)) {
            return;
        }

        ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
        DataInputStream in = new DataInputStream(stream);

        try {
            String subChannel = in.readUTF();

            switch (subChannel) {
                case "ServerSelector": {
                    UUID playerId = UUID.fromString(in.readUTF());
                    ProxiedPlayer player = Spring.getPlugin().getProxy().getPlayer(playerId);
                    InventoryManager.openServerSelector(player);
                    break;
                }
                case "LeagueSelector": {
                    UUID playerId = UUID.fromString(in.readUTF());
                    ProxiedPlayer player = Spring.getPlugin().getProxy().getPlayer(playerId);
                    InventoryManager.openLeagueSelector(player);
                    break;
                }
                case "PrivateServer": {
                    UUID playerId = UUID.fromString(in.readUTF());
                    ProxiedPlayer player = Spring.getPlugin().getProxy().getPlayer(playerId);
                    InventoryManager.openPrivateServerInv(player);
                    break;
                }
                case "RemoveServer": {
                    String name = in.readUTF();
                    ServerManager.removeServer(name);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
