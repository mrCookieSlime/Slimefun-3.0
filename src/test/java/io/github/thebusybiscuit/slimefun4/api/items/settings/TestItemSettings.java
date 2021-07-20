package io.github.thebusybiscuit.slimefun4.api.items.settings;

import java.util.Optional;

import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestItemSettings {

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
    @DisplayName("Test illegal Item Settings arguments")
    void testIllegalItemSettings() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "ITEM_SETTINGS_TEST", new CustomItemStack(Material.DIAMOND, "&cTest"));
        item.register(plugin);

        Assertions.assertThrows(IllegalArgumentException.class, () -> item.addItemSetting());
        Assertions.assertThrows(IllegalArgumentException.class, () -> item.addItemSetting((ItemSetting<String>) null));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> item.addItemSetting(new ItemSetting<>(item, "test", "Hello World")));
    }

    @Test
    @DisplayName("Test adding an Item Setting")
    void testAddItemSetting() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "ITEM_SETTINGS_TEST_2", new CustomItemStack(Material.DIAMOND, "&cTest"));
        ItemSetting<String> setting = new ItemSetting<>(item, "test", "Hello World");

        Assertions.assertTrue(setting.isType(String.class));
        Assertions.assertFalse(setting.isType(Integer.class));

        item.addItemSetting(setting);
        item.register(plugin);

        Assertions.assertTrue(item.getItemSettings().contains(setting));

        Optional<ItemSetting<String>> optional = item.getItemSetting(setting.getKey(), String.class);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(setting, optional.get());
        Assertions.assertEquals("Hello World", setting.getValue());

        Assertions.assertFalse(item.getItemSetting(setting.getKey(), Boolean.class).isPresent());
        Assertions.assertFalse(item.getItemSetting("I do not exist", String.class).isPresent());
    }

    @Test
    @DisplayName("Test updating an Item Settings value")
    void testUpdateItemSetting() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "ITEM_SETTINGS_TEST_3", new CustomItemStack(Material.DIAMOND, "&cTest"));
        ItemSetting<String> setting = new ItemSetting<>(item, "test", "Hello World");

        item.addItemSetting(setting);
        item.register(plugin);

        Assertions.assertEquals("Hello World", setting.getValue());
        setting.update("I am different");
        Assertions.assertEquals("I am different", setting.getValue());

        Assertions.assertThrows(IllegalArgumentException.class, () -> setting.update(null));
        Assertions.assertEquals("I am different", setting.getValue());
    }

    @Test
    @DisplayName("Test Item Settings double-registration")
    void testAlreadyExistingItemSetting() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "ITEM_SETTINGS_TEST", new CustomItemStack(Material.DIAMOND, "&cTest"));
        ItemSetting<String> setting = new ItemSetting<>(item, "test", "Hello World");

        item.addItemSetting(setting);
        Assertions.assertThrows(IllegalArgumentException.class, () -> item.addItemSetting(setting));
    }
}
