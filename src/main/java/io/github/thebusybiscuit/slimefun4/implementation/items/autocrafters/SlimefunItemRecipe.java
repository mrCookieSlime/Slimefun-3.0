package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

/**
 * This {@link AbstractRecipe} implementation stands for a {@link SlimefunItem} which
 * is crafted using any {@link RecipeType}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SlimefunAutoCrafter
 *
 */
class SlimefunItemRecipe extends AbstractRecipe {

    private final int[] slots = { 11, 12, 13, 20, 21, 22, 29, 30, 31 };
    private final SlimefunItem item;

    SlimefunItemRecipe(@Nonnull SlimefunItem item) {
        super(getInputs(item), item.getRecipeOutput());
        this.item = item;
    }

    @Nonnull
    private static Collection<Predicate<ItemStack>> getInputs(@Nonnull SlimefunItem item) {
        List<Predicate<ItemStack>> predicates = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            ItemStack ingredient = item.getRecipe()[i];

            if (ingredient != null && !ingredient.getType().isAir()) {
                predicates.add(stack -> SlimefunUtils.isItemSimilar(stack, ingredient, true));
            }
        }

        return predicates;
    }

    @Override
    public void show(@Nonnull ChestMenu menu, @Nonnull AsyncRecipeChoiceTask task) {
        Validate.notNull(menu, "The ChestMenu cannot be null!");
        Validate.notNull(task, "The RecipeChoiceTask cannot be null!");
        menu.addItem(24, getResult().clone(), ChestMenuUtils.getEmptyClickHandler());
        ItemStack[] recipe = item.getRecipe();

        for (int i = 0; i < 9; i++) {
            menu.addItem(slots[i], recipe[i], ChestMenuUtils.getEmptyClickHandler());
        }
    }

    @Override
    public String toString() {
        return item.getId();
    }
}
