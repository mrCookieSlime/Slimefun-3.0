package io.github.thebusybiscuit.slimefun4.core.attributes;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.BasicCircuitBoard;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.entity.MobDropListener;

/**
 * This interface, when attached to a {@link SlimefunItem}, provides an easy method for adding
 * a % chance to drop for an {@link SlimefunItem} on {@link EntityDeathEvent}, this chance is 0-100
 * and used in conjunction with the {@link RecipeType#MOB_DROP}.
 * 
 * @author dNiym
 *
 * @see BasicCircuitBoard
 * @see MobDropListener
 * @see PiglinBarterDrop
 * 
 */
public interface RandomMobDrop extends ItemAttribute {

    /**
     * Implement this method to make the object have a variable chance of being
     * added to the dropList when {@link EntityType} specified in
     * the {@link RecipeType#MOB_DROP} is killed by the {@link Player}.
     * 
     * @return The integer chance (0-100%) {@link SlimefunItem} has to drop.
     */
    int getMobDropChance();

}
