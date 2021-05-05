package net.purelic.spring.commands.staff;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.commons.Commons;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.profile.AltAccount;
import net.purelic.spring.utils.ChatUtils;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.DatabaseUtils;
import net.purelic.spring.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AltsCommand implements ProxyCommand {

    @SuppressWarnings("unchecked")
    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("alts")
            .senderType(ProxiedPlayer.class)
            .argument(StringArgument.of("player"))
            .handler(c -> {
                ProxiedPlayer sender = (ProxiedPlayer) c.getSender();
                String target = c.get("player");

                if (!PermissionUtils.isStaff(sender)) {
                    CommandUtils.sendNoPermissionMessage(sender);
                    return;
                }

                QueryDocumentSnapshot doc = DatabaseUtils.getPlayerDoc(target);

                if (doc == null) {
                    CommandUtils.sendNoPlayerMessage(sender, target);
                    return;
                }

                Query query = Commons.getFirestore().collection("player_ips").whereArrayContains("uuids", doc.getId());
                ApiFuture<QuerySnapshot> future = query.get();

                try {
                    QuerySnapshot querySnapshot = future.get();

                    if (querySnapshot.isEmpty()) return;

                    Map<String, AltAccount> accounts = new HashMap<>();

                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        List<Map<String, Object>> accountsData = (List<Map<String, Object>>) documentSnapshot.get("accounts");

                        if (accountsData == null || accountsData.isEmpty()) continue;

                        for (Map<String, Object> data : accountsData) {
                            AltAccount account = new AltAccount(data);
                            String id = account.getId();

                            if (accounts.containsKey(id)) {
                                accounts.put(id, account.merge(accounts.get(id)));
                            } else {
                                accounts.put(id, account);
                            }
                        }
                    }

                    ComponentBuilder message = ChatUtils.getHeader(doc.getString("name") + "'s Alts");

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
