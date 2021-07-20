package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
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
import org.mockito.Mockito;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting.BrewingStandListener;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestBrewingStandListener {

    private static Slimefun plugin;
    private static BrewingStandListener listener;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new BrewingStandListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    private InventoryClickEvent mockBrewingEvent(ItemStack item) {
        Player player = server.addPlayer();
        Inventory inv = TestUtilities.mockInventory(InventoryType.BREWING);
        Mockito.when(inv.getHolder()).thenReturn(Mockito.mock(BrewingStand.class));
        Mockito.when(inv.getSize()).thenReturn(5);

        InventoryView view = player.openInventory(inv);
        view.setCursor(item);
        InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 1, ClickType.LEFT, InventoryAction.PICKUP_ONE);
        listener.onPreBrew(event);
        return event;
    }

    @Test
    void testBrewingWithoutSlimefunItems() {
        InventoryClickEvent event = mockBrewingEvent(new ItemStack(Material.BLAZE_POWDER));
        Assertions.assertEquals(Result.ALLOW, event.getResult());
    }

    @Test
    void testBrewingWithSlimefunItem() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "MOCK_POWDER", new CustomItemStack(Material.BLAZE_POWDER, "&6Magic Mock Powder"));
        item.register(plugin);

        InventoryClickEvent event = mockBrewingEvent(item.getItem());
        Assertions.assertEquals(Result.DENY, event.getResult());
    }

    @Test
    void testBrewingWithVanillaItem() {
        VanillaItem item = TestUtilities.mockVanillaItem(plugin, Material.BLAZE_POWDER, true);
        item.register(plugin);

        InventoryClickEvent event = mockBrewingEvent(item.getItem());
        Assertions.assertEquals(Result.ALLOW, event.getResult());
    }
}
