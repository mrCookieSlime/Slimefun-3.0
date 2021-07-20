package io.github.thebusybiscuit.slimefun4.core.networks.cargo;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

class ItemStackAndInteger {

    private ItemStack item;
    private ItemStackWrapper wrapper;
    private int number;

    ItemStackAndInteger(@Nonnull ItemStack item, int amount) {
        Validate.notNull(item, "Item cannot be null!");
        this.number = amount;
        this.item = item;
    }

    public int getInt() {
        return number;
    }

    public @Nonnull ItemStack getItem() {
        initializeItem();
        return item;
    }

    public @Nonnull ItemStackWrapper getItemStackWrapper() {
        if (wrapper == null) {
            wrapper = ItemStackWrapper.wrap(item);
        }

        return wrapper;
    }

    public void add(int amount) {
        number += amount;
    }

    private void initializeItem() {
        if (this.item instanceof ItemStackWrapper) {
            ItemStack copy = new ItemStack(item.getType(), item.getAmount());
            if (this.item.hasItemMeta()) {
                copy.setItemMeta(this.item.getItemMeta());
            }
            this.item = copy;
        }
    }

}
