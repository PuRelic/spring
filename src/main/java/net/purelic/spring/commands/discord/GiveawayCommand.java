package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.jda.JDA4CommandManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.commands.parsers.GuildUser;
import net.purelic.spring.discord.Role;
import net.purelic.spring.utils.DiscordUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GiveawayCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("giveaway")
            .senderType(GuildUser.class)
            .permission(Role.ADMIN)
            .argument(IntegerArgument.<DiscordUser>newBuilder("winners").withMin(1).withMax(10).asOptionalWithDefault("1"))
            .handler(c -> {
                GuildUser sender = (GuildUser) c.getSender();
                MessageChannel channel = sender.getChannel();
                int winners = c.get("winners");

                List<Member> verified = DiscordUtils.getGuild().getMembers().stream()
                    .filter(member ->
                        DiscordUtils.hasRole(member.getUser(), Role.VERIFIED)
                        && !DiscordUtils.hasRole(member.getUser(), Role.staff()))
                    .collect(Collectors.toList());

                Collections.shuffle(verified);

                for (int i = 0; i < winners; i++) {
                    channel.sendMessage("\uD83C\uDF89 " + verified.get(i).getAsMention() + " \uD83C\uDF89").queue();
                }
            });
    }

}
