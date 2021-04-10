package net.purelic.spring.utils;

import cloud.commandframework.jda.JDACommandSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.md_5.bungee.api.ProxyServer;
import net.purelic.commons.Commons;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
public class DiscordUtils {

    private static final long GUILD_ID = 746125703040335934L;
    private static final long MEMBERS_CHANNEL = 826287350992076810L;
    private static final long LOG_CHANNEL = 830354705296916520L;

    private static final JDA BOT = Commons.getDiscordBot();
    private static final Guild GUILD = BOT.getGuildById(GUILD_ID);

    public static boolean hasRole(JDACommandSender sender, String permission) {
        List<Role> roles = Arrays.stream(permission.split("::"))
            .map(DiscordUtils::getRoleByID)
            .collect(Collectors.toList());

        return GUILD.getMember(sender.getUser()).getRoles()
            .stream().anyMatch(roles::contains);
    }

    public static AuditableRestAction<Void> addRole(User user, String role) {
        return GUILD.addRoleToMember(user.getId(), getRoleByID(role));
    }

    public static AuditableRestAction<Void> removeRole(User user, String role) {
        return GUILD.removeRoleFromMember(user.getId(), getRoleByID(role));
    }

    private static Role getRoleByID(String id) {
        return BOT.getRoleById(id);
    }

    public static void updateOnlineCount() {
        Activity activity = Activity.playing("PuRelic (" + ProxyServer.getInstance().getOnlineCount() + " Online)");
        BOT.getPresence().setActivity(activity);
    }

    public static void updateMemberCount() {
        BOT.getVoiceChannelById(MEMBERS_CHANNEL)
            .getManager()
            .setName(GUILD.getMemberCount() + " Members")
            .queue();
    }

    public static void log(String message) {
        BOT.getTextChannelById(LOG_CHANNEL).sendMessage(message).queue();
    }

    public static void log(String message, User mention) {
        BOT.getTextChannelById(LOG_CHANNEL).sendMessage(String.format(message, "<@" + mention.getId() + ">")).queue();
    }

}
