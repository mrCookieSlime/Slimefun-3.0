package io.github.thebusybiscuit.slimefun4.api.events;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors.Reactor;

/**
 * The {@link ReactorExplodeEvent} is called whenever a reactor explodes.
 * 
 * @author TheBusyBiscuit
 *
 */
public class ReactorExplodeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Location location;
    private final Reactor reactor;

    public ReactorExplodeEvent(@Nonnull Location l, @Nonnull Reactor reactor) {
        Validate.notNull(l, "A Location must be provided");
        Validate.notNull(reactor, "A Reactor cannot be null");

        this.location = l;
        this.reactor = reactor;
    }

    /**
     * This returns the {@link Location} where the reactor exploded.
     * 
     * @return The {@link Location} of this explosion
     */
    @Nonnull
    public Location getLocation() {
        return location;
    }

    /**
     * The {@link SlimefunItem} instance of the exploded reactor.
     * 
     * @return The {@link SlimefunItem} instance
     */
    @Nonnull
    public Reactor getReactor() {
        return reactor;
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
