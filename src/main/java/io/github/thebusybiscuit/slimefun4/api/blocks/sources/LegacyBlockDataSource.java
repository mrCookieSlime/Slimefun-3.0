package io.github.thebusybiscuit.slimefun4.api.blocks.sources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.thebusybiscuit.cscorelib2.blocks.BlockPosition;
import io.github.thebusybiscuit.cscorelib2.blocks.ChunkPosition;
import io.github.thebusybiscuit.slimefun4.api.blocks.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.blocks.SlimefunChunkData;
import io.github.thebusybiscuit.slimefun4.api.blocks.SlimefunWorldData;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;

public class LegacyBlockDataSource implements BlockDataSource {

    private static final String PATH_BLOCKS = "data-storage/Slimefun/stored-blocks/";
    private static final String PATH_CHUNKS = "data-storage/Slimefun/stored-chunks/";

    /**
     * Our {@link JsonParser} instance.
     */
    private final JsonParser parser = new JsonParser();

    @Override
    public void loadBlocks(SlimefunWorldData data, Map<Long, SlimefunBlockData> blocks) {
        File dir = new File(PATH_BLOCKS + data.getName());

        if (dir.exists()) {
            loadBlocks(data, dir, blocks);
        } else {
            dir.mkdirs();
        }
    }

    private void loadBlocks(@Nonnull SlimefunWorldData data, @Nonnull File directory, @Nonnull Map<Long, SlimefunBlockData> blocks) {
        File[] files = directory.listFiles();
        long total = files.length;
        long start = System.currentTimeMillis();
        long done = 0;
        long timestamp = System.currentTimeMillis();
        long totalBlocks = 0;
        int delay = SlimefunPlugin.getCfg().getInt("URID.info-delay");

        try {
            for (File file : files) {
                if (file.getName().equals("null.sfb")) {
                    SlimefunPlugin.logger().log(Level.WARNING, "File with corrupted blocks detected!");
                    SlimefunPlugin.logger().log(Level.WARNING, "Slimefun will simply skip this File, you should look inside though!");
                    SlimefunPlugin.logger().log(Level.WARNING, file.getPath());
                } else if (file.getName().endsWith(".sfb")) {
                    if (timestamp + delay < System.currentTimeMillis()) {
                        int progress = Math.round((((done * 100.0F) / total) * 100.0F) / 100.0F);
                        SlimefunPlugin.logger().log(Level.INFO, "Loading Blocks... {0}% done (\"{1}\")", new Object[] { progress, data.getName() });
                        timestamp = System.currentTimeMillis();
                    }

                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

                    for (String key : cfg.getKeys(false)) {
                        loadBlock(blocks, file, cfg, key);
                        totalBlocks++;
                    }

                    done++;
                }
            }
        } finally {
            long time = (System.currentTimeMillis() - start);
            SlimefunPlugin.logger().log(Level.INFO, "Loading Blocks... 100% (FINISHED - {0}ms)", time);
            SlimefunPlugin.logger().log(Level.INFO, "Loaded a total of {0} Blocks for World \"{1}\"", new Object[] { totalBlocks, data.getName() });

            if (totalBlocks > 0) {
                SlimefunPlugin.logger().log(Level.INFO, "Avg: {0}ms/Block", NumberUtils.roundDecimalNumber((double) time / (double) totalBlocks));
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void loadBlock(Map<Long, SlimefunBlockData> blocks, File file, FileConfiguration cfg, String key) {
        Location l = deserializeLocation(key);

        if (l == null) {
            // That location was malformed, we will skip this one
            return;
        }

        try {
            String json = cfg.getString(key);
            SlimefunBlockData blockInfo = parseBlockInfo(l, json);

            if (blockInfo != null && blockInfo.hasValue("id")) {
                long pos = BlockPosition.getAsLong(l.getBlockX(), l.getBlockY(), l.getBlockZ());

                if (blocks.putIfAbsent(pos, blockInfo) != null) {
                    /*
                     * It should not be possible to have two blocks on the same location.
                     * Ignore the new entry if a block is already present and print an
                     * error to the console (if enabled).
                     */
                    if (SlimefunPlugin.getRegistry().logDuplicateBlockEntries()) {
                        SlimefunPlugin.logger().log(Level.INFO, "Ignoring duplicate block @ {0}, {1}, {2} ({3} -> {4})", new Object[] { l.getBlockX(), l.getBlockY(), l.getBlockZ(), blockInfo.getId(), blocks.get(pos).getId() });
                    }

                    return;
                }

                String fileName = file.getName().replace(".sfb", "");

                if (SlimefunPlugin.getRegistry().getTickerBlocks().contains(fileName)) {
                    SlimefunPlugin.getTickerTask().enableTicker(l);
                }
            }
        } catch (Exception x) {
            SlimefunPlugin.logger().log(Level.WARNING, x, () -> "Failed to load " + file.getName() + '(' + key + ") for Slimefun " + SlimefunPlugin.getVersion());
        }
    }

    @Nullable
    private Location deserializeLocation(@Nonnull String l) {
        try {
            String[] components = PatternUtils.SEMICOLON.split(l);

            if (components.length != 4) {
                return null;
            }

            World w = Bukkit.getWorld(components[0]);

            if (w != null) {
                return new Location(w, Integer.parseInt(components[1]), Integer.parseInt(components[2]), Integer.parseInt(components[3]));
            }
        } catch (NumberFormatException x) {
            SlimefunPlugin.logger().log(Level.WARNING, "Could not parse Number", x);
        }

        return null;
    }

    @ParametersAreNonnullByDefault
    @Nullable
    private SlimefunBlockData parseBlockInfo(Location l, String json) {
        try {
            return new SlimefunBlockData(parseJSON(json));
        } catch (Exception x) {
            Logger logger = SlimefunPlugin.logger();
            logger.log(Level.WARNING, x.getClass().getName());
            logger.log(Level.WARNING, "Failed to parse BlockInfo for Block @ {0}, {1}, {2}", new Object[] { l.getBlockX(), l.getBlockY(), l.getBlockZ() });
            logger.log(Level.WARNING, json);
            logger.log(Level.WARNING, "");
            logger.log(Level.WARNING, "IGNORE THIS ERROR UNLESS IT IS SPAMMING");
            logger.log(Level.WARNING, "");
            logger.log(Level.SEVERE, x, () -> "An Error occurred while parsing Block Info for Slimefun " + SlimefunPlugin.getVersion());
            return null;
        }
    }

    @Nonnull
    private Map<String, String> parseJSON(String json) {
        Map<String, String> map = new HashMap<>();

        if (json != null && json.length() > 2) {
            JsonObject obj = parser.parse(json).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                map.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        return map;
    }

    @Override
    public void loadChunks(SlimefunWorldData data, Map<Long, SlimefunChunkData> chunks) {
        File file = new File(PATH_CHUNKS + "chunks.sfc");

        if (file.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            for (String key : cfg.getKeys(false)) {
                try {
                    OptionalLong pos = deserializeChunk(key);

                    if (pos.isPresent() && data.getName().equals(PatternUtils.SEMICOLON.split(key)[0])) {
                        SlimefunChunkData chunkData = new SlimefunChunkData(parseJSON(cfg.getString(key)));
                        chunks.put(pos.getAsLong(), chunkData);
                    }
                } catch (Exception x) {
                    SlimefunPlugin.logger().log(Level.WARNING, x, () -> "Failed to load " + file.getName() + " in World " + data.getName() + '(' + key + ") for Slimefun " + SlimefunPlugin.getVersion());
                }
            }
        }
    }

    @Nonnull
    private OptionalLong deserializeChunk(@Nonnull String string) {
        try {
            String[] segments = PatternUtils.SEMICOLON.split(string);
            int x = Integer.parseInt(segments[2]);
            int z = Integer.parseInt(segments[3]);
            return OptionalLong.of(ChunkPosition.getAsLong(x, z));
        } catch (Exception x) {
            SlimefunPlugin.logger().log(Level.WARNING, x, () -> "Could not parse Chunk: " + string);
            return OptionalLong.empty();
        }
    }

    @Override
    public void saveBlocks(SlimefunWorldData data, Map<Long, SlimefunBlockData> blocks) {
        // TODO Implement block saving
    }

    @Override
    public void saveChunks(SlimefunWorldData data, Map<Long, SlimefunChunkData> chunks) {
        // TODO Implement chunk saving
    }

}
