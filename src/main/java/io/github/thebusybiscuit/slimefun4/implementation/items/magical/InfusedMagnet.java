package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.DoubleRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;

/**
 * The {@link InfusedMagnet} is a {@link SlimefunItem} that allows a {@link Player} to
 * automatically pick up items in a certain radius while holding shift and having an
 * {@link InfusedMagnet} in their {@link Inventory}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class InfusedMagnet extends UnplaceableBlock {

    private final ItemSetting<Double> radius = new DoubleRangeSetting(this, "pickup-radius", 0.1, 6.0, Double.MAX_VALUE);

    @ParametersAreNonnullByDefault
    public InfusedMagnet(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(radius);
    }

    /**
     * This returns the radius in which items are picked up
     * 
     * @return The radius of the {@link InfusedMagnet}
     */
    public double getRadius() {
        return radius.getValue();
    }

}
