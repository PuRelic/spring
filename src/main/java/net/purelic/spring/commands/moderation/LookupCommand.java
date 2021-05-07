package net.purelic.spring.commands.moderation;

import cloud.commandframework.Command;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.ProfileArgument;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.punishment.Punishment;
import net.purelic.spring.utils.*;
import org.apache.commons.lang.WordUtils;

@SuppressWarnings("deprecation")
public class LookupCommand extends PunishmentUtils implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("lookup")
            .senderType(ProxiedPlayer.class)
            .argument(ProfileArgument.of("player"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                Profile profile = c.get("player");
                boolean isStaff = PermissionUtils.isStaff(player);

                ChatUtils.sendMessage(player,
                    ChatUtils.getHeader(isStaff ? player.getName() : NickUtils.getName(profile) + "'s Punishments"));

                int numPunishments = profile.getPunishments().size();

                if (numPunishments == 0) {
                    ChatUtils.sendMessage(player, " • This player has a clean record!");
                    return;
                }

                int numAppealed = 0;
                int numStale = 0;

                for (Punishment punishment : profile.getPunishments()) {
                    if (punishment.isAppealed()) numAppealed++;
                    if (punishment.isStale()) numStale++;
                }

                // Don't show appealed or stale punishments to non-staff
                if (numAppealed + numStale == numPunishments && !PermissionUtils.isStaff(player)) {
                    ChatUtils.sendMessage(player, " • This player has a clean record!");
                    return;
                }

                ComponentBuilder builder = new ComponentBuilder("");
                boolean first = true;

                for (Punishment punishment : profile.getPunishments()) {
                    if (!PermissionUtils.isStaff(player) && (punishment.isAppealed() || punishment.isStale())) continue;

                    builder.append((first ? "" : "\n") + " • ").color(ChatColor.WHITE)
                        .append(WordUtils.capitalizeFully(punishment.getType().getPastTense()))
                        .color(ItemUtils.getPunishmentColor(punishment));

                    first = false;

                    ComponentBuilder hover = new ComponentBuilder("Punished by ").color(ChatColor.DARK_AQUA);
                    ProxiedPlayer punisherPlayer = Spring.getPlayer(punishment.getPunisher());

                    if (punisherPlayer != null) {
                        hover.append(ChatColor.DARK_AQUA + punisherPlayer.getName());
                    } else {
                        hover.append(ChatColor.DARK_AQUA + Fetcher.getNameOf(punishment.getPunisher()));
                    }

                    hover.append("\n").append("Received " + ChatUtils.format(punishment.getTimestamp().toDate())).color(ChatColor.DARK_AQUA);

                    if (punishment.hasExpirationTimestamp()) {
                        hover.append("\n").append((punishment.isExpired() ? "Expired " : "Expires ") + ChatUtils.format(punishment.getExpirationTimestamp().toDate()))
                            .color(ChatColor.DARK_AQUA);
                    }

                    if (punishment.isAppealed()) {
                        hover.append("\n").append("Appealed by ").color(ChatColor.DARK_AQUA);
                        ProxiedPlayer appealedBy = Spring.getPlayer(punishment.getAppealedBy());

                        if (appealedBy != null) {
                            hover.append(ChatColor.DARK_AQUA + appealedBy.getName());
                        } else {
                            hover.append(ChatColor.DARK_AQUA + Fetcher.getNameOf(punishment.getAppealedBy()));
                        }

                        hover.append("\n").append("Appealed " + ChatUtils.format(punishment.getAppealedTimestamp().toDate()))
                            .color(ChatColor.DARK_AQUA);
                    }

                    builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()))
                        .append(" for: \"" + punishment.getReason() + "\"").color(ChatColor.WHITE);
                }

                ChatUtils.sendMessage(player, builder);
            });
    }

}
