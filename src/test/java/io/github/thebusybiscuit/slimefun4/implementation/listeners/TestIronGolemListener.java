package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.entity.IronGolemListener;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestIronGolemListener {

    private static Slimefun plugin;
    private static IronGolemListener listener;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new IronGolemListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    private PlayerInteractEntityEvent callIronGolemEvent(EquipmentSlot hand, ItemStack itemInHand) {
        // Fake Player with an Iron Ingot
        Player player = server.addPlayer();

        if (hand == EquipmentSlot.HAND) {
            player.getInventory().setItemInMainHand(itemInHand);
        } else {
            player.getInventory().setItemInOffHand(itemInHand);
        }

        // Fake Iron Golem
        IronGolem golem = Mockito.mock(IronGolem.class);
        Mockito.when(golem.getType()).thenReturn(EntityType.IRON_GOLEM);

        // Fake Event
        PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, golem, hand);
        listener.onIronGolemHeal(event);
        return event;
    }

    @Test
    @DisplayName("Test Iron Golem Healing not being disturbed")
    void testWithIron() {
        // This should heal the Iron Golem
        ItemStack item = new ItemStack(Material.IRON_INGOT);

        PlayerInteractEntityEvent event = callIronGolemEvent(EquipmentSlot.HAND, item);
        Assertions.assertFalse(event.isCancelled());

        PlayerInteractEntityEvent event2 = callIronGolemEvent(EquipmentSlot.OFF_HAND, item);
        Assertions.assertFalse(event2.isCancelled());
    }

    @Test
    @DisplayName("Test Iron Golem Healing with Slimefun Items being cancelled")
    void testWithSlimefunIron() {
        SlimefunItem slimefunItem = TestUtilities.mockSlimefunItem(plugin, "SLIMEFUN_IRON", new CustomItemStack(Material.IRON_INGOT, "&cSlimefun Iron"));
        slimefunItem.register(plugin);

        // The Event should be cancelled, we do not wanna use Slimefun Items for this
        PlayerInteractEntityEvent event = callIronGolemEvent(EquipmentSlot.HAND, slimefunItem.getItem());
        Assertions.assertTrue(event.isCancelled());

        PlayerInteractEntityEvent event2 = callIronGolemEvent(EquipmentSlot.OFF_HAND, slimefunItem.getItem());
        Assertions.assertTrue(event2.isCancelled());
    }

    @Test
    @DisplayName("Test Iron Golem Healing with vanilla items not being disturbed")
    void testWithVanillaIron() {
        VanillaItem item = TestUtilities.mockVanillaItem(plugin, Material.IRON_INGOT, true);
        item.register(plugin);

        PlayerInteractEntityEvent event = callIronGolemEvent(EquipmentSlot.HAND, item.getItem());
        Assertions.assertFalse(event.isCancelled());

        PlayerInteractEntityEvent event2 = callIronGolemEvent(EquipmentSlot.OFF_HAND, item.getItem());
        Assertions.assertFalse(event2.isCancelled());
    }

}
