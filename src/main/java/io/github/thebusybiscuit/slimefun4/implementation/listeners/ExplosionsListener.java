package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.WitherProof;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link ExplosionsListener} is a {@link Listener} which listens to any explosion events.
 * Any {@link WitherProof} block is excluded from these explosions and this {@link Listener} also
 * calls the explosive part of the {@link BlockBreakHandler}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see BlockBreakHandler
 * @see WitherProof
 *
 */
public class ExplosionsListener implements Listener {

    public ExplosionsListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        removeResistantBlocks(e.blockList().iterator());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {
        removeResistantBlocks(e.blockList().iterator());
    }

    private void removeResistantBlocks(@Nonnull Iterator<Block> blocks) {
        while (blocks.hasNext()) {
            Block block = blocks.next();
            SlimefunItem item = BlockStorage.check(block);

            if (item != null) {
                blocks.remove();

                if (!(item instanceof WitherProof) && !item.callItemHandler(BlockBreakHandler.class, handler -> handleExplosion(handler, block))) {
                    BlockStorage.clearBlockInfo(block);
                    block.setType(Material.AIR);
                }
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void handleExplosion(BlockBreakHandler handler, Block block) {
        if (handler.isExplosionAllowed(block)) {
            BlockStorage.clearBlockInfo(block);
            block.setType(Material.AIR);

            List<ItemStack> drops = new ArrayList<>();
            handler.onExplode(block, drops);

            for (ItemStack drop : drops) {
                if (drop != null && !drop.getType().isAir()) {
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                }
            }
        }
    }
}
