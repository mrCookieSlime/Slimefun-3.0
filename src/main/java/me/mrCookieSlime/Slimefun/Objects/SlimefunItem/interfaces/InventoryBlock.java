package me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces;

import java.lang.reflect.Array;
import java.util.function.Consumer;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;

/**
 * 
 * @deprecated This interface is not designed to be used by addons. The entire inventory system will be replaced
 *             eventually.
 *
 */
public interface InventoryBlock {

    /**
     * This method returns an {@link Array} of slots that serve as the input
     * for the {@link Inventory} of this block.
     * 
     * @return The input slots for the {@link Inventory} of this block
     */
    int[] getInputSlots();

    /**
     * This method returns an {@link Array} of slots that serve as the output
     * for the {@link Inventory} of this block.
     * 
     * @return The output slots for the {@link Inventory} of this block
     */
    int[] getOutputSlots();

    default void createPreset(SlimefunItem item, Consumer<BlockMenuPreset> setup) {
        createPreset(item, item.getItemName(), setup);
    }

    default void createPreset(SlimefunItem item, String title, Consumer<BlockMenuPreset> setup) {
        new BlockMenuPreset(item.getId(), title) {

            @Override
            public void init() {
                setup.accept(this);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow == ItemTransportFlow.INSERT) {
                    return getInputSlots();
                } else {
                    return getOutputSlots();
                }
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                if (p.hasPermission("slimefun.inventory.bypass")) {
                    return true;
                } else {
                    return item.canUse(p, false) && Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.INTERACT_BLOCK);
                }
            }
        };
    }

}
