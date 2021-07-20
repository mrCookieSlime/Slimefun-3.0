package io.github.thebusybiscuit.slimefun4.utils;

import java.util.Arrays;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFormEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;

class TestInfiniteBlockGenerators {

    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test if invalid Cobblestone generators are ignored")
    void testInvalidCobblestoneGenerator() {
        World world = new WorldMock();
        Block block = world.getBlockAt(0, 100, 0);

        block.setType(Material.STONE);
        Assertions.assertFalse(InfiniteBlockGenerator.COBBLESTONE_GENERATOR.test(block));
        Assertions.assertNull(InfiniteBlockGenerator.findAt(block));

        block.setType(Material.COBBLESTONE);
        Assertions.assertFalse(InfiniteBlockGenerator.COBBLESTONE_GENERATOR.test(block));
        Assertions.assertNull(InfiniteBlockGenerator.findAt(block));
    }

    @ParameterizedTest
    @MethodSource(value = "provideFaces")
    @DisplayName("Test if a Cobblestone Generator can be detected")
    void testValidCobblestoneGenerator(BlockFace water, BlockFace lava) {
        InfiniteBlockGenerator generator = InfiniteBlockGenerator.COBBLESTONE_GENERATOR;

        World world = new WorldMock();
        Block block = world.getBlockAt(0, 100, 0);

        block.setType(Material.COBBLESTONE);
        block.getRelative(water).setType(Material.WATER);
        block.getRelative(lava).setType(Material.LAVA);

        Assertions.assertTrue(generator.test(block));
        Assertions.assertNotNull(generator.callEvent(block));

        server.getPluginManager().assertEventFired(BlockFormEvent.class);
        server.getPluginManager().clearEvents();

        Assertions.assertEquals(generator, InfiniteBlockGenerator.findAt(block));
    }

    @Test
    @DisplayName("Test if invalid Basalt generators are ignored")
    void testInvalidBasaltGenerator() {
        World world = new WorldMock();
        Block block = world.getBlockAt(0, 100, 0);

        block.setType(Material.COBBLESTONE);
        Assertions.assertFalse(InfiniteBlockGenerator.BASALT_GENERATOR.test(block));
        Assertions.assertNull(InfiniteBlockGenerator.findAt(block));

        block.setType(Material.BASALT);
        Assertions.assertFalse(InfiniteBlockGenerator.BASALT_GENERATOR.test(block));
        Assertions.assertNull(InfiniteBlockGenerator.findAt(block));

        block.getRelative(BlockFace.DOWN).setType(Material.SOUL_SOIL);
        Assertions.assertFalse(InfiniteBlockGenerator.BASALT_GENERATOR.test(block));
        Assertions.assertNull(InfiniteBlockGenerator.findAt(block));
    }

    @ParameterizedTest
    @MethodSource(value = "provideFaces")
    @DisplayName("Test if a Basalt Generator can be detected")
    void testValidBasaltGenerator(BlockFace ice, BlockFace lava) {
        InfiniteBlockGenerator generator = InfiniteBlockGenerator.BASALT_GENERATOR;

        World world = new WorldMock();
        Block block = world.getBlockAt(0, 100, 0);

        block.setType(Material.BASALT);
        block.getRelative(BlockFace.DOWN).setType(Material.SOUL_SOIL);
        block.getRelative(ice).setType(Material.BLUE_ICE);
        block.getRelative(lava).setType(Material.LAVA);

        Assertions.assertTrue(generator.test(block));
        Assertions.assertNotNull(generator.callEvent(block));

        server.getPluginManager().assertEventFired(BlockFormEvent.class);
        server.getPluginManager().clearEvents();

        Assertions.assertEquals(generator, InfiniteBlockGenerator.findAt(block));
    }

    @Test
    @DisplayName("Test if invalid Stone generators are ignored")
    void testInvalidStoneGenerator() {
        World world = new WorldMock();
        Block block = world.getBlockAt(0, 100, 0);

        block.setType(Material.COBBLESTONE);
        Assertions.assertFalse(InfiniteBlockGenerator.STONE_GENERATOR.test(block));
        Assertions.assertNull(InfiniteBlockGenerator.findAt(block));

        block.setType(Material.STONE);
        Assertions.assertFalse(InfiniteBlockGenerator.STONE_GENERATOR.test(block));
        Assertions.assertNull(InfiniteBlockGenerator.findAt(block));

        block.getRelative(BlockFace.UP).setType(Material.LAVA);
        Assertions.assertFalse(InfiniteBlockGenerator.STONE_GENERATOR.test(block));
        Assertions.assertNull(InfiniteBlockGenerator.findAt(block));
    }

    @ParameterizedTest
    @EnumSource(value = BlockFace.class, names = { "NORTH", "EAST", "SOUTH", "WEST" })
    @DisplayName("Test if a Stone Generator can be detected")
    void testValidStoneGenerator(BlockFace water) {
        InfiniteBlockGenerator generator = InfiniteBlockGenerator.STONE_GENERATOR;

        World world = new WorldMock();
        Block block = world.getBlockAt(0, 100, 0);

        block.setType(Material.STONE);
        block.getRelative(BlockFace.UP).setType(Material.LAVA);
        block.getRelative(water).setType(Material.WATER);

        Assertions.assertTrue(generator.test(block));
        Assertions.assertNotNull(generator.callEvent(block));

        server.getPluginManager().assertEventFired(BlockFormEvent.class);
        server.getPluginManager().clearEvents();

        Assertions.assertEquals(generator, InfiniteBlockGenerator.findAt(block));
    }

    private static Stream<Arguments> provideFaces() {
        BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
        Stream<BlockFace> stream = Arrays.stream(faces);
        return stream.flatMap(a -> Arrays.stream(faces).filter(b -> a != b).map(b -> Arguments.of(a, b)));
    }
}
