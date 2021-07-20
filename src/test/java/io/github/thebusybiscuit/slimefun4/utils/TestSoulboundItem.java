package io.github.thebusybiscuit.slimefun4.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Soulbound;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestSoulboundItem {

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
    @DisplayName("Verify that null and air throw Illegal Argument Exceptions")
    void testNullAndAir() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> SlimefunUtils.setSoulbound(null, true));

        ItemStack item = new ItemStack(Material.AIR);
        Assertions.assertThrows(IllegalArgumentException.class, () -> SlimefunUtils.setSoulbound(item, true));

        Assertions.assertFalse(SlimefunUtils.isSoulbound(null));
        Assertions.assertFalse(SlimefunUtils.isSoulbound(item));
    }

    @Test
    @DisplayName("Test whether an Item can be marked as soulbound")
    void testSetSoulbound() {
        ItemStack item = new CustomItemStack(Material.DIAMOND, "&cI wanna be soulbound!");

        Assertions.assertFalse(SlimefunUtils.isSoulbound(item));

        SlimefunUtils.setSoulbound(item, true);
        Assertions.assertTrue(SlimefunUtils.isSoulbound(item));
        Assertions.assertEquals(1, item.getItemMeta().getLore().size());

        SlimefunUtils.setSoulbound(item, false);
        Assertions.assertFalse(SlimefunUtils.isSoulbound(item));
        Assertions.assertEquals(0, item.getItemMeta().getLore().size());
    }

    @Test
    @DisplayName("Make sure that marking an item as soulbound twice has no effect")
    void testDoubleCalls() {
        ItemStack item = new CustomItemStack(Material.DIAMOND, "&cI wanna be soulbound!");

        SlimefunUtils.setSoulbound(item, true);
        SlimefunUtils.setSoulbound(item, true);
        Assertions.assertTrue(SlimefunUtils.isSoulbound(item));
        Assertions.assertEquals(1, item.getItemMeta().getLore().size());

        SlimefunUtils.setSoulbound(item, false);
        SlimefunUtils.setSoulbound(item, false);
        Assertions.assertFalse(SlimefunUtils.isSoulbound(item));
        Assertions.assertEquals(0, item.getItemMeta().getLore().size());
    }

    @Test
    @DisplayName("Test that soulbound Slimefun Items are soulbound")
    void testSoulboundSlimefunItem() {
        SlimefunItem item = new SoulboundMock(new ItemGroup(new NamespacedKey(plugin, "soulbound_category"), new CustomItemStack(Material.REDSTONE, "&4Walshrus forever")));
        item.register(plugin);

        Assertions.assertTrue(SlimefunUtils.isSoulbound(item.getItem()));
    }

    private class SoulboundMock extends SlimefunItem implements Soulbound {

        public SoulboundMock(ItemGroup category) {
            super(category, new SlimefunItemStack("MOCK_SOULBOUND", Material.REDSTONE, "&4Almighty Redstone"), RecipeType.NULL, new ItemStack[9]);
        }

    }

}
