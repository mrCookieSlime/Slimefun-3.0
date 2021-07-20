package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.Parachute;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.JetBoots;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.Jetpack;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.InfusedMagnet;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.InfusedMagnetTask;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.JetBootsTask;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.JetpackTask;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.ParachuteTask;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

/**
 * This {@link Listener} is responsible for listening to the {@link PlayerToggleSneakEvent}
 * to start tasks for various gadgets that are activated by pressing shift,
 * like the {@link Jetpack} or {@link JetBoots}
 * 
 * @author TheBusyBiscuit
 * 
 * @see JetpackTask
 * @see JetBootsTask
 * @see ParachuteTask
 * @see InfusedMagnetTask
 *
 */
public class GadgetsListener implements Listener {

    public GadgetsListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player p = e.getPlayer();

            if (p.getInventory().getChestplate() != null) {
                SlimefunItem chestplate = SlimefunItem.getByItem(p.getInventory().getChestplate());
                handleChestplate(p, chestplate);
            }

            if (p.getInventory().getBoots() != null) {
                SlimefunItem boots = SlimefunItem.getByItem(p.getInventory().getBoots());
                handleBoots(p, boots);
            }

            if (SlimefunUtils.containsSimilarItem(p.getInventory(), SlimefunItems.INFUSED_MAGNET, true)) {
                InfusedMagnet magnet = (InfusedMagnet) SlimefunItems.INFUSED_MAGNET.getItem();

                if (magnet.canUse(p, true)) {
                    new InfusedMagnetTask(p, magnet.getRadius()).scheduleRepeating(0, 8);
                }
            }
        }
    }

    private void handleChestplate(@Nonnull Player p, @Nullable SlimefunItem chestplate) {
        if (chestplate == null || !chestplate.canUse(p, true)) {
            return;
        }

        if (chestplate instanceof Jetpack) {
            double thrust = ((Jetpack) chestplate).getThrust();

            if (thrust > 0.2) {
                new JetpackTask(p, (Jetpack) chestplate).scheduleRepeating(0, 3);
            }
        } else if (chestplate instanceof Parachute) {
            new ParachuteTask(p).scheduleRepeating(0, 3);
        }
    }

    private void handleBoots(@Nonnull Player p, @Nullable SlimefunItem boots) {
        if (boots instanceof JetBoots && boots.canUse(p, true)) {
            double speed = ((JetBoots) boots).getSpeed();

            if (speed > 0.2) {
                new JetBootsTask(p, (JetBoots) boots).scheduleRepeating(0, 2);
            }
        }
    }
}
