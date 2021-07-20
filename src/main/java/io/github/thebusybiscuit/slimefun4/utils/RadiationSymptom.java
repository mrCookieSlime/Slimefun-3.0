package io.github.thebusybiscuit.slimefun4.utils;


import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

/**
 * An enum of potential radiation symptoms.
 * A symptom will be applied when the minExposure
 * threshold is reached on the {@link Player}'s
 * exposure level.
 *
 * @author Semisol
 *
 * @see RadiationUtils
 */
public enum RadiationSymptom {
    SLOW(10, PotionEffectType.SLOW, 3),
    WITHER_LOW(25, PotionEffectType.WITHER, 0),
    BLINDNESS(50, PotionEffectType.BLINDNESS, 4),
    WITHER_HIGH(75, PotionEffectType.WITHER, 3),
    IMMINENT_DEATH(100, PotionEffectType.HARM, 49);

    private final int minExposure;
    private final PotionEffect potionEffect;

    RadiationSymptom(int minExposure, @Nonnull PotionEffectType type, int level) {
        Validate.notNull(type, "The effect type cannot be null");
        Validate.isTrue(minExposure > 0, "The minimum exposure must not be greater than 0.");
        Validate.isTrue(level >= 0, "The status effect level must be non-negative.");
        this.minExposure = minExposure;
        this.potionEffect = new PotionEffect(type, SlimefunPlugin.getCfg().getOrSetDefault("options.radiation-update-interval", 1) * 20 + 20, level);
    }

    /**
     * This method applies the symptom to a player.
     *
     * @param p The player
     */

    public void apply(@Nonnull Player p){
        Validate.notNull(p, "The player cannot be null");
        SlimefunPlugin.runSync(() -> p.addPotionEffect(potionEffect));
    }

    /**
     * This method returns if this symptom
     * should be applied.
     *
     * @param exposure Exposure level
     *
     * @return If the symptom should be applied
     */
    public boolean shouldApply(int exposure){
        return exposure >= minExposure;
    }
}