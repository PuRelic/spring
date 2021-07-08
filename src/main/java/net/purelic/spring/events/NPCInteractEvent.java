package net.purelic.spring.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.purelic.spring.events.constants.NPCInteractAction;

public class NPCInteractEvent extends Event {

    private final ProxiedPlayer player;
    private final String npc;
    private final NPCInteractAction action;

    public NPCInteractEvent(ProxiedPlayer player, String npc, NPCInteractAction action) {
        this.player = player;
        this.npc = npc;
        this.action = action;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public String getNPC() {
        return this.npc;
    }

    public NPCInteractAction getAction() {
        return this.action;
    }

}
