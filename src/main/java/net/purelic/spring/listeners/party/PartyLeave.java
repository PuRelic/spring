package net.purelic.spring.listeners.party;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.events.PartyLeaveEvent;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.party.Party;

public class PartyLeave implements Listener {

    @EventHandler
    public void onPartyLeave(PartyLeaveEvent event) {
        Party party = event.getParty();
        ProxiedPlayer player = event.getPlayer();
        ProfileManager.getProfile(player).setParty(null);
    }

}
