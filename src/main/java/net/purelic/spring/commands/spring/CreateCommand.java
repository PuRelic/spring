package net.purelic.spring.commands.spring;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.Permission;
import net.purelic.spring.commands.parsers.PlaylistArgument;
import net.purelic.spring.managers.ServerManager;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.server.Playlist;
import net.purelic.spring.utils.CommandUtils;

public class CreateCommand implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("spring")
            .literal("create")
            .senderType(ProxiedPlayer.class)
            // .permission(Permission.isStaff())
            .argument(StringArgument.of("name"))
            .argument(PlaylistArgument.of("playlist"))
            .argument(IntegerArgument.<CommandSender>newBuilder("max players").withMin(0).withMax(80).asOptionalWithDefault("40"))
            .argument(BooleanArgument.optional("notify", false))
            .handler(c -> {
                if (!Permission.isAdmin(c)) return;

                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                String name = c.get("name");
                Playlist playlist = c.get("playlist");
                int maxPlayers = c.get("max players");
                boolean notify = c.get("notify");

                GameServer gameServer = new GameServer(name, playlist, maxPlayers, notify);
                ServerManager.addServer(gameServer);

                CommandUtils.sendSuccessMessage(player, String.format("Server \"%s\" was created and is now starting up!", name));
            });
    }

}
