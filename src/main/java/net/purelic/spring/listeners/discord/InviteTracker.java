package net.purelic.spring.listeners.discord;

import com.google.cloud.firestore.FieldValue;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.purelic.spring.analytics.events.DiscordJoinedEvent;
import net.purelic.spring.analytics.events.DiscordInviteCreatedEvent;
import net.purelic.spring.utils.DatabaseUtils;
import net.purelic.spring.utils.DiscordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InviteTracker extends ListenerAdapter {

    private Map<Invite, Integer> invites = new HashMap<>();

    public InviteTracker() {
        this.updateInvites();
    }

    private void updateInvites() {
        DiscordUtils.getInvites().queue(invites ->
            this.invites = invites.stream()
                .collect(Collectors.toMap(invite -> invite, Invite::getUses))
        );
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        boolean firstJoin = DatabaseUtils.createDiscordDoc(member);

        DiscordUtils.getInvites().queue(invites -> {
            // Find most recently used invite
            Optional<Invite> optionalInvite = invites.stream()
                .filter(invite -> this.invites.get(invite) != invite.getUses())
                .findFirst();

            if (optionalInvite.isPresent()) {
                Invite referral = optionalInvite.get();
                User inviter = referral.getInviter();

                if (!firstJoin) {
                    new DiscordJoinedEvent(member, referral, false).track();
                } else { // only attribute referrals to first joins
                    new DiscordJoinedEvent(member, referral, true).track();

                    Map<String, Object> values = new HashMap<>();
                    values.put("referring_invite", referral.getCode());

                    if (inviter != null) {
                        String inviterId = inviter.getId();
                        values.put("referring_user", inviterId);
                        DatabaseUtils.updateDiscordDoc(inviterId, "referrals", FieldValue.increment(1));
                    }

                    DatabaseUtils.updateDiscordDoc(member.getId(), values);
                }
            } else {
                new DiscordJoinedEvent(member, null, firstJoin).track();
            }

            // Update our invite cache
            this.updateInvites();
        });
    }

    @Override
    public void onGuildInviteCreate(@NotNull GuildInviteCreateEvent event) {
        Invite invite = event.getInvite();
        this.invites.put(invite, 0);

        User inviter = invite.getInviter();
        if (inviter != null) new DiscordInviteCreatedEvent(invite, inviter).track();
    }

}
