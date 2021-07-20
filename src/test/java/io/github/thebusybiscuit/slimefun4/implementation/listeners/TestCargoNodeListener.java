package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.CargoInputNode;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;
import io.github.thebusybiscuit.slimefun4.test.providers.SlimefunItemsSource;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestCargoNodeListener {

    private static Slimefun plugin;
    private static CargoNodeListener listener;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new CargoNodeListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @DisplayName("Test placing Cargo nodes on valid sides of blocks")
    @SlimefunItemsSource(items = { "CARGO_INPUT_NODE", "CARGO_OUTPUT_NODE", "CARGO_OUTPUT_NODE_2" })
    void testSidePlacement(ItemStack item) {
        Player player = server.addPlayer();
        Location l = new Location(player.getWorld(), 190, 50, 400);
        Block b = l.getBlock();
        Block against = b.getRelative(BlockFace.NORTH);

        BlockPlaceEvent event = new BlockPlaceEvent(b, b.getState(), against, item, player, true, EquipmentSlot.HAND);
        listener.onCargoNodePlace(event);
        Assertions.assertFalse(event.isCancelled());
    }

    @Test
    @DisplayName("Test placing Cargo nodes on invalid sides of blocks")
    void testInvalidSidePlacement() {
        Player player = server.addPlayer();
        Location l = new Location(player.getWorld(), 190, 50, 400);
        Block b = l.getBlock();
        Block against = b.getRelative(BlockFace.DOWN);

        ItemGroup category = TestUtilities.getCategory(plugin, "cargo_test");
        SlimefunItemStack item = new SlimefunItemStack("MOCK_CARGO_NODE", new CustomItemStack(Material.PLAYER_HEAD, "&4Cargo node!"));
        CargoInputNode node = new CargoInputNode(category, item, RecipeType.NULL, new ItemStack[9], null);
        node.register(plugin);

        BlockPlaceEvent event = new BlockPlaceEvent(b, b.getState(), against, item, player, true, EquipmentSlot.HAND);
        listener.onCargoNodePlace(event);
        Assertions.assertTrue(event.isCancelled());
    }

    @Test
    @DisplayName("Test placing Cargo nodes on grass")
    void testGrassPlacement() {
        Player player = server.addPlayer();

        Location l = new Location(player.getWorld(), 300, 25, 1200);
        Block b = l.getBlock();
        b.setType(Material.GRASS);

        ItemGroup category = TestUtilities.getCategory(plugin, "cargo_test");
        SlimefunItemStack item = new SlimefunItemStack("MOCK_CARGO_NODE_2", new CustomItemStack(Material.PLAYER_HEAD, "&4Cargo node!"));
        CargoInputNode node = new CargoInputNode(category, item, RecipeType.NULL, new ItemStack[9], null);
        node.register(plugin);

        BlockPlaceEvent event = new BlockPlaceEvent(b, b.getState(), b, item, player, true, EquipmentSlot.HAND);
        listener.onCargoNodePlace(event);

        Assertions.assertTrue(event.isCancelled());
    }

    @Test
    @DisplayName("Test non-Cargo node not being affected")
    void testNonCargoNode() {
        Slimefun.getRegistry().setBackwardsCompatible(true);
        Player player = server.addPlayer();
        Location l = new Location(player.getWorld(), 190, 50, 400);
        Block b = l.getBlock();
        Block against = b.getRelative(BlockFace.DOWN);

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        BlockPlaceEvent event = new BlockPlaceEvent(b, b.getState(), against, item, player, true, EquipmentSlot.HAND);
        listener.onCargoNodePlace(event);
        Assertions.assertFalse(event.isCancelled());
        Slimefun.getRegistry().setBackwardsCompatible(false);
    }

}
