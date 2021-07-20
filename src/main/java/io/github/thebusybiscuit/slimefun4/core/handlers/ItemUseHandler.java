package io.github.thebusybiscuit.slimefun4.core.handlers;

import org.bukkit.entity.Player;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * This {@link ItemHandler} is triggered when the {@link SlimefunItem} it was assigned to
 * is right-clicked.
 * 
 * @author TheBusyBiscuit
 *
 * @see ItemHandler
 * @see SimpleSlimefunItem
 * 
 */
@FunctionalInterface
public interface ItemUseHandler extends ItemHandler {

    /**
     * This function is triggered when a {@link Player} right clicks with the assigned {@link SlimefunItem}
     * in his hand.
     * 
     * @param e
     *            The {@link PlayerRightClickEvent} that was triggered
     */
    void onRightClick(PlayerRightClickEvent e);

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return ItemUseHandler.class;
    }

}
