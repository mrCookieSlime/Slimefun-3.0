package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

/**
 * The {@link VanillaRecipe} implements an {@link AbstractRecipe} and represents a
 * {@link ShapedRecipe} or {@link ShapelessRecipe}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see VanillaAutoCrafter
 *
 */
class VanillaRecipe extends AbstractRecipe {

    private final int[] slots = { 11, 12, 13, 20, 21, 22, 29, 30, 31 };
    private final Recipe recipe;

    VanillaRecipe(@Nonnull ShapelessRecipe recipe) {
        super(new ArrayList<>(recipe.getChoiceList()), recipe.getResult());

        this.recipe = recipe;
    }

    VanillaRecipe(@Nonnull ShapedRecipe recipe) {
        super(getChoices(recipe), recipe.getResult());

        this.recipe = recipe;
    }

    @Nonnull
    private static Collection<Predicate<ItemStack>> getChoices(@Nonnull ShapedRecipe recipe) {
        List<Predicate<ItemStack>> choices = new ArrayList<>();

        for (String row : recipe.getShape()) {
            for (char c : row.toCharArray()) {
                RecipeChoice choice = recipe.getChoiceMap().get(c);

                if (choice != null) {
                    choices.add(choice);
                }
            }
        }

        return choices;
    }

    @Nonnull
    private static RecipeChoice[] getShape(@Nonnull Recipe recipe) {
        return Slimefun.getMinecraftRecipeService().getRecipeShape(recipe);
    }

    @Override
    public void show(@Nonnull ChestMenu menu, @Nonnull AsyncRecipeChoiceTask task) {
        Validate.notNull(menu, "The ChestMenu cannot be null!");
        Validate.notNull(task, "The RecipeChoiceTask cannot be null!");

        menu.replaceExistingItem(24, getResult().clone());
        menu.addMenuClickHandler(24, ChestMenuUtils.getEmptyClickHandler());

        RecipeChoice[] choices = Slimefun.getMinecraftRecipeService().getRecipeShape(recipe);
        ItemStack[] items = new ItemStack[9];

        if (choices.length == 1 && choices[0] instanceof MaterialChoice) {
            items[4] = new ItemStack(((MaterialChoice) choices[0]).getChoices().get(0));

            if (((MaterialChoice) choices[0]).getChoices().size() > 1) {
                task.add(slots[4], (MaterialChoice) choices[0]);
            }
        } else {
            for (int i = 0; i < choices.length; i++) {
                if (choices[i] instanceof MaterialChoice) {
                    items[i] = new ItemStack(((MaterialChoice) choices[i]).getChoices().get(0));

                    if (((MaterialChoice) choices[i]).getChoices().size() > 1) {
                        task.add(slots[i], (MaterialChoice) choices[i]);
                    }
                }
            }
        }

        for (int i = 0; i < 9; i++) {
            menu.replaceExistingItem(slots[i], items[i]);
            menu.addMenuClickHandler(slots[i], ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    public String toString() {
        if (recipe instanceof Keyed) {
            return ((Keyed) recipe).getKey().toString();
        } else {
            return "invalid-recipe";
        }
    }
}
