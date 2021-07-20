package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link BlockListener} is responsible for listening to the {@link BlockPlaceEvent}
 * and {@link BlockBreakEvent}.
 *
 * @author TheBusyBiscuit
 * @author Linox
 * @author Patbox
 *
 * @see BlockPlaceHandler
 * @see BlockBreakHandler
 * @see ToolUseHandler
 *
 */
public class BlockListener implements Listener {

    public BlockListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlaceExisting(BlockPlaceEvent e) {
        Block block = e.getBlock();

        // Fixes #2636 - This will solve the "ghost blocks" issue
        if (e.getBlockReplacedState().getType().isAir()) {
            SlimefunItem sfItem = BlockStorage.check(block);

            if (sfItem != null && !Slimefun.getTickerTask().isDeletedSoon(block.getLocation())) {
                for (ItemStack item : sfItem.getDrops()) {
                    if (item != null && !item.getType().isAir()) {
                        block.getWorld().dropItemNaturally(block.getLocation(), item);
                    }
                }

                BlockStorage.clearBlockInfo(block);
            }
        } else if (BlockStorage.hasBlockInfo(e.getBlock())) {
            // If there is no air (e.g. grass) then don't let the block be placed
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        SlimefunItem sfItem = SlimefunItem.getByItem(item);

        if (sfItem != null && !(sfItem instanceof NotPlaceable)) {
            if (!sfItem.canUse(e.getPlayer(), true)) {
                e.setCancelled(true);
            } else {
                if (Slimefun.getBlockDataService().isTileEntity(e.getBlock().getType())) {
                    Slimefun.getBlockDataService().setBlockData(e.getBlock(), sfItem.getId());
                }

                BlockStorage.addBlockInfo(e.getBlock(), "id", sfItem.getId(), true);
                sfItem.callItemHandler(BlockPlaceHandler.class, handler -> handler.onPlayerPlace(e));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        // Simply ignore any events that were faked by other plugins
        if (Slimefun.getIntegrations().isEventFaked(e)) {
            return;
        }

        // Also ignore custom blocks which were placed by other plugins
        if (Slimefun.getIntegrations().isCustomBlock(e.getBlock())) {
            return;
        }

        // Ignore blocks which we have marked as deleted (Fixes #2771)
        if (Slimefun.getTickerTask().isDeletedSoon(e.getBlock().getLocation())) {
            return;
        }

        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        checkForSensitiveBlockAbove(e, item);

        int fortune = getBonusDropsWithFortune(item, e.getBlock());
        List<ItemStack> drops = new ArrayList<>();

        if (!e.isCancelled() && !item.getType().isAir()) {
            callToolHandler(e, item, fortune, drops);
        }

        if (!e.isCancelled()) {
            callBlockHandler(e, item, drops);
        }

        dropItems(e, drops);
    }

    @ParametersAreNonnullByDefault
    private void callToolHandler(BlockBreakEvent e, ItemStack item, int fortune, List<ItemStack> drops) {
        SlimefunItem tool = SlimefunItem.getByItem(item);

        if (tool != null) {
            if (tool.canUse(e.getPlayer(), true)) {
                tool.callItemHandler(ToolUseHandler.class, handler -> handler.onToolUse(e, item, fortune, drops));
            } else {
                e.setCancelled(true);
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void callBlockHandler(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
        SlimefunItem sfItem = BlockStorage.check(e.getBlock());

        if (sfItem == null && Slimefun.getBlockDataService().isTileEntity(e.getBlock().getType())) {
            Optional<String> blockData = Slimefun.getBlockDataService().getBlockData(e.getBlock());

            if (blockData.isPresent()) {
                sfItem = SlimefunItem.getById(blockData.get());
            }
        }

        if (sfItem != null && !sfItem.useVanillaBlockBreaking()) {
            sfItem.callItemHandler(BlockBreakHandler.class, handler -> handler.onPlayerBreak(e, item, drops));

            if (e.isCancelled()) {
                return;
            }

            drops.addAll(sfItem.getDrops());
            BlockStorage.clearBlockInfo(e.getBlock());
        }
    }

    @ParametersAreNonnullByDefault
    private void dropItems(BlockBreakEvent e, List<ItemStack> drops) {
        if (!drops.isEmpty() && !e.isCancelled()) {
            // Notify plugins like CoreProtect
            Slimefun.getProtectionManager().logAction(e.getPlayer(), e.getBlock(), Interaction.BREAK_BLOCK);

            // Fixes #2560
            if (e.isDropItems()) {
                // Disable normal block drops
                e.setDropItems(false);

                for (ItemStack drop : drops) {
                    // Prevent null or air from being dropped
                    if (drop != null && drop.getType() != Material.AIR) {
                        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
                    }
                }
            }
        }
    }

    /**
     * This method checks for a sensitive {@link Block}.
     * Sensitive {@link Block Blocks} are pressure plates or saplings, which should be broken
     * when the block beneath is broken as well.
     *
     * @param p
     *            The {@link Player} who broke this {@link Block}
     * @param b
     *            The {@link Block} that was broken
     */
    @ParametersAreNonnullByDefault
    private void checkForSensitiveBlockAbove(BlockBreakEvent e, ItemStack item) {
        Block blockAbove = e.getBlock().getRelative(BlockFace.UP);

        if (SlimefunTag.SENSITIVE_MATERIALS.isTagged(blockAbove.getType())) {
            SlimefunItem sfItem = BlockStorage.check(blockAbove);

            if (sfItem != null && !sfItem.useVanillaBlockBreaking()) {
                /*
                 * We create a dummy here to pass onto the BlockBreakHandler.
                 * This will set the correct block context.
                 */
                BlockBreakEvent dummyEvent = new BlockBreakEvent(blockAbove, e.getPlayer());
                List<ItemStack> drops = new ArrayList<>();
                drops.addAll(sfItem.getDrops(e.getPlayer()));

                sfItem.callItemHandler(BlockBreakHandler.class, handler -> handler.onPlayerBreak(dummyEvent, item, drops));
                blockAbove.setType(Material.AIR);

                if (!dummyEvent.isCancelled() && dummyEvent.isDropItems()) {
                    for (ItemStack drop : drops) {
                        if (drop != null && !drop.getType().isAir()) {
                            blockAbove.getWorld().dropItemNaturally(blockAbove.getLocation(), drop);
                        }
                    }
                }

                // Fixes #2944 - Don't forget to clear the Block Data
                BlockStorage.clearBlockInfo(blockAbove);
            }
        }
    }

    private int getBonusDropsWithFortune(@Nullable ItemStack item, @Nonnull Block b) {
        int amount = 1;

        if (item != null && !item.getType().isAir() && item.hasItemMeta()) {
            /*
             * Small performance optimization:
             * ItemStack#getEnchantmentLevel() calls ItemStack#getItemMeta(), so if
             * we are handling more than one Enchantment, we should access the ItemMeta
             * directly and re use it.
             */
            ItemMeta meta = item.getItemMeta();
            int fortuneLevel = meta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);

            if (fortuneLevel > 0 && !meta.hasEnchant(Enchantment.SILK_TOUCH)) {
                Random random = ThreadLocalRandom.current();

                amount = Math.max(1, random.nextInt(fortuneLevel + 2) - 1);
                amount = (b.getType() == Material.LAPIS_ORE ? 4 + random.nextInt(5) : 1) * (amount + 1);
            }
        }

        return amount;
    }
}
