package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.AbstractMonsterSpawner;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.BrokenSpawner;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.RepairedSpawner;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.papermc.lib.PaperLib;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link PickaxeOfContainment} is a Pickaxe that allows you to break Spawners.
 * Upon breaking a Spawner, a {@link BrokenSpawner} will be dropped.
 * But it also allows you to break a {@link RepairedSpawner}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see BrokenSpawner
 * @see RepairedSpawner
 *
 */
public class PickaxeOfContainment extends SimpleSlimefunItem<ToolUseHandler> {

    @ParametersAreNonnullByDefault
    public PickaxeOfContainment(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public @Nonnull ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {
            Block b = e.getBlock();

            if (b.getType() == Material.SPAWNER) {
                ItemStack spawner = breakSpawner(b);
                SlimefunUtils.spawnItem(b.getLocation(), spawner, ItemSpawnReason.BROKEN_SPAWNER_DROP, true);

                e.setExpToDrop(0);
                e.setDropItems(false);
            }
        };
    }

    private @Nonnull ItemStack breakSpawner(@Nonnull Block b) {
        AbstractMonsterSpawner spawner;

        /**
         * If the spawner's BlockStorage has BlockInfo, then it's not a vanilla spawner
         * and should not give a broken spawner but a repaired one instead.
         */
        if (BlockStorage.hasBlockInfo(b)) {
            spawner = (AbstractMonsterSpawner) SlimefunItems.REPAIRED_SPAWNER.getItem();
        } else {
            spawner = (AbstractMonsterSpawner) SlimefunItems.BROKEN_SPAWNER.getItem();
        }

        BlockState state = PaperLib.getBlockState(b, false).getState();

        if (state instanceof CreatureSpawner) {
            EntityType entityType = ((CreatureSpawner) state).getSpawnedType();
            return spawner.getItemForEntityType(entityType);
        }

        return new ItemStack(Material.SPAWNER);
    }

}
