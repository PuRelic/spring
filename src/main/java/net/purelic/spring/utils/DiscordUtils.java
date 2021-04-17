package net.purelic.spring.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.md_5.bungee.api.ProxyServer;
import net.purelic.commons.Commons;
import net.purelic.spring.commands.parsers.DiscordUser;
import net.purelic.spring.discord.Channel;
import net.purelic.spring.events.DiscordMessageEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
public class DiscordUtils {

    private static final String GUILD_ID = "746125703040335934";
    private static final JDA BOT = Commons.getDiscordBot();
    private static final Guild GUILD = BOT.getGuildById(GUILD_ID);

    public static Guild getGuild() {
        return GUILD;
    }

    public static RestAction<List<Invite>> getInvites() {
        return GUILD.retrieveInvites();
    }

    public static boolean hasRole(DiscordUser sender, String permission) {
        return hasRole(sender.getUser(), permission);
    }

    public static boolean hasRole(User user, String permission) {
        List<Role> roles = Arrays.stream(permission.split("::"))
            .map(DiscordUtils::getRoleByID)
            .collect(Collectors.toList());

        return GUILD.getMemberById(user.getIdLong()).getRoles()
            .stream().anyMatch(roles::contains);
    }

    public static AuditableRestAction<Void> addRole(User user, String roleId) {
        return addRole(user, getRoleByID(roleId));
    }

    public static AuditableRestAction<Void> addRole(User user, Role role) {
        return GUILD.addRoleToMember(user.getId(), role);
    }

    public static AuditableRestAction<Void> removeRole(User user, String roleId) {
        return removeRole(user, getRoleByID(roleId));
    }

    public static AuditableRestAction<Void> removeRole(User user, Role role) {
        return GUILD.removeRoleFromMember(user.getId(), role);
    }

    public static Role getRoleByID(String id) {
        return BOT.getRoleById(id);
    }

    public static void updateOnlineCount() {
        Activity activity = Activity.playing("PuRelic (" + ProxyServer.getInstance().getOnlineCount() + " Online)");
        BOT.getPresence().setActivity(activity);
    }

    public static void updateMemberCount() {
        BOT.getVoiceChannelById(Channel.MEMBERS)
            .getManager()
            .setName(GUILD.getMemberCount() + " Members")
            .queue();
    }

    public static void ban(User user, String reason) {
        GUILD.ban(user, 1).reason(reason).queue();
    }

    public static void log(String message) {
        BOT.getTextChannelById(Channel.LOGS).sendMessage(message).queue();
    }

    public static void log(String message, User mention) {
        BOT.getTextChannelById(Channel.LOGS).sendMessage(String.format(message, "<@" + mention.getId() + ">")).queue();
    }

    public static void log(MessageEmbed embed) {
        BOT.getTextChannelById(Channel.LOGS).sendMessage(embed).queue();
    }

    public static void logDeletedMessage(DiscordMessageEvent event) {
        logDeletedMessage(event.getSender(), event.getChannel(), event.getMessage().getContentRaw());
    }

    private static void logDeletedMessage(User author, MessageChannel channel, String message) {
        String log = "Message sent by " + author.getAsMention() + " deleted in <#" + channel.getId() + ">";

        EmbedBuilder embed = new EmbedBuilder()
            .setAuthor(author.getAsTag(), null, author.getAvatarUrl())
            .setDescription(message)
            .setTimestamp(new Date().toInstant())
            .setColor(Color.RED);

        BOT.getTextChannelById(Channel.LOGS).sendMessage(log).queue();
        BOT.getTextChannelById(Channel.LOGS).sendMessage(embed.build()).queue();
    }

}
