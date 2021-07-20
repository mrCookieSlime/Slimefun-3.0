package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * The {@link DietCookie} gives you a {@link PotionEffect} of Type {@code PotionEffectType.LEVITATION}
 * when consumed.
 * 
 * @author Linox
 * 
 * @see FortuneCookie
 * @see ItemConsumptionHandler
 *
 */
public class DietCookie extends SimpleSlimefunItem<ItemConsumptionHandler> {

    @ParametersAreNonnullByDefault
    public DietCookie(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> {
            Slimefun.getLocalization().sendMessage(p, "messages.diet-cookie");
            p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);

            if (p.hasPotionEffect(PotionEffectType.LEVITATION)) {
                p.removePotionEffect(PotionEffectType.LEVITATION);
            }

            p.addPotionEffect(PotionEffectType.LEVITATION.createEffect(60, 1));
        };
    }

}
