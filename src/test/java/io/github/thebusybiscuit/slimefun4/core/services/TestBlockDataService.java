package io.github.thebusybiscuit.slimefun4.core.services;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestBlockDataService {

    private static Slimefun plugin;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test creating a BlockDataService")
    void testInitialization() {
        BlockDataService service = new BlockDataService(plugin, "test");
        Assertions.assertEquals(new NamespacedKey(plugin, "test"), service.getKey());
    }

    @Test
    @DisplayName("Test Tile Entity check")
    void testTileEntities() {
        BlockDataService service = new BlockDataService(plugin, "test");

        Assertions.assertFalse(service.isTileEntity(null));
        Assertions.assertFalse(service.isTileEntity(Material.AIR));
        Assertions.assertFalse(service.isTileEntity(Material.DIRT));

        Assertions.assertTrue(service.isTileEntity(Material.CHEST));
        Assertions.assertTrue(service.isTileEntity(Material.ENDER_CHEST));
        Assertions.assertTrue(service.isTileEntity(Material.ENCHANTING_TABLE));
        Assertions.assertTrue(service.isTileEntity(Material.DISPENSER));
        Assertions.assertTrue(service.isTileEntity(Material.FURNACE));
        Assertions.assertTrue(service.isTileEntity(Material.PLAYER_HEAD));
        Assertions.assertTrue(service.isTileEntity(Material.PLAYER_WALL_HEAD));
    }
}
