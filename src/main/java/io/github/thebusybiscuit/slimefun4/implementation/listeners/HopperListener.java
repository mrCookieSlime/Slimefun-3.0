package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

import io.github.thebusybiscuit.slimefun4.core.attributes.NotHopperable;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * This {@link Listener} prevents item from being transferred to
 * and from {@link AContainer} using a hopper.
 *
 * @author CURVX
 *
 * @see NotHopperable
 *
 */
public class HopperListener implements Listener {

    public HopperListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onHopperInsert(InventoryMoveItemEvent e) {
        Location loc = e.getDestination().getLocation();

        if (loc != null && e.getSource().getType() == InventoryType.HOPPER && BlockStorage.check(loc) instanceof NotHopperable) {
            e.setCancelled(true);
        }
    }
}
