package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting.GrindstoneListener;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestGrindstoneListener {

    private static Slimefun plugin;
    private static GrindstoneListener listener;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new GrindstoneListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    private InventoryClickEvent mockGrindStoneEvent(ItemStack item) {
        Player player = server.addPlayer();
        Inventory inv = TestUtilities.mockInventory(InventoryType.GRINDSTONE, item, null);
        InventoryView view = player.openInventory(inv);
        InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 2, ClickType.LEFT, InventoryAction.PICKUP_ONE);

        listener.onGrindstone(event);
        return event;
    }

    @Test
    void testGrindStoneWithoutSlimefunItems() {
        InventoryClickEvent event = mockGrindStoneEvent(new ItemStack(Material.ENCHANTED_BOOK));
        Assertions.assertEquals(Result.DEFAULT, event.getResult());
    }

    @Test
    void testGrindStoneWithSlimefunItem() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "ENCHANTED_MOCK_BOOK", new CustomItemStack(Material.ENCHANTED_BOOK, "&6Mock"));
        item.register(plugin);

        InventoryClickEvent event = mockGrindStoneEvent(item.getItem());
        Assertions.assertEquals(Result.DENY, event.getResult());
    }

    @Test
    void testGrindStoneWithVanillaItem() {
        VanillaItem item = TestUtilities.mockVanillaItem(plugin, Material.ENCHANTED_BOOK, true);
        item.register(plugin);

        InventoryClickEvent event = mockGrindStoneEvent(item.getItem());
        Assertions.assertEquals(Result.DEFAULT, event.getResult());
    }

    @ParameterizedTest
    @EnumSource(SlimefunGuideMode.class)
    void testGrindStoneWithSlimefunGuide(SlimefunGuideMode layout) {
        InventoryClickEvent event = mockGrindStoneEvent(SlimefunGuide.getItem(layout));
        Assertions.assertEquals(Result.DENY, event.getResult());
    }

}
