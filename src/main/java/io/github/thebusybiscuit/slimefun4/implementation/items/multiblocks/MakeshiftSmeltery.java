package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

/**
 * The {@link MakeshiftSmeltery} is a simpler version of the {@link Smeltery}.
 * Its functionality is very limited and it cannot be used to smelt alloys.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Smeltery
 *
 */
public class MakeshiftSmeltery extends AbstractSmeltery {

    @ParametersAreNonnullByDefault
    public MakeshiftSmeltery(ItemGroup category, SlimefunItemStack item) {
        super(category, item, new ItemStack[] { null, new ItemStack(Material.OAK_FENCE), null, new ItemStack(Material.BRICKS), new CustomItemStack(Material.DISPENSER, "Dispenser (Facing up)"), new ItemStack(Material.BRICKS), null, new ItemStack(Material.FLINT_AND_STEEL), null }, BlockFace.DOWN);
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < recipes.size() - 1; i += 2) {
            items.add(recipes.get(i)[0]);
            items.add(recipes.get(i + 1)[0]);
        }

        return items;
    }

    @Override
    protected void craft(Player p, Block b, Inventory inv, ItemStack[] recipe, ItemStack output, Inventory outputInv) {
        super.craft(p, b, inv, recipe, output, outputInv);

        Block fire = b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
        fire.getWorld().playEffect(fire.getLocation(), Effect.STEP_SOUND, fire.getType());
        fire.setType(Material.AIR);
    }

}
