package io.github.thebusybiscuit.slimefun4.test.mocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;

public class MockDamageable extends SlimefunItem implements DamageableItem {

    private final boolean itemDamageable;

    @ParametersAreNonnullByDefault
    public MockDamageable(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, boolean damageable) {
        super(category, item, recipeType, recipe);
        itemDamageable = damageable;
    }

    @Override
    public boolean isDamageable() {
        return itemDamageable;
    }

    @Override
    public void damageItem(@Nonnull Player p, @Nullable ItemStack item) {
        DamageableItem.super.damageItem(p, item);
    }
}
