package io.github.thebusybiscuit.slimefun4.implementation.items;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.exceptions.UnregisteredItemException;
import io.github.thebusybiscuit.slimefun4.api.exceptions.WrongItemStackException;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestSlimefunItem {

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
    @DisplayName("Test wiki pages getting assigned correctly")
    void testWikiPages() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "WIKI_ITEM", new CustomItemStack(Material.BOOK, "&cTest"));
        item.register(plugin);

        Assertions.assertFalse(item.getWikipage().isPresent());

        // null should not be a valid argument
        Assertions.assertThrows(IllegalArgumentException.class, () -> item.addOfficialWikipage(null));

        item.addOfficialWikipage("Test");

        Optional<String> wiki = item.getWikipage();
        Assertions.assertTrue(wiki.isPresent());
        Assertions.assertEquals("https://github.com/Slimefun/Slimefun4/wiki/Test", wiki.get());
    }

    @Test
    @DisplayName("Test SlimefunItem registering Recipes properly")
    void testRecipe() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "RECIPE_TEST", new CustomItemStack(Material.DIAMOND, "&dAnother one bites the test"));

        ItemStack[] recipe = { null, new ItemStack(Material.DIAMOND), null, null, new ItemStack(Material.DIAMOND), null, null, new ItemStack(Material.DIAMOND), null };
        item.setRecipe(recipe);
        item.register(plugin);

        Assertions.assertArrayEquals(recipe, item.getRecipe());

        Assertions.assertThrows(IllegalArgumentException.class, () -> item.setRecipe(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> item.setRecipe(new ItemStack[3]));
        Assertions.assertThrows(IllegalArgumentException.class, () -> item.setRecipe(new ItemStack[20]));
    }

    @Test
    @DisplayName("Test Recipe outputs being handled correctly")
    void testRecipeOutput() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "RECIPE_OUTPUT_TEST", new CustomItemStack(Material.DIAMOND, "&cTest"));
        item.register(plugin);

        Assertions.assertEquals(item.getItem(), item.getRecipeOutput());

        ItemStack output = new ItemStack(Material.EMERALD, 64);
        item.setRecipeOutput(output);
        Assertions.assertEquals(output, item.getRecipeOutput());

        item.setRecipeOutput(item.getItem());
        Assertions.assertEquals(item.getItem(), item.getRecipeOutput());
    }

    @Test
    @DisplayName("Test Recipe Types being handled properly")
    void testRecipeType() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "RECIPE_TYPE_TEST", new CustomItemStack(Material.DIAMOND, "&cTest"));
        item.register(plugin);

        Assertions.assertNotNull(item.getRecipeType());

        item.setRecipeType(RecipeType.ENHANCED_CRAFTING_TABLE);
        Assertions.assertEquals(RecipeType.ENHANCED_CRAFTING_TABLE, item.getRecipeType());

        item.setRecipeType(RecipeType.NULL);
        Assertions.assertEquals(RecipeType.NULL, item.getRecipeType());

        Assertions.assertThrows(IllegalArgumentException.class, () -> item.setRecipeType(null));
    }

    @ParameterizedTest
    @DisplayName("Test SlimefunItem#isItem(...)")
    @ValueSource(booleans = { true, false })
    void testIsItem(boolean compatibility) {
        ItemStack item = new CustomItemStack(Material.BEACON, "&cItem Test");
        String id = "IS_ITEM_TEST" + (compatibility ? "_COMPATIBLE" : "");
        SlimefunItem sfItem = TestUtilities.mockSlimefunItem(plugin, id, item);
        sfItem.register(plugin);

        Assertions.assertTrue(sfItem.isItem(sfItem.getItem()));

        Assertions.assertFalse(sfItem.isItem(null));
        Assertions.assertFalse(sfItem.isItem(new ItemStack(Material.BEACON)));
        Assertions.assertFalse(sfItem.isItem(new CustomItemStack(Material.REDSTONE, "&cTest")));

        if (compatibility) {
            Slimefun.getRegistry().setBackwardsCompatible(true);

            Assertions.assertEquals(sfItem, SlimefunItem.getByItem(item));
            Assertions.assertTrue(sfItem.isItem(item));
            Assertions.assertTrue(sfItem.isItem(new CustomItemStack(Material.BEACON, "&cItem Test")));

            Slimefun.getRegistry().setBackwardsCompatible(false);
        } else {
            Assertions.assertFalse(sfItem.isItem(item));
            Assertions.assertFalse(sfItem.isItem(new CustomItemStack(Material.BEACON, "&cItem Test")));
        }

        Assertions.assertEquals(sfItem, SlimefunItem.getByItem(new SlimefunItemStack(sfItem.getId(), item)));
    }

    @Test
    @DisplayName("Test WrongItemStackException")
    void testWrongItemStackException() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "WRONG_ITEMSTACK_EXCEPTION", new CustomItemStack(Material.NETHER_STAR, "&4Do not modify me"));
        item.register(plugin);
        item.load();

        ItemStack itemStack = item.getItem();
        Assertions.assertThrows(WrongItemStackException.class, () -> itemStack.setAmount(40));
    }

    @Test
    @DisplayName("Test UnregisteredItemException")
    void testUnregisteredItemException() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "UNREGISTERED_ITEM_EXCEPTION", new CustomItemStack(Material.NETHER_STAR, "&4Do not modify me"));
        Assertions.assertThrows(UnregisteredItemException.class, () -> item.getAddon());
    }

    @Test
    @DisplayName("Test SlimefunItem#equals(...)")
    void testEquals() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "EQUALS_TEST", new CustomItemStack(Material.LANTERN, "&6We are equal"));
        SlimefunItem item2 = TestUtilities.mockSlimefunItem(plugin, "EQUALS_TEST", new CustomItemStack(Material.LANTERN, "&6We are equal"));
        SlimefunItem differentItem = TestUtilities.mockSlimefunItem(plugin, "I_AM_DIFFERENT", new CustomItemStack(Material.LANTERN, "&6We are equal"));

        Assertions.assertEquals(item, item2);
        Assertions.assertNotEquals(item, differentItem);
        Assertions.assertNotEquals(item2, differentItem);
    }

    @Test
    @DisplayName("Test SlimefunItem#hashCode()")
    void testHashCode() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "EQUALS_TEST", new CustomItemStack(Material.LANTERN, "&6We are equal"));
        SlimefunItem item2 = TestUtilities.mockSlimefunItem(plugin, "EQUALS_TEST", new CustomItemStack(Material.LANTERN, "&6We are equal"));
        SlimefunItem differentItem = TestUtilities.mockSlimefunItem(plugin, "I_AM_DIFFERENT", new CustomItemStack(Material.LANTERN, "&6We are equal"));

        Assertions.assertEquals(item.hashCode(), item2.hashCode());
        Assertions.assertNotEquals(item.hashCode(), differentItem.hashCode());
        Assertions.assertNotEquals(item2.hashCode(), differentItem.hashCode());
    }
}
