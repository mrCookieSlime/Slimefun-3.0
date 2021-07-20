package me.mrCookieSlime.Slimefun.api.inventory;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;

// This class will be deprecated, relocated and rewritten in a future version.
public abstract class BlockMenuPreset extends ChestMenu {

    private final Set<Integer> occupiedSlots = new HashSet<>();
    private final String inventoryTitle;
    private final String id;

    // -1 means "automatically update according to the contents"
    private int size = -1;

    private final boolean universal;
    private boolean locked;

    protected BlockMenuPreset(@Nonnull String id, @Nonnull String title) {
        this(id, title, false);
    }

    protected BlockMenuPreset(@Nonnull String id, @Nonnull String title, boolean universal) {
        super(title);

        Validate.notNull(id, "You need to specify an id!");

        this.id = id;
        this.inventoryTitle = title;
        this.universal = universal;
        init();

        Slimefun.getRegistry().getMenuPresets().put(id, this);
    }

    private void checkIfLocked() {
        if (locked) {
            throw new UnsupportedOperationException("You cannot modify the BlockMenuPreset anymore, modify the individual instances instead.");
        }
    }

    public abstract void init();

    /**
     * This method returns whether a given {@link Player} is allowed to open the
     * {@link BlockMenu} of that {@link Block}.
     * Override this as necessary.
     * 
     * @param b
     *            The {@link Block} trying to be opened
     * @param p
     *            The {@link Player} who wants to open the {@link BlockMenu}
     * 
     * @return Whether that {@link Player} is allowed
     */
    public abstract boolean canOpen(@Nonnull Block b, @Nonnull Player p);

    public abstract int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow);

    /**
     * This method is called whenever an {@link ItemStack} changes.
     * You can override this as necessary if you need to listen to these events
     * 
     * @param menu
     *            The {@link Inventory} affected by this
     * @param slot
     *            The affected slot
     * @param previous
     *            The {@link ItemStack} in that slot before the operation
     * @param next
     *            The {@link ItemStack} that it changes to
     * 
     * @return The new outcome of this operation
     */
    @Nullable
    protected ItemStack onItemStackChange(@Nonnull DirtyChestMenu menu, int slot, @Nullable ItemStack previous, @Nullable ItemStack next) {
        // Override this as necessary
        return next;
    }

    public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
        // This method can optionally be overridden by implementations
    }

    public int[] getSlotsAccessedByItemTransport(DirtyChestMenu menu, ItemTransportFlow flow, ItemStack item) {
        // This method will default to that method, it can be overridden by subclasses though
        return getSlotsAccessedByItemTransport(flow);
    }

    @Override
    public void replaceExistingItem(int slot, ItemStack item) {
        throw new UnsupportedOperationException("BlockMenuPreset does not support this method.");
    }

    /**
     * This method will draw unclickable background items into this {@link BlockMenuPreset}.
     * 
     * @param item
     *            The {@link ItemStack} that should be used as background
     * @param slots
     *            The slots which should be treated as background
     */
    public void drawBackground(@Nonnull ItemStack item, @Nonnull int[] slots) {
        Validate.notNull(item, "The background item cannot be null!");
        checkIfLocked();

        for (int slot : slots) {
            addItem(slot, item, ChestMenuUtils.getEmptyClickHandler());
        }
    }

    /**
     * This method will draw unclickable background items into this {@link BlockMenuPreset}.
     * 
     * @param slots
     *            The slots which should be treated as background
     */
    public void drawBackground(@Nonnull int[] slots) {
        drawBackground(ChestMenuUtils.getBackground(), slots);
    }

    @Override
    public ChestMenu addItem(int slot, @Nullable ItemStack item) {
        checkIfLocked();

        occupiedSlots.add(slot);
        return super.addItem(slot, item);
    }

    @Override
    public ChestMenu addMenuClickHandler(int slot, MenuClickHandler handler) {
        checkIfLocked();
        return super.addMenuClickHandler(slot, handler);
    }

    @Nonnull
    public ChestMenu setSize(int size) {
        checkIfLocked();

        if (size % 9 == 0 && size >= 0 && size < 55) {
            this.size = size;
            return this;
        } else {
            throw new IllegalArgumentException("The size of a BlockMenuPreset must be a multiple of 9 and within the bounds 0-54, received: " + size);
        }
    }

    /**
     * This method returns the size of this {@link BlockMenuPreset}.
     * If the size has not been determined yet, this will return -1.
     * 
     * @return The size of this {@link BlockMenuPreset}
     */
    public int getSize() {
        return size;
    }

    private boolean isSizeAutomaticallyInferred() {
        return size == -1;
    }

    /**
     * This returns the title of this {@link BlockMenuPreset}, the title will
     * be visible in every {@link InventoryView} for any menu created using this {@link BlockMenuPreset}.
     * 
     * @return The inventory title for this {@link BlockMenuPreset}
     */
    public String getTitle() {
        return inventoryTitle;
    }

    /**
     * This method returns whether this {@link BlockMenuPreset} will spawn a {@link UniversalBlockMenu}.
     * 
     * @return Whether this {@link BlockMenuPreset} is universal
     */
    public boolean isUniversal() {
        return universal;
    }

    @Nonnull
    public Set<Integer> getPresetSlots() {
        return occupiedSlots;
    }

    @Nonnull
    public Set<Integer> getInventorySlots() {
        Set<Integer> emptySlots = new HashSet<>();

        if (isSizeAutomaticallyInferred()) {
            for (int i = 0; i < toInventory().getSize(); i++) {
                if (!occupiedSlots.contains(i)) {
                    emptySlots.add(i);
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (!occupiedSlots.contains(i)) {
                    emptySlots.add(i);
                }
            }
        }

        return emptySlots;
    }

    protected void clone(@Nonnull DirtyChestMenu menu) {
        menu.setPlayerInventoryClickable(true);

        for (int slot : occupiedSlots) {
            menu.addItem(slot, getItemInSlot(slot));
        }

        if (size > -1) {
            menu.addItem(size - 1, null);
        }

        if (menu instanceof BlockMenu) {
            BlockMenu blockMenu = (BlockMenu) menu;
            newInstance(blockMenu, blockMenu.getLocation());
        }

        for (int slot = 0; slot < 54; slot++) {
            if (getMenuClickHandler(slot) != null) {
                menu.addMenuClickHandler(slot, getMenuClickHandler(slot));
            }
        }

        menu.addMenuOpeningHandler(getMenuOpeningHandler());
        menu.addMenuCloseHandler(getMenuCloseHandler());
    }

    public void newInstance(@Nonnull BlockMenu menu, @Nonnull Location l) {
        Validate.notNull(l, "Cannot create a new BlockMenu without a Location");

        Slimefun.runSync(() -> {
            locked = true;

            try {
                newInstance(menu, l.getBlock());
            } catch (Exception | LinkageError x) {
                getSlimefunItem().error("An Error occurred while trying to create a BlockMenu", x);
            }
        });
    }

    /**
     * This returns the id of the associated {@link SlimefunItem}.
     * It also doubles as the id for this {@link BlockMenuPreset}.
     * 
     * @return Our identifier
     */

    @Nonnull
    public String getID() {
        return id;
    }

    /**
     * This returns the {@link SlimefunItem} associated with this {@link BlockMenuPreset}.
     * 
     * @return The associated {@link SlimefunItem}
     */

    @Nonnull
    public SlimefunItem getSlimefunItem() {
        return SlimefunItem.getById(id);
    }

    @Nullable
    public static BlockMenuPreset getPreset(@Nullable String id) {
        return id == null ? null : Slimefun.getRegistry().getMenuPresets().get(id);
    }

    public static boolean isInventory(String id) {
        return Slimefun.getRegistry().getMenuPresets().containsKey(id);
    }

    public static boolean isUniversalInventory(String id) {
        BlockMenuPreset preset = Slimefun.getRegistry().getMenuPresets().get(id);
        return preset != null && preset.isUniversal();
    }

}
