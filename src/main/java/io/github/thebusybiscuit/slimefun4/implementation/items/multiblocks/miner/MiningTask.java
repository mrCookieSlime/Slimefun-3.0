package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner;

import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.bakedlibs.dough.inventory.InvUtils;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.scheduling.TaskQueue;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.WorldUtils;
import io.papermc.lib.PaperLib;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;

/**
 * This represents a running instance of an {@link IndustrialMiner}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see IndustrialMiner
 * @see AdvancedIndustrialMiner
 *
 */
class MiningTask implements Runnable {

    private final IndustrialMiner miner;
    private final UUID owner;

    private final Block chest;
    private final Block[] pistons;

    private final BlockPosition start;
    private final BlockPosition end;
    private final int height;

    private boolean running = false;
    private int fuel = 0;
    private int ores = 0;

    private int x;
    private int z;

    @ParametersAreNonnullByDefault
    MiningTask(IndustrialMiner miner, UUID owner, Block chest, Block[] pistons, Block start, Block end) {
        this.miner = miner;
        this.owner = owner;

        this.chest = chest;
        this.pistons = pistons;

        this.start = new BlockPosition(start);
        this.end = new BlockPosition(end);

        this.height = start.getY();
        this.x = start.getX();
        this.z = start.getZ();
    }

    /**
     * This starts the {@link IndustrialMiner} at the given {@link Block}.
     * 
     * @param b
     *            The {@link Block} which marks the center of this {@link IndustrialMiner}
     */
    void start(@Nonnull Block b) {
        miner.activeMiners.put(b.getLocation(), this);
        running = true;

        warmUp();
    }

    /**
     * This method stops the {@link IndustrialMiner}.
     */
    void stop() {
        running = false;
        miner.activeMiners.remove(chest.getRelative(BlockFace.DOWN).getLocation());
    }

    /**
     * This method stops the {@link IndustrialMiner} with an error message.
     * The error message is a path to the location in Slimefun's localization files.
     * 
     * @param reason
     *            The reason why we stop
     */
    void stop(@Nonnull MinerStoppingReason reason) {
        Player p = Bukkit.getPlayer(owner);

        if (p != null) {
            Slimefun.getLocalization().sendMessage(p, reason.getErrorMessage());
        }

        stop();
    }

    /**
     * This method starts the warm-up animation for the {@link IndustrialMiner}.
     */
    private void warmUp() {
        fuel = consumeFuel();

        if (fuel <= 0) {
            // This Miner has not enough fuel.
            stop(MinerStoppingReason.NO_FUEL);
            return;
        }

        /*
         * This is our warm up animation.
         * The pistons will push after another in decreasing intervals
         */
        TaskQueue queue = new TaskQueue();

        queue.thenRun(4, () -> setPistonState(pistons[0], true));
        queue.thenRun(10, () -> setPistonState(pistons[0], false));

        queue.thenRun(8, () -> setPistonState(pistons[1], true));
        queue.thenRun(10, () -> setPistonState(pistons[1], false));

        queue.thenRun(6, () -> setPistonState(pistons[0], true));
        queue.thenRun(9, () -> setPistonState(pistons[0], false));

        queue.thenRun(4, () -> setPistonState(pistons[1], true));
        queue.thenRun(7, () -> setPistonState(pistons[1], false));

        queue.thenRun(3, () -> setPistonState(pistons[0], true));
        queue.thenRun(5, () -> setPistonState(pistons[0], false));

        queue.thenRun(2, () -> setPistonState(pistons[1], true));
        queue.thenRun(4, () -> setPistonState(pistons[1], false));

        queue.thenRun(1, () -> setPistonState(pistons[0], true));
        queue.thenRun(3, () -> setPistonState(pistons[0], false));

        queue.thenRun(1, () -> setPistonState(pistons[1], true));
        queue.thenRun(2, () -> setPistonState(pistons[1], false));

        queue.thenRun(1, this);
        queue.execute(Slimefun.instance());
    }

    @Override
    public void run() {
        if (!running) {
            // Don't continue if the machine has stopped
            return;
        }

        TaskQueue queue = new TaskQueue();

        queue.thenRun(1, () -> setPistonState(pistons[0], true));
        queue.thenRun(3, () -> setPistonState(pistons[0], false));

        queue.thenRun(1, () -> setPistonState(pistons[1], true));
        queue.thenRun(3, () -> setPistonState(pistons[1], false));

        queue.thenRun(() -> {
            try {
                Block furnace = chest.getRelative(BlockFace.DOWN);
                furnace.getWorld().playEffect(furnace.getLocation(), Effect.STEP_SOUND, Material.STONE);

                World world = start.getWorld();
                for (int y = height; y > WorldUtils.getMinHeight(world); y--) {
                    Block b = world.getBlockAt(x, y, z);

                    if (!Slimefun.getProtectionManager().hasPermission(Bukkit.getOfflinePlayer(owner), b, Interaction.BREAK_BLOCK)) {
                        stop(MinerStoppingReason.NO_PERMISSION);
                        return;
                    }

                    if (miner.canMine(b.getType()) && push(miner.getOutcome(b.getType()))) {
                        furnace.getWorld().playEffect(furnace.getLocation(), Effect.STEP_SOUND, b.getType());
                        furnace.getWorld().playSound(furnace.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.2F, 1F);

                        b.setType(Material.AIR);
                        fuel--;
                        ores++;

                        // Repeat the same column when we hit an ore.
                        Slimefun.runSync(this, 4);
                        return;
                    }
                }

                nextColumn();
            } catch (Exception e) {
                Slimefun.logger().log(Level.SEVERE, e, () -> "An Error occurred while running an Industrial Miner at " + new BlockPosition(chest));
                stop();
            }
        });

        queue.execute(Slimefun.instance());
    }

    /**
     * This advances the {@link IndustrialMiner} to the next column
     */
    private void nextColumn() {
        if (x < end.getX()) {
            x++;
        } else if (z < end.getZ()) {
            x = start.getX();
            z++;
        } else {
            // The Miner has finished
            stop();

            Player p = Bukkit.getPlayer(owner);

            if (p != null) {
                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.4F, 1F);
                Slimefun.getLocalization().sendMessage(p, "machines.INDUSTRIAL_MINER.finished", msg -> msg.replace("%ores%", String.valueOf(ores)));
            }

            return;
        }

        Slimefun.runSync(this, 5);
    }

    /**
     * This refuels the {@link IndustrialMiner} and pushes the given {@link ItemStack} to
     * its {@link Chest}.
     * 
     * @param item
     *            The {@link ItemStack} to push to the {@link Chest}.
     * 
     * @return Whether the operation was successful
     */
    private boolean push(@Nonnull ItemStack item) {
        if (fuel < 1) {
            // Restock fuel
            fuel = consumeFuel();
        }

        // Check if there is enough fuel to run
        if (fuel > 0) {
            if (chest.getType() == Material.CHEST) {
                BlockState state = PaperLib.getBlockState(chest, false).getState();

                if (state instanceof Chest) {
                    Inventory inv = ((Chest) state).getBlockInventory();

                    if (InvUtils.fits(inv, item)) {
                        inv.addItem(item);
                        return true;
                    } else {
                        stop(MinerStoppingReason.CHEST_FULL);
                    }
                } else {
                    // I won't question how this happened...
                    stop(MinerStoppingReason.STRUCTURE_DESTROYED);
                }
            } else {
                // The chest has been destroyed
                stop(MinerStoppingReason.STRUCTURE_DESTROYED);
            }
        } else {
            stop(MinerStoppingReason.NO_FUEL);
        }

        return false;
    }

    /**
     * This consumes fuel from the given {@link Chest}.
     * 
     * @return The gained fuel value
     */
    private int consumeFuel() {
        if (chest.getType() == Material.CHEST) {
            BlockState state = PaperLib.getBlockState(chest, false).getState();

            if (state instanceof Chest) {
                Inventory inv = ((Chest) state).getBlockInventory();
                return consumeFuel(inv);
            }
        }

        return 0;
    }

    private int consumeFuel(@Nonnull Inventory inv) {
        for (int i = 0; i < inv.getSize(); i++) {
            for (MachineFuel fuelType : miner.fuelTypes) {
                ItemStack item = inv.getContents()[i];

                if (fuelType.test(item)) {
                    ItemUtils.consumeItem(item, false);

                    if (miner instanceof AdvancedIndustrialMiner) {
                        inv.addItem(new ItemStack(Material.BUCKET));
                    }

                    return fuelType.getTicks();
                }
            }
        }

        return 0;
    }

    private void setPistonState(@Nonnull Block block, boolean extended) {
        if (!running) {
            return;
        }

        try {
            // Smoke Particles around the Chest for dramatic effect
            Location particleLoc = chest.getLocation().clone().add(0, -1, 0);
            block.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particleLoc, 20, 0.7, 0.7, 0.7, 0);

            if (block.getType() == Material.MOVING_PISTON) {
                // Yeah it isn't really cool when this happens
                block.getRelative(BlockFace.UP).setType(Material.AIR);
            } else if (block.getType() == Material.PISTON) {
                Block above = block.getRelative(BlockFace.UP);

                // Check if the above block is valid
                if (above.isEmpty() || above.getType() == Material.PISTON_HEAD) {
                    Piston piston = (Piston) block.getBlockData();

                    // Check if the piston is actually facing upwards
                    if (piston.getFacing() == BlockFace.UP) {
                        setExtended(block, piston, extended);
                    } else {
                        // The pistons must be facing upwards
                        stop(MinerStoppingReason.PISTON_WRONG_DIRECTION);
                    }
                } else {
                    // The pistons must be facing upwards
                    stop(MinerStoppingReason.PISTON_NO_SPACE);
                }
            } else {
                // The piston has been destroyed
                stop(MinerStoppingReason.STRUCTURE_DESTROYED);
            }
        } catch (Exception e) {
            Slimefun.logger().log(Level.SEVERE, e, () -> "An Error occurred while moving a Piston for an Industrial Miner at " + new BlockPosition(block));
            stop();
        }
    }

    private void setExtended(@Nonnull Block block, @Nonnull Piston piston, boolean extended) {
        piston.setExtended(extended);
        block.setBlockData(piston, false);

        // Updating the Piston Head
        if (extended) {
            PistonHead head = (PistonHead) Material.PISTON_HEAD.createBlockData();
            head.setFacing(BlockFace.UP);

            block.getRelative(BlockFace.UP).setBlockData(head, false);
        } else {
            block.getRelative(BlockFace.UP).setType(Material.AIR);
        }

        block.getWorld().playSound(block.getLocation(), extended ? Sound.BLOCK_PISTON_EXTEND : Sound.BLOCK_PISTON_CONTRACT, 0.1F, 1F);
    }

}
