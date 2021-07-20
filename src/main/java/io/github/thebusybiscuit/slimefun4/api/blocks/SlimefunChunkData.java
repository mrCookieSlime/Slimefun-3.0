package io.github.thebusybiscuit.slimefun4.api.blocks;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Location;

import com.google.gson.JsonObject;

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
public class SlimefunChunkData extends AbstractDataObject {

    /**
     * This creates a new {@link SlimefunChunkData} object.
     * It is initialized with an empty {@link HashMap}.
     */
    public SlimefunChunkData() {
        this(new HashMap<>());
    }

    /**
     * This creates a new {@link SlimefunChunkData} object
     * and also initializes it using the given {@link Map}
     * 
     * @param data
     *            The data {@link Map}
     */
    public SlimefunChunkData(@Nonnull Map<String, String> data) {
        super(data);
    }

}