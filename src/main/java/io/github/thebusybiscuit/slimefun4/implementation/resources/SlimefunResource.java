package io.github.thebusybiscuit.slimefun4.implementation.resources;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This is an abstract parent class for any {@link GEOResource}
 * that is added by Slimefun itself. It is package-private, therefore
 * only classes inside this package can access it.
 * <p>
 * It just provides a bit of a convenience for us to reduce redundancies
 * in our {@link GEOResource} implementations.
 * 
 * @author TheBusyBiscuit
 *
 */
abstract class SlimefunResource implements GEOResource {

    private final NamespacedKey key;
    private final String defaultName;
    private final ItemStack item;
    private final int maxDeviation;
    private final boolean geoMiner;

    @ParametersAreNonnullByDefault
    SlimefunResource(String key, String defaultName, ItemStack item, int maxDeviation, boolean geoMiner) {
        Validate.notNull(key, "NamespacedKey cannot be null!");
        Validate.notNull(defaultName, "The default name cannot be null!");
        Validate.notNull(item, "item cannot be null!");

        this.key = new NamespacedKey(Slimefun.instance(), key);
        this.defaultName = defaultName;
        this.item = item;
        this.maxDeviation = maxDeviation;
        this.geoMiner = geoMiner;
    }

    @Override
    @Nonnull
    public NamespacedKey getKey() {
        return key;
    }

    @Override
    @Nonnull
    public String getName() {
        return defaultName;
    }

    @Override
    @Nonnull
    public ItemStack getItem() {
        return item.clone();
    }

    @Override
    public int getMaxDeviation() {
        return maxDeviation;
    }

    @Override
    public boolean isObtainableFromGEOMiner() {
        return geoMiner;
    }

}
