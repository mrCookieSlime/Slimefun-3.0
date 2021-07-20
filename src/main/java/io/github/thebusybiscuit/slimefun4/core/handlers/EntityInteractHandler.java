package io.github.thebusybiscuit.slimefun4.core.handlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.entity.EntityInteractionListener;

/**
 * This is triggered when a {@link Player} interacts with an {@link Entity}.
 *
 * @author Linox
 *
 * @see EntityInteractionListener
 * @see ItemHandler
 * @see SimpleSlimefunItem
 *
 */
@FunctionalInterface
public interface EntityInteractHandler extends ItemHandler {

    /**
     * This function is triggered when a {@link Player} right clicks with the assigned {@link SlimefunItem}
     * in his hand.
     *
     * @param e
     *            The {@link PlayerInteractAtEntityEvent} which was called
     * @param item
     *            The {@link ItemStack} that was held and used while triggering
     * @param offHand
     *            <code>true</code> if the {@link EquipmentSlot} is off hand
     */
    void onInteract(PlayerInteractEntityEvent e, ItemStack item, boolean offHand);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return EntityInteractHandler.class;
    }
}