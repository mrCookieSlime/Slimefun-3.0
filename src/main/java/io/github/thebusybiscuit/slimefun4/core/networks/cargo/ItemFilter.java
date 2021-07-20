package io.github.thebusybiscuit.slimefun4.core.networks.cargo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.CargoNode;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * The {@link ItemFilter} is a performance-optimization for our {@link CargoNet}.
 * It is a snapshot of a cargo node's configuration.
 * 
 * @author TheBusyBiscuit
 * 
 * @see CargoNet
 * @see CargoNetworkTask
 *
 */
class ItemFilter implements Predicate<ItemStack> {

    /**
     * Our {@link List} of items to check against, might be empty.
     * This has a maximum capacity of 9.
     */
    private final List<ItemStackWrapper> items = new ArrayList<>(9);

    /**
     * Our default value for this {@link ItemFilter}.
     * A default value of {@literal true} will mean that it returns true if no
     * match was found. It will deny any items that match.
     * A default value of {@literal false} means that it will return false if no
     * match was found. Only items that match will make it past this {@link ItemFilter}.
     */
    private boolean rejectOnMatch;

    /**
     * Whether we should also compare the lore.
     */
    private boolean checkLore;

    /**
     * If an {@link ItemFilter} is marked as dirty / outdated, then it will be updated
     * on the next tick.
     */
    private boolean dirty = false;

    /**
     * This creates a new {@link ItemFilter} for the given {@link Block}.
     * This will copy all settings from that {@link Block} to this filter.
     * 
     * @param b
     *            The {@link Block}
     */
    public ItemFilter(@Nonnull Block b) {
        update(b);
    }

    /**
     * This updates or refreshes the {@link ItemFilter} to copy the settings
     * from the given {@link Block}. It takes a new snapshot.
     * 
     * @param b
     *            The {@link Block}
     */
    public void update(@Nonnull Block b) {
        // Store the returned Config instance to avoid heavy calls
        Config blockData = BlockStorage.getLocationInfo(b.getLocation());
        String id = blockData.getString("id");
        SlimefunItem item = SlimefunItem.getById(id);
        BlockMenu menu = BlockStorage.getInventory(b.getLocation());

        if (!(item instanceof CargoNode) || menu == null) {
            // Don't filter for a non-existing item (safety check)
            clear(false);
        } else {
            try {
                CargoNode node = (CargoNode) item;

                if (!node.hasItemFilter()) {
                    // Node does not have a filter, allow everything
                    clear(true);
                } else {
                    int[] slots = CargoUtils.getFilteringSlots();
                    int inventorySize = menu.toInventory().getSize();

                    if (inventorySize < slots[slots.length - 1]) {
                        /*
                         * Related to #2876
                         * The reason was a missing negation int he filtering statement above.
                         * However if that ever happens again, we will know the reason and be able
                         * to send a warning in response to it.
                         */
                        item.warn("Cargo Node was marked as a 'filtering' node but has an insufficient inventory size (" + inventorySize + ")");
                        return;
                    }

                    this.items.clear();
                    this.checkLore = Objects.equals(blockData.getString("filter-lore"), "true");
                    this.rejectOnMatch = !Objects.equals(blockData.getString("filter-type"), "whitelist");

                    for (int slot : slots) {
                        ItemStack stack = menu.getItemInSlot(slot);

                        if (stack != null && stack.getType() != Material.AIR) {
                            this.items.add(ItemStackWrapper.wrap(stack));
                        }
                    }
                }
            } catch (Exception | LinkageError x) {
                item.error("Something went wrong while updating the ItemFilter for this cargo node.", x);
            }
        }

        this.dirty = false;
    }

    /**
     * This will clear the {@link ItemFilter} and reject <strong>any</strong>
     * {@link ItemStack}.
     * 
     * @param rejectOnMatch
     *            Whether the item should be rejected on matches
     */
    private void clear(boolean rejectOnMatch) {
        this.items.clear();
        this.checkLore = false;
        this.rejectOnMatch = rejectOnMatch;
    }

    /**
     * Whether this {@link ItemFilter} is outdated and needs to be refreshed.
     * 
     * @return Whether the filter is outdated.
     */
    public boolean isDirty() {
        return this.dirty;
    }

    /**
     * This marks this {@link ItemFilter} as dirty / outdated.
     */
    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public boolean test(@Nonnull ItemStack item) {
        /**
         * An empty Filter does not need to be iterated over.
         * We can just return our default value in this scenario.
         */
        if (items.isEmpty()) {
            return rejectOnMatch;
        }

        // The amount of potential matches with that item.
        int potentialMatches = 0;

        /*
         * This is a first check for materials to see if we might even have any match.
         * If there is no potential match then we won't need to perform the quite
         * intense operation .getItemMeta()
         */
        for (ItemStackWrapper stack : items) {
            if (stack.getType() == item.getType()) {
                // We found a potential match based on the Material
                potentialMatches++;
            }
        }

        if (potentialMatches == 0) {
            // If there is no match, we can safely assume the default value
            return rejectOnMatch;
        } else {
            /*
             * If there is more than one potential match, create a wrapper to save
             * performance on the ItemMeta otherwise just use the item directly.
             */
            ItemStack subject = potentialMatches == 1 ? item : ItemStackWrapper.wrap(item);

            /*
             * If there is only one match, we won't need to create a Wrapper
             * and thus only perform .getItemMeta() once
             */
            for (ItemStackWrapper stack : items) {
                if (SlimefunUtils.isItemSimilar(subject, stack, checkLore, false)) {
                    /*
                     * The filter has found a match, we can return the opposite
                     * of our default value. If we exclude items, this is where we
                     * would return false. Otherwise we return true.
                     */
                    return !rejectOnMatch;
                }
            }

            // If no particular item was matched, we fallback to our default value.
            return rejectOnMatch;
        }
    }

}
