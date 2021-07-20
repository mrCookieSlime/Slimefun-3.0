package io.github.thebusybiscuit.slimefun4.core.handlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * If this {@link ItemHandler} is added to a {@link SlimefunItem} it will listen
 * for any {@link EntityDeathEvent} that was triggered by a {@link Player} using
 * the {@link SlimefunItem} this {@link EntityKillHandler} was linked to.
 * 
 * @author TheBusyBiscuit
 *
 * @see ItemHandler
 * @see SimpleSlimefunItem
 * 
 */
@FunctionalInterface
public interface EntityKillHandler extends ItemHandler {

    void onKill(EntityDeathEvent e, Entity entity, Player killer, ItemStack item);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return EntityKillHandler.class;
    }
}
