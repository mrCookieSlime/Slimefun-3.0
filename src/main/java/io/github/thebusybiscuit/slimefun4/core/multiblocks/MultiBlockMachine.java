package io.github.thebusybiscuit.slimefun4.core.multiblocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.MultiBlockInteractionHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.OutputChest;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

/**
 * A {@link MultiBlockMachine} is a {@link SlimefunItem} that is built in the {@link World}.
 * It holds recipes and a {@link MultiBlock} object which represents its structure.
 * 
 * @author TheBusyBiscuit
 * 
 * @see MultiBlock
 *
 */
public abstract class MultiBlockMachine extends SlimefunItem implements NotPlaceable, RecipeDisplayItem {

    protected final List<ItemStack[]> recipes;
    protected final List<ItemStack> displayRecipes;
    protected final MultiBlock multiblock;

    @ParametersAreNonnullByDefault
    protected MultiBlockMachine(ItemGroup category, SlimefunItemStack item, ItemStack[] recipe, ItemStack[] machineRecipes, BlockFace trigger) {
        super(category, item, RecipeType.MULTIBLOCK, recipe);
        this.recipes = new ArrayList<>();
        this.displayRecipes = new ArrayList<>();
        this.displayRecipes.addAll(Arrays.asList(machineRecipes));
        this.multiblock = new MultiBlock(this, convertItemStacksToMaterial(recipe), trigger);

        registerDefaultRecipes(displayRecipes);
    }

    @ParametersAreNonnullByDefault
    protected MultiBlockMachine(ItemGroup category, SlimefunItemStack item, ItemStack[] recipe, BlockFace trigger) {
        this(category, item, recipe, new ItemStack[0], trigger);
    }

    protected void registerDefaultRecipes(@Nonnull List<ItemStack> recipes) {
        // Override this method to register some default recipes
    }

    public @Nonnull List<ItemStack[]> getRecipes() {
        return recipes;
    }

    @Override
    public @Nonnull List<ItemStack> getDisplayRecipes() {
        return displayRecipes;
    }

    public @Nonnull MultiBlock getMultiBlock() {
        return multiblock;
    }

    public void addRecipe(ItemStack[] input, ItemStack output) {
        Validate.notNull(output, "Recipes must have an Output!");

        recipes.add(input);
        recipes.add(new ItemStack[] { output });
    }

    @Override
    public void register(@Nonnull SlimefunAddon addon) {
        addItemHandler(getInteractionHandler());
        super.register(addon);
    }

    @Override
    public void postRegister() {
        Slimefun.getRegistry().getMultiBlocks().add(multiblock);
    }

    @Override
    public void load() {
        super.load();

        for (ItemStack recipeItem : displayRecipes) {
            SlimefunItem item = SlimefunItem.getByItem(recipeItem);

            if (item == null || !item.isDisabled()) {
                recipes.add(new ItemStack[] { recipeItem });
            }
        }
    }

    protected @Nonnull MultiBlockInteractionHandler getInteractionHandler() {
        return (p, mb, b) -> {
            if (mb.equals(getMultiBlock())) {
                if (canUse(p, true) && Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK)) {
                    onInteract(p, b);
                }

                return true;
            } else {
                return false;
            }
        };
    }

    public abstract void onInteract(Player p, Block b);

    /**
     * Overloaded method for finding a potential output chest.
     * Fallbacks to the old system of putting the adding back into the dispenser.
     * Optional last argument Inventory placeCheckerInv is for a {@link MultiBlockMachine} that create
     * a dummy inventory to check if there's a space for the adding, i.e. Enhanced crafting table
     * 
     * @param adding
     *            The {@link ItemStack} that should be added
     * @param dispBlock
     *            The {@link Block} of our {@link Dispenser}
     * @param dispInv
     *            The {@link Inventory} of our {@link Dispenser}
     * 
     * @return The target {@link Inventory}
     */

    @ParametersAreNonnullByDefault
    protected @Nullable Inventory findOutputInventory(ItemStack adding, Block dispBlock, Inventory dispInv) {
        return findOutputInventory(adding, dispBlock, dispInv, dispInv);
    }

    @ParametersAreNonnullByDefault
    protected @Nullable Inventory findOutputInventory(ItemStack product, Block dispBlock, Inventory dispInv, Inventory placeCheckerInv) {
        Optional<Inventory> outputChest = OutputChest.findOutputChestFor(dispBlock, product);

        /*
         * This if-clause will trigger if no suitable output chest was found.
         * It's functionally the same as the old fit check for the dispenser,
         * only refactored.
         */
        if (!outputChest.isPresent() && InvUtils.fits(placeCheckerInv, product)) {
            return dispInv;
        } else {
            return outputChest.orElse(null);
        }
    }

    /**
     * This method handles an output {@link ItemStack} from the {@link MultiBlockMachine} which has a crafting delay
     *
     * @param outputItem
     *          A crafted {@link ItemStack} from {@link MultiBlockMachine}
     * @param block
     *          Main {@link Block} of our {@link Container} from {@link MultiBlockMachine}
     * @param blockInv
     *          The {@link Inventory} of our {@link Container}
     *
     */
    @ParametersAreNonnullByDefault
    protected void handleCraftedItem(ItemStack outputItem, Block block, Inventory blockInv) {
        Inventory outputInv = findOutputInventory(outputItem, block, blockInv);

        if (outputInv != null) {
            outputInv.addItem(outputItem);
        } else {
            ItemStack rest = blockInv.addItem(outputItem).get(0);

            // fallback: drop item
            if (rest != null) {
                SlimefunUtils.spawnItem(block.getLocation(), rest, ItemSpawnReason.MULTIBLOCK_MACHINE_OVERFLOW, true);
            }
        }
    }

    private static @Nonnull Material[] convertItemStacksToMaterial(@Nonnull ItemStack[] items) {
        List<Material> materials = new ArrayList<>();

        for (ItemStack item : items) {
            if (item == null) {
                materials.add(null);
            } else if (item.getType() == Material.FLINT_AND_STEEL) {
                materials.add(Material.FIRE);
            } else {
                materials.add(item.getType());
            }
        }

        return materials.toArray(new Material[0]);
    }

}
