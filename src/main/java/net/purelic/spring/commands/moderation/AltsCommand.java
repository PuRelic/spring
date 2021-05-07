package net.purelic.spring.commands.moderation;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.commons.Commons;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.Permission;
import net.purelic.spring.commands.parsers.ProfileArgument;
import net.purelic.spring.profile.AltAccount;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.utils.ChatUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class AltsCommand implements ProxyCommand {

    @SuppressWarnings("unchecked")
    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("alts")
            .senderType(ProxiedPlayer.class)
            // .permission(Permission.isStaff())
            .argument(ProfileArgument.of("player"))
            .handler(c -> {
                if (!Permission.isStaff(c)) return;

                ProxiedPlayer sender = (ProxiedPlayer) c.getSender();
                Profile target = c.get("player");

                Query query = Commons.getFirestore().collection("player_ips").whereArrayContains("uuids", target.getId().toString());
                ApiFuture<QuerySnapshot> future = query.get();

                try {
                    QuerySnapshot querySnapshot = future.get();

                    if (querySnapshot.isEmpty()) return;

                    Map<UUID, AltAccount> accounts = new HashMap<>();

                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        List<String> uuids = (List<String>) documentSnapshot.get("uuids");
                        List<Map<String, Object>> accountsData = (List<Map<String, Object>>) documentSnapshot.get("accounts");

                        if (accountsData == null || accountsData.isEmpty()) continue;

                        int i = 0;
                        for (Map<String, Object> data : accountsData) {
                            AltAccount account = new AltAccount(uuids.get(i), data);
                            i++;
                            UUID id = account.getId();

                            if (accounts.containsKey(id)) {
                                accounts.put(id, account.merge(accounts.get(id)));
                            } else {
                                accounts.put(id, account);
                            }
                        }
                    }

                    ComponentBuilder message = ChatUtils.getHeader(target.getName() + "'s Alts");

                    for (AltAccount account : accounts.values()) {
                        message.append("\n").reset().append(account.toComponent());
                    }

                    ChatUtils.sendMessage(sender, message);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
    }

}
