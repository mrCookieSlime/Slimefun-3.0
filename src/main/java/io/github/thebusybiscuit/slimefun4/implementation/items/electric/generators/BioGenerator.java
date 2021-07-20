package io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;

public class BioGenerator extends AGenerator {

    @ParametersAreNonnullByDefault
    public BioGenerator(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected void registerDefaultFuelTypes() {
        registerFuel(new MachineFuel(2, new ItemStack(Material.ROTTEN_FLESH)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.SPIDER_EYE)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.BONE)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.STRING)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.APPLE)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.MELON_SLICE)));
        registerFuel(new MachineFuel(27, new ItemStack(Material.MELON)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.PUMPKIN)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.PUMPKIN_SEEDS)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.MELON_SEEDS)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.WHEAT)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.WHEAT_SEEDS)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.CARROT)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.POTATO)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.SUGAR_CANE)));
        registerFuel(new MachineFuel(3, new ItemStack(Material.NETHER_WART)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.RED_MUSHROOM)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.BROWN_MUSHROOM)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.VINE)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.CACTUS)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.LILY_PAD)));
        registerFuel(new MachineFuel(8, new ItemStack(Material.CHORUS_FRUIT)));
        registerFuel(new MachineFuel(1, new ItemStack(Material.KELP)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.DRIED_KELP)));
        registerFuel(new MachineFuel(20, new ItemStack(Material.DRIED_KELP_BLOCK)));
        registerFuel(new MachineFuel(1, new ItemStack(Material.SEAGRASS)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.SEA_PICKLE)));
        registerFuel(new MachineFuel(1, new ItemStack(Material.BAMBOO)));
        registerFuel(new MachineFuel(2, new ItemStack(Material.SWEET_BERRIES)));

        // Small Flowers (formally just dandelions and poppies).
        for (Material m : Tag.SMALL_FLOWERS.getValues()) {
            registerFuel(new MachineFuel(1, new ItemStack(m)));
        }

        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_15)) {
            registerFuel(new MachineFuel(4, new ItemStack(Material.HONEYCOMB)));
            registerFuel(new MachineFuel(40, new ItemStack(Material.HONEYCOMB_BLOCK)));
        }

        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_16)) {
            registerFuel(new MachineFuel(4, new ItemStack(Material.SHROOMLIGHT)));
            registerFuel(new MachineFuel(2, new ItemStack(Material.CRIMSON_FUNGUS)));
            registerFuel(new MachineFuel(2, new ItemStack(Material.WARPED_FUNGUS)));
            registerFuel(new MachineFuel(16, SlimefunItems.STRANGE_NETHER_GOO));
        }

        // Leaves
        for (Material m : Tag.LEAVES.getValues()) {
            registerFuel(new MachineFuel(1, new ItemStack(m)));
        }

        // Saplings
        for (Material m : Tag.SAPLINGS.getValues()) {
            registerFuel(new MachineFuel(1, new ItemStack(m)));
        }

        // Corals
        for (Material m : Tag.CORALS.getValues()) {
            registerFuel(new MachineFuel(2, new ItemStack(m)));
        }

        for (Material m : Tag.CORAL_BLOCKS.getValues()) {
            registerFuel(new MachineFuel(2, new ItemStack(m)));
        }
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.GOLDEN_HOE);
    }

}
