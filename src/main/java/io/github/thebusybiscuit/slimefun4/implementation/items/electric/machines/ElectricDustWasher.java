package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.OreWasher;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * The {@link ElectricDustWasher} serves as an electrical {@link OreWasher}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see OreWasher
 *
 */
public class ElectricDustWasher extends AContainer {

    private final OreWasher oreWasher = SlimefunItems.ORE_WASHER.getItem(OreWasher.class);
    private final boolean legacyMode;

    @ParametersAreNonnullByDefault
    public ElectricDustWasher(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        legacyMode = Slimefun.getCfg().getBoolean("options.legacy-dust-washer");
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.GOLDEN_SHOVEL);
    }

    @Override
    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        for (int slot : getInputSlots()) {
            if (SlimefunUtils.isItemSimilar(menu.getItemInSlot(slot), SlimefunItems.SIFTED_ORE, true, false)) {
                if (!legacyMode && !hasFreeSlot(menu)) {
                    return null;
                }

                ItemStack dust = oreWasher.getRandomDust();
                MachineRecipe recipe = new MachineRecipe(4 / getSpeed(), new ItemStack[] { SlimefunItems.SIFTED_ORE }, new ItemStack[] { dust });

                if (!legacyMode || menu.fits(recipe.getOutput()[0], getOutputSlots())) {
                    menu.consumeItem(slot);
                    return recipe;
                }
            } else if (SlimefunUtils.isItemSimilar(menu.getItemInSlot(slot), SlimefunItems.PULVERIZED_ORE, true)) {
                MachineRecipe recipe = new MachineRecipe(4 / getSpeed(), new ItemStack[] { SlimefunItems.PULVERIZED_ORE }, new ItemStack[] { SlimefunItems.PURE_ORE_CLUSTER });

                if (menu.fits(recipe.getOutput()[0], getOutputSlots())) {
                    menu.consumeItem(slot);
                    return recipe;
                }
            }
        }

        return null;
    }

    private boolean hasFreeSlot(BlockMenu menu) {
        for (int slot : getOutputSlots()) {
            ItemStack item = menu.getItemInSlot(slot);

            if (item == null || item.getType() == Material.AIR) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getMachineIdentifier() {
        return "ELECTRIC_DUST_WASHER";
    }

}
