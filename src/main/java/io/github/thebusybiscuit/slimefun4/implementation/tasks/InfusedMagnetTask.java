package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import javax.annotation.Nonnull;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.InfusedMagnet;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

/**
 * This {@link AbstractPlayerTask} is run when a {@link Player} carries an {@link InfusedMagnet}.
 * It manages the automatic pickup of nearby items.
 * 
 * @author TheBusyBiscuit
 * 
 * @see InfusedMagnet
 *
 */
public class InfusedMagnetTask extends AbstractPlayerTask {

    /**
     * The radius in which an {@link Item} is picked up.
     */
    private final double radius;

    /**
     * This creates a new {@link InfusedMagnetTask} for the given {@link Player} with the given
     * pickup radius.
     * 
     * @param p
     *            The {@link Player} who items should be teleported to
     * @param radius
     *            The radius in which items should be picked up
     */
    public InfusedMagnetTask(@Nonnull Player p, double radius) {
        super(p);

        this.radius = radius;
    }

    @Override
    protected void executeTask() {
        boolean playSound = false;

        for (Entity n : p.getNearbyEntities(radius, radius, radius)) {
            if (n instanceof Item) {
                Item item = (Item) n;

                if (!SlimefunUtils.hasNoPickupFlag(item) && item.getPickupDelay() <= 0 && p.getLocation().distanceSquared(item.getLocation()) > 0.3) {
                    item.teleport(p.getLocation());
                    playSound = true;
                }
            }
        }

        // Only play a sound if an Item was found
        if (playSound) {
            SoundEffect.INFUSED_MAGNET_TELEPORT_SOUND.playFor(p);
        }
    }

    @Override
    protected boolean isValid() {
        return super.isValid() && p.getGameMode() != GameMode.SPECTATOR;
    }

}
