package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.parsers.UserArgument;
import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.discord.Role;
import net.purelic.spring.utils.DiscordUtils;

import java.util.concurrent.TimeUnit;

public class TempMuteCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("tempmute")
            .permission(Role.staff())
            .argument(UserArgument.of("user"))
            .argument(IntegerArgument.<DiscordUser>newBuilder("duration").withMin(1).asOptionalWithDefault(String.valueOf(1)))
            .argument(EnumArgument.<DiscordUser, TimeUnit>newBuilder(TimeUnit.class, "time unit").asOptionalWithDefault(TimeUnit.HOURS.name()))
            .handler(c -> {
                User user = c.get("user");
                int duration = c.get("duration");
                TimeUnit timeUnit = c.get("time unit");

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
            });
    }

}
