package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * The {@link InfernalBonemeal} is a special type of bone meal which will work on
 * Nether Warts.
 * 
 * @author TheBusyBiscuit
 *
 */
public class InfernalBonemeal extends SimpleSlimefunItem<ItemUseHandler> {

    @ParametersAreNonnullByDefault
    public InfernalBonemeal(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Optional<Block> block = e.getClickedBlock();
            e.setUseBlock(Result.DENY);

            if (block.isPresent()) {
                Block b = block.get();

                if (b.getType() == Material.NETHER_WART) {
                    Ageable ageable = (Ageable) b.getBlockData();

                    if (ageable.getAge() < ageable.getMaximumAge()) {
                        ageable.setAge(ageable.getMaximumAge());
                        b.setBlockData(ageable);
                        b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

                        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                            ItemUtils.consumeItem(e.getItem(), false);
                        }
                    }
                }
            }
        };
    }

}
