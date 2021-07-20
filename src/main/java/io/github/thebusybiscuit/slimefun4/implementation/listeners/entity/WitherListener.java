package io.github.thebusybiscuit.slimefun4.implementation.listeners.entity;

import javax.annotation.Nonnull;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.WitherProof;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * This {@link Listener} is responsible for implementing the functionality of blocks that
 * were marked as {@link WitherProof} to not be destroyed by a {@link Wither}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see WitherProof
 *
 */
public class WitherListener implements Listener {

    public WitherListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onWitherDestroy(EntityChangeBlockEvent e) {
        if (e.getEntity().getType() == EntityType.WITHER) {
            SlimefunItem item = BlockStorage.check(e.getBlock());

            // Hardened Glass is excluded from here
            if (item instanceof WitherProof && !item.getId().equals(SlimefunItems.HARDENED_GLASS.getItemId())) {
                e.setCancelled(true);
                ((WitherProof) item).onAttack(e.getBlock(), (Wither) e.getEntity());
            }
        }
    }

}
