package net.purelic.spring.listeners.server;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.events.NPCInteractEvent;
import net.purelic.spring.managers.InventoryManager;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.utils.CommandUtils;

public class NPCInteract implements Listener {

    @EventHandler
    public void onNPCInteract(NPCInteractEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String npc = event.getNPC();

        switch (npc) {
            case "CasualGames":
                InventoryManager.openServerSelector(player);
                break;
            case "CustomGames":
                if (ServerManager.getPrivateServers(false).size() > 0) {
                    InventoryManager.openPrivateServerSelector(player);
                } else {
                    CommandUtils.sendErrorMessage(player, "There are currently no custom game servers online!");
                }
                break;
            case "PrivateServer":
                InventoryManager.openPrivateServerInv(player);
                break;
        }
    }

}
