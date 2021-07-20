package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.inventory.ChestInventoryMock;
import be.seeseemelk.mockbukkit.inventory.InventoryMock;

class TestAutoCrafter {

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
    @DisplayName("Test crafting a valid ShapelessRecipe")
    void testValidShapelessRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "shapeless_recipe_test");
        ItemStack result = new CustomItemStack(Material.DIAMOND, "&6Special Diamond :o");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        recipe.addIngredient(new MaterialChoice(Material.IRON_NUGGET, Material.GOLD_NUGGET));

        AbstractRecipe abstractRecipe = AbstractRecipe.of(recipe);
        AbstractAutoCrafter crafter = getVanillaAutoCrafter();
        InventoryMock inv = new ChestInventoryMock(null, 9);

        // Test first choice
        inv.addItem(new ItemStack(Material.IRON_NUGGET));
        Assertions.assertTrue(crafter.craft(inv, abstractRecipe));
        Assertions.assertFalse(inv.contains(Material.IRON_NUGGET, 1));
        Assertions.assertTrue(inv.containsAtLeast(result, 1));

        inv.clear();

        // Test other choice
        inv.addItem(new ItemStack(Material.GOLD_NUGGET));
        Assertions.assertTrue(crafter.craft(inv, abstractRecipe));
        Assertions.assertFalse(inv.contains(Material.GOLD_NUGGET, 1));
        Assertions.assertTrue(inv.containsAtLeast(result, 1));
    }

    @Test
    @DisplayName("Test crafting a valid ShapelessRecipe")
    void testDisabledRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "disabled_recipe_test");
        ItemStack result = new CustomItemStack(Material.DIAMOND, "&bAmazing Diamond :o");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        recipe.addIngredient(new MaterialChoice(Material.GOLD_NUGGET));

        AbstractRecipe abstractRecipe = AbstractRecipe.of(recipe);
        AbstractAutoCrafter crafter = getVanillaAutoCrafter();
        InventoryMock inv = new ChestInventoryMock(null, 9);

        // Test enabled Recipe
        abstractRecipe.setEnabled(true);
        inv.addItem(new ItemStack(Material.GOLD_NUGGET));
        Assertions.assertTrue(crafter.craft(inv, abstractRecipe));
        Assertions.assertFalse(inv.contains(Material.GOLD_NUGGET, 1));
        Assertions.assertTrue(inv.containsAtLeast(result, 1));

        inv.clear();

        // Test disabled Recipe
        abstractRecipe.setEnabled(false);
        inv.addItem(new ItemStack(Material.GOLD_NUGGET));
        Assertions.assertFalse(crafter.craft(inv, abstractRecipe));
        Assertions.assertTrue(inv.contains(Material.GOLD_NUGGET, 1));
        Assertions.assertFalse(inv.containsAtLeast(result, 1));
    }

    @Test
    @DisplayName("Test resource leftovers when crafting")
    void testResourceLeftovers() {
        NamespacedKey key = new NamespacedKey(plugin, "resource_leftovers_test");
        ItemStack result = new CustomItemStack(Material.DIAMOND, "&9Diamond. Nuff said.");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        recipe.addIngredient(new MaterialChoice(Material.HONEY_BOTTLE));
        recipe.addIngredient(new MaterialChoice(Material.HONEY_BOTTLE));

        AbstractRecipe abstractRecipe = AbstractRecipe.of(recipe);
        AbstractAutoCrafter crafter = getVanillaAutoCrafter();
        InventoryMock inv = new ChestInventoryMock(null, 9);

        inv.addItem(new ItemStack(Material.HONEY_BOTTLE, 2));
        Assertions.assertTrue(crafter.craft(inv, abstractRecipe));

        Assertions.assertFalse(inv.contains(Material.HONEY_BOTTLE, 2));
        Assertions.assertTrue(inv.containsAtLeast(result, 1));

        // Check for leftovers
        Assertions.assertTrue(inv.contains(Material.GLASS_BOTTLE, 2));
    }

    @Test
    @DisplayName("Test crafting an invalid ShapelessRecipe")
    void testInvalidShapelessRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "shapeless_recipe_test");
        ItemStack result = new CustomItemStack(Material.DIAMOND, "&6Special Diamond :o");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        recipe.addIngredient(Material.IRON_NUGGET);

        AbstractRecipe abstractRecipe = AbstractRecipe.of(recipe);
        AbstractAutoCrafter crafter = getVanillaAutoCrafter();
        InventoryMock inv = new ChestInventoryMock(null, 9);

        // Test non-compatible Item
        inv.addItem(new ItemStack(Material.BAMBOO));
        Assertions.assertFalse(crafter.craft(inv, abstractRecipe));
        Assertions.assertTrue(inv.contains(Material.BAMBOO, 1));
        Assertions.assertFalse(inv.containsAtLeast(result, 1));
    }

    @Test
    @DisplayName("Test crafting a ShapelessRecipe with a SlimefunItem")
    void ShapelessRecipeWithSlimefunItem() {
        NamespacedKey key = new NamespacedKey(plugin, "shapeless_recipe_test");
        ItemStack result = new CustomItemStack(Material.DIAMOND, "&6Special Diamond :o");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        recipe.addIngredient(Material.BAMBOO);

        AbstractRecipe abstractRecipe = AbstractRecipe.of(recipe);
        AbstractAutoCrafter crafter = getVanillaAutoCrafter();
        InventoryMock inv = new ChestInventoryMock(null, 9);

        SlimefunItemStack itemStack = new SlimefunItemStack("AUTO_CRAFTER_TEST_ITEM", Material.BAMBOO, "Panda Candy");
        SlimefunItem slimefunItem = TestUtilities.mockSlimefunItem(plugin, itemStack.getItemId(), itemStack);
        slimefunItem.register(plugin);

        inv.addItem(itemStack.clone());

        // Test unusable SlimefunItem
        slimefunItem.setUseableInWorkbench(false);
        Assertions.assertFalse(crafter.craft(inv, abstractRecipe));
        Assertions.assertTrue(inv.containsAtLeast(itemStack, 1));
        Assertions.assertFalse(inv.containsAtLeast(result, 1));

        // Test allowed SlimefunItem
        slimefunItem.setUseableInWorkbench(true);
        Assertions.assertTrue(crafter.craft(inv, abstractRecipe));
        Assertions.assertFalse(inv.containsAtLeast(itemStack, 1));
        Assertions.assertTrue(inv.containsAtLeast(result, 1));
    }

    @Test
    @DisplayName("Test crafting with a full Inventory")
    void testFullInventory() {
        NamespacedKey key = new NamespacedKey(plugin, "shapeless_recipe_test");
        ItemStack result = new CustomItemStack(Material.DIAMOND, "&6Special Diamond :o");
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        recipe.addIngredient(Material.IRON_NUGGET);

        AbstractRecipe abstractRecipe = AbstractRecipe.of(recipe);
        AbstractAutoCrafter crafter = getVanillaAutoCrafter();

        InventoryMock inv = new ChestInventoryMock(null, 9);

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, new ItemStack(Material.OAK_SAPLING));
        }

        // Test valid item but inventory is full.
        inv.addItem(new ItemStack(Material.IRON_NUGGET));
        Assertions.assertFalse(crafter.craft(inv, abstractRecipe));
        Assertions.assertTrue(inv.contains(Material.OAK_SAPLING, 9));
        Assertions.assertFalse(inv.containsAtLeast(result, 1));
    }

    @Test
    @DisplayName("Verify Auto Crafters are marked as energy consumers")
    void testEnergyConsumer() {
        AbstractAutoCrafter crafter = getVanillaAutoCrafter();
        Assertions.assertEquals(EnergyNetComponentType.CONSUMER, crafter.getEnergyComponentType());
    }

    @Nonnull
    private AbstractAutoCrafter getVanillaAutoCrafter() {
        SlimefunItemStack item = new SlimefunItemStack("MOCK_AUTO_CRAFTER", Material.CRAFTING_TABLE, "Mock Auto Crafter");
        return new VanillaAutoCrafter(TestUtilities.getCategory(plugin, "auto_crafter"), item, RecipeType.NULL, new ItemStack[9]);
    }

}
