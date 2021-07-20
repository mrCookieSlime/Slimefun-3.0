package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.OrganicFood;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;

public class FoodFabricator extends AContainer {

    public FoodFabricator(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected void registerDefaultRecipes() {
        registerRecipe(12, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(Material.WHEAT) }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.WHEAT_ORGANIC_FOOD, OrganicFood.OUTPUT) });
        registerRecipe(12, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(Material.CARROT) }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.CARROT_ORGANIC_FOOD, OrganicFood.OUTPUT) });
        registerRecipe(12, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(Material.POTATO) }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.POTATO_ORGANIC_FOOD, OrganicFood.OUTPUT) });
        registerRecipe(12, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(Material.WHEAT_SEEDS) }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.SEEDS_ORGANIC_FOOD, OrganicFood.OUTPUT) });
        registerRecipe(12, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(Material.BEETROOT) }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.BEETROOT_ORGANIC_FOOD, OrganicFood.OUTPUT) });
        registerRecipe(12, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(Material.MELON_SLICE) }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.MELON_ORGANIC_FOOD, OrganicFood.OUTPUT) });
        registerRecipe(12, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(Material.APPLE) }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.APPLE_ORGANIC_FOOD, OrganicFood.OUTPUT) });
        registerRecipe(12, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(Material.DRIED_KELP) }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.KELP_ORGANIC_FOOD, OrganicFood.OUTPUT) });
        registerRecipe(12, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(Material.COCOA_BEANS) }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.COCOA_ORGANIC_FOOD, OrganicFood.OUTPUT) });
        registerRecipe(12, new ItemStack[] { SlimefunItems.TIN_CAN, new ItemStack(Material.SWEET_BERRIES) }, new ItemStack[] { new SlimefunItemStack(SlimefunItems.SWEET_BERRIES_ORGANIC_FOOD, OrganicFood.OUTPUT) });
    }

    @Override
    public String getMachineIdentifier() {
        return "FOOD_FABRICATOR";
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.GOLDEN_HOE);
    }

}
