package net.purelic.spring.analytics.events;

import net.purelic.spring.analytics.AnalyticsEvent;
import net.purelic.spring.party.Party;

public class PartyCreatedEvent extends AnalyticsEvent {

    public PartyCreatedEvent(Party party) {
        super("Party Created", party.getLeader());

        // set event properties
        this.properties.put("party_id", party.getId());
        this.properties.put("party_name", party.getName());
        this.properties.put("custom_name", party.hasCustomName());
    }

}
