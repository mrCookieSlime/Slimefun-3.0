package io.github.thebusybiscuit.slimefun4.implementation.items.blocks;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.VanillaInventoryDropHandler;
import io.papermc.lib.PaperLib;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link OutputChest} can be used to capture the output items from a {@link MultiBlockMachine}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see MultiBlockMachine
 *
 */
public class OutputChest extends SlimefunItem {

    // @formatter:off
    private static final BlockFace[] possibleFaces = {
        BlockFace.UP,
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST
    };
    // @formatter:on

    @ParametersAreNonnullByDefault
    public OutputChest(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(new VanillaInventoryDropHandler<>(Chest.class));
    }

    /**
     * This method checks if an {@link OutputChest} is placed next to the given {@link Block}.
     * The returned object is an {@link Optional}.
     * This {@link Optional} will be empty if no {@link OutputChest} was found or if no {@link OutputChest}
     * could fit the {@link ItemStack} inside.
     * Otherwise the {@link Inventory} of the {@link OutputChest} will be returned (as an {@link Optional}).
     * 
     * @param b
     *            The {@link Block} from which to look for an adjacent {@link OutputChest}
     * @param item
     *            The {@link ItemStack} to insert
     * 
     * @return An {@link Optional} describing the result
     */
    @Nonnull
    public static Optional<Inventory> findOutputChestFor(@Nonnull Block b, @Nonnull ItemStack item) {
        Validate.notNull(b, "The target block must not be null!");
        Validate.notNull(item, "The ItemStack should not be null!");

        for (BlockFace face : possibleFaces) {
            Block potentialOutput = b.getRelative(face);

            // Check if the target block is a Chest
            if (potentialOutput.getType() == Material.CHEST) {
                SlimefunItem slimefunItem = BlockStorage.check(potentialOutput);

                // Fixes #3012 - Check if the OutputChest is not disabled here.
                if (slimefunItem instanceof OutputChest && !slimefunItem.isDisabledIn(b.getWorld())) {
                    // Found the output chest! Now, let's check if we can fit the product in it.
                    BlockState state = PaperLib.getBlockState(potentialOutput, false).getState();

                    if (state instanceof Chest) {
                        Inventory inv = ((Chest) state).getInventory();

                        // Check if the Item fits into that inventory.
                        if (InvUtils.fits(inv, item)) {
                            return Optional.of(inv);
                        }
                    }
                }
            }
        }

        // No Inventory was found
        return Optional.empty();
    }

}
