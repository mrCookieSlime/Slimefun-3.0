package io.github.thebusybiscuit.slimefun4.api.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;

/**
 * This {@link Event} is fired whenever an {@link MachineProcessor} has completed a {@link MachineOperation}.
 *
 * @author poma123
 * @author TheBusyBiscuit
 *
 */
public class AsyncMachineOperationFinishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final BlockPosition position;
    private final MachineProcessor<?> machineProcessor;
    private final MachineOperation machineOperation;

    public <T extends MachineOperation> AsyncMachineOperationFinishEvent(BlockPosition pos, MachineProcessor<T> processor, T operation) {
        super(!Bukkit.isPrimaryThread());

        this.position = pos;
        this.machineProcessor = processor;
        this.machineOperation = operation;
    }

    /**
     * This returns the {@link BlockPosition} of the machine.
     *
     * @return The {@link BlockPosition} of the machine
     */
    @Nonnull
    public BlockPosition getPosition() {
        return position;
    }

    /**
     * The {@link MachineProcessor} instance of the machine.
     *
     * @return The {@link MachineProcessor} instance of the machine
     */
    @Nullable
    public MachineProcessor<?> getProcessor() {
        return machineProcessor;
    }

    /**
     * This returns the used {@link MachineOperation} in the process.
     *
     * @return The {@link MachineOperation} of the process
     */
    @Nullable
    public MachineOperation getOperation() {
        return machineOperation;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
