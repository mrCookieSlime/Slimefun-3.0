package io.github.thebusybiscuit.slimefun4.api.items.groups;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * Represents a {@link ItemGroup} that cannot be opened until the parent category/categories
 * are fully unlocked.
 * <p>
 * See {@link ItemGroup} for the complete documentation.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ItemGroup
 * @see SeasonalItemGroup
 * 
 */
public class LockedItemGroup extends ItemGroup {

    private final NamespacedKey[] keys;
    private final Set<ItemGroup> parents = new HashSet<>();

    /**
     * The basic constructor for a LockedCategory.
     * Like {@link ItemGroup}, the default tier is automatically set to 3.
     * 
     * @param key
     *            A unique identifier for this category
     * @param item
     *            The display item for this category
     * @param parents
     *            The parent categories for this category
     * 
     */
    @ParametersAreNonnullByDefault
    public LockedItemGroup(NamespacedKey key, ItemStack item, NamespacedKey... parents) {
        this(key, item, 3, parents);
    }

    /**
     * The constructor for a LockedCategory.
     * 
     * @param key
     *            A unique identifier for this category
     * @param item
     *            The display item for this category
     * @param tier
     *            The tier of this category
     * @param parents
     *            The parent categories for this category
     * 
     */
    @ParametersAreNonnullByDefault
    public LockedItemGroup(NamespacedKey key, ItemStack item, int tier, NamespacedKey... parents) {
        super(key, item, tier);
        Validate.noNullElements(parents, "A LockedCategory must not have any 'null' parents!");

        this.keys = parents;
    }

    @Override
    public void register(@Nonnull SlimefunAddon addon) {
        super.register(addon);

        List<NamespacedKey> namespacedKeys = new ArrayList<>();

        for (NamespacedKey key : keys) {
            if (key != null) {
                namespacedKeys.add(key);
            }
        }

        for (ItemGroup category : Slimefun.getRegistry().getCategories()) {
            if (namespacedKeys.remove(category.getKey())) {
                addParent(category);
            }
        }

        for (NamespacedKey key : namespacedKeys) {
            Slimefun.logger().log(Level.INFO, "Parent \"{0}\" for Category \"{1}\" was not found, probably just disabled.", new Object[] { key, getKey() });
        }
    }

    /**
     * Gets the list of parent categories for this {@link LockedItemGroup}.
     * 
     * @return the list of parent categories
     * 
     * @see #addParent(ItemGroup)
     * @see #removeParent(ItemGroup)
     */
    @Nonnull
    public Set<ItemGroup> getParents() {
        return parents;
    }

    /**
     * Adds a parent {@link ItemGroup} to this {@link LockedItemGroup}.
     * 
     * @param category
     *            The {@link ItemGroup} to add as a parent
     *
     * @see #getParents()
     * @see #removeParent(ItemGroup)
     */
    public void addParent(ItemGroup category) {
        if (category == this || category == null) {
            throw new IllegalArgumentException("Category '" + item.getItemMeta().getDisplayName() + "' cannot be a parent of itself or have a 'null' parent.");
        }

        parents.add(category);
    }

    /**
     * Removes a {@link ItemGroup} from the parents of this {@link LockedItemGroup}.
     * 
     * @param category
     *            The {@link ItemGroup} to remove from the parents of this {@link LockedItemGroup}
     * 
     * @see #getParents()
     * @see #addParent(ItemGroup)
     */
    public void removeParent(@Nonnull ItemGroup category) {
        parents.remove(category);
    }

    /**
     * Checks if the {@link Player} has fully unlocked all parent categories.
     * 
     * @param p
     *            The {@link Player} to check
     * @param profile
     *            The {@link PlayerProfile} that belongs to the given {@link Player}
     * 
     * @return Whether the {@link Player} has fully completed all parent categories, otherwise false
     */
    public boolean hasUnlocked(@Nonnull Player p, @Nonnull PlayerProfile profile) {
        Validate.notNull(p, "The player cannot be null!");
        Validate.notNull(profile, "The Profile cannot be null!");

        for (ItemGroup category : parents) {
            for (SlimefunItem item : category.getItems()) {
                // Check if the Player has researched every item (if the item is enabled)
                if (!item.isDisabledIn(p.getWorld()) && item.hasResearch() && !profile.hasUnlocked(item.getResearch())) {
                    return false;
                }
            }
        }

        return true;
    }
}
