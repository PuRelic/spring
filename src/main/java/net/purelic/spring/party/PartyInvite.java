package net.purelic.spring.party;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.purelic.spring.managers.PartyManager;
import net.purelic.spring.utils.NickUtils;
import net.purelic.spring.utils.PartyUtils;
import net.purelic.spring.utils.TaskUtils;

public class PartyInvite {

    private final Party party;
    private final ProxiedPlayer sender;
    private final ProxiedPlayer invited;
    private ScheduledTask timer;

    public PartyInvite(Party party, ProxiedPlayer sender, ProxiedPlayer invited) {
        this.party = party;
        this.sender = sender;
        this.invited = invited;
        this.timer = null;
    }

    public Party getParty() {
        return this.party;
    }

    public ProxiedPlayer getSender() {
        return this.sender;
    }

    public ProxiedPlayer getInvited() {
        return this.invited;
    }

    private Runnable getTimer() {
        return () -> {
            this.party.sendMessage("The party invite to " + NickUtils.getDisplayName(this.invited, this.sender) + " has expired!");
            PartyUtils.sendPartyMessage(this.invited, "Your party invite from " + NickUtils.getDisplayName(this.sender, this.invited) + " has expired!");
            this.remove();
        };
    }

    @SuppressWarnings("deprecation")
    public void send() {
        BaseComponent[] message = new ComponentBuilder(NickUtils.getDisplayName(this.sender, this.invited) + " has invited you to " + (this.party.hasCustomName() ? this.party.getName() + "'s" : "their") + " party  ")
            .append("[ACCEPT]")
            .color(ChatColor.GREEN)
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Accept").color(ChatColor.GREEN).create()))
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + NickUtils.getNick(this.sender)))
            .append("  ")
            .append("[DENY]")
            .color(ChatColor.RED)
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Deny").color(ChatColor.RED).create()))
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + NickUtils.getNick(this.sender)))
            .create();

        PartyUtils.sendPartyMessage(this.invited, message);
        this.party.sendMessage(NickUtils.getNick(this.sender) + " invited " + NickUtils.getNick(this.invited) + " to the party! They have 30 seconds to respond.");

        this.startTimer();
    }

    private void startTimer() {
        this.timer = TaskUtils.scheduleTask(this.getTimer(), 30L);
    }

    public void accept() {
        this.party.add(this.invited);
        this.party.sendMessage(NickUtils.getNick(this.invited) + " joined the party!");
        this.remove();
    }

    public void deny() {
        PartyUtils.sendPartyMessage(this.invited, "You denied " + this.sender.getName() + "'s party invite!");
        this.party.sendMessage(NickUtils.getNick(this.invited) + " denied the party invite!");
        this.remove();
    }

    private void cancelTimer() {
        if (this.timer == null) return;
        this.timer.cancel();
    }

    public void remove() {
        this.cancelTimer();
        PartyManager.removeInvite(this);
    }

}
