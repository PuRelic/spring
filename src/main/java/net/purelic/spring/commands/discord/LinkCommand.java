package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.jda.JDA4CommandManager;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.commands.parsers.GuildUser;
import net.purelic.spring.discord.Channel;
import net.purelic.spring.discord.Role;
import net.purelic.spring.managers.DiscordManager;
import net.purelic.spring.utils.DiscordUtils;
import net.purelic.spring.utils.TaskUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LinkCommand implements DiscordCommand {

    public static final Map<String, User> CODES = new HashMap<>();

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("link")
            .senderType(GuildUser.class)
            .handler(c -> {
                GuildUser sender = (GuildUser) c.getSender();
                User user = sender.getUser();
                String mention = user.getAsMention();
                MessageChannel channel = sender.getChannel();

                if (!channel.getId().equals(Channel.LINK)) {
                    return;
                }

                if (DiscordUtils.hasRole(sender, Role.VERIFIED)) {
                    channel.sendMessage(mention + ", you are already verified").queue();
                    return;
                }

                UUID playerId = DiscordManager.getLinkedId(user);

                if (playerId != null) {
                    DiscordUtils.addRole(user, Role.VERIFIED).queue();
                    DiscordManager.syncRoles(playerId, user);
                    channel.sendMessage(mention + ", your Discord account has been relinked").queue();
                    return;
                }

                String code = UUID.randomUUID().toString().split("-")[0].toUpperCase();
                CODES.put(code, user);

                user.openPrivateChannel().queue(
                     privateChannel -> privateChannel.sendMessage("**DO NOT PASTE THIS IN DISCORD!** Use this command **in-game** to verify your account: `/verify " + code + "`").queue(
                        message -> channel.sendMessage(mention + ", a verification code has been sent to you privately").queue(
                            sent -> TaskUtils.scheduleTask(() -> CODES.remove(code), 60) // remove/expire the code after 60 seconds
                        ),
                        throwable -> channel.sendMessage(mention + ", please enable private messages").queue()
                    )
                );
            });
    }

}
