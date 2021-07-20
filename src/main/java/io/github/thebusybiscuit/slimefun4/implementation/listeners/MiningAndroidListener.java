package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun4.api.events.AndroidMineEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * This {@link Listener} makes sure that an {@link AndroidMineEvent} gets properly propagated
 * to the {@link BlockBreakHandler#onAndroidBreak(AndroidMineEvent)} method of a placed block.
 * If that block is a {@link SlimefunItem} of course.
 * 
 * @author TheBusyBiscuit
 * 
 * @see BlockBreakHandler
 *
 */
public class MiningAndroidListener implements Listener {

    public MiningAndroidListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAndroidMine(AndroidMineEvent e) {
        SlimefunItem slimefunItem = BlockStorage.check(e.getBlock());

        // Fixes #2839 - Can't believe we forgot a null check here
        if (slimefunItem != null) {
            slimefunItem.callItemHandler(BlockBreakHandler.class, handler -> {
                if (handler.isAndroidAllowed(e.getBlock())) {
                    handler.onAndroidBreak(e);
                } else {
                    e.setCancelled(true);
                }
            });
        }
    }
}
