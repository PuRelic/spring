package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.parsers.UserArgument;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.commands.parsers.GuildUser;
import net.purelic.spring.discord.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NukeCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("nuke")
            .senderType(GuildUser.class)
            .permission(Role.staff())
            .argument(IntegerArgument.<DiscordUser>newBuilder("amount").withMin(1).withMax(20).asRequired())
            .argument(UserArgument.<DiscordUser>newBuilder("user").withParserMode(UserArgument.ParserMode.MENTION).asOptional())
            .handler(c -> {
                GuildUser sender = (GuildUser) c.getSender();
                MessageChannel channel = sender.getChannel();
                int amount = c.get("amount");
                Optional<User> user = c.getOptional("user");

                if (user.isPresent()) {
                    this.deleteFromUser(channel, user.get(), sender.getUser(), amount);
                } else {
                    this.deleteMessages(channel, amount + 1);
                }
            });
    }

    private void deleteMessages(MessageChannel channel, int amount) {
        channel.getIterableHistory()
            .takeAsync(amount)
            .thenAccept(channel::purgeMessages);
    }

    private void deleteFromUser(MessageChannel channel, User user, User sender, int amount) {
        List<Message> messages = new ArrayList<>();

        boolean senderIsUser = sender.getId().equals(user.getId());

        // Account for the command message if the user is the sender
        if (senderIsUser) amount += 1;

        final int finalAmount = amount;

        CompletableFuture<Void> task = channel.getIterableHistory()
            .forEachAsync(m -> { // Loop over the history and filter messages
                if (m.getAuthor().equals(user)) messages.add(m); // Add these messages to a list
                return messages.size() < finalAmount; // Loop until limit is reached
            })
            .thenRunAsync(() -> channel.purgeMessages(messages)); // Delete messages in the list

        // Delete the command message
        if (!senderIsUser) {
            task.thenRunAsync(() -> deleteMessages(channel, 1)); // Delete the command message
        }
    }

}
