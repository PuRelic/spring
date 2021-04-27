package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
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
            .handler(c -> {
                GuildUser sender = (GuildUser) c.getSender();
                MessageChannel channel = sender.getChannel();

                List<Member> verified = DiscordUtils.getGuild().getMembers().stream()
                    .filter(member ->
                        DiscordUtils.hasRole(member.getUser(), Role.VERIFIED)
                        && !DiscordUtils.hasRole(member.getUser(), Role.staff()))
                    .collect(Collectors.toList());

                Collections.shuffle(verified);

                channel.sendMessage("\uD83C\uDF89 " + verified.get(0).getAsMention() + " \uD83C\uDF89").queue();
            });
    }

}
