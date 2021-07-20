package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.blocks.Vein;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.AdvancedMenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;

/**
 * This machine collects liquids from the {@link World} and puts them
 * into buckets provided to the machine by using energy.
 *
 * @author TheBusyBiscuit
 * @author Linox
 *
 */
public class FluidPump extends SimpleSlimefunItem<BlockTicker> implements InventoryBlock, EnergyNetComponent {

    private static final int ENERGY_CONSUMPTION = 32;
    private static final int RANGE = 42;

    private final int[] border = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 13, 31, 36, 37, 38, 39, 40, 41, 42, 43, 44, 22 };
    private final int[] inputBorder = { 9, 10, 11, 12, 18, 21, 27, 28, 29, 30 };
    private final int[] outputBorder = { 14, 15, 16, 17, 23, 26, 32, 33, 34, 35 };

    private final ItemStack emptyBucket = ItemStackWrapper.wrap(new ItemStack(Material.BUCKET));

    @ParametersAreNonnullByDefault
    public FluidPump(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(onBreak());
        createPreset(this, this::constructMenu);
    }

    @Nonnull
    private BlockBreakHandler onBreak() {
        return new SimpleBlockBreakHandler() {

            @Override
            public void onBlockBreak(Block b) {
                BlockMenu inv = BlockStorage.getInventory(b);

                if (inv != null) {
                    inv.dropItems(b.getLocation(), getInputSlots());
                    inv.dropItems(b.getLocation(), getOutputSlots());
                }
            }
        };
    }

    private void constructMenu(@Nonnull BlockMenuPreset preset) {
        for (int i : border) {
            preset.addItem(i, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : inputBorder) {
            preset.addItem(i, ChestMenuUtils.getInputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }
        for (int i : outputBorder) {
            preset.addItem(i, ChestMenuUtils.getOutputSlotTexture(), ChestMenuUtils.getEmptyClickHandler());
        }

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new AdvancedMenuClickHandler() {

                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                    return cursor == null || cursor.getType() == null || cursor.getType() == Material.AIR;
                }
            });
        }
    }

    @Override
    public int[] getInputSlots() {
        return new int[] { 19, 20 };
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] { 24, 25 };
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return 512;
    }

    protected void tick(@Nonnull Block b) {
        Block fluid = b.getRelative(BlockFace.DOWN);

        if (fluid.isLiquid() && getCharge(b.getLocation()) >= ENERGY_CONSUMPTION) {
            BlockMenu menu = BlockStorage.getInventory(b);

            for (int slot : getInputSlots()) {
                if (SlimefunUtils.isItemSimilar(menu.getItemInSlot(slot), emptyBucket, true, false)) {
                    ItemStack bucket = getFilledBucket(fluid);

                    if (!menu.fits(bucket, getOutputSlots())) {
                        return;
                    }

                    Block nextFluid = findNextFluid(fluid);

                    if (nextFluid != null) {
                        removeCharge(b.getLocation(), ENERGY_CONSUMPTION);
                        menu.consumeItem(slot);
                        menu.pushItem(bucket, getOutputSlots());
                        nextFluid.setType(Material.AIR);
                    }

                    return;
                }
            }
        }
    }

    @Nullable
    private Block findNextFluid(@Nonnull Block fluid) {
        if (fluid.getType() == Material.WATER || fluid.getType() == Material.BUBBLE_COLUMN) {
            /**
             * With water we can be sure to find an infinite source whenever we
             * go further than a block, so we can just remove the water here and
             * save ourselves all of that computing...
             */
            if (isSource(fluid)) {
                return fluid;
            }
        } else if (fluid.getType() == Material.LAVA) {
            List<Block> list = Vein.find(fluid, RANGE, block -> block.getType() == fluid.getType());

            for (int i = list.size() - 1; i >= 0; i--) {
                Block block = list.get(i);

                if (isSource(block)) {
                    return block;
                }
            }
        }

        return null;
    }

    @Nonnull
    private ItemStack getFilledBucket(@Nonnull Block fluid) {
        switch (fluid.getType()) {
            case LAVA:
                return new ItemStack(Material.LAVA_BUCKET);
            case WATER:
            case BUBBLE_COLUMN:
                return new ItemStack(Material.WATER_BUCKET);
            default:
                // Fallback for any new liquids
                return new ItemStack(Material.BUCKET);
        }
    }

    /**
     * This method checks if the given {@link Block} is a liquid source {@link Block}.
     * 
     * @param block
     *            The {@link Block} in question
     * 
     * @return Whether that {@link Block} is a liquid and a source {@link Block}.
     */
    private boolean isSource(@Nonnull Block block) {
        if (block.isLiquid()) {
            BlockData data = block.getBlockData();

            if (data instanceof Levelled) {
                // Check if this is a full block.
                Levelled levelled = (Levelled) data;
                return levelled.getLevel() == 0;
            }
        }

        return false;
    }

    @Override
    public BlockTicker getItemHandler() {
        return new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                FluidPump.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        };
    }
}
