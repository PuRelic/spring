package net.purelic.spring.commands.staff;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bungee.BungeeCommandManager;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.items.ItemStack;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.purelic.spring.Spring;
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.utils.PunishmentUtils;
import net.purelic.spring.managers.ProfileManager;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.punishment.Punishment;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.Fetcher;
import net.purelic.spring.utils.ItemUtils;
import net.purelic.spring.utils.PermissionUtils;

import java.util.Optional;
import java.util.UUID;

public class AppealCommand extends PunishmentUtils implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("appeal")
            .senderType(ProxiedPlayer.class)
            .argument(StringArgument.of("player"))
            .argument(StringArgument.optional("punishment id"))
            .handler(c -> {
                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                String target = c.get("player");
                ProxiedPlayer targetPlayer = Spring.getPlayer(target);
                boolean targetOnline = targetPlayer != null;
                Optional<Object> punishmentIdArg = c.getOptional("punishment id");

                if (!PermissionUtils.isStaff(player)) {
                    CommandUtils.sendNoPermissionMessage(player);
                    return;
                }

                Profile punishmentProfile;
                UUID targetId;

                if (!targetOnline) {
                    targetId = Fetcher.getUUIDOf(target);

                    if (targetId == null) {
                        CommandUtils.sendNoPlayerMessage(player, target);
                        return;
                    }

                    punishmentProfile = ProfileManager.getProfile(targetId);
                } else {
                    targetId = targetPlayer.getUniqueId();
                    punishmentProfile = ProfileManager.getProfile(targetPlayer);
                }

                if (!punishmentIdArg.isPresent()) {
                    int rows = (punishmentProfile.getPunishments().size() / 9) + 1;
                    Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(rows), new TextComponent("Select a punishment:"));

                    int count = 0;

                    for (Punishment punishment : punishmentProfile.getPunishments()) {
                        if (punishment.isAppealed()) continue;
                        ItemStack item = ItemUtils.getPunishmentItem(punishment, targetId);
                        inventory.setItem(count, item);
                        count++;
                    }

                    if (count == 0) {
                        CommandUtils.sendErrorMessage(player, "This player has no punishments to appeal!");
                    } else {
                        InventoryModule.sendInventory(player, inventory);
                    }
                } else {
                    punishmentProfile.appealPunishment(player, (String) punishmentIdArg.get());
                }
            });
    }

}
