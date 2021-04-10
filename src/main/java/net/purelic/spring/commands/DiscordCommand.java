package net.purelic.spring.commands;

import cloud.commandframework.Command;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.JDACommandSender;

public interface DiscordCommand {

    Command.Builder<JDACommandSender> getCommandBuilder(JDA4CommandManager<JDACommandSender> commandManager);

}
