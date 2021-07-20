package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import javax.annotation.Nonnull;

import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.BeeWings;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.BeeWingsListener;

/**
 * This task is responsible for the repeating checks for our {@link BeeWings}.
 * 
 * @author beSnow
 * @author TheBusyBiscuit
 * 
 * @see BeeWings
 * @see BeeWingsListener
 *
 */
public class BeeWingsTask extends AbstractPlayerTask {

    private static final int MIN_ALTITUDE = 4;

    private Location lastLocation;

    public BeeWingsTask(@Nonnull Player p) {
        super(p);
        lastLocation = p.getLocation();
    }

    @Override
    protected void executeTask() {
        if (p.getLocation().getY() < lastLocation.getY()) {
            Location loc = p.getLocation();
            int distanceToHighestBlock = (loc.getBlockY() - loc.getWorld().getHighestBlockYAt(loc, HeightMap.WORLD_SURFACE));

            /*
             * getDistanceToGround will only fire when distanceToHighestBlock is negative
             * (which happens when a player flies beneath an existing structure)
             */
            if (distanceToHighestBlock < 0) {
                int distanceToGround = getDistanceToGround(loc.getBlock(), 6);

                if (distanceToGround < 1) {
                    return;
                }

                slowDown();
            } else if (distanceToHighestBlock <= MIN_ALTITUDE) {
                slowDown();
            }
        }

        lastLocation = p.getLocation();
    }

    private void slowDown() {
        Slimefun.getLocalization().sendMessage(p, "messages.bee-suit-slow-fall");

        p.setFallDistance(0);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 0));
    }

    /**
     * Calculates the distance of the given {@link Block} from the ground.
     *
     * @param b
     *            The {@link Block} to calculate from.
     * @param limit
     *            The limit of {@link Block blocks} to check under the given {@link Block b}.
     *
     */
    private int getDistanceToGround(@Nonnull Block b, int limit) {
        for (int i = 1; i <= limit; i++) {
            Block relative = b.getRelative(0, -i, 0);

            if (relative.getType().isSolid()) {
                return i;
            }
        }

        return 0;
    }

    @Override
    protected boolean isValid() {
        // The task is only valid as long as the Player is alive and gliding
        if (!p.isOnline() || !p.isValid() || p.isDead() || !p.isGliding() || p.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
            cancel();
            return false;
        }

        return true;
    }

}
