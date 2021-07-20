package io.github.thebusybiscuit.slimefun4.api.blocks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.World;

import io.github.thebusybiscuit.slimefun4.api.blocks.sources.BlockDataSource;
import io.github.thebusybiscuit.slimefun4.api.blocks.sources.LegacyBlockDataSource;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

/**
 * The {@link BlockDatabase} will handle {@link SlimefunBlockData} in the future and
 * possibly allow for SQL support too.
 * <p>
 * Right now, it is just merely a WIP class.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SlimefunBlockData
 *
 */
public class BlockDatabase {

    /**
     * This is our {@link SlimefunPlugin} instance.
     */
    private final SlimefunPlugin plugin;

    /**
     * This is our {@link BlockDataSource} which provides means to load and save
     * {@link SlimefunWorldData}.
     */
    private final BlockDataSource dataSource;

    /**
     * This {@link ReadWriteLock} protects our {@link HashMap} from concurrent
     * modifications.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * This is our internal data structure: We store the {@link SlimefunWorldData}
     * in this {@link Map} and use the {@link UUID} of the corresponding {@link World}
     * as the key.
     */
    private final Map<UUID, SlimefunWorldData> worlds = new HashMap<>();

    /**
     * This creates a new {@link BlockDatabase}.
     * 
     * @param plugin
     *            Our {@link SlimefunPlugin} instance
     */
    public BlockDatabase(@Nonnull SlimefunPlugin plugin) {
        Validate.notNull(plugin, "The plugin instance cannot be null!");

        this.plugin = plugin;
        this.dataSource = new LegacyBlockDataSource();
    }

    @Nonnull
    public SlimefunWorldData getWorld(@Nonnull World world) {
        lock.readLock().lock();

        try {
            SlimefunWorldData data = worlds.get(world.getUID());

            if (data != null) {
                return data;
            }
        } finally {
            lock.readLock().unlock();
        }

        // No data was found if we end up here
        return loadWorld(world);
    }

    @Nonnull
    public SlimefunWorldData loadWorld(@Nonnull World world) {
        lock.writeLock().lock();

        try {
            // Only load the world data if necessary, this one extra map operation won't hurt
            SlimefunWorldData data = worlds.get(world.getUID());

            if (data == null) {
                data = new SlimefunWorldData(world, dataSource);
                worlds.put(world.getUID(), data);
            }

            return data;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unloadWorld(@Nonnull World world) {
        lock.writeLock().lock();

        try {
            SlimefunWorldData data = worlds.remove(world.getUID());

            if (data != null) {
                data.markForCleanup();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveAll() {
        lock.writeLock().lock();

        try {
            Iterator<SlimefunWorldData> iterator = worlds.values().iterator();

            while (iterator.hasNext()) {
                SlimefunWorldData world = iterator.next();

                if (world.isMarkedForCleanup()) {
                    world.save(dataSource);
                    iterator.remove();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

}
