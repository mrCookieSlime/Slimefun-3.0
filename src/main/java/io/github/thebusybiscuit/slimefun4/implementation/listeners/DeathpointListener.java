package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

/**
 * This {@link Listener} listens to the {@link EntityDeathEvent} to automatically
 * create a waypoint for a {@link Player} who carries an Emergency Transmitter.
 * 
 * @author TheBusyBiscuit
 *
 */
public class DeathpointListener implements Listener {

    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("(MMM dd, yyyy @ hh:mm)", Locale.ROOT);

    public DeathpointListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        if (SlimefunUtils.containsSimilarItem(p.getInventory(), SlimefunItems.GPS_EMERGENCY_TRANSMITTER, true)) {
            Slimefun.getGPSNetwork().addWaypoint(p, "player:death " + Slimefun.getLocalization().getMessage(p, "gps.deathpoint").replace("%date%", format.format(LocalDateTime.now())), p.getLocation().getBlock().getLocation());
        }
    }
}
