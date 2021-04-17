package net.purelic.spring.commands.discord;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import com.google.cloud.Timestamp;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.analytics.Analytics;
import net.purelic.spring.analytics.events.DiscordLinkedEvent;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.discord.Role;
import net.purelic.spring.managers.DiscordManager;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.DatabaseUtils;
import net.purelic.spring.utils.DiscordUtils;

import java.util.HashMap;
import java.util.Map;

public class VerifyCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("verify")
            .senderType(ProxiedPlayer.class)
            .argument(StringArgument.of("code"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                String code = c.get("code");

                if (ProfileManager.getProfile(player).hasDiscordLinked()) {
                    CommandUtils.sendErrorMessage(player, "You've already linked your discord account!");
                    return;
                }

                if (!LinkCommand.CODES.containsKey(code)) {
                    CommandUtils.sendErrorMessage(player, "This verification code is either invalid or expired!");
                    return;
                }

                // Get the user that requested verification
                User user = LinkCommand.CODES.get(code);

                // Add the verified role
                DiscordUtils.addRole(user, Role.VERIFIED).queue();
                LinkCommand.CODES.remove(code);

                // Sync roles
                DiscordManager.syncRoles(player, user);

                // Update player profile
                ProfileManager.getProfile(player).setDiscordLinked(true);
                Analytics.identify(player);

                // Update fields in Discord document
                Map<String, Object> values = new HashMap<>();
                values.put("player_uuid", player.getUniqueId().toString());
                values.put("verified_at", Timestamp.now());
                DatabaseUtils.updateDiscordDoc(user.getId(), values);

                // Track the analytics event
                new DiscordLinkedEvent(user).track();

                CommandUtils.sendSuccessMessage(player, "Your discord account was successfully verified!");
            });
    }

    private void syncRoles() {

    }

}
