package io.github.thebusybiscuit.slimefun4.core.attributes;

import javax.annotation.Nonnull;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;

/**
 * This {@link ItemAttribute} marks a {@link SlimefunItem} as a {@link MachineProcessHolder}.
 * A {@link MachineProcessHolder} can hold a {@link MachineProcessor} which is responsible for
 * handling any {@link MachineOperation}.
 * 
 * @author TheBusyBiscuit
 * 
 * @param <T>
 *            The type of {@link MachineOperation} the {@link MachineProcessor} should hold.
 */
public interface MachineProcessHolder<T extends MachineOperation> extends ItemAttribute {

    /**
     * This method returns our {@link MachineProcessor} instance.
     * 
     * @return Our {@link MachineProcessor}
     */
    @Nonnull
    MachineProcessor<T> getMachineProcessor();

}
