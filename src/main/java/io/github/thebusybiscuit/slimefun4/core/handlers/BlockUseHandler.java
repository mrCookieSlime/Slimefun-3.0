package io.github.thebusybiscuit.slimefun4.core.handlers;

import java.util.Optional;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;

@FunctionalInterface
public interface BlockUseHandler extends ItemHandler {

    void onRightClick(PlayerRightClickEvent e);

    @Override
    default Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
        if (item instanceof NotPlaceable || !item.getItem().getType().isBlock()) {
            return Optional.of(new IncompatibleItemHandlerException("Only blocks that are not marked as 'NotPlaceable' can have a BlockUseHandler.", item, this));
        }

        return Optional.empty();
    }

    @Override
    default Class<? extends ItemHandler> getIdentifier() {
        return BlockUseHandler.class;
    }

}
