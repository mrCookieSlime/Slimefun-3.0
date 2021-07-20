package io.github.thebusybiscuit.slimefun4.implementation.items.weapons;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.WeaponUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * The {@link VampireBlade} is a weapon that applies a Healing effect to any {@link Player}
 * who damages another {@link LivingEntity} with this sword.
 * 
 * @author TheBusyBiscuit
 *
 */
public class VampireBlade extends SimpleSlimefunItem<WeaponUseHandler> {

    private static final double HEALING_AMOUNT = 4.0;

    private final ItemSetting<Integer> chance = new IntRangeSetting(this, "chance", 0, 45, 100);

    @ParametersAreNonnullByDefault
    public VampireBlade(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(chance);
    }

    @Override
    public @Nonnull WeaponUseHandler getItemHandler() {
        return (e, p, item) -> {
            if (ThreadLocalRandom.current().nextInt(100) < getChance()) {
                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.7F, 0.7F);
                double health = p.getHealth() + HEALING_AMOUNT;
                double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                p.setHealth(Math.min(health, maxHealth));
            }
        };
    }

    /**
     * This method returns the chance of a {@link VampireBlade} to apply its healing effect.
     * 
     * @return The chance for a healing effect
     */
    public int getChance() {
        return chance.getValue();
    }

}
