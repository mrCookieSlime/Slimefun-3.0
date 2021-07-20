package io.github.thebusybiscuit.slimefun4.implementation.items.misc;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.VillagerTradingListener;

/**
 * The {@link SyntheticEmerald} is an almost normal emerald.
 * It can even be used to trade with {@link Villager Villagers}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see VillagerTradingListener
 *
 */
public class SyntheticEmerald extends SlimefunItem {

    @ParametersAreNonnullByDefault
    public SyntheticEmerald(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        setUseableInWorkbench(true);
    }

}
