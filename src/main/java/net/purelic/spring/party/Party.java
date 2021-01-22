package net.purelic.spring.party;

import com.google.cloud.Timestamp;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.events.PartyDisbandEvent;
import net.purelic.spring.managers.PartyManager;
import net.purelic.spring.server.GameServer;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.PartyUtils;
import net.purelic.spring.utils.ServerUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Party {

    private final String id;
    private final Timestamp created;
    private ProxiedPlayer leader;
    private String name;
    private final List<ProxiedPlayer> members;

    public Party(ProxiedPlayer leader, String name) {
        this.id = UUID.randomUUID().toString();
        this.created = Timestamp.now();
        this.leader = leader;
        this.name = name;
        this.members = new ArrayList<>();
        this.add(leader);
        CommandUtils.sendSuccessMessage(leader, "You successfully created a party!");
    }

    public Party(Map<String, Object> data) {
        this.id = (String) data.get("id");
        this.created = (Timestamp) data.get("created");
        this.leader = Spring.getPlayer((UUID) data.get("leader"));
        this.name = (String) data.get("name");
        this.members = ((List<UUID>) data.get("members")).stream().map(Spring::getPlayer).collect(Collectors.toList());

        // clean up members
        this.members.remove(null);
        if (this.members.size() == 0) this.disband();
        else if (this.leader == null) this.setLeader(this.members.get(0));
    }

    public String getId() {
        return this.id;
    }

    public ProxiedPlayer getLeader() {
        return leader;
    }

    public boolean isLeader(ProxiedPlayer player) {
        return this.leader == player;
    }

    public void setLeader(ProxiedPlayer leader) {
        if (!this.hasCustomName()) this.setName(leader.getName());
        this.leader = leader;
        this.sendMessage(leader.getName() + " is now the party leader!");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasCustomName() {
        return !this.name.equals(this.leader.getName());
    }

    public List<ProxiedPlayer> getMembers() {
        return members;
    }

    public int size() {
        return this.members.size();
    }

    public void add(ProxiedPlayer player) {
        this.members.add(player);
        PartyManager.setParty(player, this);
    }

    public void remove(ProxiedPlayer player, boolean kick) {
        this.remove(player, kick, player.isConnected());
    }

    private void remove(ProxiedPlayer player, boolean kick, boolean online) {
        if (this.members.size() == 1) {
            this.disband();
            return;
        }

        this.members.remove(player);
        PartyManager.removeParty(player);

        if (kick) {
            this.sendMessage(player.getName() + " has been kicked from the party!");
            PartyUtils.sendPartyMessage(player, "You were kicked from " + this.name + "'s party!");
        } else {
            this.sendMessage(player.getName() + " left the party!" + (online ? "" : ChatColor.GRAY + " (disconnected)"));

            if (online) {
                PartyUtils.sendPartyMessage(player, "You left " + this.name + "'s party!");
            }
        }

        if (player == this.leader) {
            this.setLeader(this.members.get(0));
        }
    }

    public void sendMessage(ProxiedPlayer sender, String message) {
        this.sendMessage(sender.getName() + ": " + message);
    }

    public void sendMessage(String message) {
        this.members.forEach(member -> PartyUtils.sendPartyMessage(member, this.hasCustomName() ? this.name : "Party", message));
    }

    public void warp() {
        this.warp(ServerUtils.getGameServer(this.leader));
    }

    public void warp(GameServer server) {
        if (server == null) {
            ServerInfo hub = ProxyServer.getInstance().getServerInfo("Hub");
            this.sendMessage("Warping party members to server " + ChatColor.AQUA + hub.getName());
            this.members.stream().filter(member -> member != this.leader && !ServerUtils.isRankedPlayer(member)).forEach(member -> member.connect(hub));
        } else {
            this.sendMessage("Warping party members to server " + ChatColor.AQUA + server.getName());
            this.members.stream().filter(member -> member != this.leader && !ServerUtils.isRankedPlayer(member)).forEach(server::connect);
        }
    }

    public void disband() {
        this.sendMessage("The party you were in has been disbanded!");
        PartyManager.removeParty(this);
        Spring.callEvent(new PartyDisbandEvent(this));
    }

    public Map<String, Object> toData() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);
        data.put("created", this.created);
        data.put("leader", this.leader.getUniqueId());
        data.put("members", this.members.stream().map(ProxiedPlayer::getUniqueId).collect(Collectors.toList()));
        data.put("name", this.name);
        return data;
    }

}
