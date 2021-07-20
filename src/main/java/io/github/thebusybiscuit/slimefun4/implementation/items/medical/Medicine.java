package io.github.thebusybiscuit.slimefun4.implementation.items.medical;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.utils.RadiationUtils;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class Medicine extends MedicalSupply<ItemConsumptionHandler> {

    @ParametersAreNonnullByDefault
    public Medicine(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, 8, item, recipeType, recipe);
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> {
            p.setFireTicks(0);
            clearNegativeEffects(p);
            RadiationUtils.clearExposure(p);
            heal(p);
        };
    }

}
