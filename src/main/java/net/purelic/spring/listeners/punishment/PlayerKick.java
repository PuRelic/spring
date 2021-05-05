package net.purelic.spring.listeners.punishment;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.events.PlayerKickEvent;

public class PlayerKick implements Listener {

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        event.getPlayer().disconnect(new TextComponent(
            ChatColor.RED + "" + ChatColor.BOLD + "Kicked!" + "\n\n" + ChatColor.RESET +
                ChatColor.RED + event.getReason() + "\n\n" +
                ChatColor.WHITE + "Please read the rules at " +
                ChatColor.AQUA + "purelic.net/rules"
        ));
    }

}
