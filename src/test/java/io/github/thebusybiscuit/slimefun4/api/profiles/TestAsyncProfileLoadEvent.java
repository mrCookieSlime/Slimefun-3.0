package io.github.thebusybiscuit.slimefun4.api.profiles;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.api.events.AsyncProfileLoadEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;
import io.github.thebusybiscuit.slimefun4.test.mocks.MockProfile;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.OfflinePlayerMock;

class TestAsyncProfileLoadEvent {

    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test AsyncProfileLoadEvent being thrown")
    void testEventFired() throws InterruptedException {
        server.getPluginManager().clearEvents();

        OfflinePlayer player = new OfflinePlayerMock("EventFire");
        TestUtilities.awaitProfile(player);

        server.getPluginManager().assertEventFired(AsyncProfileLoadEvent.class, Event::isAsynchronous);
    }

    @Test
    @DisplayName("Test AsyncProfileLoadEvent#getProfile()")
    void testEventGetter() throws InterruptedException {
        server.getPluginManager().clearEvents();

        OfflinePlayer player = new OfflinePlayerMock("GetProfile");
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        server.getPluginManager().assertEventFired(AsyncProfileLoadEvent.class, e -> e.getProfile().equals(profile));
    }

    @Test
    @DisplayName("Test Profile Injection")
    void testProfileInjection() throws InterruptedException {
        OfflinePlayer player = new OfflinePlayerMock("SomePlayer");
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        PlayerProfile mockProfile = new MockProfile(player);

        AsyncProfileLoadEvent event = new AsyncProfileLoadEvent(profile);

        Assertions.assertEquals(player.getUniqueId(), event.getPlayerUUID());
        Assertions.assertEquals(profile, event.getProfile());
        Assertions.assertFalse(event.getProfile() instanceof MockProfile);

        event.setProfile(mockProfile);

        Assertions.assertEquals(mockProfile, event.getProfile());
        Assertions.assertTrue(event.getProfile() instanceof MockProfile);
    }

    @Test
    @DisplayName("Test Profile Mismatch")
    void testProfileMismatch() throws InterruptedException {
        OfflinePlayer player = new OfflinePlayerMock("ValidProfile");
        OfflinePlayer player2 = new OfflinePlayerMock("UnrelatedProfile");

        PlayerProfile profile = TestUtilities.awaitProfile(player);
        PlayerProfile profile2 = TestUtilities.awaitProfile(player2);

        AsyncProfileLoadEvent event = new AsyncProfileLoadEvent(profile);

        // Profile is null
        Assertions.assertThrows(IllegalArgumentException.class, () -> event.setProfile(null));

        // UUID mismatch
        Assertions.assertThrows(IllegalArgumentException.class, () -> event.setProfile(profile2));
    }

}
