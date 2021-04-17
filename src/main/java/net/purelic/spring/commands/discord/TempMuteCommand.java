package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.parsers.UserArgument;
import net.purelic.spring.Spring;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.commands.parsers.GuildUser;
import net.purelic.spring.discord.Role;
import net.purelic.spring.events.DiscordTempMuteEvent;

import java.util.concurrent.TimeUnit;

public class TempMuteCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("tempmute")
            .senderType(GuildUser.class)
            .permission(Role.staff())
            .argument(UserArgument.of("user"))
            .argument(IntegerArgument.<DiscordUser>newBuilder("duration").withMin(1).asOptionalWithDefault(String.valueOf(1)))
            .argument(EnumArgument.<DiscordUser, TimeUnit>newBuilder(TimeUnit.class, "time unit").asOptionalWithDefault(TimeUnit.HOURS.name()))
            .handler(c -> Spring.callEvent(new DiscordTempMuteEvent(
                c.get("user"),
                c.get("duration"),
                c.get("time unit")
            )));
    }

}
