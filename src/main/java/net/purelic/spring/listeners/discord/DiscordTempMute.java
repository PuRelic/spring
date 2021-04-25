package net.purelic.spring.listeners.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.discord.Role;
import net.purelic.spring.events.DiscordTempMuteEvent;
import net.purelic.spring.utils.DiscordUtils;

import java.awt.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DiscordTempMute implements Listener {

    @EventHandler
    public void onDiscordMute(DiscordTempMuteEvent event) {
        User user = event.getUser();
        int duration = event.getDuration();
        TimeUnit timeUnit = event.getTimeUnit();
        String muteLength = event.getMuteLength();

        DiscordUtils.addRole(user, Role.MUTED).queue(
            muted -> {
                // Log the mute
                DiscordUtils.log("Successfully temp muted %s for " + muteLength + "!", user);

                // Alert the user privately
                user.openPrivateChannel().queue(
                    channel -> {
                        channel.sendMessage("Your recent message was automatically removed and you've been temporarily muted " +
                            "for " + muteLength + ". Please avoid using vulgar or offensive words.").queue(message -> {}, throwable -> {});

                        EmbedBuilder embed = new EmbedBuilder()
                            .setAuthor(user.getAsTag(), null, user.getAvatarUrl())
                            .setDescription(event.getMessage())
                            .setTimestamp(new Date().toInstant())
                            .setColor(Color.RED);

                        channel.sendMessage(embed.build()).queue(message -> {}, throwable -> {});
                    },
                    throwable -> {}
                );

                // Schedule the unmute
                DiscordUtils.removeRole(user, Role.MUTED).queueAfter(duration, timeUnit);

                // No longer logging when someone is unmuted
//                DiscordUtils.removeRole(user, Role.MUTED).queueAfter(duration, timeUnit,
//                    unmuted -> DiscordUtils.log("Successfully unmuted %s after " + duration + " " + timeUnit.name().toLowerCase() + "!", user),
//                    error -> DiscordUtils.log("Failed to unmute %s after " + duration + " " + timeUnit.name().toLowerCase() + "!", user)
//                );
            },
            throwable -> DiscordUtils.log("Failed to mute %s!", user)
        );
    }

}
