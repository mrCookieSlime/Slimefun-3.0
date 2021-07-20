package io.github.thebusybiscuit.slimefun4.implementation.items.armor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectionType;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectiveArmor;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.ElytraImpactListener;
import io.github.thebusybiscuit.slimefun4.utils.UnbreakingAlgorithm;

/**
 * The {@link ElytraCap} negates damage taken when crashing into a wall using an elytra.
 *
 * @author Seggan
 *
 * @see ElytraImpactListener
 */
public class ElytraCap extends SlimefunArmorPiece implements DamageableItem, ProtectiveArmor {

    private final NamespacedKey key;

    @ParametersAreNonnullByDefault
    public ElytraCap(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe, null);

        key = new NamespacedKey(Slimefun.instance(), "elytra_armor");
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public void damageItem(@Nonnull Player p, @Nullable ItemStack item) {
        if (p.getGameMode() != GameMode.CREATIVE) {
            DamageableItem.super.damageItem(p, item);
        }
    }

    @Override
    public boolean evaluateUnbreakingEnchantment(int unbreakingLevel) {
        return UnbreakingAlgorithm.ARMOR.evaluate(unbreakingLevel);
    }

    @Nonnull
    @Override
    public ProtectionType[] getProtectionTypes() {
        return new ProtectionType[] { ProtectionType.FLYING_INTO_WALL };
    }

    @Override
    public boolean isFullSetRequired() {
        return false;
    }

    @Nonnull
    @Override
    public NamespacedKey getArmorSetId() {
        return key;
    }
}
