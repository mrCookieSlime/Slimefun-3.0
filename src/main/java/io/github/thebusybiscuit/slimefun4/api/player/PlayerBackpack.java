package io.github.thebusybiscuit.slimefun4.api.player;

import java.io.File;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.BackpackListener;

/**
 * This class represents the instance of a {@link SlimefunBackpack} that is ready to
 * be opened.
 * 
 * It holds an actual {@link Inventory} and represents the backpack on the
 * level of an individual {@link ItemStack} as opposed to the class {@link SlimefunBackpack}.
 * 
 * @author TheBusyBiscuit
 *
 * @see SlimefunBackpack
 * @see BackpackListener
 */
public class PlayerBackpack {

    private static final String CONFIG_PREFIX = "backpacks.";

    private final PlayerProfile profile;
    private final int id;
    private final Config cfg;

    private Inventory inventory;
    private int size;

    /**
     * This constructor loads an existing Backpack
     * 
     * @param profile
     *            The {@link PlayerProfile} of this Backpack
     * @param id
     *            The id of this Backpack
     */
    public PlayerBackpack(@Nonnull PlayerProfile profile, int id) {
        this(profile, id, profile.getConfig().getInt(CONFIG_PREFIX + id + ".size"));

        for (int i = 0; i < size; i++) {
            inventory.setItem(i, cfg.getItem(CONFIG_PREFIX + id + ".contents." + i));
        }
    }

    /**
     * This constructor creates a new Backpack
     * 
     * @param profile
     *            The {@link PlayerProfile} of this Backpack
     * @param id
     *            The id of this Backpack
     * @param size
     *            The size of this Backpack
     */
    public PlayerBackpack(@Nonnull PlayerProfile profile, int id, int size) {
        if (size < 9 || size > 54 || size % 9 != 0) {
            throw new IllegalArgumentException("Invalid size! Size must be one of: [9, 18, 27, 36, 45, 54]");
        }

        this.profile = profile;
        this.id = id;
        this.cfg = profile.getConfig();
        this.size = size;

        cfg.setValue(CONFIG_PREFIX + id + ".size", size);
        markDirty();

        inventory = Bukkit.createInventory(null, size, "Backpack [" + size + " Slots]");
    }

    /**
     * This returns the id of this {@link PlayerBackpack}
     * 
     * @return The id of this {@link PlayerBackpack}
     */
    public int getId() {
        return id;
    }

    /**
     * This method returns the {@link PlayerProfile} this {@link PlayerBackpack} belongs to
     * 
     * @return The owning {@link PlayerProfile}
     */
    @Nonnull
    public PlayerProfile getOwner() {
        return profile;
    }

    /**
     * This returns the size of this {@link PlayerBackpack}.
     * 
     * @return The size of this {@link PlayerBackpack}
     */
    public int getSize() {
        return size;
    }

    /**
     * This method returns the {@link Inventory} of this {@link PlayerBackpack}
     * 
     * @return The {@link Inventory} of this {@link PlayerBackpack}
     */
    @Nonnull
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * This will open the {@link Inventory} of this backpack to every {@link Player}
     * that was passed onto this method.
     * 
     * @param players
     *            The players who this Backpack will be shown to
     */
    public void open(Player... players) {
        Slimefun.runSync(() -> {
            for (Player p : players) {
                p.openInventory(inventory);
            }
        });
    }

    /**
     * This will change the current size of this Backpack to the specified size.
     * 
     * @param size
     *            The new size for this Backpack
     */
    public void setSize(int size) {
        if (size < 9 || size > 54 || size % 9 != 0) {
            throw new IllegalArgumentException("Invalid size! Size must be one of: [9, 18, 27, 36, 45, 54]");
        }

        this.size = size;
        cfg.setValue(CONFIG_PREFIX + id + ".size", size);

        Inventory inv = Bukkit.createInventory(null, size, "Backpack [" + size + " Slots]");

        for (int slot = 0; slot < this.inventory.getSize(); slot++) {
            inv.setItem(slot, this.inventory.getItem(slot));
        }

        this.inventory = inv;

        markDirty();
    }

    /**
     * This method will save the contents of this backpack to a {@link File}.
     */
    public void save() {
        for (int i = 0; i < size; i++) {
            cfg.setValue(CONFIG_PREFIX + id + ".contents." + i, inventory.getItem(i));
        }
    }

    /**
     * This method marks the backpack dirty, it will then be queued for an autosave
     * using {@link PlayerBackpack#save()}
     */
    public void markDirty() {
        profile.markDirty();
    }

}
