package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.ItemEntityMock;
import be.seeseemelk.mockbukkit.inventory.HopperInventoryMock;

class TestItemPickupListener {

    private static Slimefun plugin;
    private static ItemPickupListener listener;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new ItemPickupListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testNoPickupFlagForEntities(boolean flag) {
        Player player = server.addPlayer();
        Item item = new ItemEntityMock(server, UUID.randomUUID(), new ItemStack(Material.COMPASS));

        if (flag) {
            SlimefunUtils.markAsNoPickup(item, "Unit Test");
        }

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 1);
        listener.onEntityPickup(event);

        Assertions.assertEquals(flag, event.isCancelled());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testNoPickupFlagForInventories(boolean flag) {
        Inventory inventory = new HopperInventoryMock(null);
        Item item = new ItemEntityMock(server, UUID.randomUUID(), new ItemStack(Material.COMPASS));

        if (flag) {
            SlimefunUtils.markAsNoPickup(item, "Unit Test");
        }

        InventoryPickupItemEvent event = new InventoryPickupItemEvent(inventory, item);
        listener.onHopperPickup(event);

        Assertions.assertEquals(flag, event.isCancelled());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testAltarProbeForEntities(boolean flag) {
        Player player = server.addPlayer();
        ItemStack stack;

        if (flag) {
            stack = new CustomItemStack(Material.DIAMOND, AncientPedestal.ITEM_PREFIX + System.nanoTime());
        } else {
            stack = new CustomItemStack(Material.DIAMOND, "&5Just a normal named diamond");
        }

        AtomicBoolean removed = new AtomicBoolean(false);
        Item item = new ItemEntityMock(server, UUID.randomUUID(), stack) {

            @Override
            public void remove() {
                removed.set(true);
            }
        };

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 1);
        listener.onEntityPickup(event);

        Assertions.assertEquals(flag, event.isCancelled());
        Assertions.assertEquals(flag, removed.get());
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testAltarProbeForInventories(boolean flag) {
        Inventory inventory = new HopperInventoryMock(null);
        ItemStack stack;

        if (flag) {
            stack = new CustomItemStack(Material.DIAMOND, AncientPedestal.ITEM_PREFIX + System.nanoTime());
        } else {
            stack = new CustomItemStack(Material.DIAMOND, "&5Just a normal named diamond");
        }

        AtomicBoolean removed = new AtomicBoolean(false);
        Item item = new ItemEntityMock(server, UUID.randomUUID(), stack) {

            @Override
            public void remove() {
                removed.set(true);
            }
        };

        InventoryPickupItemEvent event = new InventoryPickupItemEvent(inventory, item);
        listener.onHopperPickup(event);

        Assertions.assertEquals(flag, event.isCancelled());
        Assertions.assertEquals(flag, removed.get());
    }

}
