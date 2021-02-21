package net.purelic.spring.analytics.events;

import net.purelic.spring.analytics.AnalyticsEvent;
import net.purelic.spring.party.Party;

public class PartyRenamedEvent extends AnalyticsEvent {

    public PartyRenamedEvent(Party party, String newName) {
        super("Party Renamed", party.getLeader());

        // set event properties
        this.properties.put("party_id", party.getId());
        this.properties.put("party_name", newName);
        this.properties.put("previous_name", party.getName());
    }

}
