package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * The {@link AutoAnvil} is an electric machine which can repair any {@link ItemStack} using
 * Duct tape.
 * 
 * @author TheBusyBiscuit
 *
 */
public class AutoAnvil extends AContainer {

    private final int repairFactor;

    public AutoAnvil(ItemGroup category, int repairFactor, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        this.repairFactor = repairFactor;
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.IRON_PICKAXE);
    }

    @Override
    public String getMachineIdentifier() {
        return "AUTO_ANVIL";
    }

    @Override
    protected MachineRecipe findNextRecipe(BlockMenu menu) {
        for (int slot : getInputSlots()) {
            ItemStack ductTape = menu.getItemInSlot(slot == getInputSlots()[0] ? getInputSlots()[1] : getInputSlots()[0]);
            ItemStack item = menu.getItemInSlot(slot);

            if (item != null && item.getType().getMaxDurability() > 0 && ((Damageable) item.getItemMeta()).getDamage() > 0) {
                if (SlimefunUtils.isItemSimilar(ductTape, SlimefunItems.DUCT_TAPE, true, false)) {
                    ItemStack repairedItem = repair(item);

                    if (!menu.fits(repairedItem, getOutputSlots())) {
                        return null;
                    }

                    for (int inputSlot : getInputSlots()) {
                        menu.consumeItem(inputSlot);
                    }

                    return new MachineRecipe(30, new ItemStack[] { ductTape, item }, new ItemStack[] { repairedItem });
                }

                break;
            }
        }

        return null;
    }

    private ItemStack repair(ItemStack item) {
        ItemStack repaired = item.clone();
        ItemMeta meta = repaired.getItemMeta();

        short maxDurability = item.getType().getMaxDurability();
        int repairPercentage = 100 / repairFactor;
        short durability = (short) (((Damageable) meta).getDamage() - (maxDurability / repairPercentage));

        if (durability < 0) {
            durability = 0;
        }

        ((Damageable) meta).setDamage(durability);
        repaired.setItemMeta(meta);
        return repaired;
    }

}
