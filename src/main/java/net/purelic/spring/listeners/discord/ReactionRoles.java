package net.purelic.spring.listeners.discord;

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.purelic.spring.discord.Role;
import net.purelic.spring.utils.DiscordUtils;

import java.util.HashMap;

public class ReactionRoles extends ListenerAdapter {

    private static final String ALERT_MESSAGE = "830674462323507210";
    private final HashMap<String, String> alertRoles;

    public ReactionRoles() {
        this.alertRoles = new HashMap<>();
        this.alertRoles.put("\uD83D\uDD28", Role.BETA_TESTER); // 🔨
        this.alertRoles.put("\uD83D\uDD14", Role.LOOKING_TO_PLAY); // 🔔
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        String messageId = event.getMessageId();
        String emoji = event.getReactionEmote().getEmoji();

        if (messageId.equals(ALERT_MESSAGE)) {
            String roleId = this.alertRoles.get(emoji);

            if (roleId != null) {
                DiscordUtils.addRole(event.getUser(), roleId).queue();
            }
        }
    }

    @Override
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
        String messageId = event.getMessageId();
        String emoji = event.getReactionEmote().getEmoji();

        if (messageId.equals(ALERT_MESSAGE)) {
            String roleId = this.alertRoles.get(emoji);

            if (roleId != null) {
                DiscordUtils.removeRole(event.getUser(), roleId).queue();
            }
        }
    }

}
