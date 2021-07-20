package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

/**
 * The {@link ExplosiveShovel} works similar to the {@link ExplosivePickaxe}.
 * However it can only break blocks that a shovel can break.
 * 
 * @author Linox
 *
 * @see ExplosivePickaxe
 * @see ExplosiveTool
 *
 */
public class ExplosiveShovel extends ExplosiveTool {

    @ParametersAreNonnullByDefault
    public ExplosiveShovel(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected boolean canBreak(Player p, Block b) {
        return SlimefunTag.EXPLOSIVE_SHOVEL_BLOCKS.isTagged(b.getType()) && Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.BREAK_BLOCK);
    }

}
