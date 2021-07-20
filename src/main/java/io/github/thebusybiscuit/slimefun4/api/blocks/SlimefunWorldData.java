package io.github.thebusybiscuit.slimefun4.api.blocks;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import io.github.thebusybiscuit.cscorelib2.blocks.BlockPosition;
import io.github.thebusybiscuit.cscorelib2.blocks.ChunkPosition;
import io.github.thebusybiscuit.slimefun4.api.blocks.sources.BlockDataSource;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

/**
 * 
 * @author TheBusyBiscuit
 * 
 * @see BlockDatabase
 * @see SlimefunBlockData
 *
 */
public class SlimefunWorldData {

    /**
     * This {@link ReadWriteLock} protects our {@link HashMap} from concurrent
     * modifications.
     */
    private final ReadWriteLock blocksLock = new ReentrantReadWriteLock();

    /**
     * This is our internal data structure: We store the {@link SlimefunBlockData}
     * in this {@link Map} and use the {@link BlockPosition}'s {@link Long} as the key.
     */
    private final Map<Long, SlimefunBlockData> blocks = new HashMap<>();

    /**
     * This {@link ReadWriteLock} protects our {@link HashMap} from concurrent
     * modifications.
     */
    private final ReadWriteLock chunksLock = new ReentrantReadWriteLock();

    /**
     * This is our internal data structure: We store the {@link SlimefunChunkData}
     * in this {@link Map} and use the {@link ChunkPosition}'s {@link Long} as the key.
     */
    private final Map<Long, SlimefunChunkData> chunks = new HashMap<>();

    /**
     * The name of the {@link World}.
     */
    private final String name;

    /**
     * This signals whether the {@link SlimefunWorldData} has been loaded yet.
     */
    private volatile boolean isLoaded = false;
    
    /**
     * This signals whether this world has been marked for cleanup.
     * No longer used worlds will be cleaned up and removed from memory.
     */
    private volatile boolean isMarkedForCleanup = false;

    /**
     * We keep a {@link WeakReference} of our {@link World} to prevent
     * memory leaks. Once the {@link World} is unloaded, the reference
     * will be nullified.
     * This signals that we are ready to abandon this {@link SlimefunWorldData}
     * instance.
     */
    private final WeakReference<World> world;

    /**
     * This creates a new {@link SlimefunWorldData} object for the given {@link World}.
     * 
     * @param world
     *            The {@link World}
     */
    SlimefunWorldData(@Nonnull World world, @Nonnull BlockDataSource source) {
        this.world = new WeakReference<>(world);
        this.name = world.getName();

        loadWorldData(source);
    }

    public boolean isLoaded() {
        return isLoaded;
    }
    
    public boolean isMarkedForCleanup() {
        return isMarkedForCleanup;
    }
    
    public void markForCleanup() {
        this.isMarkedForCleanup = true;
    }

    private synchronized void loadWorldData(@Nonnull BlockDataSource source) {
        SlimefunPlugin.logger().log(Level.INFO, "Loading data for World \"{0}\"", getName());
        SlimefunPlugin.logger().log(Level.INFO, "This may take a while...");

        blocksLock.writeLock().lock();

        try {
            source.loadBlocks(this, blocks);
            isLoaded = true;
        } finally {
            blocksLock.writeLock().unlock();
        }

        chunksLock.writeLock().lock();

        try {
            source.loadChunks(this, chunks);
        } finally {
            chunksLock.writeLock().unlock();
        }
    }

    public void save(@Nonnull BlockDataSource source) {
        blocksLock.readLock().lock();

        try {
            source.saveBlocks(this, blocks);
        } finally {
            blocksLock.readLock().unlock();
        }

        chunksLock.readLock().lock();

        try {
            source.saveChunks(this, chunks);
        } finally {
            chunksLock.readLock().unlock();
        }
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nullable
    public World getWorldIfLoaded() {
        return world.get();
    }

    @Nullable
    public SlimefunBlockData getBlockData(int x, int y, int z) {
        if (!isLoaded) {
            throw new IllegalStateException("World data has not been loaded yet. Check #isLoaded() before accessing this!");
        }

        blocksLock.readLock().lock();

        try {
            return blocks.get(BlockPosition.getAsLong(x, y, z));
        } finally {
            blocksLock.readLock().unlock();
        }
    }

    @Nullable
    public SlimefunBlockData getBlockData(@Nonnull BlockPosition blockPosition) {
        Validate.notNull(blockPosition, "The BlockPosition must not be null");
        if (!isLoaded) {
            throw new IllegalStateException("World data has not been loaded yet. Check #isLoaded() before accessing this!");
        }

        blocksLock.readLock().lock();

        try {
            return blocks.get(blockPosition.getPosition());
        } finally {
            blocksLock.readLock().unlock();
        }
    }

    @Nullable
    public SlimefunBlockData getBlockData(@Nonnull Location loc) {
        Validate.notNull(loc, "The Location must not be null");

        return getBlockData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    @Nullable
    public SlimefunBlockData getBlockData(@Nonnull Block b) {
        Validate.notNull(b, "The Block must not be null");

        return getBlockData(b.getX(), b.getY(), b.getZ());
    }

    @Nullable
    public SlimefunChunkData getChunkData(int x, int z) {
        if (!isLoaded) {
            throw new IllegalStateException("World data has not been loaded yet. Check #isLoaded() before accessing this!");
        }

        chunksLock.readLock().lock();

        try {
            return chunks.get(ChunkPosition.getAsLong(x, z));
        } finally {
            chunksLock.readLock().unlock();
        }
    }

}
