package io.github.thebusybiscuit.slimefun4.core.services;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestItemDataService {

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
    @DisplayName("Test CustomItemDataService constructor")
    void testInitialization() {
        CustomItemDataService service = new CustomItemDataService(plugin, "test");
        Assertions.assertEquals(new NamespacedKey(plugin, "test"), service.getKey());
    }

    @Test
    @DisplayName("Test setting item data for an ItemStack")
    void testSetDataItem() {
        CustomItemDataService service = new CustomItemDataService(plugin, "test");
        ItemStack item = new ItemStack(Material.EMERALD);

        service.setItemData(item, "Hello World");
        Optional<String> data = service.getItemData(item);

        Assertions.assertTrue(data.isPresent());
        Assertions.assertEquals("Hello World", data.get());
    }

    @Test
    @DisplayName("Test setting item data for an ItemMeta")
    void testSetDataItemMeta() {
        CustomItemDataService service = new CustomItemDataService(plugin, "test");
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        service.setItemData(meta, "Hello World");

        Optional<String> data = service.getItemData(meta);
        Assertions.assertTrue(data.isPresent());
        Assertions.assertEquals("Hello World", data.get());

        item.setItemMeta(meta);

        Optional<String> data2 = service.getItemData(item);
        Assertions.assertTrue(data2.isPresent());
        Assertions.assertEquals("Hello World", data2.get());
    }
}
