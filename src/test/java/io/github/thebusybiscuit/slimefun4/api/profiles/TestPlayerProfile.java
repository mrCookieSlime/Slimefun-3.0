package io.github.thebusybiscuit.slimefun4.api.profiles;

import java.util.Iterator;
import java.util.Optional;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.api.items.HashedArmorpiece;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.OfflinePlayerMock;

class TestPlayerProfile {

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
    @DisplayName("Test Player Profiles for offline Players")
    void testOfflinePlayer() throws InterruptedException {
        OfflinePlayer player = new OfflinePlayerMock("Offline Test Player");
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Assertions.assertNotNull(profile);
        Assertions.assertEquals(profile, Slimefun.getRegistry().getPlayerProfiles().get(player.getUniqueId()));

        // This profile should now be in memory and return true
        Assertions.assertTrue(PlayerProfile.get(player, p -> {}));
        Assertions.assertTrue(PlayerProfile.request(player));

        // The Player is offline so this should return null
        Assertions.assertNull(profile.getPlayer());

        Optional<PlayerProfile> optional = PlayerProfile.find(player);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(profile, optional.get());
    }

    @Test
    @DisplayName("Test Player Profiles for online Players")
    void testOnlinePlayer() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Assertions.assertNotNull(profile);
        Assertions.assertEquals(player, profile.getPlayer());
    }

    @Test
    @DisplayName("Test PlayerProfile iterators")
    void testIterator() throws InterruptedException {
        // Clear out any previous profiles
        Iterator<PlayerProfile> clear = PlayerProfile.iterator();
        while (clear.hasNext()) {
            clear.next();
            clear.remove();
        }

        Iterator<PlayerProfile> emptyIterator = PlayerProfile.iterator();
        Assertions.assertFalse(emptyIterator.hasNext());

        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Iterator<PlayerProfile> iterator = PlayerProfile.iterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(profile, iterator.next());
    }

    @Test
    @DisplayName("Test PlayerProfile methods")
    void testAttributes() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Assertions.assertNotNull(profile.getConfig());

        Assertions.assertFalse(profile.isDirty());
        profile.markDirty();
        Assertions.assertTrue(profile.isDirty());

        Assertions.assertFalse(profile.isMarkedForDeletion());
        profile.markForDeletion();
        Assertions.assertTrue(profile.isMarkedForDeletion());

        HashedArmorpiece[] armor = profile.getArmor();
        Assertions.assertEquals(4, armor.length);

        Assertions.assertNotNull(armor[0]);
        Assertions.assertNotNull(armor[1]);
        Assertions.assertNotNull(armor[2]);
        Assertions.assertNotNull(armor[3]);
    }

    @Test
    @DisplayName("Test a non-existent Player Profile")
    void testNotExistentProfile() throws InterruptedException {
        OfflinePlayer player = new OfflinePlayerMock("Offline Test Player 2");

        Assertions.assertFalse(PlayerProfile.find(player).isPresent());

        // This loads the profile asynchronously
        Assertions.assertFalse(PlayerProfile.request(player));
    }

    @Test
    @DisplayName("Test nullability for Player profiles")
    void testNullPlayerGetProfile() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> PlayerProfile.get(null, p -> {}));
    }

    @Test
    @DisplayName("Test PlayerProfile#hashCode()")
    void testHashCode() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Assertions.assertEquals(player.getUniqueId().hashCode(), profile.hashCode());
    }

}
