package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.papermc.lib.PaperLib;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;

/**
 * This extension of the {@link AbstractAutoCrafter} allows you to implement any
 * {@link RecipeType}.
 * The concrete implementation for this can be seen in the {@link EnhancedAutoCrafter} but
 * it theoretically works for any {@link RecipeType}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see EnhancedAutoCrafter
 *
 */
public class SlimefunAutoCrafter extends AbstractAutoCrafter {

    /**
     * The targeted {@link RecipeType} that is being crafted here.
     */
    private final RecipeType targetRecipeType;

    @ParametersAreNonnullByDefault
    protected SlimefunAutoCrafter(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, RecipeType targetRecipeType) {
        super(category, item, recipeType, recipe);

        this.targetRecipeType = targetRecipeType;
    }

    @Override
    @Nullable
    public AbstractRecipe getSelectedRecipe(@Nonnull Block b) {
        Validate.notNull(b, "The Block cannot be null!");

        BlockState state = PaperLib.getBlockState(b, false).getState();

        if (state instanceof Skull) {
            // Read the stored value from persistent data storage
            PersistentDataContainer container = ((Skull) state).getPersistentDataContainer();
            String value = container.get(recipeStorageKey, PersistentDataType.STRING);
            SlimefunItem item = SlimefunItem.getById(value);

            if (item != null) {
                boolean enabled = !container.has(recipeEnabledKey, PersistentDataType.BYTE);
                AbstractRecipe recipe = AbstractRecipe.of(item, targetRecipeType);
                recipe.setEnabled(enabled);
                return recipe;
            }
        }

        return null;
    }

    @Override
    protected void updateRecipe(@Nonnull Block b, @Nonnull Player p) {
        ItemStack itemInHand = p.getInventory().getItemInMainHand();
        SlimefunItem item = SlimefunItem.getByItem(itemInHand);

        if (item != null && item.getRecipeType().equals(targetRecipeType)) {
            // Fixes #1161
            if (item.canUse(p, true)) {
                AbstractRecipe recipe = AbstractRecipe.of(item, targetRecipeType);

                if (recipe != null) {
                    ChestMenu menu = new ChestMenu(getItemName());
                    menu.setPlayerInventoryClickable(false);
                    menu.setEmptySlotsClickable(false);

                    ChestMenuUtils.drawBackground(menu, background);
                    ChestMenuUtils.drawBackground(menu, 45, 46, 47, 48, 50, 51, 52, 53);

                    menu.addItem(49, new CustomItemStack(Material.CRAFTING_TABLE, ChatColor.GREEN + Slimefun.getLocalization().getMessage(p, "messages.auto-crafting.select")));
                    menu.addMenuClickHandler(49, (pl, stack, slot, action) -> {
                        setSelectedRecipe(b, recipe);
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                        Slimefun.getLocalization().sendMessage(p, "messages.auto-crafting.recipe-set");
                        showRecipe(p, b, recipe);
                        return false;
                    });

                    AsyncRecipeChoiceTask task = new AsyncRecipeChoiceTask();
                    recipe.show(menu, task);
                    menu.open(p);

                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

                    if (!task.isEmpty()) {
                        task.start(menu.toInventory());
                    }
                } else {
                    Slimefun.getLocalization().sendMessage(p, "messages.auto-crafting.no-recipes");
                }
            }
        } else {
            Slimefun.getLocalization().sendMessage(p, "messages.auto-crafting.no-recipes");
        }
    }
}
