package io.github.thebusybiscuit.slimefun4.api.items;

import java.util.Optional;

import javax.annotation.Nonnull;

import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BowShootHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityKillHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemConsumptionHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;

/**
 * An {@link ItemHandler} represents a certain action that a {@link SlimefunItem}
 * can perform.
 * 
 * @author TheBusyBiscuit
 *
 * @see ItemUseHandler
 * @see ItemConsumptionHandler
 * @see BlockUseHandler
 * @see EntityKillHandler
 * @see EntityInteractHandler
 * @see BowShootHandler
 */
@FunctionalInterface
public interface ItemHandler {

    /**
     * This method is used to check whether a given {@link SlimefunItem} is compatible
     * with this {@link ItemHandler}, it will return an {@link IncompatibleItemHandlerException}
     * if the items are not compatible.
     * 
     * @param item
     *            The {@link SlimefunItem} to validate
     * 
     * @return An {@link Optional} describing the result, it will contain an {@link IncompatibleItemHandlerException}
     *         should there be an issue
     */
    @Nonnull
    default Optional<IncompatibleItemHandlerException> validate(@Nonnull SlimefunItem item) {
        return Optional.empty();
    }

    /**
     * This method returns the identifier for this {@link ItemHandler}.
     * We use a {@link Class} identifier to group Item Handlers together.
     * 
     * @return The {@link Class} identifier for this {@link ItemHandler}
     */
    @Nonnull
    Class<? extends ItemHandler> getIdentifier();
}
