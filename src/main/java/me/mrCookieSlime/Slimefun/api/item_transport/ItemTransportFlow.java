package me.mrCookieSlime.Slimefun.api.item_transport;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.networks.cargo.CargoNet;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;

/**
 * This enum represents the direction of an {@link ItemTransportFlow}.
 * This is used in cases to segregate between ingoing and outgoing flows.
 * At the moment there are just these two states.
 * 
 * @author TheBusyBiscuit
 * 
 * @see CargoNet
 * @see BlockMenuPreset
 *
 */
public enum ItemTransportFlow {

    /**
     * This state represents an {@link ItemStack} being inserted into
     * an {@link Inventory}.
     */
    INSERT,

    /**
     * This state represents an {@link ItemStack} being withdrawn
     * from an {@link Inventory}.
     */
    WITHDRAW;

}
