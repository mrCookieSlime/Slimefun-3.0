package me.mrCookieSlime.Slimefun.Objects.handlers;

import java.util.Optional;

import org.bukkit.block.Block;

import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;

public abstract class BlockTicker implements ItemHandler {

    protected boolean unique = true;

    public void update() {
        if (unique) {
            uniqueTick();
            unique = false;
        }
    }

    @Override
    public Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
        if (!item.getItem().getType().isBlock()) {
            return Optional.of(new IncompatibleItemHandlerException("Only Materials that are blocks can have a BlockTicker.", item, this));
        }

        if (item instanceof NotPlaceable) {
            return Optional.of(new IncompatibleItemHandlerException("Only Slimefun items that are not marked as 'NotPlaceable' can have a BlockTicker.", item, this));
        }

        return Optional.empty();
    }

    /**
     * This method must be overridden to define whether a Block
     * needs to be run on the main server thread (World Manipulation requires that)
     * 
     * @return Whether this task should run on the main server thread
     */
    public abstract boolean isSynchronized();

    /**
     * This method is called every tick for every block
     * 
     * @param b
     *            The {@link Block} that was ticked
     * @param item
     *            The corresponding {@link SlimefunItem}
     * @param data
     *            The data stored in this {@link Block}
     */
    public abstract void tick(Block b, SlimefunItem item, Config data);

    /**
     * This method is called every tick but not per-block and only once.
     */
    public void uniqueTick() {
        // Override this method and fill it with content
    }

    @Override
    public Class<? extends ItemHandler> getIdentifier() {
        return BlockTicker.class;
    }

    /**
     * This method resets the 'unique' flag for {@link BlockTicker#uniqueTick()}
     */
    public void startNewTick() {
        unique = true;
    }

}
