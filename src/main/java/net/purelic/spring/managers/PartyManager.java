package net.purelic.spring.managers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.commons.Commons;
import net.purelic.spring.Spring;
import net.purelic.spring.analytics.events.PartyCreatedEvent;
import net.purelic.spring.events.PartyJoinEvent;
import net.purelic.spring.events.PartyLeaveEvent;
import net.purelic.spring.party.Party;
import net.purelic.spring.party.PartyInvite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PartyManager {

    private static final Map<ProxiedPlayer, Party> PARTIES = new HashMap<>();
    private static final Map<ProxiedPlayer, Set<PartyInvite>> INVITES = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void loadPartyCache() {
        Set<Map<String, Object>> cache = (Set<Map<String, Object>>) Commons.getGeneralCache().getOrDefault("parties", new HashSet<>());
        Set<Party> parties = cache.stream().map(Party::new).collect(Collectors.toSet());
        parties.forEach(party -> party.getMembers().forEach(member -> PARTIES.put(member, party)));
    }

    public static void cacheParties() {
        Commons.getGeneralCache().put("parties",
            PARTIES.values().stream()
                .map(Party::toData)
                .collect(Collectors.toSet()));
    }

    public static Party createParty(ProxiedPlayer player) {
        return createParty(player, player.getName());
    }

    public static Party createParty(ProxiedPlayer player, String name) {
        Party party = new Party(player, name);
        new PartyCreatedEvent(party).track();
        setParty(player, party);
        return party;
    }

    public static boolean hasParty(ProxiedPlayer player) {
        return PARTIES.containsKey(player);
    }

    public static Party getParty(ProxiedPlayer player) {
        return PARTIES.get(player);
    }

    public static void setParty(ProxiedPlayer player, Party party) {
        PARTIES.put(player, party);
        Spring.callEvent(new PartyJoinEvent(party, player));
    }

    public static void removeParty(ProxiedPlayer player) {
        Spring.callEvent(new PartyLeaveEvent(PARTIES.get(player), player));
        PARTIES.remove(player);
    }

    public static void removeParty(Party party) {
        party.getMembers().forEach(PartyManager::removeParty);
    }

    public static void removeMember(ProxiedPlayer player) {
        Party party = getParty(player);
        if (party != null) party.remove(player, false);
    }

    public static PartyInvite getInvite(ProxiedPlayer invited, Party party) {
        return INVITES.getOrDefault(invited, new HashSet<>())
            .stream().filter(pi -> pi.getParty() == party)
            .findFirst().orElse(null);
    }

    public static boolean hasInvite(ProxiedPlayer invited, Party party) {
        return getInvite(invited, party) != null;
    }

    public static PartyInvite createInvite(Party party, ProxiedPlayer sender, ProxiedPlayer invited) {
        PartyInvite invite = new PartyInvite(party, sender, invited);
        INVITES.putIfAbsent(invited, new HashSet<>());
        INVITES.get(invited).add(invite);
        return invite;
    }

    public static void removeInvite(PartyInvite invite) {
        INVITES.values().stream()
            .filter(invites -> invites.contains(invite))
            .forEach(invites -> invites.remove(invite));

        ProxiedPlayer invited = invite.getInvited();
        if (INVITES.get(invited).isEmpty()) INVITES.remove(invited);
    }

}
