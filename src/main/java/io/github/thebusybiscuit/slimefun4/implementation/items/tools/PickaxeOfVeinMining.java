package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.blocks.Vein;
import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.cscorelib2.protection.ProtectableAction;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerBlockBreakEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

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
    public PickaxeOfVeinMining(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
        addItemSetting(maxBlocks);
    }

    @Override
    public @Nonnull ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {
            if (SlimefunTag.PICKAXE_OF_VEIN_MINING_BLOCKS.isTagged(e.getBlock().getType())) {
                List<Block> blocks = Vein.find(e.getBlock(), maxBlocks.getValue(), b -> SlimefunTag.PICKAXE_OF_VEIN_MINING_BLOCKS.isTagged(b.getType()));

                PlayerBlockBreakEvent event = new PlayerBlockBreakEvent(e.getPlayer(), e.getBlock(), blocks);
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    breakBlocks(e.getPlayer(), event.getBlocks(), fortune, tool);
                }
            }
        };
    }

    @ParametersAreNonnullByDefault
    private void breakBlocks(Player p, List<Block> blocks, int fortune, ItemStack tool) {
        for (Block b : blocks) {
            if (SlimefunPlugin.getProtectionManager().hasPermission(p, b.getLocation(), ProtectableAction.BREAK_BLOCK)) {
                b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());

                if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
                    b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(b.getType()));
                } else {
                    for (ItemStack drop : b.getDrops(tool)) {
                        b.getWorld().dropItemNaturally(b.getLocation(), drop.getType().isBlock() ? drop : new CustomItem(drop, fortune));
                    }
                }

                b.setType(Material.AIR);
            }
        }
    }

}
