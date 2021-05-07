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
import net.purelic.spring.commands.ProxyCommand;
import net.purelic.spring.commands.parsers.Permission;
import net.purelic.spring.commands.parsers.ProfileArgument;
import net.purelic.spring.profile.Profile;
import net.purelic.spring.punishment.Punishment;
import net.purelic.spring.utils.CommandUtils;
import net.purelic.spring.utils.ItemUtils;
import net.purelic.spring.utils.PunishmentUtils;

import java.util.Optional;

public class AppealCommand extends PunishmentUtils implements ProxyCommand {

    @Override
    public Command.Builder<CommandSender> getCommandBuilder(BungeeCommandManager<CommandSender> mgr) {
        return mgr.commandBuilder("appeal")
            .senderType(ProxiedPlayer.class)
            // .permission(Permission.isStaff())
            .argument(ProfileArgument.of("player"))
            .argument(StringArgument.optional("punishment id"))
            .handler(c -> {
                if (!Permission.isStaff(c)) return;

                ProxiedPlayer player = (ProxiedPlayer) c.getSender();
                Profile profile = c.get("player");
                Optional<Object> punishmentIdArg = c.getOptional("punishment id");

                if (punishmentIdArg.isPresent()) {
                    profile.appealPunishment(player, (String) punishmentIdArg.get());
                    return;
                }

                int rows = (profile.getPunishments().size() / 9) + 1;
                Inventory inventory = new Inventory(InventoryType.getChestInventoryWithRows(rows), new TextComponent("Select a punishment:"));

                int count = 0;

                for (Punishment punishment : profile.getPunishments()) {
                    if (punishment.isAppealed()) continue;
                    ItemStack item = ItemUtils.getPunishmentItem(punishment, profile.getId());
                    inventory.setItem(count, item);
                    count++;
                }

                if (count == 0) {
                    CommandUtils.sendErrorMessage(player, "This player has no punishments to appeal!");
                } else {
                    InventoryModule.sendInventory(player, inventory);
                }
            });
    }

}
