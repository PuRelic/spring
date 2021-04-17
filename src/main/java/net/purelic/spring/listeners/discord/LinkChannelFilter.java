package net.purelic.spring.listeners.discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.commands.discord.LinkCommand;
import net.purelic.spring.discord.Channel;
import net.purelic.spring.events.DiscordMessageEvent;

public class LinkChannelFilter implements Listener {

    @EventHandler
    public void onDiscordMessage(DiscordMessageEvent event) {
        Message message = event.getMessage();
        String messageContent = message.getContentRaw();

        if (messageContent.contains("/verify")) {
            User user = event.getSender();

            event.getChannel().sendMessage(user.getAsMention() + ", use that command in-game not here! For security reasons we've removed" +
                " your verification code, please request a new one").queue();

            LinkCommand.CODES.entrySet()
                .removeIf(entry -> user.equals(entry.getValue()));

            message.delete().queue();
        } else if (event.getChannelId().equals(Channel.LINK)) {
            message.delete().queue();
        }
    }

}
