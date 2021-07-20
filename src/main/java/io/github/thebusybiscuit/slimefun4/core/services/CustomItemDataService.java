package io.github.thebusybiscuit.slimefun4.core.services;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

/**
 * This Service is responsible for applying NBT data to a {@link SlimefunItemStack}.
 * This is used to ensure that the id of a {@link SlimefunItem} is stored alongside any
 * {@link ItemStack} at all times.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SlimefunItemStack
 *
 */
public class CustomItemDataService implements Keyed {

    /**
     * This is the {@link NamespacedKey} used to store/read data.
     */
    private final NamespacedKey namespacedKey;

    /**
     * This creates a new {@link CustomItemDataService} for the given {@link Plugin} and the
     * provided data key.
     * 
     * @param plugin
     *            The {@link Plugin} for this service to use
     * @param key
     *            The key under which to store data
     */
    public CustomItemDataService(@Nonnull Plugin plugin, @Nonnull String key) {
        // Null-Validation is performed in the NamespacedKey constructor
        namespacedKey = new NamespacedKey(plugin, key);
    }

    @Override
    public NamespacedKey getKey() {
        return namespacedKey;
    }

    /**
     * This method stores the given id on the provided {@link ItemStack} via
     * persistent data.
     * 
     * @param item
     *            The {@link ItemStack} to store data on
     * @param id
     *            The id to store on the {@link ItemStack}
     */
    public void setItemData(@Nonnull ItemStack item, @Nonnull String id) {
        Validate.notNull(item, "The Item cannot be null!");
        Validate.notNull(id, "Cannot store null on an Item!");

        ItemMeta im = item.getItemMeta();
        setItemData(im, id);
        item.setItemMeta(im);
    }

    /**
     * This method stores the given id on the provided {@link ItemMeta} via
     * persistent data.
     * 
     * @param meta
     *            The {@link ItemMeta} to store data on
     * @param id
     *            The id to store on the {@link ItemMeta}
     */
    public void setItemData(@Nonnull ItemMeta meta, @Nonnull String id) {
        Validate.notNull(meta, "The ItemMeta cannot be null!");
        Validate.notNull(id, "Cannot store null on an ItemMeta!");

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(namespacedKey, PersistentDataType.STRING, id);
    }

    /**
     * This method returns an {@link Optional} holding the data stored on the given {@link ItemStack}.
     * The {@link Optional} will be empty if the given {@link ItemStack} is null, doesn't have any {@link ItemMeta}
     * or if the requested data simply does not exist on that {@link ItemStack}.
     * 
     * @param item
     *            The {@link ItemStack} to check
     * 
     * @return An {@link Optional} describing the result
     */
    @Nonnull
    public Optional<String> getItemData(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return Optional.empty();
        }

        return getItemData(item.getItemMeta());
    }

    /**
     * This method returns an {@link Optional}, either empty or holding the data stored
     * on the given {@link ItemMeta}.
     * 
     * @param meta
     *            The {@link ItemMeta} to check
     * 
     * @return An {@link Optional} describing the result
     */
    @Nonnull
    public Optional<String> getItemData(@Nonnull ItemMeta meta) {
        Validate.notNull(meta, "Cannot read data from null!");

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return Optional.ofNullable(container.get(namespacedKey, PersistentDataType.STRING));
    }

}
