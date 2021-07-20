package io.github.thebusybiscuit.slimefun4.api.items.settings;

import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestIntRangeSetting {

    private static Slimefun plugin;
    private final int min = 0;
    private final int max = 100;

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
    @DisplayName("Test Constructor validation")
    void testConstructorValidation() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "INT_RANGE_TEST_00", new CustomItemStack(Material.DIAMOND, "&cTest"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new IntRangeSetting(item, "test", min, -50, max));
    }

    @Test
    @DisplayName("Test min and max getters")
    void testMinMaxGetters() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "INT_RANGE_TEST_0", new CustomItemStack(Material.DIAMOND, "&cTest"));
        IntRangeSetting setting = new IntRangeSetting(item, "test", min, 1, max);

        Assertions.assertEquals(min, setting.getMinimum());
        Assertions.assertEquals(max, setting.getMaximum());
    }

    @Test
    @DisplayName("Test illegal values")
    void testIllegalValues() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "INT_RANGE_TEST", new CustomItemStack(Material.DIAMOND, "&cTest"));
        IntRangeSetting setting = new IntRangeSetting(item, "test", min, 1, max);

        item.addItemSetting(setting);
        item.register(plugin);

        Assertions.assertThrows(IllegalArgumentException.class, () -> setting.update(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> setting.update(-1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> setting.update(101));
    }

    @Test
    @DisplayName("Test allowed value")
    void testAllowedValue() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "INT_RANGE_TEST_2", new CustomItemStack(Material.DIAMOND, "&cTest"));
        IntRangeSetting setting = new IntRangeSetting(item, "test", min, 1, max);

        item.addItemSetting(setting);
        item.register(plugin);

        Assertions.assertEquals(1, setting.getValue());
        setting.update(42);
        Assertions.assertEquals(42, setting.getValue());
    }
}
