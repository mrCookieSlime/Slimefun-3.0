package io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;

/**
 * The {@link NuclearReactor} is an implementation of {@link Reactor} that uses
 * any {@link Radioactive} material to generate energy.
 * It needs water coolant as well as a steady supply of Reactor Coolant Cells
 * 
 * @author TheBusyBiscuit
 * 
 * @see NetherStarReactor
 *
 */
public abstract class NuclearReactor extends Reactor {

    @ParametersAreNonnullByDefault
    protected NuclearReactor(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected void registerDefaultFuelTypes() {
        registerFuel(new MachineFuel(1200, SlimefunItems.URANIUM, SlimefunItems.NEPTUNIUM));
        registerFuel(new MachineFuel(600, SlimefunItems.NEPTUNIUM, SlimefunItems.PLUTONIUM));
        registerFuel(new MachineFuel(1500, SlimefunItems.BOOSTED_URANIUM, null));
    }

    @Override
    public ItemStack getProgressBar() {
        return SlimefunItems.LAVA_CRYSTAL;
    }

    @Override
    public ItemStack getCoolant() {
        return SlimefunItems.REACTOR_COOLANT_CELL;
    }

    @Override
    public ItemStack getFuelIcon() {
        return SlimefunItems.URANIUM;
    }

    @Override
    public void extraTick(@Nonnull Location l) {
        // This machine does not need to perform anything while ticking
        // The Nether Star Reactor uses this method to generate the Wither Effect
    }

}
