package net.purelic.spring.listeners.party;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.events.PartyJoinEvent;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.party.Party;

public class PartyJoin implements Listener {

    @EventHandler
    public void onPartyJoin(PartyJoinEvent event) {
        Party party = event.getParty();
        ProxiedPlayer player = event.getPlayer();
        ProfileManager.getProfile(player).setParty(party);
    }

}
