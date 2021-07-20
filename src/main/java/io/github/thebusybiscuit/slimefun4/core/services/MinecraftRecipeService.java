package io.github.thebusybiscuit.slimefun4.core.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import io.github.bakedlibs.dough.recipes.MinecraftRecipe;
import io.github.bakedlibs.dough.recipes.RecipeSnapshot;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;

/**
 * This Service is responsible for accessing a {@link RecipeSnapshot}.
 * This snapshot contains a compiled list of all recipes that could be found on the
 * Server at the time the Service was loaded.
 * 
 * This Service is primarily used by the {@link SurvivalSlimefunGuide}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class MinecraftRecipeService {

    /**
     * Our {@link Plugin} instance
     */
    private final Plugin plugin;

    /**
     * The subscriber list for the {@link RecipeSnapshot}.
     */
    private final List<Consumer<RecipeSnapshot>> subscriptions = new LinkedList<>();

    /**
     * Our {@link RecipeSnapshot} - The centerpiece of this class.
     */
    private RecipeSnapshot snapshot;

    /**
     * This constructs a new {@link MinecraftRecipeService} for the given {@link Plugin}.
     * Slimefun already has a {@link MinecraftRecipeService} so creating your own won't be
     * of much use unless you wanna expand upon it. It is advised to use Slimefun's built-in
     * {@link MinecraftRecipeService} though.
     * 
     * @param plugin
     *            The {@link Plugin} that requests this Service
     */
    public MinecraftRecipeService(@Nonnull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * This method refreshes the {@link RecipeSnapshot} that is used by the {@link MinecraftRecipeService}.
     */
    public void refresh() {
        snapshot = new RecipeSnapshot(plugin);

        for (Consumer<RecipeSnapshot> subscriber : subscriptions) {
            subscriber.accept(snapshot);
        }
    }

    /**
     * This method subscribes to the underlying {@link RecipeSnapshot}.
     * When the {@link Server} has finished loading and a {@link Collection} of all
     * {@link Recipe Recipes} is created, the given callback will be run.
     * 
     * @param subscription
     *            A callback to run when the {@link RecipeSnapshot} has been created.
     */
    public void subscribe(@Nonnull Consumer<RecipeSnapshot> subscription) {
        Validate.notNull(subscription, "Callback must not be null!");
        subscriptions.add(subscription);
    }

    /**
     * This method returns an {@link Optional} describing the output of a {@link FurnaceRecipe}
     * with the given {@link ItemStack} as an input.
     * 
     * @param input
     *            The input {@link ItemStack}
     * 
     * @return An {@link Optional} describing the furnace output of the given {@link ItemStack}
     */
    @Nonnull
    public Optional<ItemStack> getFurnaceOutput(@Nullable ItemStack input) {
        if (snapshot == null || input == null) {
            return Optional.empty();
        }

        return snapshot.getRecipeOutput(MinecraftRecipe.FURNACE, input);
    }

    /**
     * This returns whether a given {@link ItemStack} can be smelted in a {@link FurnaceRecipe}.
     * 
     * @param input
     *            The {@link ItemStack} to test
     * 
     * @return Whether this item can be smelted
     */
    public boolean isSmeltable(@Nullable ItemStack input) {
        return getFurnaceOutput(input).isPresent();
    }

    /**
     * This returns the shape of a given {@link Recipe}.
     * For any shapeless {@link Recipe} the result will be equivalent to
     * {@link RecipeSnapshot#getRecipeInput(Recipe)}.
     * For a {@link ShapedRecipe} this method will fix the order so it matches a
     * 3x3 crafting grid.
     * 
     * @param recipe
     *            The {@link Recipe} to get the shape from
     * 
     * @return An Array of {@link RecipeChoice} representing the shape of this {@link Recipe}
     */
    @Nonnull
    public RecipeChoice[] getRecipeShape(@Nonnull Recipe recipe) {
        Validate.notNull(recipe, "Recipe must not be null!");

        if (recipe instanceof ShapedRecipe) {
            List<RecipeChoice> choices = new LinkedList<>();

            for (String row : ((ShapedRecipe) recipe).getShape()) {
                int columns = row.toCharArray().length;

                for (char key : row.toCharArray()) {
                    choices.add(((ShapedRecipe) recipe).getChoiceMap().get(key));
                }

                while (columns < 3) {
                    choices.add(null);
                    columns++;
                }
            }

            return choices.toArray(new RecipeChoice[0]);
        } else {
            return snapshot.getRecipeInput(recipe);
        }
    }

    /**
     * This returns an array containing all {@link Recipe Recipes} for crafting the given
     * {@link ItemStack}.
     * 
     * @param item
     *            The {@link ItemStack} for which to get the recipes
     * 
     * @return An array of {@link Recipe Recipes} to craft the given {@link ItemStack}
     */
    @Nonnull
    public Recipe[] getRecipesFor(@Nullable ItemStack item) {
        if (snapshot == null || item == null) {
            return new Recipe[0];
        } else {
            return snapshot.getRecipesFor(item).toArray(new Recipe[0]);
        }
    }

    /**
     * This returns the corresponding {@link Keyed} {@link Recipe} for the given {@link NamespacedKey}.
     * If no {@link Recipe} was found, null will be returned.
     * This is a significantly faster method over {@link Bukkit#getRecipe(NamespacedKey)} since we
     * operate on a cached {@link HashMap}
     * 
     * @param key
     *            The {@link NamespacedKey}
     * 
     * @return The corresponding {@link Recipe} or null
     */
    @Nullable
    public Recipe getRecipe(@Nonnull NamespacedKey key) {
        Validate.notNull(key, "The NamespacedKey should not be null");

        if (snapshot != null) {
            // We operate on a cached HashMap which is much faster than Bukkit's method.
            return snapshot.getRecipe(key);
        } else {
            return Bukkit.getRecipe(key);
        }
    }

}
