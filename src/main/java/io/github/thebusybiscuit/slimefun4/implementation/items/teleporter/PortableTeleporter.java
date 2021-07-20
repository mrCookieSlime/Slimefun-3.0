package io.github.thebusybiscuit.slimefun4.implementation.items.teleporter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * This item allows a {@link Player} to access and teleport to his waypoints
 * from anywhere.
 *
 * @author martinbrom
 *
 * @see Teleporter
 */
public class PortableTeleporter extends SimpleSlimefunItem<ItemUseHandler> implements Rechargeable {

    private static final int CAPACITY = 50;
    private static final int DEFAULT_COST = 10;

    private final ItemSetting<Integer> cost = new IntRangeSetting(this, "teleportation-cost", 0, DEFAULT_COST, CAPACITY);

    @ParametersAreNonnullByDefault
    public PortableTeleporter(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(cost);
    }

    @Nonnull
    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            ItemStack item = e.getItem();
            e.cancel();

            if (removeItemCharge(item, cost.getValue())) {
                Player p = e.getPlayer();
                Slimefun.getGPSNetwork().getTeleportationManager().openTeleporterGUI(p, p.getUniqueId(), p.getLocation().getBlock().getRelative(BlockFace.DOWN));
            }
        };
    }

    @Override
    public float getMaxItemCharge(ItemStack item) {
        return CAPACITY;
    }
}
