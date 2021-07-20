package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.JetBoots;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;

public class JetBootsTask extends AbstractPlayerTask {

    private static final float COST = 0.075F;

    private final JetBoots boots;

    public JetBootsTask(@Nonnull Player p, @Nonnull JetBoots boots) {
        super(p);
        this.boots = boots;
    }

    @Override
    protected void executeTask() {
        if (p.getInventory().getBoots() == null || p.getInventory().getBoots().getType() == Material.AIR) {
            return;
        }

        double accuracy = NumberUtils.reparseDouble(boots.getSpeed() - 0.7);

        if (boots.removeItemCharge(p.getInventory().getBoots(), COST)) {
            SoundEffect.JETBOOTS_THRUST_SOUND.playAt(p.getLocation(), SoundCategory.PLAYERS);
            p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 1, 1);
            p.setFallDistance(0F);
            double gravity = 0.04;
            double offset = ThreadLocalRandom.current().nextBoolean() ? accuracy : -accuracy;
            Vector vector = new Vector(p.getEyeLocation().getDirection().getX() * boots.getSpeed() + offset, gravity, p.getEyeLocation().getDirection().getZ() * boots.getSpeed() - offset);

            p.setVelocity(vector);
        } else {
            cancel();
        }
    }
}
