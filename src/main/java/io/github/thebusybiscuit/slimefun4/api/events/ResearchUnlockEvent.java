package io.github.thebusybiscuit.slimefun4.api.events;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.thebusybiscuit.slimefun4.api.researches.Research;

/**
 * This {@link Event} is called whenever a {@link Player} unlocks a {@link Research}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Research
 *
 */
public class ResearchUnlockEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Research research;
    private boolean cancelled;

    public ResearchUnlockEvent(@Nonnull Player p, @Nonnull Research research) {
        super(!Bukkit.isPrimaryThread());

        Validate.notNull(p, "The Player cannot be null");
        Validate.notNull(research, "Research cannot be null");

        this.player = p;
        this.research = research;
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public Research getResearch() {
        return research;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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
