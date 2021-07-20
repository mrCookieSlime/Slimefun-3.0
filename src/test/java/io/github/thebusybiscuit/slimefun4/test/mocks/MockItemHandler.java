package io.github.thebusybiscuit.slimefun4.test.mocks;

import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;

public class MockItemHandler implements ItemHandler {

    @Override
    public Class<? extends ItemHandler> getIdentifier() {
        return getClass();
    }

}
