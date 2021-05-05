package net.purelic.spring.commands.parsers;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.keys.SimpleCloudKey;
import cloud.commandframework.permission.PredicatePermission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.profile.Rank;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.PermissionUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Permission {

    public static PredicatePermission<CommandSender> of(Rank... ranks) {
        return PredicatePermission.of(
            SimpleCloudKey.of("rank"), sender -> {
                if (!(sender instanceof ProxiedPlayer)) return false;

                ProxiedPlayer player = (ProxiedPlayer) sender;
                Profile profile = ProfileManager.getProfile(player);

                return PermissionUtils.isAdmin(player) || profile.hasRank(ranks);
            }
        );
    }

    public static PredicatePermission<CommandSender> isAdmin() {
        return of();
    }

    public static PredicatePermission<CommandSender> isStaff() {
        return of(Rank.MAP_DEVELOPER, Rank.MODERATOR, Rank.HELPER);
    }

    public static PredicatePermission<CommandSender> isMod() {
        return of(Rank.MAP_DEVELOPER, Rank.MODERATOR, Rank.HELPER);
    }

    public static PredicatePermission<CommandSender> isMapDev() {
        return of(Rank.MAP_DEVELOPER);
    }

    public static boolean notPremium(@NonNull CommandContext<CommandSender> context) {
        return notPremium((ProxiedPlayer) context.getSender());
    }

    public static boolean notPremium(ProxiedPlayer player) {
        return notPremium(player, null);
    }

    public static boolean notPremium(@NonNull CommandContext<CommandSender> context, String message) {
        return notPremium((ProxiedPlayer) context.getSender(), message);
    }

    @SuppressWarnings("deprecation")
    public static boolean notPremium(ProxiedPlayer player, String message) {
        boolean hasPermission = PermissionUtils.isDonator(player);

        if (!hasPermission) {
            CommandUtils.sendErrorMessage(player,
                new ComponentBuilder(message != null ? message : "Only Premium players can use this feature!")
                    .color(ChatColor.RED)
                    .append(" Consider donating at ")
                    .color(ChatColor.RED)
                    .append("purelic.net/donate")
                    .color(ChatColor.AQUA)
                    .underlined(true)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to Open").create()))
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://purelic.net/donate"))
                    .create()
            );
        }

        return !hasPermission;
    }

    public static PredicatePermission<CommandSender> isCreator() {
        return of(Rank.CREATOR);
    }

}
