package net.purelic.spring.listeners.discord;

import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.purelic.spring.discord.Role;
import net.purelic.spring.utils.DiscordUtils;

import java.util.HashMap;

public class ReactionRoles extends ListenerAdapter {

    private static final String ALERT_MESSAGE = "830674462323507210";
    private static final String CLUB_MESSAGE = "836002343307837530";

    private final HashMap<String, String> alertRoles;
    private final HashMap<String, String> clubRoles;

    public ReactionRoles() {
        this.alertRoles = new HashMap<>();
        this.alertRoles.put("\uD83D\uDD28", Role.BETA_TESTER); // 🔨
        this.alertRoles.put("\uD83D\uDD14", Role.LOOKING_TO_PLAY); // 🔔
        this.alertRoles.put("\uD83D\uDCE3", Role.ANNOUNCEMENTS); // 📣
        this.alertRoles.put("\uD83C\uDFC6", Role.EVENTS); // 🏆

        this.clubRoles = new HashMap<>();
        this.clubRoles.put("\uD83C\uDFAC", Role.MOVIES); // 🎬
        this.clubRoles.put("\uD83C\uDFB5", Role.MUSIC); // 🎵
        this.clubRoles.put("\uD83C\uDFA8", Role.CREATIVE); // 🎨
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();

        if (!reactionEmote.isEmoji()) return;

        String emoji = reactionEmote.getEmoji();
        String messageId = event.getMessageId();

        if (messageId.equals(ALERT_MESSAGE)) {
            String roleId = this.alertRoles.get(emoji);

            if (roleId != null) {
                DiscordUtils.addRole(event.getUser(), roleId).queue();
            }
        } else if (messageId.equals(CLUB_MESSAGE)) {
            String roleId = this.clubRoles.get(emoji);

            if (roleId != null) {
                DiscordUtils.addRole(event.getUser(), roleId).queue();
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();

        if (!reactionEmote.isEmoji()) return;

        String emoji = reactionEmote.getEmoji();
        String messageId = event.getMessageId();

        if (messageId.equals(ALERT_MESSAGE)) {
            String roleId = this.alertRoles.get(emoji);

            if (roleId != null) {
                DiscordUtils.removeRole(event.getUser(), roleId).queue();
            }
        } else if (messageId.equals(CLUB_MESSAGE)) {
            String roleId = this.clubRoles.get(emoji);

            if (roleId != null) {
                DiscordUtils.removeRole(event.getUser(), roleId).queue();
            }
        }
    }

}
