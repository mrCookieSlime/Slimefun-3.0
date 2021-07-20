package io.github.thebusybiscuit.slimefun4.implementation.items;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.staves.StormStaff;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;

/**
 * This class represents an item with a limited number of uses.
 * When the item runs out of "uses", it breaks.
 *
 * @author Linox
 * @author Walshy
 * @author TheBusyBiscuit
 * @author martinbrom
 *
 * @see StormStaff
 */
public abstract class LimitedUseItem extends SimpleSlimefunItem<ItemUseHandler> {

    private final NamespacedKey defaultUsageKey;
    private int maxUseCount = -1;

    @ParametersAreNonnullByDefault
    protected LimitedUseItem(ItemGroup group, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(group, item, recipeType, recipe);

        this.defaultUsageKey = new NamespacedKey(Slimefun.instance(), "uses_left");
        addItemHandler(getItemHandler());
    }

    /**
     * Returns the number of times this item can be used.
     *
     * @return The number of times this item can be used.
     */
    public final int getMaxUseCount() {
        return maxUseCount;
    }

    /**
     * Sets the maximum number of times this item can be used.
     * The number must be greater than zero.
     *
     * @param count
     *            The maximum number of times this item can be used.
     * @return The {@link LimitedUseItem} for chaining of setters
     */
    public final @Nonnull LimitedUseItem setMaxUseCount(int count) {
        Validate.isTrue(count > 0, "The maximum use count must be greater than zero!");

        maxUseCount = count;
        return this;
    }

    /**
     * Returns the {@link NamespacedKey} under which will the amount of uses left stored.
     *
     * @return The {@link NamespacedKey} to store/load the amount of uses
     */
    protected @Nonnull NamespacedKey getStorageKey() {
        return defaultUsageKey;
    }

    @Override
    public void register(@Nonnull SlimefunAddon addon) {
        if (getMaxUseCount() < 1) {
            warn("The use count has not been configured correctly. It needs to be at least 1. The Item was disabled.");
        } else {
            super.register(addon);
        }
    }

    @ParametersAreNonnullByDefault
    protected void damageItem(Player p, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);

            // Separate one item from the stack and damage it
            ItemStack separateItem = item.clone();
            separateItem.setAmount(1);
            damageItem(p, separateItem);

            // Try to give the Player the new item
            if (!p.getInventory().addItem(separateItem).isEmpty()) {
                // or throw it on the ground
                p.getWorld().dropItemNaturally(p.getLocation(), separateItem);
            }
        } else {
            ItemMeta meta = item.getItemMeta();
            NamespacedKey key = getStorageKey();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            int usesLeft = pdc.getOrDefault(key, PersistentDataType.INTEGER, getMaxUseCount());

            if (usesLeft == 1) {
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                item.setAmount(0);
                item.setType(Material.AIR);
            } else {
                usesLeft--;
                pdc.set(key, PersistentDataType.INTEGER, usesLeft);

                updateItemLore(item, meta, usesLeft);
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void updateItemLore(ItemStack item, ItemMeta meta, int usesLeft) {
        List<String> lore = meta.getLore();

        String newLine = ChatColors.color(LoreBuilder.usesLeft(usesLeft));
        if (lore != null && !lore.isEmpty()) {
            // find the correct line
            for (int i = 0; i < lore.size(); i++) {
                if (PatternUtils.USES_LEFT_LORE.matcher(lore.get(i)).matches()) {
                    lore.set(i, newLine);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    return;
                }
            }
        } else {
            meta.setLore(Collections.singletonList(newLine));
            item.setItemMeta(meta);
        }
    }

}
