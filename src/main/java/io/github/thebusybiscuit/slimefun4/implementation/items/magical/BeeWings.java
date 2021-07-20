package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.BeeWingsListener;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.BeeWingsTask;

/**
 * The {@link BeeWings} are a special form of the elytra which gives you a slow falling effect
 * when you approach the ground.
 *
 * @author beSnow
 * @author TheBusyBiscuit
 * 
 * @see BeeWingsListener
 * @see BeeWingsTask
 * 
 */
public class BeeWings extends SlimefunItem {

    @ParametersAreNonnullByDefault
    public BeeWings(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

}
