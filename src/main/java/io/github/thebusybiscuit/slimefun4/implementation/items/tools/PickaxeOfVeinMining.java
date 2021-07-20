package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.blocks.Vein;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

/**
 * The {@link PickaxeOfVeinMining} is a powerful tool which allows you to mine an entire vein of ores
 * at once. It even works with the fortune {@link Enchantment}.
 * 
 * @author TheBusyBiscuit
 * @author Linox
 *
 */
public class PickaxeOfVeinMining extends SimpleSlimefunItem<ToolUseHandler> {

    private final ItemSetting<Integer> maxBlocks = new IntRangeSetting(this, "max-blocks", 1, 16, Integer.MAX_VALUE);

    @ParametersAreNonnullByDefault
    public PickaxeOfVeinMining(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
        addItemSetting(maxBlocks);
    }

    @Override
    public @Nonnull ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {
            if (SlimefunTag.PICKAXE_OF_VEIN_MINING_BLOCKS.isTagged(e.getBlock().getType())) {
                List<Block> blocks = Vein.find(e.getBlock(), maxBlocks.getValue(), b -> SlimefunTag.PICKAXE_OF_VEIN_MINING_BLOCKS.isTagged(b.getType()));
                breakBlocks(e.getPlayer(), blocks, fortune, tool);
            }
        };
    }

    @ParametersAreNonnullByDefault
    private void breakBlocks(Player p, List<Block> blocks, int fortune, ItemStack tool) {
        for (Block b : blocks) {
            if (Slimefun.getProtectionManager().hasPermission(p, b.getLocation(), Interaction.BREAK_BLOCK)) {
                b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());

                if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
                    b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(b.getType()));
                } else {
                    for (ItemStack drop : b.getDrops(tool)) {
                        b.getWorld().dropItemNaturally(b.getLocation(), drop.getType().isBlock() ? drop : new CustomItemStack(drop, fortune));
                    }
                }

                b.setType(Material.AIR);
            }
        }
    }

}
