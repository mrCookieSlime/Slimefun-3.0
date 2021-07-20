package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.cscorelib2.inventory.ItemUtils;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * {@link MagicSugar} is one of the oldest items in Slimefun, it is a special
 * kind of sugar which gives you the Speed {@link PotionEffect} when right clicked.
 * 
 * @author TheBusyBiscuit
 *
 */
public class MagicSugar extends SimpleSlimefunItem<ItemUseHandler> {

    @ParametersAreNonnullByDefault
    public MagicSugar(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            // Check if it is being placed into an ancient altar.
            if (e.getClickedBlock().isPresent()) {
                Material block = e.getClickedBlock().get().getType();

                if (block == Material.DISPENSER || block == Material.ENCHANTING_TABLE) {
                    return;
                }
            }

            Player p = e.getPlayer();

            if (p.getGameMode() != GameMode.CREATIVE) {
                ItemUtils.consumeItem(e.getItem(), false);
            }

            SoundEffect.MAGIC_SUGAR_CONSUME_SOUND.playFor(p);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 3));
        };
    }

}
