package io.github.thebusybiscuit.slimefun4.implementation.handlers;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.papermc.lib.PaperLib;

/**
 * This is an implementation of {@link BlockBreakHandler} which is suited for any {@link SlimefunItem}
 * that uses the vanilla {@link Inventory} from the {@link BlockState}.
 * <p>
 * The default behaviour is the following:
 * 
 * <pre>
| Broken by... | Behaviour                |
| ------------ | ------------------------ |
| Player       | Drop inventory contents. |
| Android      | Not allowed.             |
| Explosions   | Delete contents.         |
 * </pre>
 * 
 * @author TheBusyBiscuit
 *
 * @param <T>
 *            The type of {@link BlockState} and {@link InventoryHolder} we are dealing with
 */
public class VanillaInventoryDropHandler<T extends BlockState & InventoryHolder> extends BlockBreakHandler {

    private final Class<T> blockStateClass;

    /**
     * This creates a new {@link VanillaInventoryDropHandler} for the given {@link BlockState} {@link Class}.
     * 
     * @param blockStateClass
     *            The class of the block's {@link BlockState}
     */
    public VanillaInventoryDropHandler(@Nonnull Class<T> blockStateClass) {
        super(false, true);
        Validate.notNull(blockStateClass, "The provided class must not be null!");

        this.blockStateClass = blockStateClass;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
        // Fixes #2906 - Spigot being buggy as always...
        if (!PaperLib.isPaper()) {
            return;
        }

        Block b = e.getBlock();
        BlockState state = PaperLib.getBlockState(b, false).getState();

        if (blockStateClass.isInstance(state)) {
            T inventoryHolder = blockStateClass.cast(state);

            for (ItemStack stack : getInventory(inventoryHolder)) {
                if (stack != null && !stack.getType().isAir()) {
                    drops.add(stack);
                }
            }
        }
    }

    @Nonnull
    protected Inventory getInventory(@Nonnull T inventoryHolder) {
        if (inventoryHolder instanceof Chest) {
            return ((Chest) inventoryHolder).getBlockInventory();
        } else {
            return inventoryHolder.getInventory();
        }
    }

}
