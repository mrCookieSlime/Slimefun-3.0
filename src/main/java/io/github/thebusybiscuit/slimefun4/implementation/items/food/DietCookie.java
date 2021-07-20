package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

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
    public DietCookie(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> {
            SlimefunPlugin.getLocalization().sendMessage(p, "messages.diet-cookie");
            SoundEffect.DIET_COOKIE_CONSUME_SOUND.playFor(p);

            if (p.hasPotionEffect(PotionEffectType.LEVITATION)) {
                p.removePotionEffect(PotionEffectType.LEVITATION);
            }

            p.addPotionEffect(PotionEffectType.LEVITATION.createEffect(60, 1));
        };
    }

}
