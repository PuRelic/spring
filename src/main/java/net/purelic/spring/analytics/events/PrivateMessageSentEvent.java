package net.purelic.spring.analytics.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.analytics.AnalyticsEvent;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.utils.ServerUtils;

public class PrivateMessageSentEvent extends AnalyticsEvent {

    public PrivateMessageSentEvent(ProxiedPlayer sender, ProxiedPlayer recipient, String message, boolean sameServer) {
        super("Private Message Sent", sender);

        // set event properties
        GameServer senderServer = ServerUtils.getGameServer(sender);
        GameServer recipientServer = ServerUtils.getGameServer(recipient);
        this.properties.put("message", message);
        this.properties.put("recipient_name", recipient.getName());
        this.properties.put("recipient_uuid", recipient.getUniqueId().toString());
        this.properties.put("server_name", sender.getServer().getInfo().getName());
        this.properties.put("recipient_server_name", recipient.getServer().getInfo().getName());
        this.properties.put("same_server", sameServer);
        if (senderServer != null) this.properties.put("server_id", senderServer.getId());
        if (recipientServer != null) this.properties.put("recipient_server_id", recipientServer.getId());
    }

}
