package net.purelic.spring.commands.staff;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.PunishmentUtils;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.punishment.Punishment;
import net.purelic.spring.utils.*;
import org.apache.commons.lang.WordUtils;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class LookupCommand extends PunishmentUtils implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("lookup")
            .senderType(ProxiedPlayer.class)
            .argument(StringArgument.of("player"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                String target = c.get("player");
                ProxiedPlayer targetPlayer = Spring.getPlayer(target);
                boolean targetOnline = targetPlayer != null;
                Profile punishmentProfile;

                if (!targetOnline) {
                    UUID targetId = Fetcher.getUUIDOf(target);

                    if (targetId == null) {
                        CommandUtils.sendNoPlayerMessage(player, target);
                        return;
                    }

                    punishmentProfile = ProfileManager.getProfile(targetId);
                    ChatUtils.sendMessage(player, ChatUtils.getHeader(punishmentProfile.getName() + "'s Punishments", ChatColor.DARK_AQUA, ChatColor.WHITE));
                } else {
                    punishmentProfile = ProfileManager.getProfile(targetPlayer);
                    ChatUtils.sendMessage(player, ChatUtils.getHeader(targetPlayer.getName() + "'s Punishments", ChatColor.AQUA, ChatColor.WHITE));
                }

                int numPunishments = punishmentProfile.getPunishments().size();

                if (numPunishments == 0) {
                    ChatUtils.sendMessage(player, " • This player has a clean record!");
                    return;
                }

                int numAppealed = 0;
                int numStale = 0;

                for (Punishment punishment : punishmentProfile.getPunishments()) {
                    if (punishment.isAppealed()) numAppealed++;
                    if (punishment.isStale()) numStale++;
                }

                if (numAppealed + numStale == numPunishments && !PermissionUtils.isStaff(player)) {
                    ChatUtils.sendMessage(player, " • This player has a clean record!");
                    return;
                }

                ComponentBuilder builder = new ComponentBuilder("");
                boolean first = true;

                for (Punishment punishment : punishmentProfile.getPunishments()) {
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

                player.sendMessage(builder.create());
            });
    }

}
