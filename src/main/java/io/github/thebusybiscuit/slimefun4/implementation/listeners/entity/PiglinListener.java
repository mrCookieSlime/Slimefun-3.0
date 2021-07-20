package io.github.thebusybiscuit.slimefun4.implementation.listeners.entity;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.PiglinBarterDrop;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This {@link Listener} prevents a {@link Piglin} from bartering with a
 * {@link SlimefunItem}.
 * It also listens to the {@link EntityDropItemEvent} to
 * inject a {@link PiglinBarterDrop} if the chance check passes.
 *
 * @author poma123
 * @author dNiym
 * 
 */
public class PiglinListener implements Listener {

    public PiglinListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntityType() == EntityType.PIGLIN) {
            ItemStack item = e.getItem().getItemStack();

            // Don't let Piglins pick up gold from Slimefun
            if (SlimefunItem.getByItem(item) != null) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (!e.getRightClicked().isValid() || e.getRightClicked().getType() != EntityType.PIGLIN) {
            return;
        }

        Player p = e.getPlayer();
        ItemStack item;

        if (e.getHand() == EquipmentSlot.OFF_HAND) {
            item = p.getInventory().getItemInOffHand();
        } else {
            item = p.getInventory().getItemInMainHand();
        }

        // We only care about Gold since it's the actual "Bartering" we wanna prevent
        if (item.getType() == Material.GOLD_INGOT) {
            SlimefunItem sfItem = SlimefunItem.getByItem(item);

            if (sfItem != null) {
                Slimefun.getLocalization().sendMessage(p, "messages.piglin-barter", true);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPiglinDropItem(EntityDropItemEvent e) {
        if (e.getEntity() instanceof Piglin) {
            Set<ItemStack> drops = Slimefun.getRegistry().getBarteringDrops();

            /*
             * NOTE: Getting a new random number each iteration because multiple items could have the same
             * % chance to drop, and if one fails all items with that number will fail.
             * Getting a new random number will allow multiple items with the same % chance to drop.
             */

            for (ItemStack is : drops) {
                SlimefunItem sfi = SlimefunItem.getByItem(is);
                // Check the getBarteringLootChance and compare against a random number 0-100,
                // if the random number is greater then replace the item.
                if (sfi instanceof PiglinBarterDrop) {
                    int chance = ((PiglinBarterDrop) sfi).getBarteringLootChance();

                    if (chance < 1 || chance >= 100) {
                        sfi.warn("The Piglin Bartering chance must be between 1-99% on item: " + sfi.getId());
                    } else if (chance > ThreadLocalRandom.current().nextInt(100)) {
                        e.getItemDrop().setItemStack(sfi.getRecipeOutput());
                        return;
                    }
                }
            }
        }
    }
}
