package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.core.services.sounds.SoundEffect;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.papermc.lib.PaperLib;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class PressureChamber extends MultiBlockMachine {

    @ParametersAreNonnullByDefault
    public PressureChamber(Category category, SlimefunItemStack item) {
        super(category, item, new ItemStack[] { new ItemStack(Material.SMOOTH_STONE_SLAB), new CustomItem(Material.DISPENSER, "Dispenser (Facing down)"), new ItemStack(Material.SMOOTH_STONE_SLAB), new ItemStack(Material.PISTON), new ItemStack(Material.GLASS), new ItemStack(Material.PISTON), new ItemStack(Material.PISTON), new ItemStack(Material.CAULDRON), new ItemStack(Material.PISTON) }, BlockFace.UP);
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        return recipes.stream().map(items -> items[0]).collect(Collectors.toList());
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block dispBlock = b.getRelative(BlockFace.UP).getRelative(BlockFace.UP);
        BlockState state = PaperLib.getBlockState(dispBlock, false).getState();

        if (state instanceof Dispenser) {
            Dispenser disp = (Dispenser) state;
            Inventory inv = disp.getInventory();

            for (ItemStack current : inv.getContents()) {
                for (ItemStack convert : RecipeType.getRecipeInputs(this)) {
                    if (convert != null && SlimefunUtils.isItemSimilar(current, convert, true)) {
                        ItemStack output = RecipeType.getRecipeOutput(this, convert);
                        Inventory outputInv = findOutputInventory(output, dispBlock, inv);

                        if (outputInv != null) {
                            ItemStack removing = current.clone();
                            removing.setAmount(convert.getAmount());
                            inv.removeItem(removing);

                            craft(p, b, output, inv, dispBlock);
                        } else {
                            SlimefunPlugin.getLocalization().sendMessage(p, "machines.full-inventory", true);
                        }

                        return;
                    }
                }
            }
            SlimefunPlugin.getLocalization().sendMessage(p, "machines.unknown-material", true);
        }
    }

    @ParametersAreNonnullByDefault
    private void craft(Player p, Block b, ItemStack output, Inventory dispInv, Block dispenser) {
        for (int i = 0; i < 4; i++) {
            int j = i;

            SlimefunPlugin.runSync(() -> {
                SoundEffect.PRESSURE_CHAMBER_WORKING_SOUND.playAt(b);
                p.getWorld().playEffect(b.getRelative(BlockFace.UP).getLocation(), Effect.SMOKE, 4);
                p.getWorld().playEffect(b.getRelative(BlockFace.UP).getLocation(), Effect.SMOKE, 4);
                p.getWorld().playEffect(b.getRelative(BlockFace.UP).getLocation(), Effect.SMOKE, 4);

                if (j < 3) {
                    SoundEffect.PRESSURE_CHAMBER_WORKING_SOUND.playAt(b);
                } else {
                    SoundEffect.PRESSURE_CHAMBER_FINISH_SOUND.playAt(b);
                    handleCraftedItem(output, dispenser, dispInv);
                }
            }, i * 20L);
        }
    }

}
