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
import net.purelic.spring.utils.DiscordUtils;

public class ReactCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("react")
            .senderType(GuildUser.class)
            .permission(Role.ADMIN)
            .argument(ChannelArgument.of("channel"))
            .argument(StringArgument.of("message id"))
            .argument(StringArgument.of("reaction"))
            .handler(c -> {
                MessageChannel channel = c.get("channel");
                String messageId = c.get("message id");
                String reaction = c.get("reaction");

                channel.retrieveMessageById(messageId).queue(
                    (message) -> message.addReaction(reaction).queue(
                        success -> DiscordUtils.log("Successfully added the reaction!"),
                        error -> DiscordUtils.log("Failed to add the reaction!")
                    ),
                    error -> DiscordUtils.log("There was an error trying to add the reaction!")
                );
            });
    }

}
