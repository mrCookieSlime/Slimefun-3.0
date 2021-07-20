package io.github.thebusybiscuit.slimefun4.core.handlers;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.BlockPlacer;

/**
 * This {@link ItemHandler} is called whenever a {@link Block} was placed.
 * This only listens to any {@link Block} of the same {@link SlimefunItem} this is assigned
 * to.
 * 
 * @author TheBusyBiscuit
 *
 */
public abstract class BlockPlaceHandler implements ItemHandler {

    private final boolean allowBlockPlacers;

    protected BlockPlaceHandler(boolean allowBlockPlacers) {
        this.allowBlockPlacers = allowBlockPlacers;
    }

    /**
     * This method is called whenever a {@link Player} placed this {@link Block}.
     * 
     * @param e
     *            The corresponding {@link BlockPlaceEvent}
     */
    public abstract void onPlayerPlace(@Nonnull BlockPlaceEvent e);

    /**
     * This method is called whenever a {@link BlockPlacer} places this {@link Block}.
     * You cannot cancel the {@link BlockPlacerPlaceEvent} from within this method!
     * Override the method {@link #isBlockPlacerAllowed()} instead if you want to disallow the
     * {@link BlockPlacer} from placing this {@link Block}.
     * 
     * @param e
     *            The corresponding {@link BlockPlacerPlaceEvent}
     */
    public void onBlockPlacerPlace(@Nonnull BlockPlacerPlaceEvent e) {
        // This can be overridden, if necessary
    }

    /**
     * This returns whether the {@link BlockPlacer} is allowed to place a {@link Block} of this type.
     * 
     * @return Whether a {@link BlockPlacer} is allowed to place this
     */
    public boolean isBlockPlacerAllowed() {
        return allowBlockPlacers;
    }

    @Override
    public final Class<? extends ItemHandler> getIdentifier() {
        return BlockPlaceHandler.class;
    }
}
