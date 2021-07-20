package io.github.thebusybiscuit.slimefun4.core.researching;

import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerPreResearchEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestResearches {

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
    @DisplayName("Test Getter methods for Research class")
    void testResearchGetters() {
        NamespacedKey key = new NamespacedKey(plugin, "getter_test");
        Research research = new Research(key, 0, "Test", 100);
        research.register();

        Assertions.assertEquals(key, research.getKey());
        Assertions.assertEquals(100, research.getCost());

        Assertions.assertFalse(Research.getResearch(null).isPresent());
        Assertions.assertFalse(Research.getResearch(new NamespacedKey(plugin, "null")).isPresent());

        Assertions.assertTrue(Research.getResearch(key).isPresent());
        Assertions.assertEquals(research, Research.getResearch(key).get());
    }

    @Test
    @DisplayName("Test cost of Researches")
    void testResearchCost() {
        NamespacedKey key = new NamespacedKey(plugin, "cost_test");
        Research research = new Research(key, 5, "Cost Test", 100);

        Assertions.assertEquals(100, research.getCost());

        research.setCost(3000);
        Assertions.assertEquals(3000, research.getCost());

        // Negative values are not allowed
        Assertions.assertThrows(IllegalArgumentException.class, () -> research.setCost(-100));
    }

    @Test
    @DisplayName("Test registering a Research")
    void testResearchRegistration() {
        NamespacedKey key = new NamespacedKey(plugin, "test_research");
        Research research = new Research(key, 1, "Test", 100);
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "RESEARCH_TEST", new CustomItemStack(Material.TORCH, "&bResearch Test"));
        research.addItems(item, null);
        research.register();

        Slimefun.getRegistry().setResearchingEnabled(true);

        Assertions.assertTrue(research.isEnabled());
        Assertions.assertEquals(research, item.getResearch());
        Assertions.assertTrue(Slimefun.getRegistry().getResearches().contains(research));

        Optional<Research> optional = Research.getResearch(key);
        Assertions.assertTrue(optional.isPresent());
        Assertions.assertEquals(research, optional.get());
    }

    @Test
    @DisplayName("Test disabling a Research")
    void testDisabledResearch() {
        NamespacedKey key = new NamespacedKey(plugin, "disabled_research");
        Research research = new Research(key, 2, "Test", 100);
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "RESEARCH_TEST", new CustomItemStack(Material.TORCH, "&bResearch Test"));
        research.addItems(item);

        Slimefun.getRegistry().setResearchingEnabled(true);
        Slimefun.getResearchCfg().setValue(key.getNamespace() + '.' + key.getKey() + ".enabled", false);
        research.register();

        Assertions.assertFalse(research.isEnabled());
        Assertions.assertNull(item.getResearch());
    }

    @Test
    @DisplayName("Test disabling Researches server-wide")
    void testResearchGloballyDisabled() {
        NamespacedKey key = new NamespacedKey(plugin, "globally_disabled_research");
        Research research = new Research(key, 3, "Test", 100);

        Slimefun.getRegistry().setResearchingEnabled(true);
        Assertions.assertTrue(research.isEnabled());
        Slimefun.getRegistry().setResearchingEnabled(false);
        Assertions.assertFalse(research.isEnabled());
        Slimefun.getRegistry().setResearchingEnabled(true);
    }

    @Test
    @DisplayName("Test adding items to a Research")
    void testAddItems() {
        NamespacedKey key = new NamespacedKey(plugin, "add_items_to_research");
        Research research = new Research(key, 17, "Test", 100);
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "RESEARCH_ITEMS_TEST", new CustomItemStack(Material.LAPIS_LAZULI, "&9Adding items is fun"));
        item.register(plugin);

        research.addItems(item.getItem(), null);

        Assertions.assertTrue(research.getAffectedItems().contains(item));
        Assertions.assertEquals(research, item.getResearch());
    }

    @Test
    void testPlayerCanUnlockDisabledResearch() {
        Slimefun.getRegistry().setResearchingEnabled(false);

        Player player = server.addPlayer();
        NamespacedKey key = new NamespacedKey(plugin, "disabled_unlockable_research");
        Research research = new Research(key, 567, "Test", 100);

        Assertions.assertTrue(research.canUnlock(player));

        Slimefun.getRegistry().setResearchingEnabled(true);
    }

    @Test
    @DisplayName("Test 'free creative researching' option")
    void testFreeCreativeResearch() {
        Slimefun.getRegistry().setResearchingEnabled(true);

        Player player = server.addPlayer();
        NamespacedKey key = new NamespacedKey(plugin, "free_creative_research");
        Research research = new Research(key, 153, "Test", 100);

        Slimefun.getRegistry().setFreeCreativeResearchingEnabled(false);

        player.setGameMode(GameMode.SURVIVAL);
        Assertions.assertFalse(research.canUnlock(player));

        player.setGameMode(GameMode.CREATIVE);
        Assertions.assertFalse(research.canUnlock(player));

        Slimefun.getRegistry().setFreeCreativeResearchingEnabled(true);

        player.setGameMode(GameMode.SURVIVAL);
        Assertions.assertFalse(research.canUnlock(player));

        player.setGameMode(GameMode.CREATIVE);
        Assertions.assertTrue(research.canUnlock(player));
    }

    @Test
    @DisplayName("Test levels requirement")
    void testUnlockableResearch() {
        Player player = server.addPlayer();
        NamespacedKey key = new NamespacedKey(plugin, "unlocking_research");
        Research research = new Research(key, 235, "Test", 4);

        Assertions.assertFalse(research.canUnlock(player));
        player.setLevel(8);
        Assertions.assertTrue(research.canUnlock(player));
    }

    @Test
    @DisplayName("Test PlayerPreResearchEvent")
    void testPreCanUnlockResearchEvent() throws InterruptedException {
        Slimefun.getRegistry().setResearchingEnabled(true);

        NamespacedKey key = new NamespacedKey(plugin, "simple_research");
        Research research = new Research(key, 250, "Test", 10);
        research.register();

        SlimefunGuideImplementation guide = Mockito.mock(SlimefunGuideImplementation.class);
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        SlimefunItem sfItem = TestUtilities.mockSlimefunItem(plugin, "RESEARCH_TEST", new CustomItemStack(Material.TORCH, "&bResearch Test"));

        research.unlockFromGuide(guide, player, profile, sfItem, sfItem.getItemGroup(), 0);

        server.getPluginManager().assertEventFired(PlayerPreResearchEvent.class, event -> {
            Assertions.assertEquals(player, event.getPlayer());
            Assertions.assertEquals(research, event.getResearch());
            Assertions.assertEquals(sfItem, event.getSlimefunItem());
            Assertions.assertFalse(event.isCancelled());
            return true;
        });
    }

}
