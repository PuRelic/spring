package net.purelic.spring.commands.social.party;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.CommandBuilder;

public class PartyHelpCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("party", "p")
            .literal("help")
            .senderType(ProxiedPlayer.class)
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();

                ComponentBuilder builder = ChatUtils.getHeader("Party Commands", ChatColor.GOLD, ChatColor.WHITE).append("").reset()
                    .append(new CommandBuilder("/party create", "Create new party", ChatColor.GOLD).addArgument("name", "Custom party name", false).toComponent())
                    .append(new CommandBuilder("/party invite", "Invite player to party", ChatColor.GOLD).addArgument("player", "Player to invite").toComponent())
                    .append(new CommandBuilder("/party accept", "Accept party invite", ChatColor.GOLD).addArgument("player", "Player who sent the invite").toComponent())
                    .append(new CommandBuilder("/party deny", "Deny party invite", ChatColor.GOLD).addArgument("player", "Player who sent the invite").toComponent())
                    .append(new CommandBuilder("/party leave", "Leave the party", ChatColor.GOLD).toComponent())
                    .append(new CommandBuilder("/party kick", "Kick party member", ChatColor.GOLD).addArgument("player", "Player to kick").toComponent())
                    .append(new CommandBuilder("/party disband", "Disband the party", ChatColor.GOLD).toComponent())
                    .append(new CommandBuilder("/party list", "List party members", ChatColor.GOLD).toComponent())
                    .append(new CommandBuilder("/party promote", "Promote member to party leader", ChatColor.GOLD).addArgument("player", "Player to promote").toComponent())
                    .append(new CommandBuilder("/party rename", "Rename the party", ChatColor.GOLD).addArgument("name", "Custom party name").toComponent())
                    .append(new CommandBuilder("/party warp", "Warp party to same server", ChatColor.GOLD).toComponent());

                ChatUtils.sendMessage(player, builder);
            });
    }

}
