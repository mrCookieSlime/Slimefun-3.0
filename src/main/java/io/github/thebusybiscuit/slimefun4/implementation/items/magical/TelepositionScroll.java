package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * The {@link TelepositionScroll} is a magical {@link SlimefunItem} that makes nearby any {@link LivingEntity}
 * turn around by 180 degrees.
 * The radius is configurable.
 * 
 * @author TheBusyBiscuit
 *
 */
public class TelepositionScroll extends SimpleSlimefunItem<ItemUseHandler> {

    private final ItemSetting<Integer> radius = new IntRangeSetting(this, "radius", 1, 10, Integer.MAX_VALUE);

    @ParametersAreNonnullByDefault
    public TelepositionScroll(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(radius);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            int range = radius.getValue();

            for (Entity n : e.getPlayer().getNearbyEntities(range, range, range)) {
                if (n instanceof LivingEntity && !(n instanceof ArmorStand) && !n.getUniqueId().equals(e.getPlayer().getUniqueId())) {
                    float yaw = n.getLocation().getYaw() + 180F;

                    if (yaw > 360F) {
                        yaw = yaw - 360F;
                    }

                    n.teleport(new Location(n.getWorld(), n.getLocation().getX(), n.getLocation().getY(), n.getLocation().getZ(), yaw, n.getLocation().getPitch()));
                }
            }
        };
    }

}
