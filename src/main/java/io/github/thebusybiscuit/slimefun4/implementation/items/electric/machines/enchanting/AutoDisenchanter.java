package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.api.events.AutoDisenchantEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * The {@link AutoDisenchanter}, in contrast to the {@link AutoEnchanter}, removes
 * {@link Enchantment Enchantments} from a given {@link ItemStack} and transfers them
 * to a book.
 *
 * @author TheBusyBiscuit
 * @author Poslovitch
 * @author John000708
 * @author Walshy
 * @author poma123
 * @author mrcoffee1026
 * @author VoidAngel
 * @author StarWishSama
 *
 * @see AutoEnchanter
 *
 */
public class AutoDisenchanter extends AbstractEnchantmentMachine {

    @ParametersAreNonnullByDefault
    public AutoDisenchanter(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.DIAMOND_CHESTPLATE);
    }

    @Override
    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        for (int slot : getInputSlots()) {
            ItemStack item = menu.getItemInSlot(slot);

            if (!isDisenchantable(item)) {
                continue;
            }

            // Call an event so other Plugins can modify it.
            AutoDisenchantEvent event = new AutoDisenchantEvent(item);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return null;
            }

            ItemStack secondItem = menu.getItemInSlot(slot == getInputSlots()[0] ? getInputSlots()[1] : getInputSlots()[0]);

            if (secondItem != null && secondItem.getType() == Material.BOOK) {
                return disenchant(menu, item, secondItem);
            }
        }

        return null;
    }

    @Nullable
    @ParametersAreNonnullByDefault
    private MachineRecipe disenchant(BlockMenu menu, ItemStack item, ItemStack book) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        // Find enchantments
        for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
            if (isEnchantmentLevelAllowed(entry.getValue())) {
                enchantments.put(entry.getKey(), entry.getValue());
            } else if (!menu.toInventory().getViewers().isEmpty()) {
                showEnchantmentLevelWarning(menu);
                return null;
            }
        }

        // Check if we found any valid enchantments
        if (!enchantments.isEmpty()) {
            ItemStack disenchantedItem = item.clone();
            disenchantedItem.setAmount(1);

            ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
            transferEnchantments(disenchantedItem, enchantedBook, enchantments);

            MachineRecipe recipe = new MachineRecipe(90 * enchantments.size() / this.getSpeed(), new ItemStack[] { book, item }, new ItemStack[] { disenchantedItem, enchantedBook });

            if (!InvUtils.fitAll(menu.toInventory(), recipe.getOutput(), getOutputSlots())) {
                return null;
            }

            for (int inputSlot : getInputSlots()) {
                menu.consumeItem(inputSlot);
            }

            return recipe;
        } else {
            return null;
        }
    }

    @ParametersAreNonnullByDefault
    private void transferEnchantments(ItemStack item, ItemStack book, Map<Enchantment, Integer> enchantments) {
        ItemMeta itemMeta = item.getItemMeta();
        ItemMeta bookMeta = book.getItemMeta();
        ((Repairable) bookMeta).setRepairCost(((Repairable) itemMeta).getRepairCost());
        ((Repairable) itemMeta).setRepairCost(0);
        item.setItemMeta(itemMeta);
        book.setItemMeta(bookMeta);

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            item.removeEnchantment(entry.getKey());
            meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
        }

        book.setItemMeta(meta);
    }

    private boolean isDisenchantable(@Nullable ItemStack item) {
        if (item != null && !item.getType().isAir() && item.getType() != Material.BOOK && !hasIgnoredLore(item)) {
            SlimefunItem sfItem = SlimefunItem.getByItem(item);
            return sfItem == null || sfItem.isDisenchantable();
        } else {
            return false;
        }
    }

    @Override
    public String getMachineIdentifier() {
        return "AUTO_DISENCHANTER";
    }

}
