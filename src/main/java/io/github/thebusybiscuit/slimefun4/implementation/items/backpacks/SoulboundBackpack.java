package io.github.thebusybiscuit.slimefun4.implementation.items.backpacks;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Soulbound;

/**
 * This implementation of {@link SlimefunBackpack} is also {@link Soulbound}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class SoulboundBackpack extends SlimefunBackpack implements Soulbound {

    @ParametersAreNonnullByDefault
    public SoulboundBackpack(int size, ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(size, category, item, recipeType, recipe);
    }

}
