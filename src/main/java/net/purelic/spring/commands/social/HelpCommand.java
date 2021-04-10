package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.CommandBuilder;

public class HelpCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("help")
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();

                ComponentBuilder builder = ChatUtils.getHeader("Help Commands").append("").reset()

                    .append(new CommandBuilder("/report", "Report a rule breaker")
                        .addArgument("player", "Player to report", true)
                        .addArgument("reason", "Reason for report", true).toComponent())

                    .append(new CommandBuilder("/support", "Request help from staff")
                        .addArgument("request", "Support request", true).toComponent())

                    .append(new CommandBuilder("/staff", "List online staff").toComponent())

                    .append(new CommandBuilder("/discord", "Need more help? Join our discord").toComponent())

                    .append(new CommandBuilder("/website", "Learn more about PuRelic").toComponent())

                    .append(new CommandBuilder("/docs", "Documentation for map and game development").toComponent())
                    ;

                ChatUtils.sendMessage(player, builder);
            });
    }

}
