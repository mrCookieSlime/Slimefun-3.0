package io.github.thebusybiscuit.slimefun4.implementation.items.misc;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.Smeltery;

/**
 * An {@link AlloyIngot} is a blend of different metals and resources.
 * These ingots can be crafted using a {@link Smeltery}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Smeltery
 *
 */
public class AlloyIngot extends SlimefunItem {

    @ParametersAreNonnullByDefault
    public AlloyIngot(ItemGroup category, SlimefunItemStack item, ItemStack[] recipe) {
        super(category, item, RecipeType.SMELTERY, recipe);
    }

}
