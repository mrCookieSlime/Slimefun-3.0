package io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.DoubleRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.ArmorTask;

/**
 * The {@link SolarHelmet} can be worn by {@link Player}.
 * As long as that {@link Player} has contact with sunlight, the helmet will charge any
 * {@link Rechargeable} {@link SlimefunItem} that this {@link Player} is currently wearing
 * or holding.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ArmorTask
 * @see Rechargeable
 *
 */
public class SolarHelmet extends SlimefunItem {

    private final ItemSetting<Double> charge;

    @ParametersAreNonnullByDefault
    public SolarHelmet(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, double defaultChargingLevel) {
        super(category, item, recipeType, recipe);

        if (defaultChargingLevel <= 0) {
            throw new IllegalArgumentException("A Solar Helmet must have a positive charging level!");
        }

        charge = new DoubleRangeSetting(this, "charge-amount", 0.01, defaultChargingLevel, Double.MAX_VALUE);
        addItemSetting(charge);
    }

    /**
     * This method recharges the equipment of the given {@link Player} by the configured
     * factor of this {@link SolarHelmet}.
     * 
     * @param p
     *            The {@link Player} wearing this {@link SolarHelmet}
     */
    public void rechargeItems(@Nonnull Player p) {
        PlayerInventory inv = p.getInventory();

        // No need to charge the helmet since that slot is occupied by the Solar Helmet
        recharge(inv.getChestplate());
        recharge(inv.getLeggings());
        recharge(inv.getBoots());

        recharge(inv.getItemInMainHand());
        recharge(inv.getItemInOffHand());
    }

    private void recharge(@Nullable ItemStack item) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem instanceof Rechargeable) {
            ((Rechargeable) sfItem).addItemCharge(item, charge.getValue().floatValue());
        }
    }

}
