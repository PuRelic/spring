package net.purelic.spring.listeners.punishment;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.purelic.spring.events.PlayerWarnEvent;
import net.purelic.spring.utils.ChatUtils;

public class PlayerWarn implements Listener {

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerWarn(PlayerWarnEvent event) {
        ProxiedPlayer player = event.getPlayer();

        ChatUtils.sendMessage(player, ChatUtils.getHeader("WARNING", true, ChatColor.RED, ChatColor.DARK_RED, true));
        ChatUtils.sendMessage(player,
                new ComponentBuilder(event.getReason()).reset().color(ChatColor.RED)
                .append("\n" + (event.hasSeen() ? "" : "\nYou received this warning while offline")).reset().color(ChatColor.GRAY)
                .append("\nPlease read the rules at ").reset()
                .append("purelic.net/rules").color(ChatColor.AQUA).underlined(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Open").create()))
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://purelic.net/rules"))
                .create());
        ChatUtils.sendMessage(player, ChatUtils.getHeader("WARNING", true, ChatColor.RED, ChatColor.DARK_RED, true));

        showGuardianEffect(player);
    }

    // TODO reimplement on the proxy (if possible)
    private void showGuardianEffect(ProxiedPlayer player) {
//        float f = 0;
//        float data = 0;
//        int count = 1;
//        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.MOB_APPEARANCE, false, f, f, f, f, f, f, data, count);
//        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//        player.playSound(player.getLocation(), "mob.guardian.curse", 10.0f, 1.0f);
    }

}
