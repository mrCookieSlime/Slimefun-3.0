package io.github.thebusybiscuit.slimefun4.core.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This Service is responsible for applying custom model data to any {@link SlimefunItemStack}
 * if a Server Owner configured Slimefun to use those.
 * We simply use {@link ItemMeta#setCustomModelData(Integer)} for this.
 * 
 * @author TheBusyBiscuit
 *
 */
public class CustomTextureService {

    /**
     * The {@link Config} object in which the Server Owner can configure the item models.
     */
    private final Config config;

    /**
     * This nullable {@link StringBuffer} represents the "version" of the used item-models file.
     * This version is served with our resource pack.
     */
    private String version = null;

    /**
     * This boolean represents whether the file was modified anyway.
     * This is equivalent to at least one value being set to a number which
     * is not zero!
     */
    private boolean modified = false;

    /**
     * This creates a new {@link CustomTextureService} for the provided {@link Config}
     * 
     * @param config
     *            The {@link Config} to read custom model data from
     */
    public CustomTextureService(@Nonnull Config config) {
        this.config = config;
        config.getConfiguration().options().header("This file is used to assign items from Slimefun or any of its addons\n" + "the 'CustomModelData' NBT tag. This can be used in conjunction with a custom resource pack\n" + "to give items custom textures.\n0 means there is no data assigned to that item.\n\n" + "There is no official Slimefun resource pack at the moment.");
        config.getConfiguration().options().copyHeader(true);
    }

    /**
     * This method registers the given {@link SlimefunItem SlimefunItems} to this {@link CustomTextureService}.
     * If saving is enabled, it will save them to the {@link Config} file.
     * 
     * @param items
     *            The {@link SlimefunItem SlimefunItems} to register
     * @param save
     *            Whether to save this file
     */
    public void register(@Nonnull Collection<SlimefunItem> items, boolean save) {
        Validate.notEmpty(items, "items must neither be null or empty.");

        loadDefaultValues();

        for (SlimefunItem item : items) {
            if (item != null) {
                config.setDefaultValue(item.getId(), 0);

                if (config.getInt(item.getId()) != 0) {
                    modified = true;
                }
            }
        }

        version = config.getString("version");

        if (save) {
            config.save();
        }
    }

    private void loadDefaultValues() {
        InputStream inputStream = Slimefun.class.getResourceAsStream("/item-models.yml");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(reader);

            for (String key : cfg.getKeys(false)) {
                config.setDefaultValue(key, cfg.getInt(key));
            }
        } catch (Exception e) {
            Slimefun.logger().log(Level.SEVERE, "Failed to load default item-models.yml file", e);
        }
    }

    @Nullable
    public String getVersion() {
        return version;
    }

    /**
     * This returns true if any custom model data was configured.
     * If every item id has no configured custom model data, it will return false.
     * 
     * @return Whether any custom model data was configured
     */
    public boolean isActive() {
        return modified;
    }

    /**
     * This returns the configured custom model data for a given id.
     * 
     * @param id
     *            The id to get the data for
     * 
     * @return The configured custom model data
     */
    public int getModelData(@Nonnull String id) {
        Validate.notNull(id, "Cannot get the ModelData for 'null'");

        return config.getInt(id);
    }

    /**
     * This method sets the custom model data for this {@link ItemStack}
     * to the value configured for the provided item id.
     * 
     * @param item
     *            The {@link ItemStack} to set the custom model data for
     * @param id
     *            The id for which to get the configured model data
     */
    public void setTexture(@Nonnull ItemStack item, @Nonnull String id) {
        Validate.notNull(item, "The Item cannot be null!");
        Validate.notNull(id, "Cannot store null on an Item!");

        ItemMeta im = item.getItemMeta();
        setTexture(im, id);
        item.setItemMeta(im);
    }

    /**
     * This method sets the custom model data for this {@link ItemMeta}
     * to the value configured for the provided item id.
     * 
     * @param im
     *            The {@link ItemMeta} to set the custom model data for
     * @param id
     *            The id for which to get the configured model data
     */
    public void setTexture(@Nonnull ItemMeta im, @Nonnull String id) {
        Validate.notNull(im, "The ItemMeta cannot be null!");
        Validate.notNull(id, "Cannot store null on an ItemMeta!");

        int data = getModelData(id);
        im.setCustomModelData(data == 0 ? null : data);
    }

}
