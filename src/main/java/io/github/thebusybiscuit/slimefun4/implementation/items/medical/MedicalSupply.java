package io.github.thebusybiscuit.slimefun4.implementation.items.medical;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

public abstract class MedicalSupply<T extends ItemHandler> extends SimpleSlimefunItem<T> {

    private final Set<PotionEffectType> curedEffects = new HashSet<>();
    private final int healAmount;

    @ParametersAreNonnullByDefault
    protected MedicalSupply(ItemGroup category, int healAmount, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        this.healAmount = healAmount;

        curedEffects.add(PotionEffectType.POISON);
        curedEffects.add(PotionEffectType.WITHER);
        curedEffects.add(PotionEffectType.SLOW);
        curedEffects.add(PotionEffectType.SLOW_DIGGING);
        curedEffects.add(PotionEffectType.WEAKNESS);
        curedEffects.add(PotionEffectType.CONFUSION);
        curedEffects.add(PotionEffectType.BLINDNESS);
        curedEffects.add(PotionEffectType.BAD_OMEN);
    }

    /**
     * This returns the {@link PotionEffect PotionEffects} cured from this {@link MedicalSupply}.
     * 
     * @return An immutable {@link Set} of cured {@link PotionEffect PotionEffects}
     */
    @Nonnull
    public Set<PotionEffectType> getCuredEffects() {
        return Collections.unmodifiableSet(curedEffects);
    }

    /**
     * This method clears any negative {@link PotionEffect} from the given {@link LivingEntity}.
     * 
     * @param n
     *            The {@link LivingEntity} to clear the effects from.
     */
    public void clearNegativeEffects(@Nonnull LivingEntity n) {
        for (PotionEffectType effect : curedEffects) {
            if (n.hasPotionEffect(effect)) {
                n.removePotionEffect(effect);
            }
        }
    }

    /**
     * This method heals the given {@link LivingEntity} by the amount provided via the constructor.
     * 
     * @param n
     *            The {@link LivingEntity} to heal
     */
    public void heal(@Nonnull LivingEntity n) {
        double health = n.getHealth() + healAmount;
        double maxHealth = n.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        n.setHealth(Math.min(health, maxHealth));
    }

}
