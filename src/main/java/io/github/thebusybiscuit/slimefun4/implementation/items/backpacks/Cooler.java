package io.github.thebusybiscuit.slimefun4.implementation.items.backpacks;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.Juice;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.CoolerListener;

/**
 * The {@link Cooler} is a special variant of the {@link SlimefunBackpack}.
 * It can only hold {@link Juice Juices} and auto-consumes those when a {@link Player}
 * loses hunger while carrying a {@link Cooler} filled with {@link Juice}.
 * 
 * @author TheBusyBiscuit
 *
 * @see Juice
 * @see CoolerListener
 *
 */
public class Cooler extends SlimefunBackpack {

    @ParametersAreNonnullByDefault
    public Cooler(int size, ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(size, category, item, recipeType, recipe);
    }

    @Override
    public boolean isItemAllowed(ItemStack item, SlimefunItem itemAsSlimefunItem) {
        // A Cooler only allows Juices
        return itemAsSlimefunItem instanceof Juice;
    }

}
