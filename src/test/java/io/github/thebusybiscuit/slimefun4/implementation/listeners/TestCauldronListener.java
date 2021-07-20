package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting.CauldronListener;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;

class TestCauldronListener {

    private static Slimefun plugin;
    private static CauldronListener listener;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new CauldronListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    private PlayerInteractEvent mockCauldronEvent(ItemStack item) {
        Player player = server.addPlayer();
        Block block = new BlockMock(Material.CAULDRON);
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, item, block, BlockFace.UP, EquipmentSlot.HAND);

        listener.onCauldronUse(event);
        return event;
    }

    @Test
    @DisplayName("Test Cauldron handling null")
    void testCauldronWithNull() {
        PlayerInteractEvent event = mockCauldronEvent(null);
        Assertions.assertEquals(Result.DEFAULT, event.useItemInHand());
    }

    @Test
    @DisplayName("Test Cauldron working as normal with unrelated items")
    void testCauldronWithNormalItem() {
        PlayerInteractEvent event = mockCauldronEvent(new ItemStack(Material.GOLD_BLOCK));
        Assertions.assertEquals(Result.DEFAULT, event.useItemInHand());
    }

    @Test
    @DisplayName("Test Cauldron working as normal with normal leather armor")
    void testCauldronWithNormalLeatherArmor() {
        PlayerInteractEvent event = mockCauldronEvent(new ItemStack(Material.LEATHER_BOOTS));
        Assertions.assertEquals(Result.DEFAULT, event.useItemInHand());
    }

    @Test
    @DisplayName("Test Cauldron working as normal with non-leather slimefun items")
    void testCauldronWithSlimefunItem() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "CAULDRON_TEST_MOCK", new CustomItemStack(Material.GOLDEN_APPLE, "&6Mock"));
        item.register(plugin);

        PlayerInteractEvent event = mockCauldronEvent(item.getItem());
        Assertions.assertEquals(Result.DEFAULT, event.useItemInHand());
    }

    @Test
    @DisplayName("Test Cauldron being cancelled with slimefun leather armor")
    void testCauldronWithSlimefunLeatherArmor() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "CAULDRON_TEST_MOCK_LEATHER", new CustomItemStack(Material.LEATHER_BOOTS, "&6Mock Leather Armor"));
        item.register(plugin);

        PlayerInteractEvent event = mockCauldronEvent(item.getItem());
        Assertions.assertEquals(Result.DENY, event.useItemInHand());
    }

    @Test
    @DisplayName("Test Cauldron working as normal with vanilla slimefun leather armor")
    void testCauldronWithVanillaLeatherArmor() {
        VanillaItem item = TestUtilities.mockVanillaItem(plugin, Material.LEATHER_CHESTPLATE, true);
        item.register(plugin);

        PlayerInteractEvent event = mockCauldronEvent(item.getItem());
        Assertions.assertEquals(Result.DEFAULT, event.useItemInHand());
    }

}
