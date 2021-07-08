package net.purelic.spring.listeners.server;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.Spring;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.utils.ServerUtils;
import net.purelic.spring.utils.TaskUtils;

import java.util.List;
import java.util.stream.Collectors;

public class NPCUpdater implements Listener {

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {
        TaskUtils.scheduleTask(() -> this.updateNPCs(event.getPlayer()), 3);
    }

    @EventHandler
    public void onServerDisconnect(ServerDisconnectEvent event) {
        TaskUtils.scheduleTask(() -> this.updateNPCs(event.getPlayer()), 3);
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        TaskUtils.scheduleTask(() -> this.updateNPCs(event.getPlayer()), 3);
    }

    private void updateNPCs(ProxiedPlayer player) {
        List<GameServer> publicServers = ServerManager.getPublicServers(false);
        int total = ServerUtils.totalPlaying(publicServers);
        int competitiveGames = ServerUtils.totalPlaying(publicServers.stream().filter(GameServer::isRanked).collect(Collectors.toList()));
        int casualGames = total - competitiveGames;
        int customGames = ServerUtils.totalPlaying(ServerManager.getPrivateServers(false));

        this.sendUpdateMessage(player, "CompetitiveGames", "Competitive Games", competitiveGames);
        this.sendUpdateMessage(player, "CasualGames", "Casual Games", casualGames);
        this.sendUpdateMessage(player, "CustomGames", "Custom Games", customGames);
    }

    private void sendUpdateMessage(ProxiedPlayer player, String npc, String title, int playing) {
        Spring.sendPluginMessage(player, "UpdateNPC", npc, "&b&l" + title + "\n" + (playing == 0 ? "&7" : "&f") + playing + " Playing");
    }

}
