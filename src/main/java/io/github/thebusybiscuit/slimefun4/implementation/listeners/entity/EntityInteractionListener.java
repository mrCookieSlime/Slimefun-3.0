package io.github.thebusybiscuit.slimefun4.implementation.listeners.entity;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * The {@link Listener} responsible for a {@link Player} interacting with an {@link Entity}.
 *
 * @author Linox
 * @author TheBusyBiscuit
 *
 * @see EntityInteractHandler
 *
 */
public class EntityInteractionListener implements Listener {

    public EntityInteractionListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (!e.getRightClicked().isValid()) {
            return;
        }

        ItemStack itemStack;

        if (e.getHand() == EquipmentSlot.OFF_HAND) {
            itemStack = e.getPlayer().getInventory().getItemInOffHand();
        } else {
            itemStack = e.getPlayer().getInventory().getItemInMainHand();
        }

        SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);

        if (sfItem != null) {
            if (sfItem.canUse(e.getPlayer(), true)) {
                sfItem.callItemHandler(EntityInteractHandler.class, handler -> handler.onInteract(e, itemStack, e.getHand() == EquipmentSlot.OFF_HAND));
            } else if (sfItem.getState() != ItemState.VANILLA_FALLBACK) {
                /*
                 * If an Item is disabled, we don't want it to fallback to the vanilla behaviour
                 * unless it is a Vanilla Item of course.
                 * Related to Issue #2446
                 */
                e.setCancelled(true);
            }
        }
    }
}