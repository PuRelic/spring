package net.purelic.spring.listeners.discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.Spring;
import net.purelic.spring.discord.Role;
import net.purelic.spring.events.DiscordMessageEvent;
import net.purelic.spring.events.DiscordTempMuteEvent;
import net.purelic.spring.utils.DiscordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CensoredWordFilter implements Listener {

    private static List<String> bannedWords = new ArrayList<>();
    private static List<String> censoredWords = new ArrayList<>();
    private static List<String> filteredWords = new ArrayList<>();

    public static void updateFilter(Configuration config) {
        bannedWords = config.getStringList("discord.banned_words");
        censoredWords = config.getStringList("discord.censored_words");
        filteredWords = config.getStringList("discord.filtered_words");
    }

    @EventHandler
    public void onDiscordMessage(DiscordMessageEvent event) {
        User sender = event.getSender();
        Message message = event.getMessage();
        String content = message.getContentRaw();
        String lowerContent = content.toLowerCase();
        String[] words = lowerContent.split("\\b");

        if (Arrays.stream(words).anyMatch(bannedWords::contains)) {
            message.delete().queue(success -> DiscordUtils.logDeletedMessage(event));
            DiscordUtils.ban(sender, "Sending Inappropriate Messages");
        } else if (Arrays.stream(words).anyMatch(censoredWords::contains)) {
            message.delete().queue(success -> DiscordUtils.logDeletedMessage(event));
            DiscordUtils.addRole(sender, Role.MUTED).queue();
        } else if (Arrays.stream(words).anyMatch(filteredWords::contains)) {
            message.delete().queue(success -> DiscordUtils.logDeletedMessage(event));
            Spring.callEvent(new DiscordTempMuteEvent(sender, DiscordUtils.hasRole(sender, Role.VERIFIED) ? 1 : 5, TimeUnit.MINUTES, content));
        }
    }

}
