package io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;

public class CoalGenerator extends AGenerator {

    @ParametersAreNonnullByDefault
    public CoalGenerator(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected void registerDefaultFuelTypes() {
        registerFuel(new MachineFuel(80, new ItemStack(Material.COAL_BLOCK)));
        registerFuel(new MachineFuel(12, new ItemStack(Material.BLAZE_ROD)));
        registerFuel(new MachineFuel(20, new ItemStack(Material.DRIED_KELP_BLOCK)));

        // Boats
        for (Material mat : Tag.ITEMS_BOATS.getValues()) {
            registerFuel(new MachineFuel(5, new ItemStack(mat)));
        }

        // Coal & Charcoal
        registerFuel(new MachineFuel(8, new ItemStack(Material.COAL)));
        registerFuel(new MachineFuel(8, new ItemStack(Material.CHARCOAL)));

        // Logs
        for (Material mat : Tag.LOGS.getValues()) {
            registerFuel(new MachineFuel(4, new ItemStack(mat)));
        }

        // Wooden Planks
        for (Material mat : Tag.PLANKS.getValues()) {
            registerFuel(new MachineFuel(1, new ItemStack(mat)));
        }

        // Wooden Slabs
        for (Material mat : Tag.WOODEN_SLABS.getValues()) {
            registerFuel(new MachineFuel(1, new ItemStack(mat)));
        }

        // Wooden Buttons
        for (Material mat : Tag.WOODEN_BUTTONS.getValues()) {
            registerFuel(new MachineFuel(1, new ItemStack(mat)));
        }

        // Wooden Fences
        for (Material mat : Tag.WOODEN_FENCES.getValues()) {
            registerFuel(new MachineFuel(1, new ItemStack(mat)));
        }

        // wooden Trapdoors
        for (Material mat : Tag.WOODEN_TRAPDOORS.getValues()) {
            registerFuel(new MachineFuel(3, new ItemStack(mat)));
        }

        // Wooden Pressure Plates
        for (Material mat : Tag.WOODEN_PRESSURE_PLATES.getValues()) {
            registerFuel(new MachineFuel(2, new ItemStack(mat)));
        }

        // Wooden Doors
        for (Material mat : Tag.WOODEN_DOORS.getValues()) {
            registerFuel(new MachineFuel(2, new ItemStack(mat)));
        }

        // Signs
        for (Material mat : Tag.STANDING_SIGNS.getValues()) {
            registerFuel(new MachineFuel(2, new ItemStack(mat)));
        }
    }

    @Nonnull
    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.FLINT_AND_STEEL);
    }

}
