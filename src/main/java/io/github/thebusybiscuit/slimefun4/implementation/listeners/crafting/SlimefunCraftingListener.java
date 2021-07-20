package io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting;

import javax.annotation.Nullable;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;

interface SlimefunCraftingListener extends Listener {

    default boolean hasUnallowedItems(@Nullable ItemStack item1, @Nullable ItemStack item2) {
        if (SlimefunGuide.isGuideItem(item1) || SlimefunGuide.isGuideItem(item2)) {
            return true;
        } else {
            SlimefunItem sfItem1 = SlimefunItem.getByItem(item1);
            SlimefunItem sfItem2 = SlimefunItem.getByItem(item2);
            return isUnallowed(sfItem1) || isUnallowed(sfItem2);
        }
    }

    default boolean isUnallowed(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        SlimefunItem sfItem = SlimefunItem.getByItem(item);
        return isUnallowed(sfItem);
    }

    default boolean isUnallowed(@Nullable SlimefunItem item) {
        return item != null && !(item instanceof VanillaItem) && !item.isDisabled();
    }

}
