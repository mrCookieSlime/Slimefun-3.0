package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.inventory.ItemUtils;
import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.papermc.lib.PaperLib;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class ArmorForge extends AbstractCraftingTable {

    @ParametersAreNonnullByDefault
    public ArmorForge(Category category, SlimefunItemStack item) {
        super(category, item, new ItemStack[] { null, null, null, null, new ItemStack(Material.ANVIL), null, null, new CustomItem(Material.DISPENSER, "Dispenser (Facing up)"), null }, BlockFace.SELF);
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block dispenser = b.getRelative(BlockFace.DOWN);
        BlockState state = PaperLib.getBlockState(dispenser, false).getState();

        if (state instanceof Dispenser) {
            Dispenser disp = (Dispenser) state;
            Inventory inv = disp.getInventory();
            List<ItemStack[]> inputs = RecipeType.getRecipeInputList(this);

            for (int i = 0; i < inputs.size(); i++) {
                if (isCraftable(inv, inputs.get(i))) {
                    ItemStack output = RecipeType.getRecipeOutputList(this, inputs.get(i)).clone();

                    if (SlimefunUtils.canPlayerUseItem(p, output, true)) {
                        craft(p, output, inv, dispenser);
                    }

                    return;
                }
            }

            if (inv.isEmpty()) {
                SlimefunPlugin.getLocalization().sendMessage(p, "machines.inventory-empty", true);
            } else {
                SlimefunPlugin.getLocalization().sendMessage(p, "machines.pattern-not-found", true);
            }
        }
    }

    private boolean isCraftable(Inventory inv, ItemStack[] recipe) {
        for (int j = 0; j < inv.getContents().length; j++) {
            if (!SlimefunUtils.isItemSimilar(inv.getContents()[j], recipe[j], true)) {
                return false;
            }
        }

        return true;
    }

    @ParametersAreNonnullByDefault
    private void craft(Player p, ItemStack output, Inventory inv, Block dispenser) {
        Inventory fakeInv = createVirtualInventory(inv);
        Inventory outputInv = findOutputInventory(output, dispenser, inv, fakeInv);

        if (outputInv != null) {
            for (int j = 0; j < 9; j++) {
                ItemStack item = inv.getContents()[j];

                if (item != null && item.getType() != Material.AIR) {
                    ItemUtils.consumeItem(item, true);
                }
            }

            for (int j = 0; j < 4; j++) {
                int current = j;

                SlimefunPlugin.runSync(() -> {
                    if (current < 3) {
                        SoundEffect.ARMOR_FORGE_WORKING_SOUND.playAt(dispenser);
                    } else {
                        SoundEffect.ARMOR_FORGE_FINISH_SOUND.playAt(dispenser);
                        handleCraftedItem(output, dispenser, inv);
                    }
                }, j * 20L);
            }

        } else {
            SlimefunPlugin.getLocalization().sendMessage(p, "machines.full-inventory", true);
        }
    }

}
