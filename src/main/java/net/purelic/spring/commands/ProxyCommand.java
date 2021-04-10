package net.purelic.spring.commands;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;

public interface ProxyCommand {

    Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> commandManager);

}
