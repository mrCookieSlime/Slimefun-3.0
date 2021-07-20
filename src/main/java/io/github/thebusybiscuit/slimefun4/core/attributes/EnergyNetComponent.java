package io.github.thebusybiscuit.slimefun4.core.attributes;

import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNet;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.Capacitor;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * This Interface, when attached to a class that inherits from {@link SlimefunItem}, marks
 * the Item as an electric Block.
 * This will make this Block interact with an {@link EnergyNet}.
 * 
 * You can specify the Type of Block via {@link EnergyNetComponent#getEnergyComponentType()}.
 * You can also specify a capacity for this Block via {@link EnergyNetComponent#getCapacity()}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see EnergyNetComponentType
 * @see EnergyNet
 *
 */
public interface EnergyNetComponent extends ItemAttribute {

    /**
     * This method returns the Type of {@link EnergyNetComponentType} this {@link SlimefunItem} represents.
     * It describes how this Block will interact with an {@link EnergyNet}.
     * 
     * @return The {@link EnergyNetComponentType} this {@link SlimefunItem} represents.
     */
    @Nonnull
    EnergyNetComponentType getEnergyComponentType();

    /**
     * This method returns the max amount of electricity this Block can hold.
     * If the capacity is zero, then this Block cannot hold any electricity.
     * 
     * @return The max amount of electricity this Block can store.
     */
    int getCapacity();

    /**
     * This returns whether this {@link EnergyNetComponent} can hold energy charges.
     * It returns true if {@link #getCapacity()} returns a number greater than zero.
     * 
     * @return Whether this {@link EnergyNetComponent} can store energy.
     */
    default boolean isChargeable() {
        return getCapacity() > 0;
    }

    /**
     * This returns the currently stored charge at a given {@link Location}.
     * 
     * @param l
     *            The target {@link Location}
     * 
     * @return The charge stored at that {@link Location}
     */
    default int getCharge(@Nonnull Location l) {
        // Emergency fallback, this cannot hold a charge, so we'll just return zero
        if (!isChargeable()) {
            return 0;
        }

        return getCharge(l, BlockStorage.getLocationInfo(l));
    }

    /**
     * This returns the currently stored charge at a given {@link Location}.
     * This is a more performance saving option if you already have a {@link Config}
     * object for this {@link Location}.
     * 
     * @param l
     *            The target {@link Location}
     * @param data
     *            The data at this {@link Location}
     * 
     * @return The charge stored at that {@link Location}
     */
    default int getCharge(@Nonnull Location l, @Nonnull Config data) {
        Validate.notNull(l, "Location was null!");
        Validate.notNull(data, "data was null!");

        // Emergency fallback, this cannot hold a charge, so we'll just return zero
        if (!isChargeable()) {
            return 0;
        }

        String charge = data.getString("energy-charge");

        if (charge != null) {
            return Integer.parseInt(charge);
        } else {
            return 0;
        }
    }

    /**
     * This method sets the charge which is stored at a given {@link Location}
     * If this {@link EnergyNetComponent} is of type {@code EnergyNetComponentType.CAPACITOR}, then
     * this method will automatically update the texture of this {@link Capacitor} as well.
     * 
     * @param l
     *            The target {@link Location}
     * @param charge
     *            The new charge
     */
    default void setCharge(@Nonnull Location l, int charge) {
        Validate.notNull(l, "Location was null!");
        Validate.isTrue(charge >= 0, "You can only set a charge of zero or more!");

        try {
            int capacity = getCapacity();

            // This method only makes sense if we can actually store energy
            if (capacity > 0) {
                charge = NumberUtils.clamp(0, charge, capacity);

                // Do we even need to update the value?
                if (charge != getCharge(l)) {
                    BlockStorage.addBlockInfo(l, "energy-charge", String.valueOf(charge), false);

                    // Update the capacitor texture
                    if (getEnergyComponentType() == EnergyNetComponentType.CAPACITOR) {
                        SlimefunUtils.updateCapacitorTexture(l, charge, capacity);
                    }
                }
            }
        } catch (Exception | LinkageError x) {
            Slimefun.logger().log(Level.SEVERE, x, () -> "Exception while trying to set the energy-charge for \"" + getId() + "\" at " + new BlockPosition(l));
        }
    }

    default void addCharge(@Nonnull Location l, int charge) {
        Validate.notNull(l, "Location was null!");
        Validate.isTrue(charge > 0, "You can only add a positive charge!");

        try {
            int capacity = getCapacity();

            // This method only makes sense if we can actually store energy
            if (capacity > 0) {
                int currentCharge = getCharge(l);

                // Check if there is even space for new energy
                if (currentCharge < capacity) {
                    int newCharge = Math.min(capacity, currentCharge + charge);
                    BlockStorage.addBlockInfo(l, "energy-charge", String.valueOf(newCharge), false);

                    // Update the capacitor texture
                    if (getEnergyComponentType() == EnergyNetComponentType.CAPACITOR) {
                        SlimefunUtils.updateCapacitorTexture(l, charge, capacity);
                    }
                }
            }
        } catch (Exception | LinkageError x) {
            Slimefun.logger().log(Level.SEVERE, x, () -> "Exception while trying to add an energy-charge for \"" + getId() + "\" at " + new BlockPosition(l));
        }
    }

    default void removeCharge(@Nonnull Location l, int charge) {
        Validate.notNull(l, "Location was null!");
        Validate.isTrue(charge > 0, "The charge to remove must be greater than zero!");

        try {
            int capacity = getCapacity();

            // This method only makes sense if we can actually store energy
            if (capacity > 0) {
                int currentCharge = getCharge(l);

                // Check if there is even energy stored
                if (currentCharge > 0) {
                    int newCharge = Math.max(0, currentCharge - charge);
                    BlockStorage.addBlockInfo(l, "energy-charge", String.valueOf(newCharge), false);

                    // Update the capacitor texture
                    if (getEnergyComponentType() == EnergyNetComponentType.CAPACITOR) {
                        SlimefunUtils.updateCapacitorTexture(l, charge, capacity);
                    }
                }
            }
        } catch (Exception | LinkageError x) {
            Slimefun.logger().log(Level.SEVERE, x, () -> "Exception while trying to remove an energy-charge for \"" + getId() + "\" at " + new BlockPosition(l));
        }
    }

}
