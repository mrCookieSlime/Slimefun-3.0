package io.github.thebusybiscuit.slimefun4.core.handlers;

import javax.annotation.Nonnull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * This is triggered when a {@link Player} attacks an {@link Entity}.
 *
 * @author Mooy1
 *
 */
@FunctionalInterface
public interface WeaponUseHandler extends ItemHandler {

    /**
     * This function is called when an {@link Player} attacks an {@link Entity} with a {@link SlimefunItem}
     *
     * @param e
     *            The {@link EntityDamageByEntityEvent} that was fired
     * @param player
     *            The {@link Player} that used the weapon
     * @param item
     *            The {@link ItemStack} that was used to attack
     */
    void onHit(@Nonnull EntityDamageByEntityEvent e, @Nonnull Player player, @Nonnull ItemStack item);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return WeaponUseHandler.class;
    }

}
