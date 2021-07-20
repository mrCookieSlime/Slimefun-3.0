package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * {@link ChargeCommand} adds an in game command which charges any {@link Rechargeable}
 * item to maximum charge, defined by {@link Rechargeable#getMaxItemCharge(ItemStack)}.
 *
 * @author FluffyBear
 *
 */
class ChargeCommand extends SubCommand {

    @ParametersAreNonnullByDefault
    ChargeCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "charge", false);
    }

    @Override
    protected String getDescription() {
        return "commands.charge.description";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("slimefun.command.charge")) {
                Player p = (Player) sender;
                ItemStack item = p.getInventory().getItemInMainHand();
                SlimefunItem slimefunItem = SlimefunItem.getByItem(item);

                if (slimefunItem instanceof Rechargeable) {
                    Rechargeable rechargeableItem = (Rechargeable) slimefunItem;
                    rechargeableItem.setItemCharge(item, rechargeableItem.getMaxItemCharge(item));
                    Slimefun.getLocalization().sendMessage(sender, "commands.charge.charge-success", true);
                } else {
                    Slimefun.getLocalization().sendMessage(sender, "commands.charge.not-rechargeable", true);
                }
            } else {
                Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            }
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
        }
    }
}
