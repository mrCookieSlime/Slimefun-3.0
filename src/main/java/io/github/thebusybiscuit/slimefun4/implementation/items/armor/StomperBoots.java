package io.github.thebusybiscuit.slimefun4.implementation.items.armor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * The Boots of the Stomper are boots which damage nearby enemies whenever the {@link Player}
 * takes fall damage.
 *
 * @author TheBusyBiscuit
 *
 */
public class StomperBoots extends SlimefunItem {

    @ParametersAreNonnullByDefault
    public StomperBoots(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    /**
     * This will apply the "stomp" effect to the given {@link EntityDamageEvent}.
     *
     * @param fallDamageEvent
     *            The {@link EntityDamageEvent} in which the {@link Player} has taken fall damage
     */
    public void stomp(@Nonnull EntityDamageEvent fallDamageEvent) {
        Player player = (Player) fallDamageEvent.getEntity();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1F, 2F);
        player.setVelocity(new Vector(0, 0.7, 0));

        for (Entity entity : player.getNearbyEntities(4, 4, 4)) {
            if (entity instanceof LivingEntity && canPush(player, (LivingEntity) entity)) {
                Vector velocity = getShockwave(player.getLocation(), entity.getLocation());
                entity.setVelocity(velocity);

                // Check if it's not a Player or if PvP is enabled
                if (!(entity instanceof Player) || (player.getWorld().getPVP() && Slimefun.getProtectionManager().hasPermission(player, entity.getLocation(), Interaction.ATTACK_PLAYER))) {
                    EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(player, entity, DamageCause.ENTITY_ATTACK, fallDamageEvent.getDamage() / 2);
                    Bukkit.getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        ((LivingEntity) entity).damage(event.getDamage());
                    }
                }
            }
        }

        for (BlockFace face : BlockFace.values()) {
            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getRelative(face);
            player.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
        }
    }

    /**
     * This gives us the "shockwave" {@link Vector} for a given target.
     *
     * @param origin
     *            The {@link Location} of our {@link Player}
     * @param target
     *            The {@link Location} of the {@link Entity} we are pushing away
     *
     * @return A {@link Vector} to determine the velocity for our {@link Entity}
     */
    @Nonnull
    protected Vector getShockwave(@Nonnull Location origin, @Nonnull Location target) {
        // As the distance approaches zero we might slip into a "division by zero" when normalizing
        if (origin.distanceSquared(target) < 0.05) {
            return new Vector(0, 1, 0);
        } else {
            Vector direction = target.toVector().subtract(origin.toVector());
            return direction.normalize().multiply(1.4);
        }
    }

    /**
     * Checks if the stomper boots can move an entity and is not the player who is using the boots.
     * <p>
     * <b>For developers:</b> If you're spawning an immovable NPC, you should be denying
     * collision with {@link LivingEntity#setCollidable(boolean)} or
     * gravity with {@link LivingEntity#setGravity(boolean)}.
     *
     * @param entity
     *            The {@link LivingEntity} to check.
     * @param player
     *            The {@link Player} using the {@link StomperBoots}.
     * @return If the entity can move.
     */
    protected boolean canPush(@Nonnull Player player, @Nonnull LivingEntity entity) {
        return entity.isValid() && !entity.getUniqueId().equals(player.getUniqueId()) && entity.isCollidable() && entity.hasGravity();
    }
}
