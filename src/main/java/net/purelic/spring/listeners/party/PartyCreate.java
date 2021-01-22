package net.purelic.spring.listeners.party;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.events.PartyCreateEvent;
import net.purelic.spring.party.Party;

public class PartyCreate implements Listener {

    @EventHandler
    public void onPartyCreate(PartyCreateEvent event) {
        Party party = event.getParty();
        ProxiedPlayer player = event.getPlayer();
    }

}
