package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Piston;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * This {@link Listener} is responsible for listening to any physics-based events, such
 * as {@link EntityChangeBlockEvent} or a {@link BlockPistonEvent}.
 * 
 * This ensures that a {@link Piston} cannot be abused to break Slimefun blocks.
 * 
 * @author VoidAngel
 * @author Poslovitch
 * @author TheBusyBiscuit
 * @author AccelShark
 *
 */
public class BlockPhysicsListener implements Listener {

    public BlockPhysicsListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFall(EntityChangeBlockEvent e) {
        if (e.getEntity().getType() == EntityType.FALLING_BLOCK && BlockStorage.hasBlockInfo(e.getBlock())) {
            e.setCancelled(true);
            FallingBlock block = (FallingBlock) e.getEntity();

            if (block.getDropItem()) {
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getBlockData().getMaterial(), 1));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent e) {
        if (BlockStorage.hasBlockInfo(e.getBlock())) {
            e.setCancelled(true);
        } else {
            for (Block b : e.getBlocks()) {
                if (BlockStorage.hasBlockInfo(b) || (b.getRelative(e.getDirection()).getType() == Material.AIR && BlockStorage.hasBlockInfo(b.getRelative(e.getDirection())))) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent e) {
        if (BlockStorage.hasBlockInfo(e.getBlock())) {
            e.setCancelled(true);
        } else if (e.isSticky()) {
            for (Block b : e.getBlocks()) {
                if (BlockStorage.hasBlockInfo(b) || (b.getRelative(e.getDirection()).getType() == Material.AIR && BlockStorage.hasBlockInfo(b.getRelative(e.getDirection())))) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLiquidFlow(BlockFromToEvent e) {
        Block block = e.getToBlock();
        Material type = block.getType();

        // Check if this Material can be destroyed by fluids
        if (SlimefunTag.FLUID_SENSITIVE_MATERIALS.isTagged(type)) {
            // Check if this Block holds any data
            if (BlockStorage.hasBlockInfo(block)) {
                e.setCancelled(true);
            } else {
                Location loc = block.getLocation();

                // Fixes #2496 - Make sure it is not a moving block
                if (Slimefun.getTickerTask().isOccupiedSoon(loc)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBucketUse(PlayerBucketEmptyEvent e) {
        // Fix for placing water on player heads
        Location l = e.getBlockClicked().getRelative(e.getBlockFace()).getLocation();

        if (BlockStorage.hasBlockInfo(l)) {
            e.setCancelled(true);
        }
    }
}
