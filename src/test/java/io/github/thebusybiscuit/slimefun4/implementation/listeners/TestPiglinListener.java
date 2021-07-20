package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.entity.PiglinListener;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.ItemEntityMock;

class TestPiglinListener {

    private static Slimefun plugin;
    private static PiglinListener listener;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new PiglinListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    private EntityPickupItemEvent createPickupEvent(ItemStack item) {
        Piglin piglin = Mockito.mock(Piglin.class);
        Mockito.when(piglin.getType()).thenReturn(EntityType.PIGLIN);

        Item itemEntity = new ItemEntityMock(server, UUID.randomUUID(), item);

        return new EntityPickupItemEvent(piglin, itemEntity, 1);
    }

    private PlayerInteractEntityEvent createInteractEvent(EquipmentSlot hand, ItemStack item) {
        Player player = server.addPlayer();

        Piglin piglin = Mockito.mock(Piglin.class);
        Mockito.when(piglin.getType()).thenReturn(EntityType.PIGLIN);
        Mockito.when(piglin.isValid()).thenReturn(true);

        if (hand == EquipmentSlot.OFF_HAND) {
            player.getInventory().setItemInOffHand(item);
        } else {
            player.getInventory().setItemInMainHand(item);
        }

        return new PlayerInteractEntityEvent(player, piglin, hand);
    }

    @Test
    void testPiglinPickup() {
        EntityPickupItemEvent event = createPickupEvent(new ItemStack(Material.GOLD_INGOT));
        listener.onPickup(event);
        Assertions.assertFalse(event.isCancelled());
    }

    @Test
    void testPiglinPickupWithSlimefunItem() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "PIGLIN_PICKUP_MOCK", new CustomItemStack(Material.GOLD_INGOT, "&6Piglin Bait"));
        item.register(plugin);

        EntityPickupItemEvent event = createPickupEvent(item.getItem());
        listener.onPickup(event);
        Assertions.assertTrue(event.isCancelled());
    }

    @ParameterizedTest
    @EnumSource(value = EquipmentSlot.class, names = { "HAND", "OFF_HAND" })
    void testPiglinInteract(EquipmentSlot hand) {
        PlayerInteractEntityEvent event = createInteractEvent(hand, new ItemStack(Material.GOLD_INGOT));
        listener.onInteract(event);
        Assertions.assertFalse(event.isCancelled());
    }

    @ParameterizedTest
    @EnumSource(value = EquipmentSlot.class, names = { "HAND", "OFF_HAND" })
    void testPiglinInteractWithSlimefunItem(EquipmentSlot hand) {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "PIGLIN_GIVE_" + hand.name(), new CustomItemStack(Material.GOLD_INGOT, "&6Piglin Bait"));
        item.register(plugin);

        PlayerInteractEntityEvent event = createInteractEvent(hand, item.getItem());
        listener.onInteract(event);
        Assertions.assertTrue(event.isCancelled());
    }

}
