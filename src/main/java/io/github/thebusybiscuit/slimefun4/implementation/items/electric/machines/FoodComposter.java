package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.OrganicFertilizer;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;

public class FoodComposter extends AContainer implements RecipeDisplayItem {

    public FoodComposter(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected void registerDefaultRecipes() {
        registerRecipe(30, new ItemStack[] { SlimefunItems.WHEAT_ORGANIC_FOOD }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.WHEAT_FERTILIZER, OrganicFertilizer.OUTPUT) });
        registerRecipe(30, new ItemStack[] { SlimefunItems.CARROT_ORGANIC_FOOD }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.CARROT_FERTILIZER, OrganicFertilizer.OUTPUT) });
        registerRecipe(30, new ItemStack[] { SlimefunItems.POTATO_ORGANIC_FOOD }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.POTATO_FERTILIZER, OrganicFertilizer.OUTPUT) });
        registerRecipe(30, new ItemStack[] { SlimefunItems.SEEDS_ORGANIC_FOOD }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.SEEDS_FERTILIZER, OrganicFertilizer.OUTPUT) });
        registerRecipe(30, new ItemStack[] { SlimefunItems.BEETROOT_ORGANIC_FOOD }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.BEETROOT_FERTILIZER, OrganicFertilizer.OUTPUT) });
        registerRecipe(30, new ItemStack[] { SlimefunItems.MELON_ORGANIC_FOOD }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.MELON_FERTILIZER, OrganicFertilizer.OUTPUT) });
        registerRecipe(30, new ItemStack[] { SlimefunItems.APPLE_ORGANIC_FOOD }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.APPLE_FERTILIZER, OrganicFertilizer.OUTPUT) });
        registerRecipe(30, new ItemStack[] { SlimefunItems.KELP_ORGANIC_FOOD }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.KELP_FERTILIZER, OrganicFertilizer.OUTPUT) });
        registerRecipe(30, new ItemStack[] { SlimefunItems.COCOA_ORGANIC_FOOD }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.COCOA_FERTILIZER, OrganicFertilizer.OUTPUT) });
        registerRecipe(30, new ItemStack[] { SlimefunItems.SWEET_BERRIES_ORGANIC_FOOD }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.SWEET_BERRIES_FERTILIZER, OrganicFertilizer.OUTPUT) });
    }

    @Override
    public String getMachineIdentifier() {
        return "FOOD_COMPOSTER";
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.GOLDEN_HOE);
    }

}
