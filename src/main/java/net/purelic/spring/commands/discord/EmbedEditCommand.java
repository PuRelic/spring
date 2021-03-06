package net.purelic.spring.commands.discord;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.types.tuples.Triplet;
import io.leangen.geantyref.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.ChannelArgument;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.commands.parsers.GuildUser;
import net.purelic.spring.discord.Role;
import net.purelic.spring.utils.DiscordUtils;

import java.awt.*;

public class EmbedEditCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("embededit")
            .senderType(GuildUser.class)
            .permission(Role.ADMIN)
            .argument(ChannelArgument.of("channel"))
            .argument(StringArgument.of("message id"))
            .argumentTriplet(
                "color",
                TypeToken.get(Color.class),
                Triplet.of("r", "g", "b"),
                Triplet.of(Integer.class, Integer.class, Integer.class),
                (sender, triplet) -> new Color(triplet.getFirst(), triplet.getSecond(), triplet.getThird()),
                ArgumentDescription.of("Color RGB")
            )
            .argument(StringArgument.quoted("title"))
            .argument(StringArgument.greedy("description"))
            .handler(c -> {
                MessageChannel channel = c.get("channel");
                String messageId = c.get("message id");
                Color color = c.get("color");
                String title = c.get("title");
                String description = c.get("description");

                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(color);
                eb.setTitle(title, null);
                eb.setDescription(description);

                channel.retrieveMessageById(messageId).queue(
                    (message) -> {
                        message.editMessage(eb.build()).queue();
                        DiscordUtils.log("Successfully updated the message!");
                    },
                    error -> DiscordUtils.log("There was an error trying to update the message!")
                );
            });
    }

}
