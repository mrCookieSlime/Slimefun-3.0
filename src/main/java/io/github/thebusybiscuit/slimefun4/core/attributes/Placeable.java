package io.github.thebusybiscuit.slimefun4.core.attributes;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * <strong>DO NOT IMPLEMENT THIS INTERFACE</strong>
 * This is implemented by every {@link SlimefunItem} by default.
 * Might be changed in the future.
 * 
 * @author TheBusyBiscuit
 *
 */
public interface Placeable {

    @Nonnull
    Collection<ItemStack> getDrops();

    @Nonnull
    Collection<ItemStack> getDrops(@Nonnull Player p);

    /**
     * This method determines how to treat this {@link Block} when it is broken.
     * If true is returned then it will be treated as a vanilla {@link Block}.
     * If false is returned then Slimefun will treat it as a {@link Block} that is
     * the placed down version of a {@link SlimefunItem}.
     * 
     * It returns false by default.
     * This was previously represented by the HandledBlock class.
     * 
     * @return Whether to use the vanilla block breaking behaviour
     */
    default boolean useVanillaBlockBreaking() {
        return false;
    }

}
