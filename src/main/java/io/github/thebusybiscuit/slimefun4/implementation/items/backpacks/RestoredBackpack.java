package io.github.thebusybiscuit.slimefun4.implementation.items.backpacks;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

/**
 * This class represents a {@link SlimefunBackpack} that has been restored via /sf backpack for retrieving items if the
 * original has been lost.
 * This backpack cannot be crafted nor crafted into other items. Its purpose is exclusively that of restoring
 * the lost inventory and shouldn't be used as a backpack replacement.
 * Right-Clicking will open the {@link Inventory} of the restored Backpack.
 *
 * @author Sfiguz7
 *
 * @see SlimefunBackpack
 * @see PlayerBackpack
 */
public class RestoredBackpack extends SlimefunBackpack {

    /**
     * This will create a new {@link SlimefunBackpack} with the given arguments.
     *
     * @param category
     *            the category to bind this {@link SlimefunBackpack} to
     */
    @ParametersAreNonnullByDefault
    public RestoredBackpack(@Nonnull ItemGroup category) {
        super(54, category, SlimefunItems.RESTORED_BACKPACK, RecipeType.NULL, new ItemStack[9]);

        this.hidden = true;
    }
}
