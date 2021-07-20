package io.github.thebusybiscuit.slimefun4.core.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.DietCookie;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.FortuneCookie;

/**
 * This {@link ItemHandler} is triggered when the {@link SlimefunItem} it was assigned to
 * has been consumed.
 * 
 * This {@link ItemHandler} only works for food or potions.
 * 
 * @author TheBusyBiscuit
 *
 * @see ItemHandler
 * @see SimpleSlimefunItem
 * 
 * @see FortuneCookie
 * @see DietCookie
 * 
 */
@FunctionalInterface
public interface ItemConsumptionHandler extends ItemHandler {

    /**
     * This method gets fired whenever a {@link PlayerItemConsumeEvent} involving this
     * {@link SlimefunItem} has been triggered.
     * 
     * @param e
     *            The {@link PlayerItemConsumeEvent} that was fired
     * @param p
     *            The {@link Player} who consumed the given {@link ItemStack}
     * @param item
     *            The {@link ItemStack} that was consumed
     */
    void onConsume(PlayerItemConsumeEvent e, Player p, ItemStack item);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return ItemConsumptionHandler.class;
    }

}
