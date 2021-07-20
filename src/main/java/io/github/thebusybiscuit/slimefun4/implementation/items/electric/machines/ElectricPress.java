package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;

/**
 * The {@link ElectricPress} is a pretty simple electrical machine.
 * It allows you to compact items into their block variant, e.g. 9 diamonds into a diamond block.
 * 
 * @author TheBusyBiscuit
 *
 */
public class ElectricPress extends AContainer implements RecipeDisplayItem {

    @ParametersAreNonnullByDefault
    public ElectricPress(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected void registerDefaultRecipes() {
        addRecipe(4, new SlimefunItemStack(SlimefunItems.STONE_CHUNK, 3), new ItemStack(Material.COBBLESTONE));
        addRecipe(4, new ItemStack(Material.FLINT, 6), new ItemStack(Material.COBBLESTONE));
        addRecipe(5, new ItemStack(Material.GLASS), new ItemStack(Material.GLASS_PANE, 3));
        addRecipe(4, new ItemStack(Material.SNOWBALL, 4), new ItemStack(Material.SNOW_BLOCK));
        addRecipe(4, new ItemStack(Material.MAGMA_CREAM, 4), new ItemStack(Material.MAGMA_BLOCK));
        addRecipe(4, new ItemStack(Material.SLIME_BALL, 9), new ItemStack(Material.SLIME_BLOCK));

        addRecipe(3, new ItemStack(Material.DRIED_KELP, 9), new ItemStack(Material.DRIED_KELP_BLOCK));
        addRecipe(3, new ItemStack(Material.BONE_MEAL, 9), new ItemStack(Material.BONE_BLOCK));
        addRecipe(3, new ItemStack(Material.CLAY_BALL, 4), new ItemStack(Material.CLAY));
        addRecipe(3, new ItemStack(Material.BRICK, 4), new ItemStack(Material.BRICKS));

        addRecipe(6, SlimefunItems.COPPER_INGOT, new CustomItemStack(SlimefunItems.COPPER_WIRE, 3));
        addRecipe(16, new SlimefunItemStack(SlimefunItems.STEEL_INGOT, 8), SlimefunItems.STEEL_PLATE);
        addRecipe(18, new SlimefunItemStack(SlimefunItems.REINFORCED_ALLOY_INGOT, 8), SlimefunItems.REINFORCED_PLATE);

        addRecipe(8, new ItemStack(Material.NETHER_WART), new CustomItemStack(SlimefunItems.MAGIC_LUMP_1, 2));
        addRecipe(10, new SlimefunItemStack(SlimefunItems.MAGIC_LUMP_1, 4), SlimefunItems.MAGIC_LUMP_2);
        addRecipe(12, new SlimefunItemStack(SlimefunItems.MAGIC_LUMP_2, 4), SlimefunItems.MAGIC_LUMP_3);

        addRecipe(10, new ItemStack(Material.ENDER_EYE), new CustomItemStack(SlimefunItems.ENDER_LUMP_1, 2));
        addRecipe(12, new SlimefunItemStack(SlimefunItems.ENDER_LUMP_1, 4), SlimefunItems.ENDER_LUMP_2);
        addRecipe(14, new SlimefunItemStack(SlimefunItems.ENDER_LUMP_2, 4), SlimefunItems.ENDER_LUMP_3);

        addRecipe(18, new SlimefunItemStack(SlimefunItems.TINY_URANIUM, 9), SlimefunItems.SMALL_URANIUM);
        addRecipe(24, new SlimefunItemStack(SlimefunItems.SMALL_URANIUM, 4), SlimefunItems.URANIUM);

        addRecipe(4, new ItemStack(Material.QUARTZ, 4), new ItemStack(Material.QUARTZ_BLOCK));
        addRecipe(4, new ItemStack(Material.IRON_NUGGET, 9), new ItemStack(Material.IRON_INGOT));
        addRecipe(4, new ItemStack(Material.GOLD_NUGGET, 9), new ItemStack(Material.GOLD_INGOT));
        addRecipe(4, new ItemStack(Material.COAL, 9), new ItemStack(Material.COAL_BLOCK));
        addRecipe(4, new ItemStack(Material.SAND, 4), new ItemStack(Material.SANDSTONE));
        addRecipe(4, new ItemStack(Material.RED_SAND, 4), new ItemStack(Material.RED_SANDSTONE));

        addRecipe(5, new ItemStack(Material.IRON_INGOT, 9), new ItemStack(Material.IRON_BLOCK));
        addRecipe(5, new ItemStack(Material.GOLD_INGOT, 9), new ItemStack(Material.GOLD_BLOCK));

        addRecipe(6, new ItemStack(Material.REDSTONE, 9), new ItemStack(Material.REDSTONE_BLOCK));
        addRecipe(6, new ItemStack(Material.LAPIS_LAZULI, 9), new ItemStack(Material.LAPIS_BLOCK));

        addRecipe(8, new ItemStack(Material.EMERALD, 9), new ItemStack(Material.EMERALD_BLOCK));
        addRecipe(8, new ItemStack(Material.DIAMOND, 9), new ItemStack(Material.DIAMOND_BLOCK));

        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_16)) {
            addRecipe(16, new ItemStack(Material.NETHERITE_INGOT, 9), new ItemStack(Material.NETHERITE_BLOCK));
        }
    }

    @ParametersAreNonnullByDefault
    private void addRecipe(int seconds, ItemStack input, ItemStack output) {
        registerRecipe(seconds, new ItemStack[] { input }, new ItemStack[] { output });
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.IRON_HOE);

    }

    @Override
    public String getMachineIdentifier() {
        return "ELECTRIC_PRESS";

    }
}
