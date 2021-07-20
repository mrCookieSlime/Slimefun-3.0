package io.github.thebusybiscuit.slimefun4.api.items.groups;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;

/**
 * A {@link FlexItemGroup} is a {@link ItemGroup} inside the {@link SlimefunGuide} that can
 * be completely modified.
 * It cannot hold any {@link SlimefunItem} but can be completely overridden
 * to perform any action upon being opened.
 * 
 * @author TheBusyBiscuit
 *
 */
public abstract class FlexItemGroup extends ItemGroup {

    @ParametersAreNonnullByDefault
    protected FlexItemGroup(NamespacedKey key, ItemStack item) {
        this(key, item, 3);
    }

    @ParametersAreNonnullByDefault
    protected FlexItemGroup(NamespacedKey key, ItemStack item, int tier) {
        super(key, item, tier);
    }

    /**
     * This method returns whether this {@link FlexItemGroup} is visible under the given context.
     * Implementing this method gives full flexibility over who can see the Category when and where.
     * 
     * @param p
     *            The {@link Player} who opened his {@link SlimefunGuide}
     * @param profile
     *            The {@link PlayerProfile} of the {@link Player}
     * @param layout
     *            The {@link SlimefunGuideMode} in which this {@link FlexItemGroup} is viewed
     * 
     * @return Whether to display this {@link FlexItemGroup}
     */
    @ParametersAreNonnullByDefault
    public abstract boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode layout);

    /**
     * This method is called when a {@link Player} opens this {@link FlexItemGroup}.
     * This is an abstract method which needs to be implemented in order to determine what this
     * {@link FlexItemGroup} should actually do as it cannot hold any items.
     * 
     * @param p
     *            The {@link Player} who wants to open this {@link FlexItemGroup}
     * @param profile
     *            The corresponding {@link PlayerProfile} for that {@link Player}
     * @param layout
     *            The current {@link SlimefunGuideMode}
     */
    public abstract void open(Player p, PlayerProfile profile, SlimefunGuideMode layout);

    @Override
    public final boolean isHidden(@Nonnull Player p) {
        /*
         * We can stop this method right here.
         * We provide a custom method with more parameters for this.
         * See isVisible(...)
         */
        return false;
    }

    @Override
    public final void add(SlimefunItem item) {
        throw new UnsupportedOperationException("You cannot add items to a FlexCategory!");
    }

    @Override
    public final List<SlimefunItem> getItems() {
        throw new UnsupportedOperationException("A FlexCategory has no items!");
    }

    @Override
    public final boolean contains(SlimefunItem item) {
        throw new UnsupportedOperationException("A FlexCategory has no items!");
    }

    @Override
    public final void remove(SlimefunItem item) {
        throw new UnsupportedOperationException("A FlexCategory has no items, so there is nothing remove!");
    }

}
