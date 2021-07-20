package io.github.thebusybiscuit.slimefun4.api.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * Represents a category, which structure multiple {@link SlimefunItem} in the {@link SlimefunGuide}.
 * 
 * @author TheBusyBiscuit
 *
 * @see LockedItemGroup
 * @see SeasonalItemGroup
 * 
 */
public class ItemGroup implements Keyed {

    private SlimefunAddon addon;

    protected final List<SlimefunItem> items = new ArrayList<>();
    protected final NamespacedKey key;
    protected final ItemStack item;
    protected int tier;

    /**
     * Constructs a new {@link ItemGroup} with the given {@link NamespacedKey} as an identifier
     * and the given {@link ItemStack} as its display item.
     * The tier is set to a default value of {@code 3}.
     * 
     * @param key
     *            The {@link NamespacedKey} that is used to identify this {@link ItemGroup}
     * @param item
     *            The {@link ItemStack} that is used to display this {@link ItemGroup}
     */
    @ParametersAreNonnullByDefault
    public ItemGroup(NamespacedKey key, ItemStack item) {
        this(key, item, 3);
    }

    /**
     * Constructs a new {@link ItemGroup} with the given {@link NamespacedKey} as an identifier
     * and the given {@link ItemStack} as its display item.
     * 
     * @param key
     *            The {@link NamespacedKey} that is used to identify this {@link ItemGroup}
     * @param item
     *            The {@link ItemStack} that is used to display this {@link ItemGroup}
     * @param tier
     *            The tier of this {@link ItemGroup}, higher tiers will make this {@link ItemGroup} appear further down
     *            in
     *            the {@link SlimefunGuide}
     */
    @ParametersAreNonnullByDefault
    public ItemGroup(NamespacedKey key, ItemStack item, int tier) {
        Validate.notNull(key, "A Category's NamespacedKey must not be null!");
        Validate.notNull(item, "A Category's ItemStack must not be null!");

        this.item = item;
        this.key = key;

        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        this.item.setItemMeta(meta);
        this.tier = tier;
    }

    @Override
    @Nonnull
    public final NamespacedKey getKey() {
        return key;
    }

    /**
     * Registers this category.
     * <p>
     * By default, a category is automatically registered when a {@link SlimefunItem} was added to it.
     * 
     * @param addon
     *            The {@link SlimefunAddon} that wants to register this {@link ItemGroup}
     */
    public void register(@Nonnull SlimefunAddon addon) {
        Validate.notNull(addon, "The Addon cannot be null");

        if (isRegistered()) {
            throw new UnsupportedOperationException("This Category has already been registered!");
        }

        this.addon = addon;

        Slimefun.getRegistry().getCategories().add(this);
        sortCategoriesByTier();
    }

    /**
     * Returns the tier of this {@link ItemGroup}.
     * The tier determines the position of this {@link ItemGroup} in the {@link SlimefunGuide}.
     * 
     * @return the tier of this {@link ItemGroup}
     */
    public int getTier() {
        return tier;
    }

    /**
     * This sets the tier of this {@link ItemGroup}.
     * The tier determines the position of this {@link ItemGroup} in the {@link SlimefunGuide}.
     * 
     * @param tier
     *            The tier for this {@link ItemGroup}
     */
    public void setTier(int tier) {
        this.tier = tier;

        // Refresh Category order if already registered.
        if (isRegistered()) {
            sortCategoriesByTier();
        }
    }

    /**
     * This refreshes the {@link ItemGroup} order.
     */
    private void sortCategoriesByTier() {
        List<ItemGroup> categories = Slimefun.getRegistry().getCategories();
        Collections.sort(categories, Comparator.comparingInt(ItemGroup::getTier));
    }

    /**
     * This returns the {@link SlimefunAddon} which has registered this {@link ItemGroup}.
     * Or null if it has not been registered yet.
     * 
     * @return The {@link SlimefunAddon} or null if unregistered
     */
    @Nullable
    public final SlimefunAddon getAddon() {
        return addon;
    }

    /**
     * Adds the given {@link SlimefunItem} to this {@link ItemGroup}.
     * 
     * @param item
     *            the {@link SlimefunItem} that should be added to this {@link ItemGroup}
     */
    public void add(@Nonnull SlimefunItem item) {
        Validate.notNull(item, "Cannot add null Items to a Category!");

        if (items.contains(item)) {
            // Ignore duplicate entries
            return;
        }

        items.add(item);
    }

    /**
     * Removes the given {@link SlimefunItem} from this {@link ItemGroup}.
     * 
     * @param item
     *            the {@link SlimefunItem} that should be removed from this {@link ItemGroup}
     */
    public void remove(@Nonnull SlimefunItem item) {
        Validate.notNull(item, "Cannot remove null from a Category!");
        items.remove(item);
    }

    /**
     * This method returns a localized display item of this {@link ItemGroup}
     * for the specified {@link Player}.
     * 
     * @param p
     *            The Player to create this {@link ItemStack} for
     * 
     * @return A localized display item for this {@link ItemGroup}
     */
    @Nonnull
    public ItemStack getItem(@Nonnull Player p) {
        return new CustomItemStack(item, meta -> {
            String name = Slimefun.getLocalization().getCategoryName(p, getKey());

            if (name == null) {
                name = item.getItemMeta().getDisplayName();
            }

            if (this instanceof SeasonalItemGroup) {
                meta.setDisplayName(ChatColor.GOLD + name);
            } else {
                meta.setDisplayName(ChatColor.YELLOW + name);
            }

            meta.setLore(Arrays.asList("", ChatColor.GRAY + "\u21E8 " + ChatColor.GREEN + Slimefun.getLocalization().getMessage(p, "guide.tooltips.open-category")));
        });
    }

    /**
     * This method makes Walshy happy.
     * It adds a way to get the name of a {@link ItemGroup} without localization nor coloring.
     * 
     * @return The unlocalized name of this {@link ItemGroup}
     */
    @Nonnull
    public String getUnlocalizedName() {
        return ChatColor.stripColor(item.getItemMeta().getDisplayName());
    }

    /**
     * This returns the localized display name of this {@link ItemGroup} for the given {@link Player}.
     * The method will fall back to {@link #getUnlocalizedName()} if no translation was found.
     * 
     * @param p
     *            The {@link Player} who to translate the name for
     * 
     * @return The localized name of this {@link ItemGroup}
     */
    @Nonnull
    public String getDisplayName(@Nonnull Player p) {
        String localized = Slimefun.getLocalization().getCategoryName(p, getKey());

        if (localized != null) {
            return localized;
        } else {
            return getUnlocalizedName();
        }
    }

    /**
     * Returns all instances of {@link SlimefunItem} bound to this {@link ItemGroup}.
     * 
     * @return the list of SlimefunItems bound to this category
     */
    @Nonnull
    public List<SlimefunItem> getItems() {
        return items;
    }

    /**
     * This method returns whether a given {@link SlimefunItem} exists in this {@link ItemGroup}.
     * 
     * @param item
     *            The {@link SlimefunItem} to find
     * 
     * @return Whether the given {@link SlimefunItem} was found in this {@link ItemGroup}
     */
    public boolean contains(@Nullable SlimefunItem item) {
        return item != null && items.contains(item);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof ItemGroup) {
            return ((ItemGroup) obj).getKey().equals(getKey());
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {" + key + ",tier=" + tier + "}";
    }

    /**
     * This method checks whether this {@link ItemGroup} will be hidden for the specified
     * {@link Player}.
     * 
     * Categories are hidden if all of their items have been disabled.
     * 
     * @param p
     *            The {@link Player} to check for
     * 
     * @return Whether this {@link ItemGroup} will be hidden to the given {@link Player}
     */
    public boolean isHidden(@Nonnull Player p) {
        for (SlimefunItem slimefunItem : getItems()) {
            if (!slimefunItem.isHidden() && !slimefunItem.isDisabledIn(p.getWorld())) {
                return false;
            }
        }

        return true;
    }

    /**
     * This method returns whether this {@link ItemGroup} has been registered yet.
     * More specifically: Whether {@link #register(SlimefunAddon)} was called or not.
     * 
     * @return Whether this {@link ItemGroup} has been registered
     */
    public boolean isRegistered() {
        return Slimefun.getRegistry().getCategories().contains(this);
    }

}
