package net.purelic.spring.commands.social;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.DatabaseUtils;

public class SeenCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("seen")
            .senderType(ProxiedPlayer.class)
            .argument(StringArgument.of("player"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                String target = c.get("player");
                ProxiedPlayer online = Spring.getPlayer(target);

                if (online != null) {
                    FindCommand.sendFoundMessage(player, online);
                } else {
                    QueryDocumentSnapshot doc = DatabaseUtils.getPlayerDoc(target);

                    if (doc == null) {
                        CommandUtils.sendNoPlayerMessage(player, target);
                    } else {
                        Timestamp lastSeen = doc.getTimestamp("last_seen");
                        if (lastSeen == null) CommandUtils.sendErrorMessage(player, "There was an error fetching information about this player!");
                        else CommandUtils.sendAlertMessage(player, ChatColor.DARK_AQUA + doc.getString("name") + ChatColor.RESET + " was last seen " + ChatUtils.format(lastSeen));
                    }
                }
            });
    }

}
