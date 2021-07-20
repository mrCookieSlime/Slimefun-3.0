package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import io.github.thebusybiscuit.cscorelib2.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.TickerCollection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestTickerCollection {

    private static ServerMock serverMock;

    @BeforeAll
    public static void init() {
        serverMock = MockBukkit.mock();
    }

    @AfterAll
    public static void cleanup() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test the validity of the getters and setters")
    void testGettersAndAdding() {
        WorldMock world = new WorldMock();
        serverMock.addWorld(world);
        Location location = new Location(world, 0, 0, 0);
        long compressedBlockPosition = BlockPosition.getAsLong(0, 0, 0);
        TickerCollection tickerCollection = new TickerCollection();
        tickerCollection.addBlock(location);
        Set<Location> locations = tickerCollection.getBlocksAsLocations();
        Assertions.assertEquals(1, locations.size());
        Assertions.assertTrue(locations.contains(location));
        tickerCollection.getBlocks(world);
        Set<Long> compressedPositions = tickerCollection.getBlocks(world);
        Assertions.assertEquals(1, compressedPositions.size());
        Assertions.assertTrue(compressedPositions.contains(compressedBlockPosition));
    }

    @Test
    @DisplayName("Test the validity of clearing and removing")
    void testClearingAndRemoving() {
        WorldMock world = new WorldMock();
        serverMock.addWorld(world);
        Location location = new Location(world, 0, 0, 0);
        TickerCollection tickerCollection = new TickerCollection();

        tickerCollection.addBlock(location);
        Location anotherLocation = new Location(world, 1, 1, 1);
        long anotherCompressedPosition = BlockPosition.getAsLong(1, 1, 1);

        Set<Location> originalLocations = tickerCollection.getBlocksAsLocations();
        Assertions.assertEquals(1, originalLocations.size());
        Assertions.assertTrue(originalLocations.contains(location));

        Set<Long> compressedPositions = tickerCollection.getBlocks(world);
        Assertions.assertFalse(compressedPositions.contains(anotherCompressedPosition));
        Assertions.assertEquals(1, compressedPositions.size());
        tickerCollection.removeBlock(anotherLocation);

        Set<Location> newLocations1 = tickerCollection.getBlocksAsLocations();
        Assertions.assertTrue(newLocations1.contains(location));
        Assertions.assertFalse(compressedPositions.contains(anotherCompressedPosition));
        tickerCollection.removeBlock(location);

        Set<Location> newLocations2 = tickerCollection.getBlocksAsLocations();
        Assertions.assertFalse(newLocations2.contains(location));
        tickerCollection.addBlock(location);
        tickerCollection.addBlock(anotherLocation);
        Assertions.assertEquals(2, tickerCollection.getBlocksAsLocations().size());
        Assertions.assertEquals(2, tickerCollection.getBlocks(world).size());

        tickerCollection.clear();
        Assertions.assertTrue(tickerCollection.getBlocksAsLocations().isEmpty());
        Assertions.assertTrue(compressedPositions.isEmpty());
    }

    @Test
    @DisplayName("Test the concurrent capability of the collection")
    void testConcurrency() {
        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processors);
        final TickerCollection collection = new TickerCollection();
        final Map<World, Queue<BlockPosition>> worldPositionMap = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            WorldMock world = new WorldMock();
            serverMock.addWorld(world);
            Queue<BlockPosition> positions = new ArrayDeque<>(20);
            for (int j = 0; j < 20; j++) {
                positions.add(new BlockPosition(world, j, j, j));
            }
            worldPositionMap.put(world, positions);
        }
        int i = 0;
        for (Map.Entry<World, Queue<BlockPosition>> entry : worldPositionMap.entrySet()) {
            World world = entry.getKey();
            Queue<BlockPosition> positions = entry.getValue();
            BlockPosition removed;
            // Write tasks to read tasks = 1 : 2
            for (; !positions.isEmpty(); i++) {
                removed = positions.remove();
                BlockPosition finalPosition = removed;
                if (i++ % 3 == 0) {
                    executor.submit(() -> collection.addBlock(finalPosition));
                } else {
                    executor.submit(() -> collection.getBlocks(world).contains(finalPosition.getPosition()));
                }
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception ex) {
            String msg = "Failed to shutdown the executor service for TestTickerCollection#testConcucrrency";
            Assertions.fail(msg, ex);
        }
    }

}
