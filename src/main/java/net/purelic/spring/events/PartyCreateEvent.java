package net.purelic.spring.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;
import net.purelic.spring.party.Party;

public class PartyCreateEvent extends Event {

    private final Party party;
    private final ProxiedPlayer player;

    public PartyCreateEvent(Party party, ProxiedPlayer player) {
        this.party = party;
        this.player = player;
    }

    public Party getParty() {
        return this.party;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

}
