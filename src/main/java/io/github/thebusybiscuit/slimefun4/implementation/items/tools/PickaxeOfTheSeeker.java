package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

/**
 * The {@link PickaxeOfTheSeeker} will make you face the nearest ore upon right clicking.
 * 
 * @author TheBusyBiscuit
 *
 */
public class PickaxeOfTheSeeker extends SimpleSlimefunItem<ItemUseHandler> implements DamageableItem {

    private final ItemSetting<Integer> maxRange = new IntRangeSetting(this, "max-range", 1, 5, Integer.MAX_VALUE);

    @ParametersAreNonnullByDefault
    public PickaxeOfTheSeeker(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(maxRange);
    }

    @Override
    public @Nonnull ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();
            Block closest = findClosestOre(p);

            e.setUseBlock(Result.DENY);

            if (closest == null) {
                Slimefun.getLocalization().sendMessage(p, "messages.pickaxe-of-the-seeker.no-ores");
            } else {
                double l = closest.getX() + 0.5 - p.getLocation().getX();
                double w = closest.getZ() + 0.5 - p.getLocation().getZ();

                double c = Math.sqrt(l * l + w * w);
                float alpha1 = (float) -(Math.asin(l / c) / Math.PI * 180);
                float alpha2 = (float) (Math.acos(w / c) / Math.PI * 180);

                float yaw = alpha2 > 90 ? (180 - alpha1) : alpha1;
                float pitch = (float) ((-Math.atan((closest.getY() - 0.5 - p.getLocation().getY()) / Math.sqrt(l * l + w * w))) * 180 / Math.PI);

                // We could teleport them asynchronously here...
                // But we're only changing the pitch and yaw anyway.
                Location loc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), yaw, pitch);
                p.teleport(loc);
            }

            damageItem(p, e.getItem());
        };
    }

    private @Nullable Block findClosestOre(@Nonnull Player p) {
        Block start = p.getLocation().getBlock();
        Block closest = null;
        double lastDistance = Double.MAX_VALUE;
        int range = maxRange.getValue();

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    Block block = start.getRelative(x, y, z);

                    if (SlimefunTag.PICKAXE_OF_THE_SEEKER_BLOCKS.isTagged(block.getType())) {
                        double distance = block.getLocation().distanceSquared(start.getLocation());

                        if (closest == null || distance < lastDistance) {
                            closest = block;
                            lastDistance = distance;
                        }
                    }
                }
            }
        }

        return closest;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

}
