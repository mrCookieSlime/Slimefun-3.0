package io.github.thebusybiscuit.slimefun4.api.events;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.BlockPlacer;

/**
 * This {@link Event} is fired whenever a {@link BlockPlacer} wants to place a {@link Block}.
 * 
 * @author TheBusyBiscuit
 * 
 */
public class BlockPlacerPlaceEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Block blockPlacer;
    private ItemStack placedItem;

    private boolean cancelled = false;
    private boolean locked = false;

    /**
     * This creates a new {@link BlockPlacerPlaceEvent}.
     * 
     * @param blockPlacer
     *            The {@link BlockPlacer}
     * @param placedItem
     *            The {@link ItemStack} of the {@link Block} that was placed
     * @param block
     *            The placed {@link Block}
     */
    @ParametersAreNonnullByDefault
    public BlockPlacerPlaceEvent(Block blockPlacer, ItemStack placedItem, Block block) {
        super(block);

        this.placedItem = placedItem;
        this.blockPlacer = blockPlacer;
    }

    /**
     * This method returns the {@link BlockPlacer}
     *
     * @return The {@link BlockPlacer}
     */
    @Nonnull
    public Block getBlockPlacer() {
        return blockPlacer;
    }

    /**
     * This returns the placed {@link ItemStack}.
     * 
     * @return The placed {@link ItemStack}
     */
    @Nonnull
    public ItemStack getItemStack() {
        return placedItem;
    }

    /**
     * This sets the placed {@link ItemStack}.
     * 
     * @param item
     *            The {@link ItemStack} to be placed
     */
    public void setItemStack(@Nonnull ItemStack item) {
        Validate.notNull(item, "The ItemStack must not be null!");

        if (!locked) {
            this.placedItem = item;
        } else {
            SlimefunItem.getByItem(placedItem).warn("A BlockPlacerPlaceEvent cannot be modified from within a BlockPlaceHandler!");
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        if (!locked) {
            cancelled = cancel;
        } else {
            SlimefunItem.getByItem(placedItem).warn("A BlockPlacerPlaceEvent cannot be modified from within a BlockPlaceHandler!");
        }
    }

    /**
     * This marks this {@link Event} as immutable, it can no longer be modified afterwards.
     */
    public void setImmutable() {
        locked = true;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

}