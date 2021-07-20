package io.github.thebusybiscuit.slimefun4.core.services.profiler;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * This represents an entry in our {@link SlimefunProfiler}.
 * It is a modification of {@link BlockPosition} to be as memory-efficient as possible.
 * 
 * @author TheBusyBiscuit
 *
 */
final class ProfiledBlock {

    /**
     * The {@link World} this {@link Block} is in.
     * It is fine to keep an actual reference here since this is a throwaway object anyway.
     */
    private final World world;

    /**
     * A {@link Long} representation of our {@link Location} (x, y, z).
     */
    private final long position;

    /**
     * The {@link SlimefunItem} whihc is located at this {@link Location}.
     */
    private final SlimefunItem item;

    /**
     * This creates a new {@link ProfiledBlock} for the given {@link Location} and
     * the {@link SlimefunItem} found at this {@link Location}.
     * 
     * @param l
     *            The {@link Location}
     * @param item
     *            The {@link SlimefunItem} found at that {@link Location}
     */
    ProfiledBlock(@Nonnull Location l, @Nonnull SlimefunItem item) {
        this.world = l.getWorld();
        this.position = getLocationAsLong((int) l.getX(), (int) l.getY(), (int) l.getZ());
        this.item = item;
    }

    /**
     * This is just a <strong>dummy</strong> constructor.
     * Please only use this for comparisons or lookups.
     * 
     * @param b
     *            A {@link Block}
     */
    ProfiledBlock(@Nonnull Block b) {
        this.world = b.getWorld();
        this.position = getLocationAsLong(b.getX(), b.getY(), b.getZ());
        this.item = null;
    }

    /**
     * This compresses our {@link Location} into a long for more efficient memory usage
     * 
     * @param x
     *            The x value
     * @param y
     *            The y value
     * @param z
     *            The z value
     * 
     * @return A {@link Long} representation of this {@link Location}
     */
    private static long getLocationAsLong(int x, int y, int z) {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (long) (y & 0xFFF);
    }

    @Nonnull
    public World getWorld() {
        return world;
    }

    /**
     * Gets the x for this block.
     *
     * @return This blocks x coordinate.
     */
    public int getX() {
        return (int) (this.position >> 38);
    }

    /**
     * Gets the y for this block.
     *
     * @return This blocks y coordinate.
     */
    public int getY() {
        return (int) (this.position & 0xFFF);
    }

    /**
     * Gets the z for this block.
     *
     * @return This blocks z coordinate.
     */
    public int getZ() {
        return (int) (this.position << 26 >> 38);
    }

    /**
     * Gets the chunks x coordinate for this block.
     *
     * @return The blocks chunks x coordinate.
     */
    public int getChunkX() {
        return this.getX() >> 4;
    }

    /**
     * Gets the chunks z coordinate for this block.
     *
     * @return The blocks chunks z coordinate.
     */
    public int getChunkZ() {
        return this.getZ() >> 4;
    }

    @Nonnull
    public String getId() {
        return item.getId();
    }

    @Nonnull
    public SlimefunAddon getAddon() {
        return item.getAddon();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProfiledBlock) {
            ProfiledBlock block = (ProfiledBlock) obj;
            return position == block.position && Objects.equals(world, block.world);
        }

        return false;
    }

    @Override
    public int hashCode() {
        long hilo = world.getUID().getMostSignificantBits() ^ world.getUID().getLeastSignificantBits();
        return (int) (position ^ (position >> 32) ^ hilo ^ (hilo >> 32));
    }

}
