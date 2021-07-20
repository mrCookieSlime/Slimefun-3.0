package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import io.github.thebusybiscuit.slimefun4.api.events.CoolerFeedPlayerEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.Cooler;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.Juice;

/**
 * This {@link Listener} listens for a {@link FoodLevelChangeEvent} or an {@link EntityDamageEvent} for starvation
 * damage
 * and consumes a {@link Juice} from any {@link Cooler} that can be found in the {@link Inventory} of the given
 * {@link Player}.
 * 
 * @author TheBusyBiscuit
 * @author Linox
 * 
 * @see Cooler
 * @see Juice
 *
 */
public class CoolerListener implements Listener {

    private final Slimefun plugin;
    private final Cooler cooler;

    public CoolerListener(@Nonnull Slimefun plugin, @Nonnull Cooler cooler) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
        this.cooler = cooler;
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            if (e.getFoodLevel() < p.getFoodLevel()) {
                checkAndConsume(p);
            }
        }
    }

    @EventHandler
    public void onHungerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() == DamageCause.STARVATION) {
            checkAndConsume((Player) e.getEntity());
        }
    }

    private void checkAndConsume(@Nonnull Player p) {
        if (cooler == null || cooler.isDisabled()) {
            // Do not proceed if the Cooler was disabled
            return;
        }

        for (ItemStack item : p.getInventory().getContents()) {
            if (cooler.isItem(item)) {
                if (cooler.canUse(p, true)) {
                    takeJuiceFromCooler(p, item);
                } else {
                    return;
                }
            }
        }
    }

    /**
     * This takes a {@link Juice} from the given {@link Cooler} and consumes it in order
     * to restore hunger for the given {@link Player}.
     * 
     * @param p
     *            The {@link Player}
     * @param cooler
     *            The {@link Cooler} {@link ItemStack} to take the {@link Juice} from
     */
    private void takeJuiceFromCooler(@Nonnull Player p, @Nonnull ItemStack cooler) {
        PlayerProfile.getBackpack(cooler, backpack -> {
            if (backpack != null) {
                Slimefun.runSync(() -> consumeJuice(p, cooler, backpack));
            }
        });
    }

    private boolean consumeJuice(@Nonnull Player p, @Nonnull ItemStack coolerItem, @Nonnull PlayerBackpack backpack) {
        Inventory inv = backpack.getInventory();
        int slot = -1;

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (stack != null && stack.getType() == Material.POTION && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
                slot = i;
                break;
            }
        }

        if (slot >= 0) {
            ItemStack item = inv.getItem(slot);
            CoolerFeedPlayerEvent event = new CoolerFeedPlayerEvent(p, cooler, coolerItem, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                PotionMeta im = (PotionMeta) event.getConsumedItem().getItemMeta();

                for (PotionEffect effect : im.getCustomEffects()) {
                    p.addPotionEffect(effect);
                }

                p.setSaturation(6F);
                p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1F, 1F);
                inv.setItem(slot, null);
                backpack.markDirty();

                return true;
            } else {
                return false;
            }
        }

        return false;
    }

}
