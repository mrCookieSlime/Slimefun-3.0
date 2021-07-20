package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestPlayerProfileListener {

    private static Slimefun plugin;
    private static PlayerProfileListener listener;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new PlayerProfileListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test PlayerProfile being marked for deletion when Player leaves")
    void testPlayerLeave() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        PlayerQuitEvent event = new PlayerQuitEvent(player, "bye");
        listener.onDisconnect(event);

        Assertions.assertTrue(profile.isMarkedForDeletion());
    }

    @Test
    @DisplayName("Test PlayerProfile being unloaded when Player leaves")
    void testUnloadedPlayerLeave() {
        Player player = server.addPlayer();
        PlayerQuitEvent event = new PlayerQuitEvent(player, "bye");
        listener.onDisconnect(event);

        Assertions.assertFalse(PlayerProfile.find(player).isPresent());
    }

    @Test
    @DisplayName("Test PlayerProfile being marked for deletion when Player is kicked")
    void testPlayerKick() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        PlayerKickEvent event = new PlayerKickEvent(player, "You're not welcome anymore", "bye");
        listener.onKick(event);

        Assertions.assertTrue(profile.isMarkedForDeletion());
    }

    @Test
    @DisplayName("Test PlayerProfile being unloaded when Player is kicked")
    void testUnloadedPlayerKick() {
        Player player = server.addPlayer();
        PlayerKickEvent event = new PlayerKickEvent(player, "You're not welcome anymore", "bye");
        listener.onKick(event);

        Assertions.assertFalse(PlayerProfile.find(player).isPresent());
    }

}
