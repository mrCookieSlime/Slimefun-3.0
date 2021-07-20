package io.github.thebusybiscuit.slimefun4.core.services;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.recipes.RecipeSnapshot;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestRecipeService {

    private static ServerMock server;
    private static Slimefun plugin;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test getting a Minecraft Recipe for an ItemStack result")
    void testRecipe() {
        MinecraftRecipeService service = new MinecraftRecipeService(plugin);

        NamespacedKey key = new NamespacedKey(plugin, "furnace_recipe_test");
        ItemStack result = new ItemStack(Material.EMERALD_BLOCK);
        FurnaceRecipe recipe = new FurnaceRecipe(key, result, new MaterialChoice(Material.DIAMOND), 1, 2);
        server.addRecipe(recipe);

        // The Snapshot has not been taken, so it should fallback to an empty array
        Assertions.assertEquals(0, service.getRecipesFor(result).length);

        service.refresh();

        Recipe[] recipes = service.getRecipesFor(result);
        Assertions.assertEquals(1, recipes.length);
        Assertions.assertEquals(recipe, recipes[0]);
    }

    @Test
    @DisplayName("Test getting no Minecraft Recipes for uncraftable items or null")
    void testNoRecipes() {
        MinecraftRecipeService service = new MinecraftRecipeService(plugin);
        service.refresh();

        Assertions.assertEquals(0, service.getRecipesFor(null).length);
        Assertions.assertEquals(0, service.getRecipesFor(new ItemStack(Material.BEDROCK)).length);
    }

    @Test
    @DisplayName("Test if furnace recipes are handled correctly")
    void testFurnaceOutput() {
        MinecraftRecipeService service = new MinecraftRecipeService(plugin);

        NamespacedKey key = new NamespacedKey(plugin, "furnace_recipe_test2");
        ItemStack result = new ItemStack(Material.GOLD_BLOCK);
        MaterialChoice materials = new MaterialChoice(Material.DIRT, Material.COBBLESTONE);
        FurnaceRecipe recipe = new FurnaceRecipe(key, result, materials, 1, 2);
        server.addRecipe(recipe);

        // The Snapshot has not been taken, so it should fallback to an empty Optional
        Assertions.assertFalse(service.getFurnaceOutput(new ItemStack(Material.DIRT)).isPresent());

        service.refresh();

        Assertions.assertFalse(service.getFurnaceOutput(null).isPresent());
        Assertions.assertFalse(service.getFurnaceOutput(new ItemStack(Material.BEDROCK)).isPresent());

        Optional<ItemStack> optional = service.getFurnaceOutput(new ItemStack(Material.DIRT));
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(result, optional.get());

        Optional<ItemStack> optional2 = service.getFurnaceOutput(new ItemStack(Material.COBBLESTONE));
        Assertions.assertTrue(optional2.isPresent());
        Assertions.assertEquals(result, optional2.get());

        // Cobblestone should be smeltable
        Assertions.assertTrue(service.isSmeltable(new ItemStack(Material.COBBLESTONE)));
    }

    @Test
    @DisplayName("Test Shaped Recipes on a 3x3 grid")
    void testBigShapedRecipe() {
        MinecraftRecipeService service = new MinecraftRecipeService(plugin);

        NamespacedKey key = new NamespacedKey(plugin, "shaped_recipe_9");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
        MaterialChoice choice = new MaterialChoice(Material.TNT, Material.TNT_MINECART);

        recipe.shape("t t", " t ", "t t");
        recipe.setIngredient('t', choice);
        server.addRecipe(recipe);
        service.refresh();

        RecipeChoice[] shape = service.getRecipeShape(recipe);
        Assertions.assertArrayEquals(new RecipeChoice[] { choice, null, choice, null, choice, null, choice, null, choice }, shape);
    }

    @Test
    @DisplayName("Test Shaped Recipes on a 2x2 grid")
    void testSmallShapedRecipe() {
        MinecraftRecipeService service = new MinecraftRecipeService(plugin);

        NamespacedKey key = new NamespacedKey(plugin, "shaped_recipe_4");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
        MaterialChoice choice = new MaterialChoice(Material.TNT, Material.TNT_MINECART);

        recipe.shape("tt", "tt");
        recipe.setIngredient('t', choice);
        server.addRecipe(recipe);
        service.refresh();

        RecipeChoice[] shape = service.getRecipeShape(recipe);
        Assertions.assertArrayEquals(new RecipeChoice[] { choice, choice, null, choice, choice, null }, shape);
    }

    @Test
    @DisplayName("Test shapeless Recipes")
    void testShapelessRecipeShape() {
        MinecraftRecipeService service = new MinecraftRecipeService(plugin);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.getRecipeShape(null));

        NamespacedKey key = new NamespacedKey(plugin, "shapeless_test");
        ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemStack(Material.TNT_MINECART));
        MaterialChoice choice = new MaterialChoice(Material.TNT);
        recipe.addIngredient(choice);

        server.addRecipe(recipe);
        service.refresh();

        Assertions.assertArrayEquals(new RecipeChoice[] { choice }, service.getRecipeShape(recipe));
    }

    @Test
    @DisplayName("Test the Recipe Subscription Service")
    void testSubscriptions() {
        MinecraftRecipeService service = new MinecraftRecipeService(plugin);
        AtomicReference<RecipeSnapshot> reference = new AtomicReference<>();

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.subscribe(null));

        service.subscribe(reference::set);
        service.refresh();

        // The callback was executed
        Assertions.assertNotNull(reference.get());
    }
}
