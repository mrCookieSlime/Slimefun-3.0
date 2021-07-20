package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.apache.commons.lang.mutable.MutableObject;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting.CraftingTableListener;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestCraftingTableListener {

    private static Slimefun plugin;
    private static CraftingTableListener listener;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new CraftingTableListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    private CraftItemEvent mockCraftingEvent(ItemStack item) {
        Recipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "test_recipe"), new ItemStack(Material.EMERALD));
        Player player = server.addPlayer();

        CraftingInventory inv = Mockito.mock(CraftingInventory.class);
        Mockito.when(inv.getContents()).thenReturn(new ItemStack[] { item, null, null, null, null, null, null, null, null });

        InventoryView view = player.openInventory(inv);
        CraftItemEvent event = new CraftItemEvent(recipe, view, SlotType.RESULT, 9, ClickType.LEFT, InventoryAction.PICKUP_ALL);

        listener.onCraft(event);
        return event;
    }

    private PrepareItemCraftEvent mockPreCraftingEvent(ItemStack item) {
        Player player = server.addPlayer();

        CraftingInventory inv = Mockito.mock(CraftingInventory.class);
        MutableObject result = new MutableObject(new ItemStack(Material.EMERALD));

        Mockito.doAnswer(invocation -> {
            ItemStack argument = invocation.getArgument(0);
            result.setValue(argument);
            return null;
        }).when(inv).setResult(Mockito.any());

        Mockito.when(inv.getResult()).thenAnswer(invocation -> result.getValue());
        Mockito.when(inv.getContents()).thenReturn(new ItemStack[] { null, null, item, null, null, null, null, null, null });

        InventoryView view = player.openInventory(inv);
        PrepareItemCraftEvent event = new PrepareItemCraftEvent(inv, view, false);

        listener.onPrepareCraft(event);
        return event;
    }

    @Test
    void testCraftEventWithoutSlimefunItems() {
        CraftItemEvent event = mockCraftingEvent(new ItemStack(Material.DIAMOND));
        Assertions.assertEquals(Result.DEFAULT, event.getResult());
    }

    @Test
    void testCraftEventWithSlimefunItem() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "MOCK_DIAMOND", new CustomItemStack(Material.DIAMOND, "&cMock Diamond"));
        item.register(plugin);

        CraftItemEvent event = mockCraftingEvent(item.getItem());
        Assertions.assertEquals(Result.DENY, event.getResult());
    }

    @Test
    void testCraftEventWithChangingSlimefunItem() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "CHANGING_ITEM", new CustomItemStack(Material.DIAMOND, "&dChanging Diamond"));
        item.register(plugin);

        item.setUseableInWorkbench(true);
        CraftItemEvent event = mockCraftingEvent(item.getItem());
        Assertions.assertEquals(Result.DEFAULT, event.getResult());

        item.setUseableInWorkbench(false);
        CraftItemEvent event2 = mockCraftingEvent(item.getItem());
        Assertions.assertEquals(Result.DENY, event2.getResult());
    }

    @Test
    void testCraftEventWithVanillaItem() {
        VanillaItem item = TestUtilities.mockVanillaItem(plugin, Material.DIAMOND, true);
        item.register(plugin);

        CraftItemEvent event = mockCraftingEvent(item.getItem());
        Assertions.assertEquals(Result.DEFAULT, event.getResult());
    }

    @Test
    void testPreCraftEventWithoutSlimefunItems() {
        PrepareItemCraftEvent event = mockPreCraftingEvent(new ItemStack(Material.DIAMOND));
        Assertions.assertNotNull(event.getInventory().getResult());
    }

    @Test
    void testPreCraftEventWithSlimefunItem() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "MOCK_DIAMOND2", new CustomItemStack(Material.DIAMOND, "&cMock Diamond"));
        item.register(plugin);

        PrepareItemCraftEvent event = mockPreCraftingEvent(item.getItem());
        Assertions.assertNull(event.getInventory().getResult());
    }

    @Test
    void testPreCraftEventWithVanillaItem() {
        VanillaItem item = TestUtilities.mockVanillaItem(plugin, Material.GOLD_INGOT, true);
        item.register(plugin);

        PrepareItemCraftEvent event = mockPreCraftingEvent(item.getItem());
        Assertions.assertNotNull(event.getInventory().getResult());
    }

}
