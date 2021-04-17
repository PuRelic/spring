package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.parsers.UserArgument;
import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.commands.parsers.GuildUser;
import net.purelic.spring.discord.Role;
import net.purelic.spring.utils.DiscordUtils;

public class MuteCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("mute")
            .senderType(GuildUser.class)
            .permission(Role.staff())
            .argument(UserArgument.of("user"))
            .handler(c -> {
                User user = c.get("user");

                if (DiscordUtils.hasRole(user, Role.MUTED)) {
                    DiscordUtils.log("This user is already muted!");
                    return;
                }

                DiscordUtils.addRole(user, Role.MUTED).queue(
                    muted -> DiscordUtils.log("Successfully muted %s!", user),
                    error -> DiscordUtils.log("Failed to mute %s!", user)
                );
            });
    }

}
