package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import javax.annotation.ParametersAreNonnullByDefault;

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
 * {@link MonsterJerky} is basically just Rotten Flesh but without the Hunger Effect.
 * 
 * @author TheBusyBiscuit
 * 
 * @see MeatJerky
 *
 */
public class MonsterJerky extends SimpleSlimefunItem<ItemConsumptionHandler> {

    @ParametersAreNonnullByDefault
    public MonsterJerky(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> Slimefun.runSync(() -> {
            if (p.hasPotionEffect(PotionEffectType.HUNGER)) {
                p.removePotionEffect(PotionEffectType.HUNGER);
            }

            p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 5, 0));
        }, 1L);
    }

}
