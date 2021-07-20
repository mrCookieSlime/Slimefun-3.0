package io.github.thebusybiscuit.slimefun4.core.attributes;

import org.bukkit.entity.Piglin;
import org.bukkit.event.entity.EntityDropItemEvent;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.entity.PiglinListener;

/**
 * This interface, when attached to a {@link SlimefunItem}, provides a variable (0-100%) chance for
 * a {@link SlimefunItem} to be dropped by a {@link Piglin} on {@link EntityDropItemEvent}.
 * 
 * @author dNiym
 *
 * @see PiglinListener
 * @see RandomMobDrop
 * 
 */
public interface PiglinBarterDrop extends ItemAttribute {

    /**
     * Implement this method to make this {@link SlimefunItem} have a variable chance
     * of being dropped by a {@link Piglin} when bartering with them. This interface
     * should be used with the {@link RecipeType#BARTER_DROP}.
     *
     * It is recommended that this chance is kept reasonably low to feel like
     * a vanilla drop as a 100% chance will completely override all {@link Piglin}
     * barter drops. (NOTE: this feature only exists in 1.16+)
     * 
     * @return The integer chance (1-99%) this {@link SlimefunItem} has to drop.
     */
    int getBarteringLootChance();

}
