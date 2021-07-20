package io.github.thebusybiscuit.slimefun4.api.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import org.apache.commons.lang.Validate;

import com.google.gson.GsonBuilder;

/**
 * This abstract class is responsible for handling data for block or chunk data.
 * 
 * @author TheBusyBiscuit
 * @author creator3
 * 
 * @see SlimefunBlockData
 * @see SlimefunChunkData
 *
 */
class AbstractDataObject {

    protected static final Gson DEFAULT_GSON = new GsonBuilder().create();

    /**
     * The {@link ReadWriteLock} protects us from concurrent modifications.
     * This way, we can be flexible with our data access and be {@link Thread}-safe.
     */
    protected final ReadWriteLock lock;

    /**
     * The internal data structure: A simple {@link HashMap}
     * to store the keys and values
     */
    protected final Map<String, String> data;

    /**
     * This flag marks whether this object has recently been modified.
     * A dirty object needs to be saved.
     */
    private volatile boolean isDirty = false;

    /**
     * This creates a new {@link AbstractDataObject} object
     * and also initializes it using the given {@link Map}
     * 
     * @param data
     *            The data {@link Map}
     */
    public AbstractDataObject(@Nonnull Map<String, String> data) {
        Validate.notNull(data, "Map cannot be null");

        this.lock = new ReentrantReadWriteLock();
        this.data = data;
    }

    public synchronized void markDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setValue(@Nonnull String key, @Nullable Object value) {
        Validate.notNull(key, "Cannot set block data with a key that is null");

        if (value == null) {
            lock.writeLock().lock();

            try {
                data.remove(key);
                markDirty(true);
            } finally {
                lock.writeLock().unlock();
            }
        } else if (value instanceof String) {
            lock.writeLock().lock();

            try {
                data.put(key, (String) value);
                markDirty(true);
            } finally {
                lock.writeLock().unlock();
            }
        } else {
            throw new UnsupportedOperationException("Can't set \"" + key + "\" to \"" + value + "\" (type: " + value.getClass().getSimpleName() + ") because SlimefunBlockData only supports Strings");
        }
    }

    @Nullable
    public String getValue(@Nonnull String key) {
        Validate.notNull(key, "Keys cannot be null.");

        lock.readLock().lock();

        try {
            return data.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nonnull
    public String getValueOrDefault(@Nonnull String key, @Nonnull String defaultValue) {
        Validate.notNull(key, "Keys cannot be null.");
        Validate.notNull(defaultValue, "The provided default value cannot be null.");

        String value = getValue(key);
        return value != null ? value : defaultValue;
    }

    public boolean hasValue(@Nonnull String key) {
        Validate.notNull(key, "Keys cannot be null.");

        lock.readLock().lock();

        try {
            return data.containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Nonnull
    public String toJSON() {
        return toJSON(DEFAULT_GSON);
    }

    @Nonnull
    public String toJSON(@Nonnull Gson gson) {
        Validate.notNull(gson, "The provided gson instance cannot be null!");
        lock.readLock().lock();

        try {
            return gson.toJson(data);
        } finally {
            lock.readLock().unlock();
        }
    }

}
