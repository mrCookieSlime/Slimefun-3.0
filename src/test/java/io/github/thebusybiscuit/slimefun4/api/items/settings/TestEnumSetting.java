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

class TestEnumSetting {

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
    @DisplayName("Test Enum Getters")
    void testEnumGetters() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "MATERIAL_SETTING_TEST_0", new CustomItemStack(Material.DIAMOND, "&cTest"));
        EnumSetting<Material> setting = new EnumSetting<>(item, "test", Material.class, Material.DIAMOND);
        Assertions.assertArrayEquals(Material.values(), setting.getAllowedValues());
    }

    @Test
    @DisplayName("Test illegal values")
    void testIllegalValues() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "ENUM_SETTING_TEST", new CustomItemStack(Material.DIAMOND, "&cTest"));
        EnumSetting<Material> setting = new EnumSetting<>(item, "test", Material.class, Material.DIAMOND);

        item.addItemSetting(setting);
        item.register(plugin);

        Assertions.assertThrows(IllegalArgumentException.class, () -> setting.update("I_DO_NOT_EXIST"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> setting.update(null));
    }

    @Test
    @DisplayName("Test allowed value")
    void testAllowedValue() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "MATERIAL_SETTING_TEST_2", new CustomItemStack(Material.DIAMOND, "&cTest"));
        EnumSetting<Material> setting = new EnumSetting<>(item, "test", Material.class, Material.DIAMOND);

        item.addItemSetting(setting);
        item.register(plugin);

        Assertions.assertEquals(Material.DIAMOND.name(), setting.getValue());
        Assertions.assertEquals(Material.DIAMOND, setting.getAsEnumConstant());

        setting.update(Material.EMERALD.name());
        Assertions.assertEquals(Material.EMERALD.name(), setting.getValue());
        Assertions.assertEquals(Material.EMERALD, setting.getAsEnumConstant());
    }
}
