package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.SoundCategory;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * The {@link MagicEyeOfEnder} allows you to launch an {@link EnderPearl}
 * out of thin air as long as you are wearing Ender Armor.
 * 
 * @author TheBusyBiscuit
 *
 */
public class MagicEyeOfEnder extends SimpleSlimefunItem<ItemUseHandler> {

    @ParametersAreNonnullByDefault
    public MagicEyeOfEnder(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Player p = e.getPlayer();

            if (hasArmor(p.getInventory())) {
                p.launchProjectile(EnderPearl.class);
                SoundEffect.MAGICAL_EYE_OF_ENDER_USE_SOUND.playAt(p.getLocation(), SoundCategory.PLAYERS);
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
