package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestAbstractRecipe {

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
    @DisplayName("Test ShapelessRecipe as AbstractRecipe")
    void testShapelessRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "shapeless_recipe_test");
        ItemStack result = new CustomItemStack(Material.DIAMOND, "&6Special Diamond :o");

        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        recipe.addIngredient(new MaterialChoice(Material.IRON_NUGGET, Material.GOLD_NUGGET));

        AbstractRecipe abstractRecipe = AbstractRecipe.of(recipe);

        Assertions.assertNotNull(abstractRecipe);
        Assertions.assertEquals(result, abstractRecipe.getResult());
        Assertions.assertIterableEquals(recipe.getChoiceList(), abstractRecipe.getIngredients());
        Assertions.assertEquals(key.toString(), abstractRecipe.toString());
    }

    @Test
    @DisplayName("Test ShapedRecipe as AbstractRecipe")
    void testShapedRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "shaped_recipe_test");
        ItemStack result = new CustomItemStack(Material.EMERALD, "&6Special Emerald :o");

        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("OXO", " X ", "OXO");

        List<MaterialChoice> choices = Arrays.asList(new MaterialChoice(Material.IRON_NUGGET), new MaterialChoice(Tag.PLANKS));
        recipe.setIngredient('X', choices.get(0));
        recipe.setIngredient('O', choices.get(1));

        AbstractRecipe abstractRecipe = AbstractRecipe.of(recipe);

        Assertions.assertNotNull(abstractRecipe);
        Assertions.assertEquals(result, abstractRecipe.getResult());
        Assertions.assertEquals(new HashSet<>(choices), new HashSet<>(abstractRecipe.getIngredients()));
        Assertions.assertEquals(key.toString(), abstractRecipe.toString());
    }

    @Test
    @DisplayName("Test invalid recipes as AbstractRecipe")
    void testInvalidRecipes() {
        NamespacedKey key = new NamespacedKey(plugin, "furnace_recipe_test");
        ItemStack result = new CustomItemStack(Material.COAL, "&6Special Coal :o");
        FurnaceRecipe recipe = new FurnaceRecipe(key, result, Material.COAL, 1, 1);

        Assertions.assertNull(AbstractRecipe.of(recipe));
        Assertions.assertNull(AbstractRecipe.of((ShapedRecipe) null));
    }

}
