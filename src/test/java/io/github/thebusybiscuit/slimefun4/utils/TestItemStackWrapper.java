package io.github.thebusybiscuit.slimefun4.utils;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestItemStackWrapper {

    @BeforeAll
    public static void load() {
        MockBukkit.mock();
        MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test if an ItemStackWrappers can be compared properly (With ItemMeta)")
    void testEqualityWithItemMeta() {
        ItemStack item = new CustomItemStack(Material.LAVA_BUCKET, "&4SuperHot.exe", "", "&6Hello");
        ItemStackWrapper wrapper = ItemStackWrapper.wrap(item);

        Assertions.assertEquals(item.getType(), wrapper.getType());
        Assertions.assertEquals(item.hasItemMeta(), wrapper.hasItemMeta());
        Assertions.assertEquals(item.getItemMeta(), wrapper.getItemMeta());
        Assertions.assertTrue(SlimefunUtils.isItemSimilar(wrapper, item, true));
    }

    @Test
    @DisplayName("Test if an ItemStackWrappers can be compared properly (No ItemMeta)")
    void testEqualityWithoutItemMeta() {
        ItemStack item = new ItemStack(Material.DIAMOND_AXE);
        ItemStackWrapper wrapper = ItemStackWrapper.wrap(item);

        Assertions.assertEquals(item.getType(), wrapper.getType());
        Assertions.assertEquals(item.hasItemMeta(), wrapper.hasItemMeta());
        // Assertions.assertEquals(item.getItemMeta(), wrapper.getItemMeta());
        Assertions.assertTrue(SlimefunUtils.isItemSimilar(wrapper, item, true));
    }

    @Test
    @DisplayName("Test if an ItemStackWrapper is immutable")
    void testImmutability() {
        ItemStack item = new CustomItemStack(Material.LAVA_BUCKET, "&4SuperHot.exe", "", "&6Hello");
        ItemStackWrapper wrapper = ItemStackWrapper.wrap(item);

        Assertions.assertThrows(UnsupportedOperationException.class, () -> wrapper.setType(Material.BEDROCK));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> wrapper.setAmount(3));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> wrapper.setItemMeta(item.getItemMeta()));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> wrapper.addUnsafeEnchantment(null, 1));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> wrapper.hashCode());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> wrapper.clone());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> wrapper.equals(wrapper));
    }

    @Test
    @DisplayName("Test if the ItemStackWrapper static method constructors are checking for nested wrapping properly")
    void testWrapperChecking() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ItemStackWrapper.wrap(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> ItemStackWrapper.forceWrap(null));
        ItemStack item = new CustomItemStack(Material.IRON_INGOT, "A Name", "line 1", "line2");
        ItemStackWrapper wrapper = ItemStackWrapper.wrap(item);
        ItemStackWrapper secondWrap = ItemStackWrapper.wrap(wrapper);
        // We want to check that the wrapper returned is of reference equality
        Assertions.assertSame(wrapper, secondWrap);
        ItemStackWrapper forceSecondWrap = ItemStackWrapper.forceWrap(wrapper);
        // Want to check that the wrapper returned is of different reference equality
        Assertions.assertNotSame(wrapper, forceSecondWrap);
        assertWrapped(wrapper, forceSecondWrap);
    }

    @Test
    @DisplayName("Test wrapping an ItemStack Array")
    void testWrapArray() {
        ItemStack[] items = { new ItemStack(Material.DIAMOND), null, new ItemStack(Material.EMERALD), new CustomItemStack(Material.REDSTONE, "&4Firey thing", "with lore :o") };
        ItemStackWrapper[] wrappers = ItemStackWrapper.wrapArray(items);

        Assertions.assertEquals(items.length, wrappers.length);

        for (int i = 0; i < items.length; i++) {
            assertWrapped(items[i], wrappers[i]);
        }

        ItemStackWrapper[] nestedWrap = ItemStackWrapper.wrapArray(wrappers);

        Assertions.assertEquals(wrappers.length, nestedWrap.length);
        for (int i = 0; i < wrappers.length; i++) {
            // We want to check that the wrapper returned is of reference equality
            Assertions.assertSame(wrappers[i], nestedWrap[i]);
        }
    }

    @Test
    @DisplayName("Test wrapping an ItemStack List")
    void testWrapList() {
        List<ItemStack> items = Arrays.asList(new ItemStack(Material.DIAMOND), null, new ItemStack(Material.EMERALD), new CustomItemStack(Material.REDSTONE, "&4Firey thing", "with lore :o"));
        List<? extends ItemStack> wrappers = ItemStackWrapper.wrapList(items);

        Assertions.assertEquals(items.size(), wrappers.size());

        for (int i = 0; i < items.size(); i++) {
            assertWrapped(items.get(i), wrappers.get(i));
        }

        @SuppressWarnings("unchecked")
        List<ItemStackWrapper> nestedWrappers = ItemStackWrapper.wrapList((List<ItemStack>) wrappers);

        Assertions.assertEquals(wrappers.size(), nestedWrappers.size());
        for (int i = 0; i < items.size(); i++) {
            // We want to check that the wrapper returned is of reference equality
            Assertions.assertSame(wrappers.get(i), nestedWrappers.get(i));
        }
    }

    private void assertWrapped(@Nullable ItemStack expected, @Nullable ItemStack actual) {
        if (expected == null) {
            Assertions.assertNull(actual);
        } else {
            Assertions.assertTrue(actual instanceof ItemStackWrapper);
            Assertions.assertTrue(SlimefunUtils.isItemSimilar(actual, expected, true));
        }
    }
}
