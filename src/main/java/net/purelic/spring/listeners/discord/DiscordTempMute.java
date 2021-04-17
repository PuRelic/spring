package net.purelic.spring.listeners.discord;

import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.discord.Role;
import net.purelic.spring.events.DiscordTempMuteEvent;
import net.purelic.spring.utils.DiscordUtils;

import java.util.concurrent.TimeUnit;

public class DiscordTempMute implements Listener {

    @EventHandler
    public void onDiscordMute(DiscordTempMuteEvent event) {
        User user = event.getUser();
        int duration = event.getDuration();
        TimeUnit timeUnit = event.getTimeUnit();

        DiscordUtils.addRole(user, Role.MUTED).queue(
            muted -> {
                DiscordUtils.log("Successfully muted %s for " + duration + " " + timeUnit.name().toLowerCase() + "!", user);

                DiscordUtils.removeRole(user, Role.MUTED).queueAfter(duration, timeUnit,
                    unmuted -> DiscordUtils.log("Successfully unmuted %s after " + duration + " " + timeUnit.name().toLowerCase() + "!", user),
                    error -> DiscordUtils.log("Failed to unmute %s after " + duration + " " + timeUnit.name().toLowerCase() + "!", user)
                );
            },
            error -> DiscordUtils.log("Failed to mute %s!", user)
        );
    }

}
