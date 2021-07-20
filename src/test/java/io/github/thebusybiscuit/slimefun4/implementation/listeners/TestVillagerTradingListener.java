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
import org.mockito.Mockito;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.SyntheticEmerald;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestVillagerTradingListener {

    private static Slimefun plugin;
    private static VillagerTradingListener listener;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new VillagerTradingListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    private InventoryClickEvent mockClickEvent(ItemStack item) {
        Player player = server.addPlayer();
        Inventory inv = TestUtilities.mockInventory(InventoryType.MERCHANT);
        Mockito.when(inv.getSize()).thenReturn(8);

        InventoryView view = player.openInventory(inv);
        view.setCursor(item);
        InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, 1, ClickType.LEFT, InventoryAction.PICKUP_ONE);
        listener.onPreTrade(event);
        return event;
    }

    @Test
    void testTradingWithoutSlimefunItems() {
        InventoryClickEvent event = mockClickEvent(new ItemStack(Material.EMERALD));
        Assertions.assertEquals(Result.ALLOW, event.getResult());
    }

    @Test
    void testTradingWithSlimefunItem() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "MOCKED_FAKE_EMERALD", new CustomItemStack(Material.EMERALD, "&aFake Emerald"));
        item.register(plugin);

        InventoryClickEvent event = mockClickEvent(item.getItem());
        Assertions.assertEquals(Result.DENY, event.getResult());
    }

    @Test
    void testTradingWithVanillaItem() {
        VanillaItem item = TestUtilities.mockVanillaItem(plugin, Material.EMERALD, true);
        item.register(plugin);

        InventoryClickEvent event = mockClickEvent(item.getItem());
        Assertions.assertEquals(Result.ALLOW, event.getResult());
    }

    @Test
    void testTradingWithSyntheticEmerald() {
        ItemGroup category = TestUtilities.getCategory(plugin, "shiny_emeralds");
        SlimefunItemStack stack = new SlimefunItemStack("FAKE_EMERALD", Material.EMERALD, "&aTrade me");
        SyntheticEmerald item = new SyntheticEmerald(category, stack, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[9]);
        item.register(plugin);

        InventoryClickEvent event = mockClickEvent(item.getItem());
        Assertions.assertEquals(Result.ALLOW, event.getResult());
    }
}
