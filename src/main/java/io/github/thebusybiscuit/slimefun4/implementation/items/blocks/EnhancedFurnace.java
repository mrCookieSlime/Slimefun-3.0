package io.github.thebusybiscuit.slimefun4.implementation.items.blocks;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.VanillaInventoryDropHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link EnhancedFurnace} is an upgraded version of a {@link Furnace}.
 * It has a custom speed, efficiency and also a level of fortune.
 * All of these values are tweaked for every instance of this class.
 * 
 * It uses a {@link BlockTicker} to manipulate the {@link Furnace} into working faster.
 * 
 * @author TheBusyBiscuit
 *
 */
public class EnhancedFurnace extends SimpleSlimefunItem<BlockTicker> {

    private final int speed;
    private final int efficiency;
    private final int fortuneLevel;

    public EnhancedFurnace(ItemGroup category, int speed, int efficiency, int fortune, SlimefunItemStack item, ItemStack[] recipe) {
        super(category, item, RecipeType.ENHANCED_CRAFTING_TABLE, recipe);

        this.speed = speed - 1;
        this.efficiency = efficiency - 1;
        this.fortuneLevel = fortune - 1;

        addItemHandler(new VanillaInventoryDropHandler<>(Furnace.class));
    }

    /**
     * This returns the processing speed of this {@link EnhancedFurnace}.
     * 
     * @return The processing speed
     */
    public int getProcessingSpeed() {
        return speed;
    }

    /**
     * This returns the fuel efficiency of this {@link EnhancedFurnace}.
     * The fuel efficiency is a multiplier that is applied to any fuel burnt in this {@link EnhancedFurnace}.
     * 
     * @return The fuel multiplier
     */
    public int getFuelEfficiency() {
        return efficiency;
    }

    public int getRandomOutputAmount() {
        int bonus = ThreadLocalRandom.current().nextInt(fortuneLevel + 2);
        return 1 + bonus;
    }

    @Override
    public BlockTicker getItemHandler() {
        return new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem item, Config data) {
                if (b.getType() != Material.FURNACE) {
                    // The Furnace has been destroyed, we can clear the block data
                    BlockStorage.clearBlockInfo(b);
                } else {
                    BlockStateSnapshotResult result = PaperLib.getBlockState(b, false);
                    BlockState state = result.getState();

                    // Check if the BlockState is a Furnace and cooking something
                    if (state instanceof Furnace && ((Furnace) state).getCookTime() > 0) {
                        setProgress((Furnace) state);

                        // Only update if necessary
                        if (result.isSnapshot()) {
                            state.update(true, false);
                        }
                    }
                }
            }

            @Override
            public boolean isSynchronized() {
                // This messes with BlockStates, so it needs to be synchronized
                return true;
            }
        };
    }

    private void setProgress(@Nonnull Furnace furnace) {
        // Update the cooktime
        int cookTime = furnace.getCookTime() + getProcessingSpeed() * 10;
        furnace.setCookTime((short) Math.min(cookTime, furnace.getCookTimeTotal() - 1));
    }
}
