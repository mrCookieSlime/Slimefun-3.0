package io.github.thebusybiscuit.slimefun4.core.researching;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.thebusybiscuit.slimefun4.api.events.ResearchUnlockEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestResearchUnlocking {

    private ServerMock server;
    private Slimefun plugin;

    @BeforeEach
    public void load() throws InterruptedException {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
    }

    @AfterEach
    public void unload() {
        MockBukkit.unmock();
    }

    private Player awaitUnlock(Player player, Research research, boolean instant) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Player> ref = new AtomicReference<>();

        // This loads the profile asynchronously
        research.unlock(player, instant, p -> {
            ref.set(p);
            latch.countDown();
        });

        latch.await(10, TimeUnit.SECONDS);
        return ref.get();
    }

    @ParameterizedTest
    @DisplayName("Test Unlocking Researches")
    @ValueSource(booleans = { true, false })
    void testUnlock(boolean instant) throws InterruptedException {
        Slimefun.getRegistry().setResearchingEnabled(true);
        Player player = server.addPlayer();
        Research research = new Research(new NamespacedKey(plugin, "unlock_me"), 1842, "Unlock me", 500);

        Player p = awaitUnlock(player, research, instant);
        Optional<PlayerProfile> profile = PlayerProfile.find(p);

        server.getPluginManager().assertEventFired(ResearchUnlockEvent.class, event -> {
            Assertions.assertEquals(p, event.getPlayer());
            Assertions.assertEquals(research, event.getResearch());
            Assertions.assertFalse(event.isCancelled());
            return true;
        });

        Assertions.assertEquals(player, p);
        Assertions.assertTrue(profile.isPresent());
        Assertions.assertTrue(profile.get().hasUnlocked(research));
    }

}
