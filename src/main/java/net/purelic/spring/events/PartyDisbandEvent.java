package net.purelic.spring.events;

import net.md_5.bungee.api.plugin.Event;
import net.purelic.spring.party.Party;

public class PartyDisbandEvent extends Event {

    private final Party party;

    public PartyDisbandEvent(Party party) {
        this.party = party;
    }

    public Party getParty() {
        return this.party;
    }

}
