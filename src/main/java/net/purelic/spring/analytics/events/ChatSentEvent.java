package net.purelic.spring.analytics.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.purelic.spring.analytics.AnalyticsEvent;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.utils.ServerUtils;

public class ChatSentEvent extends AnalyticsEvent {

    public ChatSentEvent(ProxiedPlayer sender, ChatEvent chat) {
        super("Chat Sent", sender);

        // set event properties
        GameServer server = ServerUtils.getGameServer(sender);
        this.properties.put("message", chat.getMessage());
        this.properties.put("command", chat.isCommand());
        this.properties.put("proxy_command", chat.isProxyCommand());
        this.properties.put("server_name", sender.getServer().getInfo().getName());
        if (server != null) this.properties.put("server_id", server.getId());
        if (chat.isCommand()) this.properties.put("command_base", chat.getMessage().split(" ")[0]);
    }

}
