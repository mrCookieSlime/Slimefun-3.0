package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

/**
 * The {@link ExplosivePickaxe} is a pickaxe which can destroy {@link Block}s
 * in a size of 3 by 3. It also creates a explosion animation.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ExplosiveShovel
 * @see ExplosiveTool
 *
 */
public class ExplosivePickaxe extends ExplosiveTool {

    @ParametersAreNonnullByDefault
    public ExplosivePickaxe(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

}
