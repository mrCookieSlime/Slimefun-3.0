package io.github.thebusybiscuit.slimefun4.core.attributes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.UnbreakingAlgorithm;

/**
 * This interface, when attached to a {@link SlimefunItem}, provides an easy method for damaging
 * an {@link ItemStack}, see {@link #damageItem(Player, ItemStack)}.
 * 
 * It also provides a simple {@link #isDamageable()} method, in case you wanna add a config
 * option that decides whether or not this {@link SlimefunItem} shall be damageable.
 * 
 * @author TheBusyBiscuit
 * @author RobotHanzo
 * 
 * @see UnbreakingAlgorithm
 * 
 */
public interface DamageableItem extends ItemAttribute {

    /**
     * Implement this method to make the behaviour of this interface dependent
     * on the state of your object.
     * You could add a {@link Config} option to toggle the behaviour for example.
     * 
     * @return Whether this {@link SlimefunItem} is damageable
     */
    boolean isDamageable();

    /**
     * This method will damage the given {@link ItemStack} once.
     * It also takes into account the {@link Enchantment} {@code Unbreaking}.
     * 
     * It will only apply the damage if {@link #isDamageable()} returned true.
     * 
     * @param p
     *            The {@link Player} to which the item belongs
     * @param item
     *            The {@link ItemStack} to damage
     */
    default void damageItem(@Nonnull Player p, @Nullable ItemStack item) {
        if (isDamageable() && item != null && !item.getType().isAir() && item.getAmount() > 0) {
            int unbreakingLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);

            if (evaluateUnbreakingEnchantment(unbreakingLevel)) {
                return;
            }

            ItemMeta meta = item.getItemMeta();

            if (!meta.isUnbreakable()) {
                Damageable damageable = (Damageable) meta;

                if (damageable.getDamage() >= item.getType().getMaxDurability()) {
                    p.playSound(p.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                    item.setAmount(0);
                } else {
                    damageable.setDamage(damageable.getDamage() + 1);
                    item.setItemMeta(meta);
                }
            }
        }
    }

    /**
     * This method will randomly decide if the item should be damaged or not
     * This does not damage the item, it is called by {@link #damageItem(Player, ItemStack)} to randomly generate a
     * boolean
     * This function should be overridden when the item type is not a tool which is the default value
     *
     * @param unbreakingLevel
     *            The {@link Integer} level of the unbreaking {@link Enchantment}
     * 
     * @return Whether to save the item from taking damage
     *
     */
    default boolean evaluateUnbreakingEnchantment(int unbreakingLevel) {
        return UnbreakingAlgorithm.TOOLS.evaluate(unbreakingLevel);
    }

}
