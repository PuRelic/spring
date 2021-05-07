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
import org.jetbrains.annotations.NotNull;

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

    public static boolean notPremium(@NotNull CommandContext<CommandSender> context) {
        return notPremium((ProxiedPlayer) context.getSender());
    }

    public static boolean notPremium(ProxiedPlayer player) {
        return notPremium(player, null);
    }

    public static boolean notPremium(@NotNull CommandContext<CommandSender> context, String message) {
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

    // Temp checks since PermissionPredicates don't work :)

    public static boolean isAdmin(@NotNull CommandContext<CommandSender> context) {
        return hasPermission(context);
    }

    public static boolean isStaff(@NotNull CommandContext<CommandSender> context) {
        return hasPermission(context, Rank.MAP_DEVELOPER, Rank.MODERATOR, Rank.HELPER);
    }

    private static boolean hasPermission(@NotNull CommandContext<CommandSender> context, Rank... ranks) {
        if (!(context.getSender() instanceof ProxiedPlayer)) return false;

        ProxiedPlayer player = (ProxiedPlayer) context.getSender();
        Profile profile = ProfileManager.getProfile(player);
        boolean hasPermission = PermissionUtils.isAdmin(player) || profile.hasRank(ranks);

        if (!hasPermission) {
            CommandUtils.sendErrorMessage(player, "You don't have permission to use this command!");
        }

        return hasPermission;
    }

}
