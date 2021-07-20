package io.github.thebusybiscuit.slimefun4.implementation.registration;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IdConflictException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestSlimefunItemRegistration {

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
    @DisplayName("Test SlimefunItem registering properly")
    void testSuccessfulRegistration() {
        String id = "TEST_ITEM";
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, id, new CustomItemStack(Material.DIAMOND, "&cTest"));

        Assertions.assertEquals(ItemState.UNREGISTERED, item.getState());

        item.register(plugin);

        Assertions.assertEquals(ItemState.ENABLED, item.getState());
        Assertions.assertFalse(item.isDisabled());
        Assertions.assertEquals(id, item.getId());
        Assertions.assertEquals(item, SlimefunItem.getById(id));
    }

    @Test
    @DisplayName("Test disabled SlimefunItem being disabled")
    void testDisabledItem() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "DISABLED_ITEM", new CustomItemStack(Material.DIAMOND, "&cTest"));
        Slimefun.getItemCfg().setValue("DISABLED_ITEM.enabled", false);
        item.register(plugin);

        Assertions.assertEquals(ItemState.DISABLED, item.getState());
        Assertions.assertTrue(item.isDisabled());
    }

    @Test
    @DisplayName("Test VanillaItem falling back to vanilla.")
    void testVanillaItemFallback() {
        VanillaItem item = TestUtilities.mockVanillaItem(plugin, Material.ACACIA_SIGN, false);
        item.register(plugin);

        Assertions.assertTrue(item.isUseableInWorkbench());
        Assertions.assertEquals(ItemState.VANILLA_FALLBACK, item.getState());
        Assertions.assertTrue(item.isDisabled());
    }

    @Test
    @DisplayName("Test id conflicts being handled with an exception")
    void testIdConflict() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "DUPLICATE_ID", new CustomItemStack(Material.DIAMOND, "&cTest"));
        item.register(plugin);

        SlimefunItem item2 = TestUtilities.mockSlimefunItem(plugin, "DUPLICATE_ID", new CustomItemStack(Material.DIAMOND, "&cTest"));
        Assertions.assertThrows(IdConflictException.class, () -> item2.register(plugin));

        Assertions.assertEquals(ItemState.ENABLED, item.getState());
        Assertions.assertEquals(ItemState.UNREGISTERED, item2.getState());
    }

    @Test
    @DisplayName("Test Category registration when registering an item")
    void testCategoryRegistration() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "CATEGORY_TEST", new CustomItemStack(Material.DIAMOND, "&cTest"));
        item.register(plugin);
        item.load();

        // null should not be a valid argument
        Assertions.assertThrows(IllegalArgumentException.class, () -> item.setCategory(null));

        ItemGroup category = item.getItemGroup();
        ItemGroup category2 = new ItemGroup(new NamespacedKey(plugin, "test2"), new CustomItemStack(Material.OBSIDIAN, "&6Test 2"));

        Assertions.assertTrue(category.contains(item));
        Assertions.assertFalse(category2.contains(item));
        Assertions.assertEquals(category, item.getItemGroup());

        item.setCategory(category2);
        Assertions.assertFalse(category.contains(item));
        Assertions.assertTrue(category2.contains(item));
        Assertions.assertEquals(category2, item.getItemGroup());
    }

    @Test
    @DisplayName("Test hidden items being hidden")
    void testHiddenItem() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "HIDDEN_TEST", new CustomItemStack(Material.DIAMOND, "&cTest"));
        item.setHidden(true);
        item.register(plugin);
        item.load();

        ItemGroup category = item.getItemGroup();

        Assertions.assertTrue(item.isHidden());
        Assertions.assertFalse(category.contains(item));
        Assertions.assertEquals(category, item.getItemGroup());

        item.setHidden(false);
        Assertions.assertFalse(item.isHidden());
        Assertions.assertTrue(category.contains(item));
        Assertions.assertEquals(category, item.getItemGroup());

        item.setHidden(true);
        Assertions.assertTrue(item.isHidden());
        Assertions.assertFalse(category.contains(item));
        Assertions.assertEquals(category, item.getItemGroup());

        // Do nothing if the value hasn't changed
        item.setHidden(true);
        Assertions.assertTrue(item.isHidden());
        Assertions.assertFalse(category.contains(item));
    }
}
