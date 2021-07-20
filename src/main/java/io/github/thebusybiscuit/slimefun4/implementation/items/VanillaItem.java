package io.github.thebusybiscuit.slimefun4.implementation.items;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

/**
 * Represents a vanilla item that is overridden by Slimefun (like {@code ELYTRA}).
 * <p>
 * A {@link VanillaItem} uses a non-modified {@link ItemStack} (no display name neither lore).
 * When a {@link VanillaItem} gets disabled, its {@link ItemState} goes on {@code State.VANILLA} which
 * automatically
 * replace it in the recipes by its vanilla equivalent.
 * 
 * A {@link VanillaItem} is also automatically useable in workbenches.
 *
 * @author TheBusyBiscuit
 * 
 * @see SlimefunItem
 *
 */
public class VanillaItem extends SlimefunItem {

    /**
     * Instantiates a new {@link VanillaItem} with the given arguments.
     *
     * @param itemGroup
     *            the category to bind this {@link VanillaItem} to
     * @param item
     *            the item corresponding to this {@link VanillaItem}
     * @param id
     *            the id of this {@link VanillaItem}
     * @param recipeType
     *            the type of the recipe to obtain this {@link VanillaItem}
     * @param recipe
     *            the recipe to obtain this {@link VanillaItem}
     */
    @ParametersAreNonnullByDefault
    public VanillaItem(ItemGroup itemGroup, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, id, recipeType, recipe);

        useableInWorkbench = true;
    }
}
