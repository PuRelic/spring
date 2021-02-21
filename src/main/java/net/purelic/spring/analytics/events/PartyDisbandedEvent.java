package net.purelic.spring.analytics.events;

import com.google.cloud.Timestamp;
import net.purelic.spring.analytics.AnalyticsEvent;
import net.purelic.spring.party.Party;

public class PartyDisbandedEvent extends AnalyticsEvent {

    public PartyDisbandedEvent(Party party) {
        super("Party Disbanded", party.getLeader());

        // set event properties
        this.properties.put("party_id", party.getId());
        this.properties.put("party_name", party.getName());
        this.properties.put("custom_name", party.hasCustomName());
        this.properties.put("party_time", Timestamp.now().getSeconds() - party.getCreatedAt().getSeconds());
        this.properties.put("total_members", party.getMembers().size());
        this.properties.put("members", party.getMemberIds());
    }

}
