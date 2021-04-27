package net.purelic.spring.managers;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.FieldValue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.profile.AltAccount;
import net.purelic.spring.utils.DatabaseUtils;
import net.purelic.spring.utils.ServerUtils;
import net.purelic.spring.utils.TaskUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AltManager {

    @SuppressWarnings("unchecked")
    public static void track(ProxiedPlayer player) {
        String ip = ServerUtils.getIP(player);
        String uuid = player.getUniqueId().toString();
        Map<String, Object> data = DatabaseUtils.getIPDoc(ip);

        if (data.isEmpty()) { // first time seeing this ip
            createIPDoc(player, ip);
            return;
        }

        List<Map<String, Object>> accounts = (List<Map<String, Object>>) data.get("accounts");
        List<String> uuids = (List<String>) data.get("uuids");

        if (!uuids.contains(uuid)) { // new account for this ip
            uuids.add(uuid);
            data.put("uuids", uuids);

            accounts.add(new AltAccount(player).toData(false));
            data.put("accounts", accounts);
        } else { // known account for this ip
            List<Map<String, Object>> accountsCopy = new ArrayList<>();

            // update account records
            for (Map<String, Object> accountData : accounts) {
                AltAccount account = new AltAccount(accountData);

                if (account.getId().equals(uuid)) {
                    account.setName(player.getName());
                    accountsCopy.add(account.toData(true));
                } else {
                    accountsCopy.add(account.toData(false));
                }
            }

            data.put("accounts", accountsCopy);
        }

        // if they can bypass ban evasion or are not currently banned we can return
        if ((boolean) data.get("bypass") || !((boolean) data.get("banned"))) {
            DatabaseUtils.updateIPDoc(ip, data);
            return;
        }

        Timestamp unbannedAt = (Timestamp) data.get("unbanned_at");

        if (unbannedAt != null && unbannedAt.compareTo(Timestamp.now()) < 0) { // not banned
            data.put("banned", false);
            data.put("unbanned_at", FieldValue.delete());
        } else { // banned
            TaskUtils.scheduleTask(() -> player.disconnect(getBanEvasionMessage()), 500, TimeUnit.MILLISECONDS);
        }

        DatabaseUtils.updateIPDoc(ip, data);
    }

    private static void createIPDoc(ProxiedPlayer player, String ip) {
        Map<String, Object> data = new HashMap<>();

        List<String> uuids = new ArrayList<>();
        uuids.add(player.getUniqueId().toString());

        List<Map<String, Object>> accounts = new ArrayList<>();
        accounts.add(new AltAccount(player).toData(false));

        data.put("uuids", uuids);
        data.put("accounts", accounts);
        data.put("banned", false);
        data.put("bypass", false);

        DatabaseUtils.createIPDoc(ip, data);
    }

    private static TextComponent getBanEvasionMessage() {
        return new TextComponent(
            ChatColor.RED + "" + ChatColor.BOLD + "Disconnected!\n\n" + ChatColor.RESET +
            ChatColor.RED + "Ban Evasion\n\n" +
            ChatColor.WHITE + "Please read the rules at " + ChatColor.AQUA + "purelic.net/rules\n" +
            ChatColor.WHITE + "or appeal your ban at " + ChatColor.AQUA + "purelic.net/appeal"
        );
    }

}
