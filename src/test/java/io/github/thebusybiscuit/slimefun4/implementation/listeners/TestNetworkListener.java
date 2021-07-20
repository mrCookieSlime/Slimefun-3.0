package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.thebusybiscuit.slimefun4.api.network.Network;
import io.github.thebusybiscuit.slimefun4.core.networks.NetworkManager;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestNetworkListener {

    private static Slimefun plugin;
    private static NetworkListener listener;
    private static NetworkManager manager = new NetworkManager(80);
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new NetworkListener(plugin, manager);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test Network updates on Block breaking")
    void testBlockBreak() {
        World world = server.addSimpleWorld("Simple Network Listener World");
        Location l = new Location(world, 3000, 120, -500);

        Network network = Mockito.mock(Network.class);
        Mockito.when(network.connectsTo(l)).thenReturn(true);
        manager.registerNetwork(network);

        listener.onBlockBreak(new BlockBreakEvent(l.getBlock(), server.addPlayer()));
        Mockito.verify(network).markDirty(l);
    }

    @Test
    @DisplayName("Test Network updates on Block placing")
    void testBlockPlace() {
        World world = server.addSimpleWorld("Simple Network Listener World");
        Location l = new Location(world, 3000, 120, -500);
        Location l2 = new Location(world, 3000, 121, -500);

        Network network = Mockito.mock(Network.class);
        Mockito.when(network.connectsTo(l)).thenReturn(true);
        manager.registerNetwork(network);

        BlockState state = Mockito.mock(BlockState.class);
        listener.onBlockPlace(new BlockPlaceEvent(l.getBlock(), state, l2.getBlock(), new ItemStack(Material.AIR), server.addPlayer(), true, EquipmentSlot.HAND));
        Mockito.verify(network).markDirty(l);
    }

}
