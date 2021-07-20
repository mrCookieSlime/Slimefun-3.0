package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.EnhancedCraftingTable;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.AutoCrafterListener;

/**
 * The {@link EnhancedAutoCrafter} is an implementation of the {@link AbstractAutoCrafter}.
 * It can craft items that are crafted using the {@link EnhancedCraftingTable}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see AbstractAutoCrafter
 * @see VanillaAutoCrafter
 * @see SlimefunItemRecipe
 * @see AutoCrafterListener
 *
 */
public class EnhancedAutoCrafter extends SlimefunAutoCrafter {

    @ParametersAreNonnullByDefault
    public EnhancedAutoCrafter(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe, RecipeType.ENHANCED_CRAFTING_TABLE);
    }

}
