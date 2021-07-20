package io.github.thebusybiscuit.slimefun4.implementation.items;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;
import io.github.thebusybiscuit.slimefun4.test.mocks.MockDamageable;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestDamageableItem {

    private static Slimefun plugin;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    public static MockDamageable getDummyItem(String id, boolean damageable, @Nullable Enchantment enchantment, @Nullable Integer enchantmentLevel) {
        ItemGroup category = TestUtilities.getCategory(plugin, "damageable_item_test");
        SlimefunItemStack stack = new SlimefunItemStack("DAMAGEABLE_PICKAXE_" + id, Material.DIAMOND_PICKAXE, "&4This pickaxe can break", "&6It appears, it breaks, but most importantly, it tests.");
        if (enchantment != null && enchantmentLevel != null) {
            ItemMeta im = stack.getItemMeta();
            im.addEnchant(enchantment, enchantmentLevel, true);
            stack.setItemMeta(im);
        }
        MockDamageable item = new MockDamageable(category, stack, RecipeType.NULL, new ItemStack[9], damageable);
        item.register(plugin);
        return item;
    }

    @Test
    @DisplayName("Test DamageableItem actually damages")
    void testDamageableItemDamagesItem() {
        MockDamageable unDamageableItem = getDummyItem("UDM", false, null, null);
        // Check if damageable is successfully registered
        Assertions.assertFalse(unDamageableItem.isDamageable());

        Player player = server.addPlayer();
        ItemStack unDamageableDummy = unDamageableItem.getItem().clone();
        unDamageableItem.damageItem(player, unDamageableDummy);
        Assertions.assertTrue(unDamageableDummy.hasItemMeta());
        // Check if the item is not damaged as we intended it to not be
        Assertions.assertEquals(((Damageable) unDamageableDummy.getItemMeta()).getDamage(), 0);

        MockDamageable damageableItem = getDummyItem("DM", true, null, null);
        // Check if damageable is successfully registered
        Assertions.assertTrue(damageableItem.isDamageable());

        ItemStack damageableDummy = damageableItem.getItem().clone();
        damageableItem.damageItem(player, damageableDummy);
        Assertions.assertTrue(damageableDummy.hasItemMeta());
        // Check if the item is damaged as we intended it to not be
        Assertions.assertEquals(((Damageable) damageableDummy.getItemMeta()).getDamage(), 1);
    }

    @Test
    @DisplayName("Test if DamageableItem cares about unbreaking levels")
    void testDamageableItemCaresUnbreaking() {
        MockDamageable noUnbreakingItem = getDummyItem("NU", true, null, null);
        MockDamageable iiiUnbreakingItem = getDummyItem("IIIU", true, Enchantment.DURABILITY, 3);
        MockDamageable xUnbreakingItem = getDummyItem("XU", true, Enchantment.DURABILITY, 10);
        ItemStack noUnbreakingItemIS = noUnbreakingItem.getItem().clone();
        ItemStack iiiUnbreakingItemIS = iiiUnbreakingItem.getItem().clone();
        ItemStack xUnbreakingItemIS = xUnbreakingItem.getItem().clone();
        Player player = server.addPlayer();
        for (int i = 0; i < 500; ++i) {
            noUnbreakingItem.damageItem(player, noUnbreakingItemIS);
            noUnbreakingItem.damageItem(player, iiiUnbreakingItemIS);
            noUnbreakingItem.damageItem(player, xUnbreakingItemIS);
        }
        Assertions.assertTrue(noUnbreakingItemIS.hasItemMeta());
        Assertions.assertTrue(iiiUnbreakingItemIS.hasItemMeta());
        Assertions.assertTrue(xUnbreakingItemIS.hasItemMeta());
        Assertions.assertTrue(((Damageable) xUnbreakingItemIS.getItemMeta()).getDamage() < ((Damageable) iiiUnbreakingItemIS.getItemMeta()).getDamage());
        Assertions.assertTrue(((Damageable) iiiUnbreakingItemIS.getItemMeta()).getDamage() < ((Damageable) noUnbreakingItemIS.getItemMeta()).getDamage());
    }

}
