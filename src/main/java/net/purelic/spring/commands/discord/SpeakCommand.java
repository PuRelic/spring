package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.jda.JDA4CommandManager;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.ChannelArgument;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.commands.parsers.GuildUser;
import net.purelic.spring.discord.Role;

public class SpeakCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("speak")
            .senderType(GuildUser.class)
            .permission(Role.ADMIN)
            .argument(ChannelArgument.of("channel"))
            .argument(StringArgument.greedy("message"))
            .handler(c -> {
                MessageChannel channel = c.get("channel");
                String message = c.get("message");
                channel.sendMessage(message).queue();
            });
    }

}
