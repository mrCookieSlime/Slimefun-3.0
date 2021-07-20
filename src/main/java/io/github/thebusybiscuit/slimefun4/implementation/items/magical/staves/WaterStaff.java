package io.github.thebusybiscuit.slimefun4.implementation.items.magical.staves;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * The {@link WaterStaff} is a magical {@link SlimefunItem}.
 * When you right click it, any fire on your {@link Player} will be extinguished.
 * 
 * @author TheBusyBiscuit
 *
 */
public class WaterStaff extends SimpleSlimefunItem<ItemUseHandler> {

    @ParametersAreNonnullByDefault
    public WaterStaff(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();

            p.setFireTicks(0);
            Slimefun.getLocalization().sendMessage(p, "messages.fire-extinguish", true);
        };
    }

}
