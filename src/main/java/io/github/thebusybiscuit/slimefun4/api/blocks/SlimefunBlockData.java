package io.github.thebusybiscuit.slimefun4.api.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.bukkit.Location;

import com.google.gson.JsonObject;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

/**
 * This class is used to speed up parsing of a {@link JsonObject} that is stored at
 * a given {@link Location}.
 * 
 * @author TheBusyBiscuit
 * @author creator3
 * 
 * @see BlockDatabase
 *
 */
public class SlimefunBlockData extends AbstractDataObject {

    /**
     * This creates a new {@link SlimefunBlockData} object.
     * It is initialized with an empty {@link HashMap}.
     */
    public SlimefunBlockData() {
        this(new HashMap<>());
    }

    /**
     * This creates a new {@link SlimefunBlockData} object
     * and also initializes it using the given {@link Map}
     * 
     * @param data
     *            The data {@link Map}
     */
    public SlimefunBlockData(@Nonnull Map<String, String> data) {
        super(data);
    }

    @Override
    public void setValue(String key, Object value) {
        if (Objects.equals(key, "id") && value == null) {
            throw new IllegalArgumentException("SlimefunItem Ids are not nullable.");
        } else {
            super.setValue(key, value);
        }
    }

    /**
     * This returns the id of the associated {@link SlimefunItem} with this {@link SlimefunBlockData}.
     * 
     * @return The {@link SlimefunItem} id
     */
    @Nonnull
    public String getId() {
        return getValue("id");
    }

    /**
     * This returns the {@link SlimefunItem} associated with this {@link SlimefunBlockData}.
     * 
     * @return The {@link SlimefunItem}
     */
    @Nonnull
    public SlimefunItem getSlimefunItem() {
        return SlimefunItem.getByID(getId());
    }

    @Deprecated
    @Nonnull
    public Config getLegacyAdapter() {
        return new LegacyAdapter(this);
    }

}
