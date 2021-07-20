package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.inventory.InvUtils;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.papermc.lib.PaperLib;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * An abstract super class for the {@link Smeltery} and {@link MakeshiftSmeltery}.
 * 
 * @author TheBusyBiscuit
 *
 */
abstract class AbstractSmeltery extends MultiBlockMachine {

    @ParametersAreNonnullByDefault
    protected AbstractSmeltery(Category category, SlimefunItemStack item, ItemStack[] recipe, BlockFace trigger) {
        super(category, item, recipe, trigger);
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block dispBlock = b.getRelative(BlockFace.DOWN);
        BlockState state = PaperLib.getBlockState(dispBlock, false).getState();

        if (state instanceof Dispenser) {
            Dispenser disp = (Dispenser) state;
            Inventory inv = disp.getInventory();
            List<ItemStack[]> inputs = RecipeType.getRecipeInputList(this);

            for (int i = 0; i < inputs.size(); i++) {
                if (canCraft(inv, inputs, i)) {
                    ItemStack output = RecipeType.getRecipeOutputList(this, inputs.get(i)).clone();

                    if (SlimefunUtils.canPlayerUseItem(p, output, true)) {
                        Inventory outputInv = findOutputInventory(output, dispBlock, inv);

                        if (outputInv != null) {
                            craft(p, b, inv, inputs.get(i), output, outputInv);
                        } else {
                            SlimefunPlugin.getLocalization().sendMessage(p, "machines.full-inventory", true);
                        }
                    }

                    return;
                }
            }

            SlimefunPlugin.getLocalization().sendMessage(p, "machines.unknown-material", true);
        }
    }

    private boolean canCraft(Inventory inv, List<ItemStack[]> inputs, int i) {
        for (ItemStack expectedInput : inputs.get(i)) {
            if (expectedInput != null) {
                for (int j = 0; j < inv.getContents().length; j++) {
                    if (j == (inv.getContents().length - 1) && !SlimefunUtils.isItemSimilar(inv.getContents()[j], expectedInput, true)) {
                        return false;
                    } else if (SlimefunUtils.isItemSimilar(inv.getContents()[j], expectedInput, true)) {
                        break;
                    }
                }
            }
        }

        return true;
    }

    protected void craft(Player p, Block b, Inventory inv, ItemStack[] recipe, ItemStack output, Inventory outputInv) {
        for (ItemStack removing : recipe) {
            if (removing != null) {
                InvUtils.removeItem(inv, removing.getAmount(), true, stack -> SlimefunUtils.isItemSimilar(stack, removing, true));
            }
        }

        outputInv.addItem(output);
        SoundEffect.SMELTERY_CRAFT_SOUND.playAt(b);
        p.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
    }

}
