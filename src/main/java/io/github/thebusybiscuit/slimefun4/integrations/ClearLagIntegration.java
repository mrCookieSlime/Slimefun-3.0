package io.github.thebusybiscuit.slimefun4.integrations;

import java.util.Iterator;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.events.EntityRemoveEvent;

/**
 * This handles all integrations with {@link Clearlag}.
 * We don't want it to clear our altar items.
 * 
 * @author TheBusyBiscuit
 *
 */
class ClearLagIntegration implements Listener {

    private final Slimefun plugin;

    ClearLagIntegration(@Nonnull Slimefun plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveEvent e) {
        Iterator<Entity> iterator = e.getEntityList().iterator();

        while (iterator.hasNext()) {
            Entity n = iterator.next();

            if (n instanceof Item && SlimefunUtils.hasNoPickupFlag((Item) n)) {
                iterator.remove();
            }
        }
    }
}
