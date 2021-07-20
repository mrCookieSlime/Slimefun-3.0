package io.github.thebusybiscuit.slimefun4.core.networks.cargo;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import io.papermc.lib.PaperLib;

import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;

/**
 * This is a helper class for the {@link CargoNet} which provides
 * a free static utility methods to let the {@link CargoNet} interact with
 * an {@link Inventory} or {@link BlockMenu}.
 * 
 * @author TheBusyBiscuit
 * @author Walshy
 * @author DNx5
 *
 */
final class CargoUtils {

    /**
     * These are the slots where our filter items sit.
     */
    private static final int[] FILTER_SLOTS = { 19, 20, 21, 28, 29, 30, 37, 38, 39 };

    /**
     * This is a utility class and should not be instantiated.
     * Therefore we just hide the public constructor.
     */
    private CargoUtils() {}

    /**
     * This is a performance-saving shortcut to quickly test whether a given
     * {@link Block} might be an {@link InventoryHolder} or not.
     * 
     * @param block
     *            The {@link Block} to check
     * 
     * @return Whether this {@link Block} represents a {@link BlockState} that is an {@link InventoryHolder}
     */
    static boolean hasInventory(@Nullable Block block) {
        if (block == null) {
            // No block, no inventory
            return false;
        }

        Material type = block.getType();

        switch (type) {
            case CHEST:
            case TRAPPED_CHEST:
            case FURNACE:
            case DISPENSER:
            case DROPPER:
            case HOPPER:
            case BREWING_STAND:
            case BARREL:
            case BLAST_FURNACE:
            case SMOKER:
                return true;
            default:
                return SlimefunTag.SHULKER_BOXES.isTagged(type);
        }
    }

    @Nonnull
    static int[] getInputSlotRange(@Nonnull Inventory inv, @Nullable ItemStack item) {
        if (inv instanceof FurnaceInventory) {
            if (item != null && item.getType().isFuel()) {
                if (isSmeltable(item, true)) {
                    // Any non-smeltable items should not land in the upper slot
                    return new int[] { 0, 2 };
                } else {
                    return new int[] { 1, 2 };
                }
            } else {
                return new int[] { 0, 1 };
            }
        } else if (inv instanceof BrewerInventory) {
            if (isPotion(item)) {
                // Slots for potions
                return new int[] { 0, 3 };
            } else if (item != null && item.getType() == Material.BLAZE_POWDER) {
                // Blaze Powder slot
                return new int[] { 4, 5 };
            } else {
                // Input slot
                return new int[] { 3, 4 };
            }
        } else {
            // Slot 0-size
            return new int[] { 0, inv.getSize() };
        }
    }

    @Nonnull
    static int[] getOutputSlotRange(@Nonnull Inventory inv) {
        if (inv instanceof FurnaceInventory) {
            // Slot 2-3
            return new int[] { 2, 3 };
        } else if (inv instanceof BrewerInventory) {
            // Slot 0-3
            return new int[] { 0, 3 };
        } else {
            // Slot 0-size
            return new int[] { 0, inv.getSize() };
        }
    }

    @Nullable
    static ItemStack withdraw(AbstractItemNetwork network, Map<Location, Inventory> inventories, Block node, Block target, ItemStack template) {
        DirtyChestMenu menu = getChestMenu(target);

        if (menu == null) {
            if (hasInventory(target)) {
                Inventory inventory = inventories.get(target.getLocation());

                if (inventory != null) {
                    return withdrawFromVanillaInventory(network, node, template, inventory);
                }

                BlockState state = PaperLib.getBlockState(target, false).getState();

                if (state instanceof InventoryHolder) {
                    inventory = ((InventoryHolder) state).getInventory();
                    inventories.put(target.getLocation(), inventory);
                    return withdrawFromVanillaInventory(network, node, template, inventory);
                }
            }

            return null;
        }

        ItemStackWrapper wrapperTemplate = ItemStackWrapper.wrap(template);

        for (int slot : menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.WITHDRAW, null)) {
            ItemStack is = menu.getItemInSlot(slot);
            ItemStackWrapper wrapperItemInSlot = ItemStackWrapper.wrap(is);

            if (SlimefunUtils.isItemSimilar(wrapperItemInSlot, wrapperTemplate, true) && matchesFilter(network, node, wrapperItemInSlot)) {
                if (is.getAmount() > template.getAmount()) {
                    is.setAmount(is.getAmount() - template.getAmount());
                    menu.replaceExistingItem(slot, is);
                    return template;
                } else {
                    menu.replaceExistingItem(slot, null);
                    return is;
                }
            }
        }

        return null;
    }

    @Nullable
    static ItemStack withdrawFromVanillaInventory(AbstractItemNetwork network, Block node, ItemStack template, Inventory inv) {
        ItemStack[] contents = inv.getContents();
        int[] range = getOutputSlotRange(inv);
        int minSlot = range[0];
        int maxSlot = range[1];

        ItemStackWrapper wrapper = ItemStackWrapper.wrap(template);

        for (int slot = minSlot; slot < maxSlot; slot++) {
            // Changes to these ItemStacks are synchronized with the Item in the Inventory
            ItemStack itemInSlot = contents[slot];
            if (itemInSlot == null || itemInSlot.getType().isAir()) {
                continue;
            }

            ItemStackWrapper wrapperInSlot = ItemStackWrapper.wrap(itemInSlot);
            if (SlimefunUtils.isItemSimilar(wrapperInSlot, wrapper, true, false) && matchesFilter(network, node, wrapperInSlot)) {
                if (itemInSlot.getAmount() > template.getAmount()) {
                    itemInSlot.setAmount(itemInSlot.getAmount() - template.getAmount());
                    return template;
                } else {
                    ItemStack clone = itemInSlot.clone();
                    itemInSlot.setAmount(0);
                    return clone;
                }
            }
        }

        return null;
    }

    @Nullable
    static ItemStackAndInteger withdraw(AbstractItemNetwork network, Map<Location, Inventory> inventories, Block node, Block target) {
        DirtyChestMenu menu = getChestMenu(target);

        if (menu != null) {
            for (int slot : menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.WITHDRAW, null)) {
                ItemStack is = menu.getItemInSlot(slot);

                if (matchesFilter(network, node, is)) {
                    menu.replaceExistingItem(slot, null);
                    return new ItemStackAndInteger(is, slot);
                }
            }
        } else if (hasInventory(target)) {
            Inventory inventory = inventories.get(target.getLocation());

            if (inventory != null) {
                return withdrawFromVanillaInventory(network, node, inventory);
            }

            BlockState state = PaperLib.getBlockState(target, false).getState();

            if (state instanceof InventoryHolder) {
                inventory = ((InventoryHolder) state).getInventory();
                inventories.put(target.getLocation(), inventory);
                return withdrawFromVanillaInventory(network, node, inventory);
            }
        }

        return null;
    }

    @Nullable
    private static ItemStackAndInteger withdrawFromVanillaInventory(AbstractItemNetwork network, Block node, Inventory inv) {
        ItemStack[] contents = inv.getContents();
        int[] range = getOutputSlotRange(inv);
        int minSlot = range[0];
        int maxSlot = range[1];

        for (int slot = minSlot; slot < maxSlot; slot++) {
            ItemStack item = contents[slot];

            if (matchesFilter(network, node, item)) {
                inv.setItem(slot, null);
                return new ItemStackAndInteger(item, slot);
            }
        }

        return null;
    }

    @Nullable
    static ItemStack insert(AbstractItemNetwork network, Map<Location, Inventory> inventories, Block node, Block target, boolean smartFill, ItemStack stack, ItemStackWrapper wrapper) {
        if (!matchesFilter(network, node, stack)) {
            return stack;
        }

        DirtyChestMenu menu = getChestMenu(target);

        if (menu == null) {
            if (hasInventory(target)) {
                Inventory inventory = inventories.get(target.getLocation());

                if (inventory != null) {
                    return insertIntoVanillaInventory(stack, wrapper, smartFill, inventory);
                }

                BlockState state = PaperLib.getBlockState(target, false).getState();

                if (state instanceof InventoryHolder) {
                    inventory = ((InventoryHolder) state).getInventory();
                    inventories.put(target.getLocation(), inventory);
                    return insertIntoVanillaInventory(stack, wrapper, smartFill, inventory);
                }
            }

            return stack;
        }

        for (int slot : menu.getPreset().getSlotsAccessedByItemTransport(menu, ItemTransportFlow.INSERT, wrapper)) {
            ItemStack itemInSlot = menu.getItemInSlot(slot);

            if (itemInSlot == null) {
                menu.replaceExistingItem(slot, stack);
                return null;
            }

            int maxStackSize = itemInSlot.getType().getMaxStackSize();
            int currentAmount = itemInSlot.getAmount();

            if (!smartFill && currentAmount == maxStackSize) {
                // Skip full stacks - Performance optimization for non-smartfill nodes
                continue;
            }

            if (SlimefunUtils.isItemSimilar(itemInSlot, wrapper, true, false)) {
                if (currentAmount < maxStackSize) {
                    int amount = currentAmount + stack.getAmount();

                    itemInSlot.setAmount(Math.min(amount, maxStackSize));
                    if (amount > maxStackSize) {
                        stack.setAmount(amount - maxStackSize);
                    } else {
                        stack = null;
                    }

                    menu.replaceExistingItem(slot, itemInSlot);
                    return stack;
                } else if (smartFill) {
                    return stack;
                }
            }
        }

        return stack;
    }

    @Nullable
    private static ItemStack insertIntoVanillaInventory(@Nonnull ItemStack stack, @Nonnull ItemStackWrapper wrapper, boolean smartFill, @Nonnull Inventory inv) {
        /*
         * If the Inventory does not accept this Item Type, bounce the item back.
         * Example: Shulker boxes within shulker boxes (fixes #2662)
         */
        if (!InvUtils.isItemAllowed(stack.getType(), inv.getType())) {
            return stack;
        }

        ItemStack[] contents = inv.getContents();
        int[] range = getInputSlotRange(inv, stack);
        int minSlot = range[0];
        int maxSlot = range[1];

        for (int slot = minSlot; slot < maxSlot; slot++) {
            // Changes to this ItemStack are synchronized with the Item in the Inventory
            ItemStack itemInSlot = contents[slot];

            if (itemInSlot == null) {
                inv.setItem(slot, stack);
                return null;
            } else {
                int currentAmount = itemInSlot.getAmount();
                int maxStackSize = itemInSlot.getType().getMaxStackSize();

                if (!smartFill && currentAmount == maxStackSize) {
                    // Skip full stacks - Performance optimization for non-smartfill nodes
                    continue;
                }

                if (SlimefunUtils.isItemSimilar(itemInSlot, wrapper, true, false)) {
                    if (currentAmount < maxStackSize) {
                        int amount = currentAmount + stack.getAmount();

                        if (amount > maxStackSize) {
                            stack.setAmount(amount - maxStackSize);
                            itemInSlot.setAmount(maxStackSize);
                            return stack;
                        } else {
                            itemInSlot.setAmount(Math.min(amount, maxStackSize));
                            return null;
                        }
                    } else if (smartFill) {
                        return stack;
                    }
                }
            }
        }

        return stack;
    }

    @Nullable
    static DirtyChestMenu getChestMenu(@Nonnull Block block) {
        if (BlockStorage.hasInventory(block)) {
            return BlockStorage.getInventory(block);
        } else {
            return BlockStorage.getUniversalInventory(block);
        }
    }

    static boolean matchesFilter(@Nonnull AbstractItemNetwork network, @Nonnull Block node, @Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        return network.getItemFilter(node).test(item);
    }

    /**
     * This method checks if a given {@link ItemStack} is smeltable or not.
     * The lazy-option is a performance-saver since actually calculating this can be quite expensive.
     * For the current applicational purposes a quick check for any wooden logs is sufficient.
     * Otherwise the "lazyness" can be turned off in the future.
     * 
     * @param stack
     *            The {@link ItemStack} to test
     * @param lazy
     *            Whether or not to perform a "lazy" but performance-saving check
     * 
     * @return Whether the given {@link ItemStack} can be smelted or not
     */
    private static boolean isSmeltable(@Nullable ItemStack stack, boolean lazy) {
        if (lazy) {
            return stack != null && Tag.LOGS.isTagged(stack.getType());
        } else {
            return Slimefun.getMinecraftRecipeService().isSmeltable(stack);
        }
    }

    private static boolean isPotion(@Nullable ItemStack item) {
        if (item != null) {
            Material type = item.getType();
            return type == Material.POTION || type == Material.SPLASH_POTION || type == Material.LINGERING_POTION;
        } else {
            return false;
        }
    }

    /**
     * Gets the {@link ItemFilter} slots for a Cargo Node. If you wish to access the items
     * in the cargo (without hardcoding the slots in case of change) then you can use this method.
     *
     * @return The slots where the {@link ItemFilter} section for a cargo node sits
     */
    @Nonnull
    public static int[] getFilteringSlots() {
        return FILTER_SLOTS;
    }
}
