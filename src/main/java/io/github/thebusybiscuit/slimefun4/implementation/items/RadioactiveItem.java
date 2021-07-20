package io.github.thebusybiscuit.slimefun4.implementation.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;

/**
 * A quick and easy implementation of {@link SlimefunItem} that also implements the
 * interface {@link Radioactive}.
 * This implementation is {@link NotPlaceable}!
 * 
 * Simply specify a level of {@link Radioactivity} in the constructor.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Radioactive
 * @see Radioactivity
 *
 */
public class RadioactiveItem extends SlimefunItem implements Radioactive, NotPlaceable {

    /**
     * This is the level of {@link Radioactivity} for this {@link SlimefunItem}
     */
    private final Radioactivity radioactivity;

    /**
     * This will create a new {@link RadioactiveItem} with the given level of {@link Radioactivity}
     * 
     * @param category
     *            The {@link ItemGroup} of this {@link SlimefunItem}
     * @param radioactivity
     *            the level of {@link Radioactivity}
     * @param item
     *            the {@link SlimefunItemStack} this {@link SlimefunItem} represents
     * @param recipeType
     *            The {@link RecipeType} for this item
     * @param recipe
     *            The recipe of how to craft this {@link SlimefunItem}
     */
    @ParametersAreNonnullByDefault
    public RadioactiveItem(ItemGroup category, Radioactivity radioactivity, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        this(category, radioactivity, item, recipeType, recipe, null);
    }

    /**
     * This will create a new {@link RadioactiveItem} with the given level of {@link Radioactivity}
     * 
     * @param category
     *            The {@link ItemGroup} of this {@link SlimefunItem}
     * @param radioactivity
     *            the level of {@link Radioactivity}
     * @param item
     *            the {@link SlimefunItemStack} this {@link SlimefunItem} represents
     * @param recipeType
     *            The {@link RecipeType} for this item
     * @param recipe
     *            The recipe of how to craft this {@link SlimefunItem}
     * @param recipeOutput
     *            The recipe output
     */
    @ParametersAreNonnullByDefault
    public RadioactiveItem(ItemGroup category, Radioactivity radioactivity, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, @Nullable ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput);

        this.radioactivity = radioactivity;
        addItemHandler(onRightClick());
    }

    @Nonnull
    private ItemUseHandler onRightClick() {
        return PlayerRightClickEvent::cancel;
    }

    @Override
    @Nonnull
    public Radioactivity getRadioactivity() {
        return radioactivity;
    }

}
