package io.github.thebusybiscuit.slimefun4.api.exceptions;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * An {@link IdConflictException} is thrown whenever two Addons try to add
 * a {@link SlimefunItem} with the same id.
 * 
 * @author TheBusyBiscuit
 *
 */
public class IdConflictException extends RuntimeException {

    private static final long serialVersionUID = -733012666374895255L;

    /**
     * Constructs a new {@link IdConflictException} with the given items.
     * 
     * @param item1
     *            The first {@link SlimefunItem} with this id
     * @param item2
     *            The second {@link SlimefunItem} with this id
     */
    @ParametersAreNonnullByDefault
    public IdConflictException(SlimefunItem item1, SlimefunItem item2) {
        super("Two items have conflicting ids: " + item1.toString() + " and " + item2.toString());
    }

}
