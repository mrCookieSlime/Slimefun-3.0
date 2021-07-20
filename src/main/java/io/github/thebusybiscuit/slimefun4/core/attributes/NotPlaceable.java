package io.github.thebusybiscuit.slimefun4.core.attributes;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * Implement this interface for any {@link SlimefunItem} to prevent
 * that {@link SlimefunItem} from being placed.
 * 
 * <b>Important</b>: This will not cancel any {@link BlockPlaceEvent}.
 * It will simply prevent Slimefun from ever registering this {@link SlimefunItem}
 * as a placed {@link Block}.
 * 
 * @author TheBusyBiscuit
 *
 */
public interface NotPlaceable extends ItemAttribute {

}
