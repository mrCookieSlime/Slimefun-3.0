package io.github.thebusybiscuit.slimefun4.implementation.items.misc;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.FoodFabricator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators.AnimalGrowthAccelerator;

/**
 * {@link OrganicFood} is created using a {@link FoodFabricator} and can
 * be used to fuel an {@link AnimalGrowthAccelerator}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see AnimalGrowthAccelerator
 *
 */
public class OrganicFood extends SlimefunItem {

    public static final int OUTPUT = 2;

    @ParametersAreNonnullByDefault
    public OrganicFood(ItemGroup category, SlimefunItemStack item, Material ingredient) {
        super(category, item, RecipeType.FOOD_FABRICATOR, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(ingredient), null, null, null, null, null, null, null }, new SlimefunItemStack(item, OUTPUT));
    }

}
