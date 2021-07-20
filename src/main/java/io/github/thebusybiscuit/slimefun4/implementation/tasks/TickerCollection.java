package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import io.github.thebusybiscuit.cscorelib2.blocks.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Memory efficient, synchronized collection of {@link BlockPosition BlockPositions} used by the
 * {@link TickerTask}. All methods are thread-safe unless explicitly stated otherwise.
 *
 * @author md5sha256
 */
@ThreadSafe
final class TickerCollection {

    private final Map<UUID, Set<Long>> blocks = new ConcurrentHashMap<>();

    /**
     * Get the compressed {@link BlockPosition BlockPositions} mapped to a given {@link World}.
     * The returned {@link Set} is thread-safe. Changes to set will be reflected in this instance.
     *
     * @param world The world
     * @return Returns a {@link Set} of {@link Long Longs} which are compressed {@link BlockPosition BlockPositions}
     *
     */
    public @Nonnull Set<Long> getBlocks(@Nonnull World world) {
        return blocks.computeIfAbsent(world.getUID(), unused -> Collections.synchronizedSet(new HashSet<>()));
    }

    /**
     * Get a copy of all the blocks stored in the form of {@link BlockPosition BlockPositions}
     * The returned {@link Set} is not thread-safe.
     *
     * @return Returns a {@link Set} of {@link BlockPosition BlockPositions}
     */
    public @Nonnull Set<Location> getBlocksAsLocations() {
        Set<Location> locations = new HashSet<>();
        blocks.keySet().removeIf(uid -> Bukkit.getWorld(uid) == null);
        for (Map.Entry<UUID, Set<Long>> entry : blocks.entrySet()) {
            World world = Bukkit.getWorld(entry.getKey());
            locations.addAll(convertToLocation(world, entry.getValue()));
        }
        return locations;
    }

    /**
     * Add a {@link BlockPosition} to this ticker collection, duplicates will be ignored.
     *
     * @param block The {@link BlockPosition} to add
     * @see #addBlock(Location)
     */
    public void addBlock(@Nonnull BlockPosition block) {
        getBlocks(block.getWorld()).add(block.getPosition());
    }

    /**
     * Add a {@link Location} to this ticker collection, duplicates will be ignored.
     *
     * @param location The {@link Location} of the block to add
     * @see #addBlock(BlockPosition)
     */
    public void addBlock(@Nonnull Location location) {
        addBlock(new BlockPosition(location));
    }

    /**
     * Remove a {@link BlockPosition} from this ticker collection. If the collection is empty after a successful removal then this will also remove the underline {@link Set} for this {@link World}.
     *
     * @param block The {@link BlockPosition} to remove
     * @see #removeBlock(Location)
     */
    public void removeBlock(@Nonnull BlockPosition block) {
        World world = block.getWorld();
        Collection<Long> positions = blocks.get(world.getUID());
        if (positions == null) {
            return;
        }
        positions.remove(block.getPosition());
        if (positions.isEmpty()) {
            blocks.remove(world.getUID());
        }
    }

    /**
     * Remove a {@link Location} from this ticker collection.
     *
     * @param location The {@link Location} of the block to remove
     * @see #removeBlock(BlockPosition)
     */
    public void removeBlock(@Nonnull Location location) {
        removeBlock(new BlockPosition(location));
    }

    /**
     * Clear all the blocks for a given {@link World}
     *
     * @param key The {@link World} to remove blocks from
     */
    public void clear(@Nonnull World key) {
        blocks.remove(key.getUID());
    }

    /**
     * Clear all entries from this instance
     */
    public void clear() {
        blocks.clear();
    }

    private static @Nonnull Set<Location> convertToLocation(@Nonnull World world, @Nonnull Collection<Long> raw) {
        Set<Location> locations = new HashSet<>(raw.size());
        for (long compressed : raw) {
            int[] decompressed = BlockPosition.decompress(compressed);
            locations.add(new Location(world, decompressed[0], decompressed[1], decompressed[2]));
        }
        return locations;
    }

}
