package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import io.github.thebusybiscuit.slimefun4.api.events.ExplosiveToolBreakBlocksEvent;
import io.github.thebusybiscuit.slimefun4.api.network.Network;
import io.github.thebusybiscuit.slimefun4.core.networks.NetworkManager;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This {@link Listener} is responsible for all updates to a {@link Network}.
 * 
 * @author meiamsome
 * @author TheBusyBiscuit
 * 
 * @see Network
 * @see NetworkManager
 *
 */
public class NetworkListener implements Listener {

    /**
     * Our {@link NetworkManager} instance.
     */
    private final NetworkManager manager;

    public NetworkListener(@Nonnull Slimefun plugin, @Nonnull NetworkManager manager) {
        this.manager = manager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        manager.updateAllNetworks(e.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        manager.updateAllNetworks(e.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplosiveToolUse(ExplosiveToolBreakBlocksEvent e) {
        // Fixes #3013 - Also update networks when using an explosive tool
        for (Block b : e.getAdditionalBlocks()) {
            manager.updateAllNetworks(b.getLocation());
        }
    }
}
