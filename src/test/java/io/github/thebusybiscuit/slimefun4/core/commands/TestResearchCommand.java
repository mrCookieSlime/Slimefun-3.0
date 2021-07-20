package io.github.thebusybiscuit.slimefun4.core.commands;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestResearchCommand {

    private static ServerMock server;

    private static Research research;
    private static Research research2;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();

        Slimefun plugin = MockBukkit.load(Slimefun.class);
        research = new Research(new NamespacedKey(plugin, "command_test"), 999, "Test", 10);
        research.register();

        research2 = new Research(new NamespacedKey(plugin, "command_test_two"), 1000, "Test Two", 10);
        research2.register();
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test /sf research all")
    void testResearchAll() throws InterruptedException {
        Slimefun.getRegistry().setResearchingEnabled(true);
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        server.executeConsole("slimefun", "research", player.getName(), "all").assertSucceeded();

        Assertions.assertTrue(profile.hasUnlocked(research));
        Assertions.assertTrue(profile.hasUnlocked(research2));
    }

    @Test
    @DisplayName("Test /sf research <research id>")
    void testResearchSpecific() throws InterruptedException {
        Slimefun.getRegistry().setResearchingEnabled(true);
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        server.executeConsole("slimefun", "research", player.getName(), research.getKey().toString()).assertSucceeded();

        Assertions.assertTrue(profile.hasUnlocked(research));
        Assertions.assertFalse(profile.hasUnlocked(research2));
    }

    @Test
    @DisplayName("Test /sf research reset")
    void testResearchReset() throws InterruptedException {
        Slimefun.getRegistry().setResearchingEnabled(true);
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        server.executeConsole("slimefun", "research", player.getName(), "all").assertSucceeded();

        Assertions.assertTrue(profile.hasUnlocked(research));
        Assertions.assertTrue(profile.hasUnlocked(research2));

        server.executeConsole("slimefun", "research", player.getName(), "reset").assertSucceeded();

        Assertions.assertFalse(profile.hasUnlocked(research));
        Assertions.assertFalse(profile.hasUnlocked(research2));
    }
}
