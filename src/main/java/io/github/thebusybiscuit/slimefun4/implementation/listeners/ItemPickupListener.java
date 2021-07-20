package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

/**
 * Listens to the ItemPickup events to prevent it if the item has the "no_pickup" metadata or is an ALTAR_PROBE.
 *
 * @author TheBusyBiscuit
 */
public class ItemPickupListener implements Listener {

    public ItemPickupListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent e) {
        if (SlimefunUtils.hasNoPickupFlag(e.getItem())) {
            e.setCancelled(true);
        } else if (e.getItem().getItemStack().hasItemMeta()) {
            ItemMeta meta = e.getItem().getItemStack().getItemMeta();

            if (meta.hasDisplayName() && meta.getDisplayName().startsWith(AncientPedestal.ITEM_PREFIX)) {
                e.setCancelled(true);
                e.getItem().remove();
            }
        }
    }

    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent e) {
        if (SlimefunUtils.hasNoPickupFlag(e.getItem())) {
            e.setCancelled(true);
        } else if (e.getItem().getItemStack().hasItemMeta()) {
            ItemMeta meta = e.getItem().getItemStack().getItemMeta();

            if (meta.hasDisplayName() && meta.getDisplayName().startsWith(AncientPedestal.ITEM_PREFIX)) {
                e.setCancelled(true);
                e.getItem().remove();
            }
        }
    }
}
