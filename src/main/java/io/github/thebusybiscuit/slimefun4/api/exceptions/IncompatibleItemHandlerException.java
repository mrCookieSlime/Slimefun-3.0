package io.github.thebusybiscuit.slimefun4.api.exceptions;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.plugin.Plugin;

import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;

/**
 * An {@link IncompatibleItemHandlerException} is thrown whenever a {@link Plugin} tried
 * to add an {@link ItemHandler} to a {@link SlimefunItem} despite the {@link SlimefunItem}
 * not allowing an {@link ItemHandler} of that type to be added.
 * 
 * An example for this is the {@link BlockUseHandler}, it can only be added to blocks.
 * So it will throw this exception when it is added to a non-block item.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ItemHandler
 * @see SlimefunItem
 *
 */
public class IncompatibleItemHandlerException extends RuntimeException {

    private static final long serialVersionUID = -6723066421114874138L;

    /**
     * Constructs a new {@link IncompatibleItemHandlerException} with the given {@link SlimefunItem} and
     * {@link ItemHandler}
     * 
     * @param message
     *            The reason why they are incompatible
     * @param item
     *            The {@link SlimefunItem} that was affected by this
     * @param handler
     *            The {@link ItemHandler} which someone tried to add
     */
    @ParametersAreNonnullByDefault
    public IncompatibleItemHandlerException(String message, SlimefunItem item, ItemHandler handler) {
        super("The item handler type: \"" + handler.getIdentifier().getSimpleName() + "\" is not compatible with " + item + " (" + message + ')');
    }

}
