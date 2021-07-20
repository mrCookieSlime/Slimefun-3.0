package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.OutputChest;

/**
 * The {@link TableSaw} is an implementation of a {@link MultiBlockMachine} that allows
 * you to turn Logs into Wooden Planks.
 * 
 * It also replaced the old "Saw Mill" from earlier versions.
 * 
 * @author dniym
 * @author svr333
 * @author TheBusyBiscuit
 * 
 * @see MultiBlockMachine
 *
 */
public class TableSaw extends MultiBlockMachine {

    private final List<ItemStack> displayedRecipes = new ArrayList<>();

    @ParametersAreNonnullByDefault
    public TableSaw(ItemGroup group, SlimefunItemStack item) {
        super(group, item, new ItemStack[] { null, null, null, new ItemStack(Material.SMOOTH_STONE_SLAB), new ItemStack(Material.STONECUTTER), new ItemStack(Material.SMOOTH_STONE_SLAB), null, new ItemStack(Material.IRON_BLOCK), null }, BlockFace.SELF);

        for (Material log : Tag.LOGS.getValues()) {
            Optional<Material> planks = getPlanks(log);

            if (planks.isPresent()) {
                displayedRecipes.add(new ItemStack(log));
                displayedRecipes.add(new ItemStack(planks.get(), 8));
            }
        }

        for (Material plank : Tag.PLANKS.getValues()) {
            displayedRecipes.add(new ItemStack(plank));
            displayedRecipes.add(new ItemStack(Material.STICK, 4));
        }
    }

    private @Nonnull Optional<Material> getPlanks(@Nonnull Material log) {
        String materialName = log.name().replace("STRIPPED_", "");
        materialName = materialName.substring(0, materialName.lastIndexOf('_')) + "_PLANKS";
        return Optional.ofNullable(Material.getMaterial(materialName));
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        return displayedRecipes;
    }

    @Override
    public void onInteract(@Nonnull Player p, @Nonnull Block b) {
        ItemStack item = p.getInventory().getItemInMainHand();
        ItemStack output = getOutputFromMaterial(item.getType());

        if (output == null) {
            Slimefun.getLocalization().sendMessage(p, "machines.wrong-item", true);
            return;
        }

        if (p.getGameMode() != GameMode.CREATIVE) {
            ItemUtils.consumeItem(item, true);
        }

        outputItems(b, output);
        b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, item.getType());
    }

    private @Nullable ItemStack getOutputFromMaterial(@Nonnull Material item) {
        if (Tag.LOGS.isTagged(item)) {
            Optional<Material> planks = getPlanks(item);

            if (planks.isPresent()) {
                return new ItemStack(planks.get(), 8);
            } else {
                return null;
            }
        } else if (Tag.PLANKS.isTagged(item)) {
            return new ItemStack(Material.STICK, 4);
        } else {
            return null;
        }
    }

    private void outputItems(@Nonnull Block b, @Nonnull ItemStack output) {
        Optional<Inventory> outputChest = OutputChest.findOutputChestFor(b, output);

        if (outputChest.isPresent()) {
            outputChest.get().addItem(output);
        } else {
            b.getWorld().dropItemNaturally(b.getLocation(), output);
        }
    }
}
