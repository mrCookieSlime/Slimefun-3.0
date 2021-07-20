package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators;

import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * The {@link TreeGrowthAccelerator} is an electrical machine that works similar to
 * the {@link CropGrowthAccelerator} but boosts the growth of nearby trees.
 * 
 * @author TheBusyBiscuit
 * 
 * @see CropGrowthAccelerator
 * @see AnimalGrowthAccelerator
 *
 */
public class TreeGrowthAccelerator extends AbstractGrowthAccelerator {

    private static final int ENERGY_CONSUMPTION = 24;
    private static final int RADIUS = 9;

    // We wanna strip the Slimefun Item id here
    private static final ItemStack organicFertilizer = ItemStackWrapper.wrap(SlimefunItems.FERTILIZER);

    public TreeGrowthAccelerator(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public int getCapacity() {
        return 1024;
    }

    @Override
    protected void tick(Block b) {
        BlockMenu inv = BlockStorage.getInventory(b);

        if (getCharge(b.getLocation()) >= ENERGY_CONSUMPTION) {
            for (int x = -RADIUS; x <= RADIUS; x++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    Block block = b.getRelative(x, 0, z);

                    if (Tag.SAPLINGS.isTagged(block.getType())) {
                        Sapling sapling = (Sapling) block.getBlockData();

                        if (sapling.getStage() < sapling.getMaximumStage() && growSapling(b, block, inv, sapling)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean growSapling(Block machine, Block block, BlockMenu inv, Sapling sapling) {
        for (int slot : getInputSlots()) {
            if (SlimefunUtils.isItemSimilar(inv.getItemInSlot(slot), organicFertilizer, false, false)) {
                removeCharge(machine.getLocation(), ENERGY_CONSUMPTION);

                sapling.setStage(sapling.getStage() + 1);
                block.setBlockData(sapling, false);

                inv.consumeItem(slot);
                block.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation().add(0.5D, 0.5D, 0.5D), 4, 0.1F, 0.1F, 0.1F);
                return true;
            }
        }

        return false;
    }

}
