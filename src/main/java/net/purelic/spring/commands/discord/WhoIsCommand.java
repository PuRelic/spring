package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.parsers.UserArgument;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.commands.parsers.GuildUser;
import net.purelic.spring.discord.Role;
import net.purelic.spring.managers.DiscordManager;
import net.purelic.spring.profile.DiscordProfile;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.utils.DiscordUtils;

import java.awt.*;

public class WhoIsCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("whois")
            .senderType(GuildUser.class)
            .permission(Role.staff())
            .argument(UserArgument.of("user"))
            .handler(c -> {
                User user = c.get("user");
                DiscordProfile profile = DiscordManager.getProfile(user);

                EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor(user.getAsTag(), null, user.getAvatarUrl())
                    .setFooter("Joined")
                    .setTimestamp(profile.getJoined().toDate().toInstant())
                    .setColor(Color.WHITE)
                    .addField("Referrals", "" + profile.getReferrals(), false);

                if (profile.wasReferred()) {
                    User referredBy = profile.getReferringUser();

                    if (referredBy == null) {
                        embed.addField("Referred By", profile.getReferringUserId(), false);
                    } else {
                        embed.addField("Referred By", referredBy.getAsMention(), false);
                    }

                    embed.addField("Invite Code", profile.getReferringInvite(), false);
                }

                if (profile.isVerified()) {
                    Profile playerProfile = profile.getPlayerProfile();

                    embed.addField("Verified At", profile.getVerifiedTimestamp().toDate().toString(), false)
                        .addField("Username", playerProfile.getName(), false)
                        .addField("UUID", profile.getPlayerId(), false);
                }

                DiscordUtils.log(embed.build());
            });
    }

}
