package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

/**
 * The {@link MagicEyeOfEnder} allows you to launch an {@link EnderPearl}
 * out of thin air as long as you are wearing Ender Armor.
 * 
 * @author TheBusyBiscuit
 *
 */
public class MagicEyeOfEnder extends SimpleSlimefunItem<ItemUseHandler> {

    @ParametersAreNonnullByDefault
    public MagicEyeOfEnder(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Player p = e.getPlayer();

            if (hasArmor(p.getInventory())) {
                p.launchProjectile(EnderPearl.class);
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
        };
    }

    private boolean hasArmor(@Nonnull PlayerInventory inv) {
        // @formatter:off
        return SlimefunUtils.isItemSimilar(inv.getHelmet(), SlimefunItems.ENDER_HELMET, true) 
            && SlimefunUtils.isItemSimilar(inv.getChestplate(), SlimefunItems.ENDER_CHESTPLATE, true)
            && SlimefunUtils.isItemSimilar(inv.getLeggings(), SlimefunItems.ENDER_LEGGINGS, true)
            && SlimefunUtils.isItemSimilar(inv.getBoots(), SlimefunItems.ENDER_BOOTS, true);
        // @formatter:on
    }
}
