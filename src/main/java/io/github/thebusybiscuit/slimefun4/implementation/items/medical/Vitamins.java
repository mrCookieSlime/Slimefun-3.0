package io.github.thebusybiscuit.slimefun4.implementation.items.medical;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.utils.RadiationUtils;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.inventory.ItemUtils;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class Vitamins extends MedicalSupply<ItemUseHandler> {

    @ParametersAreNonnullByDefault
    public Vitamins(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, 8, item, recipeType, recipe);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);

            if (p.getGameMode() != GameMode.CREATIVE) {
                ItemUtils.consumeItem(e.getItem(), false);
            }

            e.cancel();
            p.setFireTicks(0);
            clearNegativeEffects(p);
            RadiationUtils.clearExposure(p);
            heal(p);
        };
    }

}
