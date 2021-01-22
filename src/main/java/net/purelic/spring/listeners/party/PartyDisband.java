package net.purelic.spring.listeners.party;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.events.PartyDisbandEvent;
import net.purelic.spring.party.Party;

public class PartyDisband implements Listener {

    @EventHandler
    public void onPartyDisband(PartyDisbandEvent event) {
        Party party = event.getParty();
    }

}
