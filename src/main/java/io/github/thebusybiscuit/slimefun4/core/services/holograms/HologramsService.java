package io.github.thebusybiscuit.slimefun4.core.services.holograms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This service is responsible for handling holograms.
 * 
 * @author TheBusyBiscuit
 *
 * @see HologramOwner
 */
public class HologramsService {

    /**
     * The radius in which we scan for holograms
     */
    private static final double RADIUS = 0.45;

    /**
     * The frequency at which to purge.
     * Every 45 seconds.
     */
    private static final long PURGE_RATE = 45L * 20L;

    /**
     * Our {@link Plugin} instance
     */
    private final Plugin plugin;

    /**
     * The default hologram offset
     */
    private final Vector defaultOffset = new Vector(0.5, 0.75, 0.5);

    /**
     * The {@link NamespacedKey} used to store data on a hologram
     */
    private final NamespacedKey persistentDataKey;

    /**
     * Our cache to save {@link Entity} lookups
     */
    private final Map<BlockPosition, Hologram> cache = new HashMap<>();

    /**
     * This constructs a new {@link HologramsService}.
     * 
     * @param plugin
     *            Our {@link Plugin} instance
     */
    public HologramsService(@Nonnull Plugin plugin) {
        this.plugin = plugin;

        // Null-Validation is performed in the NamespacedKey constructor
        persistentDataKey = new NamespacedKey(plugin, "hologram_id");
    }

    /**
     * This will start the {@link HologramsService} and schedule a repeating
     * purge-task.
     */
    public void start() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::purge, PURGE_RATE, PURGE_RATE);
    }

    /**
     * This returns the default {@link Hologram} offset.
     * 
     * @return The default offset
     */
    @Nonnull
    public Vector getDefaultOffset() {
        return defaultOffset;
    }

    /**
     * This purges any expired {@link Hologram}.
     */
    private void purge() {
        Iterator<Hologram> iterator = cache.values().iterator();

        while (iterator.hasNext()) {
            Hologram hologram = iterator.next();

            if (hologram.hasExpired()) {
                iterator.remove();
            }
        }
    }

    /**
     * This returns the {@link Hologram} associated with the given {@link Location}.
     * If createIfNoneExists is set to true a new {@link ArmorStand} will be spawned
     * if no existing one could be found.
     * 
     * @param loc
     *            The {@link Location}
     * @param createIfNoneExists
     *            Whether to create a new {@link ArmorStand} if none was found
     * 
     * @return The existing (or newly created) hologram
     */
    @Nullable
    private Hologram getHologram(@Nonnull Location loc, boolean createIfNoneExists) {
        Validate.notNull(loc, "Location cannot be null");

        BlockPosition position = new BlockPosition(loc);
        Hologram hologram = cache.get(position);

        // Check if the ArmorStand was cached and still exists
        if (hologram != null && !hologram.hasDespawned()) {
            return hologram;
        }

        // Scan all nearby entities which could be possible holograms
        Collection<Entity> holograms = loc.getWorld().getNearbyEntities(loc, RADIUS, RADIUS, RADIUS, this::isHologram);

        for (Entity n : holograms) {
            if (n instanceof ArmorStand) {
                PersistentDataContainer container = n.getPersistentDataContainer();

                /*
                 * Any hologram we created will have a persistent data key for identification.
                 * Make sure that the value matches our BlockPosition.
                 */
                if (hasHologramData(container, position)) {
                    if (hologram != null) {
                        // Fixes #2927 - Remove any duplicates we find
                        n.remove();
                    } else {
                        hologram = getAsHologram(position, n, container);
                    }
                }
            }
        }

        if (hologram == null && createIfNoneExists) {
            // Spawn a new ArmorStand
            ArmorStand armorstand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            PersistentDataContainer container = armorstand.getPersistentDataContainer();

            return getAsHologram(position, armorstand, container);
        } else {
            return hologram;
        }
    }

    @ParametersAreNonnullByDefault
    private boolean hasHologramData(PersistentDataContainer container, BlockPosition position) {
        if (container.has(persistentDataKey, PersistentDataType.LONG)) {
            long value = container.get(persistentDataKey, PersistentDataType.LONG);
            return value == position.getPosition();
        } else {
            return false;
        }
    }

    /**
     * This checks if a given {@link Entity} is an {@link ArmorStand}
     * and whether it has the correct attributes to be considered a {@link Hologram}.
     * 
     * @param n
     *            The {@link Entity} to check
     * 
     * @return Whether this could be a hologram
     */
    private boolean isHologram(@Nonnull Entity n) {
        if (n instanceof ArmorStand) {
            ArmorStand armorstand = (ArmorStand) n;

            // The absolute minimum requirements to count as a hologram
            return !armorstand.isVisible() && armorstand.isSilent() && !armorstand.hasGravity();
        } else {
            return false;
        }
    }

    /**
     * This will cast the {@link Entity} to an {@link ArmorStand} and it will apply
     * all necessary attributes to the {@link ArmorStand}, then return a {@link Hologram}.
     * 
     * @param position
     *            The {@link BlockPosition} of this hologram
     * @param entity
     *            The {@link Entity}
     * @param container
     *            The {@link PersistentDataContainer} of the given {@link Entity}
     * 
     * @return The {@link Hologram}
     */
    @Nullable
    private Hologram getAsHologram(@Nonnull BlockPosition position, @Nonnull Entity entity, @Nonnull PersistentDataContainer container) {
        if (entity instanceof ArmorStand) {
            ArmorStand armorstand = (ArmorStand) entity;

            armorstand.setVisible(false);
            armorstand.setInvulnerable(true);
            armorstand.setSilent(true);
            armorstand.setMarker(true);
            armorstand.setAI(false);
            armorstand.setGravity(false);
            armorstand.setRemoveWhenFarAway(false);

            // Set a persistent tag to re-identify the correct hologram later
            container.set(persistentDataKey, PersistentDataType.LONG, position.getPosition());

            // Store in cache for faster access
            Hologram hologram = new Hologram(armorstand.getUniqueId());
            cache.put(position, hologram);

            return hologram;
        } else {
            // This should never be reached
            return null;
        }
    }

    /**
     * This updates the {@link Hologram}.
     * You can use it to set the nametag or other properties.
     * <p>
     * <strong>This method must be executed on the main {@link Server} {@link Thread}.</strong>
     * 
     * @param loc
     *            The {@link Location}
     * @param consumer
     *            The callback to run
     */
    private void updateHologram(@Nonnull Location loc, @Nonnull Consumer<Hologram> consumer) {
        Validate.notNull(loc, "Location must not be null");
        Validate.notNull(consumer, "Callbacks must not be null");

        Runnable runnable = () -> {
            try {
                Hologram hologram = getHologram(loc, true);

                if (hologram != null) {
                    consumer.accept(hologram);
                }
            } catch (Exception | LinkageError x) {
                Slimefun.logger().log(Level.SEVERE, "Hologram located at {0}", new BlockPosition(loc));
                Slimefun.logger().log(Level.SEVERE, "Something went wrong while trying to update this hologram", x);
            }
        };

        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Slimefun.runSync(runnable);
        }
    }

    /**
     * This removes the {@link Hologram} at that given {@link Location}.
     * <p>
     * <strong>This method must be executed on the main {@link Server} {@link Thread}.</strong>
     * 
     * @param loc
     *            The {@link Location}
     * 
     * @return Whether the {@link Hologram} could be removed, false if the {@link Hologram} does not
     *         exist or was already removed
     */
    public boolean removeHologram(@Nonnull Location loc) {
        Validate.notNull(loc, "Location cannot be null");

        if (Bukkit.isPrimaryThread()) {
            try {
                Hologram hologram = getHologram(loc, false);

                if (hologram != null) {
                    cache.remove(new BlockPosition(loc));
                    hologram.remove();
                    return true;
                } else {
                    return false;
                }
            } catch (Exception | LinkageError x) {
                Slimefun.logger().log(Level.SEVERE, "Hologram located at {0}", new BlockPosition(loc));
                Slimefun.logger().log(Level.SEVERE, "Something went wrong while trying to remove this hologram", x);
                return false;
            }
        } else {
            throw new UnsupportedOperationException("You cannot remove a hologram asynchronously.");
        }
    }

    /**
     * This will update the label of the {@link Hologram}.
     * 
     * @param loc
     *            The {@link Location} of this {@link Hologram}
     * @param label
     *            The label to set, can be null
     */
    public void setHologramLabel(@Nonnull Location loc, @Nullable String label) {
        Validate.notNull(loc, "Location must not be null");

        updateHologram(loc, hologram -> hologram.setLabel(label));
    }

}
