package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.WeaponUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This {@link Listener} is responsible for calling the {@link WeaponUseHandler}.
 * 
 * @author Mooy1
 * 
 * @see WeaponUseHandler
 *
 */
public class SlimefunItemHitListener implements Listener {

    public SlimefunItemHitListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getDamager();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (!item.getType().isAir()) {
            SlimefunItem sfItem = SlimefunItem.getByItem(item);

            if (sfItem != null && sfItem.canUse(p, true)) {
                sfItem.callItemHandler(WeaponUseHandler.class, handler -> handler.onHit(e, p, item));
            }
        }
    }

}
