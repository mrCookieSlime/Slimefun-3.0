package io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.JetpackTask;

/**
 * {@link JetBoots} allow you to fly up into the air.
 * You can find the actual behaviour in the {@link JetpackTask} class.
 * 
 * @author TheBusyBiscuit
 * 
 * @see JetBoots
 * @see JetpackTask
 *
 */
public class Jetpack extends SlimefunItem implements Rechargeable {

    private final double thrust;
    private final float capacity;

    @ParametersAreNonnullByDefault
    public Jetpack(ItemGroup category, SlimefunItemStack item, ItemStack[] recipe, double thrust, float capacity) {
        super(category, item, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);

        this.thrust = thrust;
        this.capacity = capacity;
    }

    public double getThrust() {
        return thrust;
    }

    @Override
    public float getMaxItemCharge(ItemStack item) {
        return capacity;
    }

}
