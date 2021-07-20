package io.github.thebusybiscuit.slimefun4.implementation.listeners.crafting;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

/**
 * This {@link Listener} prevents any {@link SlimefunItem} from being used in a
 * Cauldron.
 * This is mainly used to prevent the discoloring of leather armor.
 * 
 * @author TheBusyBiscuit
 *
 */
public class CauldronListener implements SlimefunCraftingListener {

    public CauldronListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCauldronUse(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();

            if (block.getType() == Material.CAULDRON) {
                ItemStack item = e.getItem();

                if (item != null && SlimefunTag.LEATHER_ARMOR.isTagged(item.getType())) {
                    SlimefunItem sfItem = SlimefunItem.getByItem(item);

                    if (isUnallowed(sfItem)) {
                        e.setCancelled(true);
                        Slimefun.getLocalization().sendMessage(e.getPlayer(), "cauldron.no-discoloring");
                    }
                }
            }
        }
    }

}
