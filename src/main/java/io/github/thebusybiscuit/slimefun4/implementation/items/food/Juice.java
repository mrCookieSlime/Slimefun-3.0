package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.Cooler;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.CoolerListener;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

/**
 * This class represents a {@link SlimefunItem} that can be stored inside
 * of a {@link Cooler}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Cooler
 * @see CoolerListener
 *
 */
public class Juice extends SimpleSlimefunItem<ItemConsumptionHandler> {

    private final List<PotionEffect> effects;

    @ParametersAreNonnullByDefault
    public Juice(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        this(category, item, recipeType, recipe, null);
    }

    @ParametersAreNonnullByDefault
    public Juice(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, @Nullable ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput);

        ItemMeta meta = item.getItemMeta();

        if (meta instanceof PotionMeta) {
            effects = ((PotionMeta) meta).getCustomEffects();
        } else {
            effects = new ArrayList<>();
        }
    }

    @Override
    public ItemConsumptionHandler getItemHandler() {
        return (e, p, item) -> {
            /*
             * Fix for Saturation on potions is no longer working,
             * Minecraft has been broken when it comes to Saturation potions for a long time
             */
            for (PotionEffect effect : effects) {
                if (effect.getType().equals(PotionEffectType.SATURATION)) {
                    p.addPotionEffect(effect);
                    break;
                }
            }

            removeGlassBottle(p, item);
        };
    }

    /**
     * Determines from which hand the juice is being drunk, and its amount
     * 
     * @param p
     *            The {@link Player} that triggered this
     * @param item
     *            The {@link ItemStack} in question
     */
    @ParametersAreNonnullByDefault
    private void removeGlassBottle(Player p, ItemStack item) {
        if (SlimefunUtils.isItemSimilar(item, p.getInventory().getItemInMainHand(), true)) {
            if (p.getInventory().getItemInMainHand().getAmount() == 1) {
                Slimefun.runSync(() -> p.getEquipment().getItemInMainHand().setAmount(0));
            } else {
                Slimefun.runSync(() -> p.getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE, 1)));
            }
        } else if (SlimefunUtils.isItemSimilar(item, p.getInventory().getItemInOffHand(), true)) {
            if (p.getInventory().getItemInOffHand().getAmount() == 1) {
                Slimefun.runSync(() -> p.getEquipment().getItemInOffHand().setAmount(0));
            } else {
                Slimefun.runSync(() -> p.getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE, 1)));
            }
        }
    }

}
