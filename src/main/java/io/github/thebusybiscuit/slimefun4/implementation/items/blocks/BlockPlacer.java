package io.github.thebusybiscuit.slimefun4.implementation.items.blocks;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.MaterialTagSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockDispenseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.VanillaInventoryDropHandler;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link BlockPlacer} is a machine which can place {@link Block Blocks}, as the name
 * would suggest.
 * It really just is a special type of {@link Dispenser} which places items instead of
 * shooting them.
 * 
 * @author TheBusyBiscuit
 * 
 * @see BlockPlacerPlaceEvent
 *
 */
public class BlockPlacer extends SlimefunItem {

    private final ItemSetting<List<String>> unplaceableBlocks = new MaterialTagSetting(this, "unplaceable-blocks", SlimefunTag.UNBREAKABLE_MATERIALS);

    @ParametersAreNonnullByDefault
    public BlockPlacer(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(unplaceableBlocks);

        addItemHandler(onPlace(), onBlockDispense());
        addItemHandler(new VanillaInventoryDropHandler<>(Dispenser.class));
    }

    @Nonnull
    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                Player p = e.getPlayer();
                Block b = e.getBlock();

                BlockStorage.addBlockInfo(b, "owner", p.getUniqueId().toString());
            }
        };
    }

    @Nonnull
    private BlockDispenseHandler onBlockDispense() {
        return (e, dispenser, facedBlock, machine) -> {
            if (!hasPermission(dispenser, facedBlock)) {
                e.setCancelled(true);
                return;
            }

            Material material = e.getItem().getType();

            if (SlimefunTag.SHULKER_BOXES.isTagged(material)) {
                /*
                 * Since vanilla Dispensers can already place Shulker boxes,
                 * we simply fallback to the vanilla behaviour.
                 */
                return;
            }

            e.setCancelled(true);

            if (facedBlock.isEmpty() && dispenser.getInventory().getViewers().isEmpty() && isAllowed(facedBlock, material)) {
                SlimefunItem item = SlimefunItem.getByItem(e.getItem());

                if (item != null) {
                    // Check if this Item can even be placed down
                    if (!(item instanceof NotPlaceable)) {
                        placeSlimefunBlock(item, e.getItem(), facedBlock, dispenser);
                    }
                } else {
                    placeBlock(e.getItem(), facedBlock, dispenser);
                }
            }
        };
    }

    /**
     * This checks whether the {@link Player} who placed down this {@link BlockPlacer} has
     * building permissions at that {@link Location}.
     * 
     * @param dispenser
     *            The {@link Dispenser} who represents our {@link BlockPlacer}
     * @param target
     *            The {@link Block} where it should be placed
     * 
     * @return Whether this action is permitted or not
     */
    @ParametersAreNonnullByDefault
    private boolean hasPermission(Dispenser dispenser, Block target) {
        String owner = BlockStorage.getLocationInfo(dispenser.getLocation(), "owner");

        if (owner == null) {
            /*
             * If no owner was set, then we will fallback to the previous behaviour:
             * Allowing block placers to bypass protection, newly placed Block placers
             * will respect protection plugins.
             */
            return true;
        }

        // Get the corresponding OfflinePlayer
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(owner));
        return Slimefun.getProtectionManager().hasPermission(player, target, Interaction.PLACE_BLOCK);
    }

    /**
     * This checks if the given {@link Material} is allowed to be placed.
     * 
     * @param type
     *            The {@link Material} to check
     * 
     * @return Whether placing this {@link Material} is allowed
     */
    private boolean isAllowed(@Nonnull Block facedBlock, @Nonnull Material type) {
        if (!type.isBlock()) {
            // Make sure the material is actually a block.
            return false;
        } else if (type == Material.CAKE) {
            /*
             * Special case for cakes.
             * Cakes are a lie but I really want the Block Placer to place them down!!!
             */
            return !facedBlock.getRelative(BlockFace.DOWN).isPassable();
        } else if (SlimefunTag.BLOCK_PLACER_IGNORED_MATERIALS.isTagged(type)) {
            /*
             * Some materials cannot be reliably placed, like beds,
             * it would look kinda wonky, so we just ignore these altogether.
             * The event has already been cancelled too, so they won't drop.
             */
            return false;
        } else {
            // Check for all unplaceable block
            for (String blockType : unplaceableBlocks.getValue()) {
                if (type.toString().equals(blockType)) {
                    return false;
                }
            }

            return true;
        }
    }

    @ParametersAreNonnullByDefault
    private void placeSlimefunBlock(SlimefunItem sfItem, ItemStack item, Block block, Dispenser dispenser) {
        BlockPlacerPlaceEvent e = new BlockPlacerPlaceEvent(dispenser.getBlock(), item, block);
        Bukkit.getPluginManager().callEvent(e);

        if (!e.isCancelled()) {
            boolean hasItemHandler = sfItem.callItemHandler(BlockPlaceHandler.class, handler -> {
                if (handler.isBlockPlacerAllowed()) {
                    schedulePlacement(block, dispenser.getInventory(), item, () -> {
                        block.setType(item.getType());
                        BlockStorage.store(block, sfItem.getId());

                        handler.onBlockPlacerPlace(e);
                    });
                }
            });

            if (!hasItemHandler) {
                schedulePlacement(block, dispenser.getInventory(), item, () -> {
                    block.setType(item.getType());
                    BlockStorage.store(block, sfItem.getId());
                });
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void placeBlock(ItemStack item, Block facedBlock, Dispenser dispenser) {
        BlockPlacerPlaceEvent e = new BlockPlacerPlaceEvent(dispenser.getBlock(), item, facedBlock);
        Bukkit.getPluginManager().callEvent(e);

        if (!e.isCancelled()) {
            schedulePlacement(facedBlock, dispenser.getInventory(), item, () -> {
                facedBlock.setType(item.getType());

                if (item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();

                    if (meta.hasDisplayName()) {
                        BlockStateSnapshotResult blockState = PaperLib.getBlockState(facedBlock, false);

                        if ((blockState.getState() instanceof Nameable)) {
                            Nameable nameable = ((Nameable) blockState.getState());
                            nameable.setCustomName(meta.getDisplayName());

                            if (blockState.isSnapshot()) {
                                // Update block state after changing name
                                blockState.getState().update(true, false);
                            }
                        }
                    }

                }
            });
        }
    }

    @ParametersAreNonnullByDefault
    private void schedulePlacement(Block b, Inventory inv, ItemStack item, Runnable runnable) {
        // We need to delay this due to Dispenser-Inventory synchronization issues in Spigot.
        Slimefun.runSync(() -> {
            // Make sure the Block has not been occupied yet
            if (b.isEmpty()) {
                // Only remove 1 item.
                ItemStack removedItem = item.clone();
                removedItem.setAmount(1);

                // Play particles
                b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, item.getType());

                // Make sure the item was actually removed (fixes #2817)

                try {
                    if (inv.removeItem(removedItem).isEmpty()) {
                        runnable.run();
                    }
                } catch (Exception x) {
                    error("An Exception was thrown while a BlockPlacer was performing its action", x);
                }
            }
        }, 2L);
    }
}
