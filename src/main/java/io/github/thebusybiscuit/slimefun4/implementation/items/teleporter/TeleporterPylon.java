package io.github.thebusybiscuit.slimefun4.implementation.items.teleporter;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.RainbowTickHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.RainbowBlock;

/**
 * The {@link TeleporterPylon} is a special kind of {@link RainbowBlock} which is required
 * for the {@link Teleporter}.
 * 
 * @author TheBusyBiscuit
 *
 * @see Teleporter
 * @see RainbowBlock
 * @see RainbowTickHandler
 */
public class TeleporterPylon extends RainbowBlock {

    @ParametersAreNonnullByDefault
    public TeleporterPylon(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput, new RainbowTickHandler(Material.CYAN_STAINED_GLASS, Material.PURPLE_STAINED_GLASS));
    }

}
