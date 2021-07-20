package io.github.thebusybiscuit.slimefun4.api.blocks.sources;

import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.api.blocks.BlockDatabase;
import io.github.thebusybiscuit.slimefun4.api.blocks.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.blocks.SlimefunChunkData;
import io.github.thebusybiscuit.slimefun4.api.blocks.SlimefunWorldData;

/**
 * The {@link BlockDataSource} interface is responsible for loading saving {@link SlimefunWorldData}.
 * There may be different implementations or sources for this data.
 * 
 * @author TheBusyBiscuit
 * 
 * @see BlockDatabase
 * @see SlimefunWorldData
 *
 */
public interface BlockDataSource {

    @ParametersAreNonnullByDefault
    void loadBlocks(SlimefunWorldData data, Map<Long, SlimefunBlockData> blocks);

    @ParametersAreNonnullByDefault
    void loadChunks(SlimefunWorldData data, Map<Long, SlimefunChunkData> chunks);

    @ParametersAreNonnullByDefault
    void saveBlocks(SlimefunWorldData data, Map<Long, SlimefunBlockData> blocks);

    @ParametersAreNonnullByDefault
    void saveChunks(SlimefunWorldData data, Map<Long, SlimefunChunkData> chunks);

}
