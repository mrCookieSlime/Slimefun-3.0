package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.services.MinecraftRecipeService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.papermc.lib.PaperLib;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

/**
 * The {@link VanillaAutoCrafter} is an implementation of the {@link AbstractAutoCrafter}.
 * It can craft items that are crafted using a normal crafting table.
 * Only {@link ShapedRecipe} and {@link ShapelessRecipe} are therefore supported.
 * 
 * @author TheBusyBiscuit
 * 
 * @see AbstractAutoCrafter
 * @see EnhancedAutoCrafter
 * @see VanillaRecipe
 *
 */
public class VanillaAutoCrafter extends AbstractAutoCrafter {

    @ParametersAreNonnullByDefault
    public VanillaAutoCrafter(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public @Nullable AbstractRecipe getSelectedRecipe(@Nonnull Block b) {
        BlockState state = PaperLib.getBlockState(b, false).getState();

        if (state instanceof Skull) {
            // Read the stored value from persistent data storage
            PersistentDataContainer container = ((Skull) state).getPersistentDataContainer();
            String value = container.get(recipeStorageKey, PersistentDataType.STRING);

            if (value != null) {
                String[] values = CommonPatterns.COLON.split(value);

                /*
                 * Normally this constructor should not be used.
                 * But it is completely fine for this purpose since we only use
                 * it for lookups.
                 */
                @SuppressWarnings("deprecation")
                NamespacedKey key = new NamespacedKey(values[0], values[1]);
                Recipe keyedRecipe = Slimefun.getMinecraftRecipeService().getRecipe(key);

                if (keyedRecipe != null) {
                    boolean enabled = !container.has(recipeEnabledKey, PersistentDataType.BYTE);
                    AbstractRecipe recipe = AbstractRecipe.of(keyedRecipe);
                    recipe.setEnabled(enabled);

                    return recipe;
                }
            }
        }

        return null;
    }

    @Override
    protected boolean matches(@Nonnull ItemStack item, @Nonnull Predicate<ItemStack> predicate) {
        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        // Slimefunitems should be ignored (unless allowed)
        if (sfItem == null || sfItem.isUseableInWorkbench()) {
            return super.matches(item, predicate);
        } else {
            return false;
        }
    }

    @Override
    protected void updateRecipe(@Nonnull Block b, @Nonnull Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        List<Recipe> recipes = getRecipesFor(item);

        if (recipes.isEmpty()) {
            Slimefun.getLocalization().sendMessage(p, "messages.auto-crafting.no-recipes");
        } else {
            ChestMenu menu = new ChestMenu(getItemName());
            menu.setPlayerInventoryClickable(false);
            menu.setEmptySlotsClickable(false);

            ChestMenuUtils.drawBackground(menu, background);
            ChestMenuUtils.drawBackground(menu, 45, 47, 48, 50, 51, 53);

            AsyncRecipeChoiceTask task = new AsyncRecipeChoiceTask();
            offerRecipe(p, b, recipes, 0, menu, task);

            menu.open(p);
            task.start(menu.toInventory());
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        }
    }

    @ParametersAreNonnullByDefault
    private void offerRecipe(Player p, Block b, List<Recipe> recipes, int index, ChestMenu menu, AsyncRecipeChoiceTask task) {
        Validate.isTrue(index >= 0 && index < recipes.size(), "page must be between 0 and " + (recipes.size() - 1));

        menu.replaceExistingItem(46, ChestMenuUtils.getPreviousButton(p, index + 1, recipes.size()));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            if (index > 0) {
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                offerRecipe(p, b, recipes, index - 1, menu, task);
            }

            return false;
        });

        menu.replaceExistingItem(52, ChestMenuUtils.getNextButton(p, index + 1, recipes.size()));
        menu.addMenuClickHandler(52, (pl, slot, item, action) -> {
            if (index < (recipes.size() - 1)) {
                pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                offerRecipe(p, b, recipes, index + 1, menu, task);
            }

            return false;
        });

        AbstractRecipe recipe = AbstractRecipe.of(recipes.get(index));

        menu.replaceExistingItem(49, new CustomItemStack(Material.CRAFTING_TABLE, ChatColor.GREEN + Slimefun.getLocalization().getMessage(p, "messages.auto-crafting.select")));
        menu.addMenuClickHandler(49, (pl, slot, item, action) -> {
            setSelectedRecipe(b, recipe);
            pl.closeInventory();

            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            Slimefun.getLocalization().sendMessage(p, "messages.auto-crafting.recipe-set");
            showRecipe(p, b, recipe);

            return false;
        });

        task.clear();
        recipe.show(menu, task);
    }

    @Nonnull
    private List<Recipe> getRecipesFor(@Nonnull ItemStack item) {
        List<Recipe> recipes = new ArrayList<>();

        // Fixes #2913 - Bukkit.getRecipesFor() only checks for Materials
        MinecraftRecipeService recipeService = Slimefun.getMinecraftRecipeService();

        for (Recipe recipe : recipeService.getRecipesFor(item)) {
            if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
                recipes.add(recipe);
            }
        }

        return recipes;
    }
}
