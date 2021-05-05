package net.purelic.spring.listeners.server;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.Spring;
import net.purelic.spring.managers.InventoryManager;
import net.purelic.spring.utils.PunishmentUtils;

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
            UUID playerId = UUID.fromString(in.readUTF());
            ProxiedPlayer player = Spring.getPlugin().getProxy().getPlayer(playerId);

            switch (subChannel) {
                case "ServerSelector": {
                    InventoryManager.openServerSelector(player);
                    break;
                }
                case "LeagueSelector": {
                    InventoryManager.openLeagueSelector(player);
                    break;
                }
                case "PrivateServer": {
                    InventoryManager.openPrivateServerInv(player);
                    break;
                }
                case "ViewStats": {
                    InventoryManager.openStatsMenu(player);
                    break;
                }
                case "ViewMatches": {
                    InventoryManager.openMatchesMenu(player);
                    break;
                }
                case "AutoBan": {
                    String reason = in.readUTF();
                    PunishmentUtils.autoBan(player, reason);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
