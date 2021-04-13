package net.purelic.spring.commands;

import cloud.commandframework.Command;
import cloud.commandframework.jda.JDA4CommandManager;
import net.purelic.spring.commands.parsers.DiscordUser;

public interface DiscordCommand {

    Command.Builder<DiscordUser> getCommandBuilder(JDA4CommandManager<DiscordUser> commandManager);

}
