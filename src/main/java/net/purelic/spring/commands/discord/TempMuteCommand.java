package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.JDACommandSender;
import cloud.commandframework.jda.parsers.UserArgument;
import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.discord.Role;
import net.purelic.spring.utils.DiscordUtils;

import java.util.concurrent.TimeUnit;

public class TempMuteCommand implements DiscordCommand {

    @Override
    public Command.Builder<JDACommandSender> getCommandBuilder(JDA4CommandManager<JDACommandSender> mgr) {
        return mgr.commandBuilder("tempmute")
            .permission(Role.staff())
            .argument(UserArgument.of("user"))
            .argument(IntegerArgument.<JDACommandSender>newBuilder("duration").withMin(1).asRequired())
            .argument(EnumArgument.of(TimeUnit.class, "time unit"))
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
