package net.purelic.spring.managers;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.FieldValue;
import net.md_5.bungee.api.connection.PendingConnection;
import net.purelic.spring.profile.AltAccount;
import net.purelic.spring.utils.DatabaseUtils;
import net.purelic.spring.utils.ServerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AltManager {

    // returns true if the player is ban evading
    @SuppressWarnings("unchecked")
    public static boolean track(PendingConnection connection) {
        String ip = ServerUtils.getIP(connection);
        String uuid = connection.getUniqueId().toString();
        String name = connection.getName();
        Map<String, Object> data = DatabaseUtils.getIPDoc(ip);

        if (data.isEmpty()) { // first time seeing this ip
            createIPDoc(uuid, name, ip);
            return false;
        }

        List<Map<String, Object>> accounts = (List<Map<String, Object>>) data.get("accounts");
        List<String> uuids = (List<String>) data.get("uuids");

        if (!uuids.contains(uuid)) { // new account for this ip
            uuids.add(uuid);
            data.put("uuids", uuids);

            accounts.add(new AltAccount(uuid, name).toData(false));
            data.put("accounts", accounts);
        } else { // known account for this ip
            List<Map<String, Object>> accountsCopy = new ArrayList<>();

            // update account records
            for (Map<String, Object> accountData : accounts) {
                AltAccount account = new AltAccount(accountData);

                if (account.getId().equals(uuid)) {
                    account.setName(name);
                    accountsCopy.add(account.toData(true));
                } else {
                    accountsCopy.add(account.toData(false));
                }
            }

            data.put("accounts", accountsCopy);
        }

        // if they can bypass ban evasion or are not currently banned
        if ((boolean) data.get("bypass") || !((boolean) data.get("banned"))) {
            DatabaseUtils.updateIPDoc(ip, data);
            return false;
        }

        Timestamp unbannedAt = (Timestamp) data.get("unbanned_at");

        if (unbannedAt != null && unbannedAt.compareTo(Timestamp.now()) < 0) { // not banned
            data.put("banned", false);
            data.put("unbanned_at", FieldValue.delete());
            DatabaseUtils.updateIPDoc(ip, data);
            return false;
        }

        // banned
        DatabaseUtils.updateIPDoc(ip, data);
        return true;
    }

    private static void createIPDoc(String uuid, String name, String ip) {
        Map<String, Object> data = new HashMap<>();

        List<String> uuids = new ArrayList<>();
        uuids.add(uuid);

        List<Map<String, Object>> accounts = new ArrayList<>();
        accounts.add(new AltAccount(uuid, name).toData(false));

        data.put("uuids", uuids);
        data.put("accounts", accounts);
        data.put("banned", false);
        data.put("bypass", false);

        DatabaseUtils.createIPDoc(ip, data);
    }

}
