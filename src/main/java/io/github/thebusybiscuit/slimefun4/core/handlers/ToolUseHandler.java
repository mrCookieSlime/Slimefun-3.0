package io.github.thebusybiscuit.slimefun4.core.handlers;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * This {@link ItemHandler} is called when a {@link Block} is broken with a {@link SlimefunItem}
 * as its tool.
 * 
 * @author TheBusyBiscuit
 *
 * @see BlockBreakHandler
 *
 */
@FunctionalInterface
public interface ToolUseHandler extends ItemHandler {

    /**
     * This method is called whenever a {@link BlockBreakEvent} was fired when using this
     * {@link SlimefunItem} to break a {@link Block}.
     * 
     * @param e
     *            The {@link BlockBreakEvent}
     * @param tool
     *            The tool that was used
     * @param fortune
     *            The amount of bonus drops to be expected from the fortune {@link Enchantment}.
     * @param drops
     *            The dropped items
     * 
     */
    void onToolUse(BlockBreakEvent e, ItemStack tool, int fortune, List<ItemStack> drops);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return ToolUseHandler.class;
    }
}
