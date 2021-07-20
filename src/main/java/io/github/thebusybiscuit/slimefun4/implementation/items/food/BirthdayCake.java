package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;

/**
 * The {@link BirthdayCake} is a seasonal item which only appears in october.
 * This is just a nod to me, TheBusyBiscuit, being born on October 26th.
 * 
 * @author TheBusyBiscuit
 * 
 */
public class BirthdayCake extends SlimefunItem implements NotPlaceable {

    @ParametersAreNonnullByDefault
    public BirthdayCake(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

}
