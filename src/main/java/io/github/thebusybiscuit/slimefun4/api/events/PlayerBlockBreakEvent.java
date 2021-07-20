package io.github.thebusybiscuit.slimefun4.api.events;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This {@link Event} is called when a {@link Player} breaks multiply blocks.
 * 
 * @author NgLoader
 *
 */
public class PlayerBlockBreakEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Block clickedBlock;
    private final List<Block> blocks;
    private boolean cancelled;

    @ParametersAreNonnullByDefault
    public PlayerBlockBreakEvent(Player player, Block clicked, List<Block> blocks) {
        this.player = player;
        this.clickedBlock = clicked;
        this.blocks = blocks;
    }

    /**
     * This returns the specific {@link Block} that was breaked.
     *
     * @return The {@link Block} that was breaked
     */
    @Nonnull
    public Block getClickedBlock() {
        return clickedBlock;
    }

    /**
     * This returns the list of all breaking blocks.
     *
     * @return A list of {@link Block}
     */
    @Nonnull
    public List<Block> getBlocks() {
        return this.blocks;
    }

    @Nonnull
    public Player getPlayer() {
        return this.player;
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
