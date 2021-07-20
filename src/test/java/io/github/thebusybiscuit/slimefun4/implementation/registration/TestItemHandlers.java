package io.github.thebusybiscuit.slimefun4.implementation.registration;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BowShootHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;
import io.github.thebusybiscuit.slimefun4.test.mocks.MockItemHandler;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestItemHandlers {

    private static Slimefun plugin;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test validation for Item Handlers")
    void testIllegalItemHandlers() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "ITEM_HANDLER_TEST", new CustomItemStack(Material.DIAMOND, "&cTest"));
        item.register(plugin);

        Assertions.assertThrows(IllegalArgumentException.class, () -> item.addItemHandler());
        Assertions.assertThrows(IllegalArgumentException.class, () -> item.addItemHandler((MockItemHandler) null));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> item.addItemHandler(new MockItemHandler()));
    }

    @Test
    @DisplayName("Test calling an ItemHandler")
    void testItemHandler() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "ITEM_HANDLER_TEST_2", new CustomItemStack(Material.DIAMOND, "&cTest"));

        MockItemHandler handler = new MockItemHandler();
        item.addItemHandler(handler);

        item.register(plugin);

        Assertions.assertTrue(item.getHandlers().contains(handler));

        AtomicBoolean bool = new AtomicBoolean(false);
        Assertions.assertTrue(item.callItemHandler(MockItemHandler.class, x -> bool.set(true)));
        Assertions.assertTrue(bool.get());

        AtomicBoolean bool2 = new AtomicBoolean(false);
        Assertions.assertFalse(item.callItemHandler(ItemUseHandler.class, x -> bool2.set(true)));
        Assertions.assertFalse(bool2.get());
    }

    @Test
    @DisplayName("Test validation for BowShootHandler")
    void testBowShootHandler() {
        BowShootHandler handler = (e, n) -> {};
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "NOT_A_BOW", new CustomItemStack(Material.KELP, "&bNot a bow!"));

        Optional<IncompatibleItemHandlerException> exception = handler.validate(item);
        Assertions.assertTrue(exception.isPresent());

        SlimefunItem bow = TestUtilities.mockSlimefunItem(plugin, "A_BOW", new CustomItemStack(Material.BOW, "&bA bow!"));
        Optional<IncompatibleItemHandlerException> exception2 = handler.validate(bow);
        Assertions.assertFalse(exception2.isPresent());
    }
}
