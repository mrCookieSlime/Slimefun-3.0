package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * Represents Book Binder, a machine that binds multiple enchantments books into one.
 *
 * @author ProfElements
 */
public class BookBinder extends AContainer {

    private final ItemSetting<Boolean> bypassVanillaMaxLevel = new ItemSetting<>(this, "bypass-vanilla-max-level", false);
    private final ItemSetting<Boolean> hasCustomMaxLevel = new ItemSetting<>(this, "has-custom-max-level", false);
    private final ItemSetting<Integer> customMaxLevel = new IntRangeSetting(this, "custom-max-level", 0, 15, Integer.MAX_VALUE);

    @ParametersAreNonnullByDefault
    public BookBinder(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(bypassVanillaMaxLevel, hasCustomMaxLevel, customMaxLevel);
    }

    @Override
    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        for (int slot : getInputSlots()) {
            ItemStack target = menu.getItemInSlot(slot == getInputSlots()[0] ? getInputSlots()[1] : getInputSlots()[0]);
            ItemStack item = menu.getItemInSlot(slot);

            if (isCompatible(item) && isCompatible(target)) {
                EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) item.getItemMeta();
                EnchantmentStorageMeta targetMeta = (EnchantmentStorageMeta) target.getItemMeta();

                Map<Enchantment, Integer> storedItemEnchantments = itemMeta.getStoredEnchants();
                Map<Enchantment, Integer> storedTargetEnchantments = targetMeta.getStoredEnchants();
                Map<Enchantment, Integer> enchantments = combineEnchantments(storedItemEnchantments, storedTargetEnchantments);

                // Just return if no enchantments exist. This shouldn't ever happen. :NotLikeThis:
                if (enchantments.size() > 0) {
                    ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);

                    EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) book.getItemMeta();

                    for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                        enchantMeta.addStoredEnchant(entry.getKey(), entry.getValue(), bypassVanillaMaxLevel.getValue());
                    }

                    // Make sure we never return an enchanted book with no enchantments.
                    if (enchantMeta.getStoredEnchants().isEmpty()) {
                        return null;
                    }

                    book.setItemMeta(enchantMeta);

                    MachineRecipe recipe = new MachineRecipe(25 * (enchantments.size() / this.getSpeed()), new ItemStack[] { target, item }, new ItemStack[] { book });

                    if (!InvUtils.fitAll(menu.toInventory(), recipe.getOutput(), getOutputSlots())) {
                        return null;
                    }

                    for (int inputSlot : getInputSlots()) {
                        menu.consumeItem(inputSlot);
                    }

                    return recipe;
                }

                return null;
            }

        }

        return null;
    }

    private boolean isCompatible(@Nullable ItemStack item) {
        return item != null && item.getType() == Material.ENCHANTED_BOOK;
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.IRON_CHESTPLATE);
    }

    @Override
    public String getMachineIdentifier() {
        return "BOOK_BINDER";
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    private Map<Enchantment, Integer> combineEnchantments(Map<Enchantment, Integer> ech1, Map<Enchantment, Integer> ech2) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        enchantments.putAll(ech1);
        boolean hasConflicts = false;

        for (Map.Entry<Enchantment, Integer> entry : ech2.entrySet()) {
            for (Map.Entry<Enchantment, Integer> conflictsWith : enchantments.entrySet()) {

                /*
                 * Check if entry enchantment and conflictsWith enchantment conflict
                 * and confirm that the enchantsments aren't the exact same.
                 */
                if (entry.getKey().conflictsWith(conflictsWith.getKey()) && !entry.getKey().equals(conflictsWith.getKey())) {
                    hasConflicts = true;
                }
            }

            if (!hasConflicts) {
                enchantments.merge(entry.getKey(), entry.getValue(), (a, b) -> {
                    int maxLevel = entry.getKey().getMaxLevel();
                    return combineEnchantmentLevels(maxLevel, a, b);
                });
            }
        }

        return enchantments;

    }

    private int combineEnchantmentLevels(int maxLevel, int lvl1, int lvl2) {
        if (lvl1 == lvl2) {
            /*
             * Confirm the entry's enchant level doesn't go over the maximum
             * unless it uses bypass-vanilla-max-level
             */
            if (maxLevel <= lvl1 && !bypassVanillaMaxLevel.getValue()) {
                return maxLevel;
            } else if (hasCustomMaxLevel.getValue()) {
                return lvl1 + 1 > customMaxLevel.getValue() ? customMaxLevel.getValue() : lvl1 + 1;
            } else {
                return lvl1 + 1;
            }
        } else {
            int highestLevel = Math.max(lvl1, lvl2);

            /*
             * Confirm the entry's enchant level doesn't go over the maximum
             * unless it uses bypass-vanilla-max-level
             */
            if (maxLevel <= highestLevel && !bypassVanillaMaxLevel.getValue()) {
                return maxLevel;
            } else if (hasCustomMaxLevel.getValue()) {
                return highestLevel > customMaxLevel.getValue() ? customMaxLevel.getValue() : highestLevel;
            } else {
                return highestLevel;
            }

        }
    }
}