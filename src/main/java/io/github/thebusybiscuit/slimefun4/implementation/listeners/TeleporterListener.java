package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.elevator.ElevatorPlate;
import io.github.thebusybiscuit.slimefun4.implementation.items.teleporter.AbstractTeleporterPlate;
import io.github.thebusybiscuit.slimefun4.implementation.items.teleporter.Teleporter;
import io.github.thebusybiscuit.slimefun4.implementation.items.teleporter.TeleporterPylon;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * This {@link Listener} is responsible for the {@link Teleporter} (and {@link ElevatorPlate}).
 * 
 * @author TheBusyBiscuit
 * @author Walshy
 * @author Sfiguz7
 * @author SoSeDiK
 *
 */
public class TeleporterListener implements Listener {

    // @formatter:off
    private final BlockFace[] faces = {
        BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST,
        BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST
    };
    // @formatter:on

    public TeleporterListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPressurePlateEnter(PlayerInteractEvent e) {
        if (e.getAction() != Action.PHYSICAL || e.getClickedBlock() == null) {
            return;
        }

        Block b = e.getClickedBlock();
        SlimefunItem item = BlockStorage.check(b);
        Player p = e.getPlayer();

        // Fixes #2966 - Check if Players can use these
        if (item == null || !item.canUse(p, true)) {
            return;
        }

        if (item instanceof ElevatorPlate) {
            // Pressure plate was an elevator
            ElevatorPlate elevator = SlimefunItems.ELEVATOR_PLATE.getItem(ElevatorPlate.class);
            elevator.openInterface(p, b);
        } else if (item instanceof AbstractTeleporterPlate && ((AbstractTeleporterPlate) item).hasAccess(p, b)) {
            // Pressure plate was a teleporter
            SlimefunItem teleporter = BlockStorage.check(b.getRelative(BlockFace.DOWN));

            if (teleporter instanceof Teleporter && checkForPylons(b.getRelative(BlockFace.DOWN))) {
                Block block = b.getRelative(BlockFace.DOWN);
                UUID owner = UUID.fromString(BlockStorage.getLocationInfo(block.getLocation(), "owner"));
                Slimefun.getGPSNetwork().getTeleportationManager().openTeleporterGUI(p, owner, block);
            }
        }
    }

    /**
     * This methoc checks if the given teleporter {@link Block} is surrounded
     * by all the necessary {@link TeleporterPylon}s.
     * 
     * @param teleporter
     *            The teleporter {@link Block}
     * 
     * @return Whether the teleporter is surrounded by pylons.
     */
    private boolean checkForPylons(@Nonnull Block teleporter) {
        for (BlockFace face : faces) {
            if (!(BlockStorage.check(teleporter.getRelative(face)) instanceof TeleporterPylon)) {
                return false;
            }
        }

        return true;
    }

}