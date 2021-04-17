package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.jda.JDA4CommandManager;
import com.google.cloud.firestore.FieldValue;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.purelic.spring.analytics.events.DiscordUnlinkedEvent;
import net.purelic.spring.commands.DiscordCommand;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.commands.parsers.GuildUser;
import net.purelic.spring.discord.Channel;
import net.purelic.spring.discord.Role;
import net.purelic.spring.managers.DiscordManager;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.utils.DatabaseUtils;
import net.purelic.spring.utils.DiscordUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UnlinkCommand implements DiscordCommand {

    @Override
    public Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> mgr) {
        return mgr.commandBuilder("unlink")
            .handler(c -> {
                GuildUser sender = (GuildUser) c.getSender();
                User user = sender.getUser();
                String id = user.getId();
                String mention = user.getAsMention();
                MessageChannel channel = sender.getChannel();

                if (!channel.getId().equals(Channel.LINK)) {
                    return;
                }

                if (!DiscordUtils.hasRole(user, Role.VERIFIED)) {
                    channel.sendMessage(mention + ", you are not currently verified").queue();
                    return;
                }

                // Track the analytics event
                new DiscordUnlinkedEvent(user).track();

                // Update verified flag in player profile
                UUID uuid = DiscordManager.getLinkedId(user);
                ProfileManager.getProfile(uuid).setDiscordLinked(false);

                // Update fields in Discord document
                Map<String, Object> values = new HashMap<>();
                values.put("player_uuid", FieldValue.delete());
                values.put("verified_at", FieldValue.delete());
                DatabaseUtils.updateDiscordDoc(id, values);

                // Remove verified role in Discord
                DiscordUtils.removeRole(user, Role.VERIFIED).queue();

                channel.sendMessage(mention + ", your discord account has been unlinked").queue();
            });
    }

}
