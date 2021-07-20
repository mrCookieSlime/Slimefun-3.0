package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

public class HerculesPickaxe extends SimpleSlimefunItem<ToolUseHandler> {

    @ParametersAreNonnullByDefault
    public HerculesPickaxe(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public @Nonnull ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {
            if (SlimefunTag.ORES.isTagged(e.getBlock().getType())) {
                if (e.getBlock().getType() == Material.IRON_ORE) {
                    drops.add(new CustomItemStack(SlimefunItems.IRON_DUST, 2));
                } else if (e.getBlock().getType() == Material.GOLD_ORE) {
                    drops.add(new CustomItemStack(SlimefunItems.GOLD_DUST, 2));
                } else {
                    for (ItemStack drop : e.getBlock().getDrops(tool)) {
                        drops.add(new CustomItemStack(drop, drop.getAmount() * 2));
                    }
                }
            }
        };
    }

}
