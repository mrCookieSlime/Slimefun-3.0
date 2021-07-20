package io.github.thebusybiscuit.slimefun4.core.researching;

import java.util.Arrays;

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

class TestProfileResearches {

    private static ServerMock server;
    private static Slimefun plugin;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test setting a Research as unlocked")
    void testSetResearched() throws InterruptedException {
        Slimefun.getRegistry().setResearchingEnabled(true);

        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Assertions.assertThrows(IllegalArgumentException.class, () -> profile.setResearched(null, true));

        NamespacedKey key = new NamespacedKey(plugin, "player_profile_test");
        Research research = new Research(key, 250, "Test", 100);
        research.register();

        Assertions.assertFalse(profile.isDirty());
        profile.setResearched(research, true);
        Assertions.assertTrue(profile.isDirty());

        Assertions.assertTrue(profile.hasUnlocked(research));
    }

    @Test
    @DisplayName("Test checking if Research is unlocked")
    void testHasUnlocked() throws InterruptedException {
        Slimefun.getRegistry().setResearchingEnabled(true);

        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        NamespacedKey key = new NamespacedKey(plugin, "player_profile_test");
        Research research = new Research(key, 280, "Test", 100);
        research.register();

        profile.setResearched(research, true);

        Assertions.assertTrue(profile.hasUnlocked(research));
        Assertions.assertTrue(profile.hasUnlocked(null));

        profile.setResearched(research, false);
        Assertions.assertFalse(profile.hasUnlocked(research));

        // Researches are disabled now, so this method should pass
        // Whether Research#isEnabled() works correctly is covered elsewhere
        Slimefun.getRegistry().setResearchingEnabled(false);
        Assertions.assertTrue(profile.hasUnlocked(research));
    }

    @Test
    @DisplayName("Test getting all unlocked Researches")
    void testGetResearches() throws InterruptedException {
        Slimefun.getRegistry().setResearchingEnabled(true);

        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Assertions.assertTrue(profile.getResearches().isEmpty());

        NamespacedKey key = new NamespacedKey(plugin, "player_profile_test");
        Research research = new Research(key, 260, "Test", 100);
        research.register();

        profile.setResearched(research, true);
        Assertions.assertIterableEquals(Arrays.asList(research), profile.getResearches());

        profile.setResearched(research, false);
        Assertions.assertTrue(profile.getResearches().isEmpty());
    }

}
