package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This {@link Listener} is responsible for handling the {@link ItemConsumptionHandler}
 * for any {@link SlimefunItem}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class SlimefunItemConsumeListener implements Listener {

    public SlimefunItemConsumeListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem != null) {
            if (sfItem.canUse(p, true)) {
                sfItem.callItemHandler(ItemConsumptionHandler.class, handler -> handler.onConsume(e, p, item));
            } else {
                e.setCancelled(true);
            }
        }
    }
}
