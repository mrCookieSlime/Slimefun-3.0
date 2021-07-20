package io.github.thebusybiscuit.slimefun4.api.gps;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.api.events.WaypointCreateEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestWaypoints {

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
    @DisplayName("Test Waypoints being added to the profile")
    void testAddWaypointToProfile() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Assertions.assertTrue(profile.getWaypoints().isEmpty());
        Waypoint waypoint = new Waypoint(profile, "hello", player.getLocation(), "HELLO");
        profile.addWaypoint(waypoint);
        Assertions.assertTrue(profile.isDirty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> profile.addWaypoint(null));

        Assertions.assertFalse(profile.getWaypoints().isEmpty());
        Assertions.assertEquals(1, profile.getWaypoints().size());
        Assertions.assertEquals(waypoint, profile.getWaypoints().get(0));
    }

    @Test
    @DisplayName("Test Waypoints being removed from the profile")
    void testRemoveWaypointFromProfile() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Waypoint waypoint = new Waypoint(profile, "hello", player.getLocation(), "HELLO");
        profile.addWaypoint(waypoint);
        Assertions.assertEquals(1, profile.getWaypoints().size());

        profile.removeWaypoint(waypoint);
        Assertions.assertThrows(IllegalArgumentException.class, () -> profile.removeWaypoint(null));

        Assertions.assertTrue(profile.getWaypoints().isEmpty());
        Assertions.assertEquals(0, profile.getWaypoints().size());

        // This will not remove it but neither should it throw an error
        profile.removeWaypoint(waypoint);
        Assertions.assertTrue(profile.getWaypoints().isEmpty());
    }

    @Test
    @DisplayName("Verify that two waypoints cannot have the same name")
    void testWaypointAlreadyExisting() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Waypoint waypoint = new Waypoint(profile, "test", player.getLocation(), "Testing");
        profile.addWaypoint(waypoint);

        Assertions.assertEquals(1, profile.getWaypoints().size());
        Assertions.assertThrows(IllegalArgumentException.class, () -> profile.addWaypoint(waypoint));
        Assertions.assertEquals(1, profile.getWaypoints().size());
    }

    @Test
    @DisplayName("Verify that a maximum amount of waypoints is enforced")
    void testTooManyWaypoints() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        for (int i = 0; i < 99; i++) {
            Waypoint waypoint = new Waypoint(profile, String.valueOf(i), player.getLocation(), "Test");
            profile.addWaypoint(waypoint);
        }

        // Size should be capped at 21.
        Assertions.assertEquals(21, profile.getWaypoints().size());
    }

    @Test
    @DisplayName("Verify that a WaypointCreateEvent is thrown")
    void testWaypointEvent() throws InterruptedException {
        GPSNetwork network = new GPSNetwork(plugin);
        Player player = server.addPlayer();
        TestUtilities.awaitProfile(player);

        network.addWaypoint(player, "Hello world", player.getLocation());
        server.getPluginManager().assertEventFired(WaypointCreateEvent.class, event -> event.getPlayer() == player);
    }

    @Test
    @DisplayName("Test equal Waypoints being equal")
    void testWaypointComparison() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Waypoint waypoint = new Waypoint(profile, "waypoint", player.getLocation(), "Test");
        Waypoint same = new Waypoint(profile, "waypoint", player.getLocation(), "Test");
        Waypoint different = new Waypoint(profile, "waypoint_nope", player.getLocation(), "Test2");

        Assertions.assertEquals(waypoint, same);
        Assertions.assertEquals(waypoint.hashCode(), same.hashCode());

        Assertions.assertNotEquals(waypoint, different);
        Assertions.assertNotEquals(waypoint.hashCode(), different.hashCode());
    }

    @Test
    @DisplayName("Test Deathpoints being recognized as Deathpoints")
    void testIsDeathpoint() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Waypoint waypoint = new Waypoint(profile, "waypoint", player.getLocation(), "Some Waypoint");
        Waypoint deathpoint = new Waypoint(profile, "deathpoint", player.getLocation(), "player:death I died");

        Assertions.assertFalse(waypoint.isDeathpoint());
        Assertions.assertTrue(deathpoint.isDeathpoint());
    }
}
