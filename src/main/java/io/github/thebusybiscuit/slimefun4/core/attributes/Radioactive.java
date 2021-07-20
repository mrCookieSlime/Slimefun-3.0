package io.github.thebusybiscuit.slimefun4.core.attributes;

import javax.annotation.Nonnull;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * This Interface, when attached to a class that inherits from {@link SlimefunItem}, marks
 * the Item as radioactive.
 * Carrying such an item will give the wearer the radiation effect.
 * 
 * You can specify a level of {@link Radioactivity} for the severity of the effect.
 * 
 * @author TheBusyBiscuit
 *
 */
public interface Radioactive extends ItemAttribute {

    /**
     * This method returns the level of {@link Radioactivity} for this {@link Radioactive} item.
     * Higher levels cause more severe radiation effects.
     * 
     * @return The level of {@link Radioactivity} of this item.
     */
    @Nonnull
    Radioactivity getRadioactivity();

}
