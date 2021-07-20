package io.github.thebusybiscuit.slimefun4.implementation.items.misc;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Cow;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;

/**
 * The {@link SteelThruster} is a pretty basic crafting component.
 * However... as it is actually a bucket. We need to make sure that
 * Cows cannot be milked using it.
 * 
 * @author TheBusyBiscuit
 *
 */
public class SteelThruster extends SlimefunItem {

    @ParametersAreNonnullByDefault
    public SteelThruster(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(onRightClickBlock(), onRightClickEntity());
    }

    @Nonnull
    private ItemUseHandler onRightClickBlock() {
        return PlayerRightClickEvent::cancel;
    }

    @Nonnull
    private EntityInteractHandler onRightClickEntity() {
        return (e, item, hand) -> {
            // Milking cows with a rocket engine? Yeah, that would be weird.
            if (e.getRightClicked() instanceof Cow) {
                e.setCancelled(true);
            }
        };
    }

}