package io.github.thebusybiscuit.slimefun4.api.profiles;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestGuideHistory {

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
    @DisplayName("Test Guide History being empty by default")
    void testDefaults() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Assertions.assertNotNull(profile.getGuideHistory());
        Assertions.assertEquals(0, profile.getGuideHistory().size());

        Assertions.assertThrows(IllegalArgumentException.class, () -> new GuideHistory(null));
    }

    @Test
    @DisplayName("Test adding a search term to the history")
    void testSearchTerm() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        GuideHistory history = profile.getGuideHistory();

        Assertions.assertThrows(IllegalArgumentException.class, () -> history.add((String) null));

        Assertions.assertEquals(0, history.size());
        history.add("Walshrus");
        Assertions.assertEquals(1, history.size());
    }

    @Test
    @DisplayName("Test clearing the Guide History")
    void testClear() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        GuideHistory history = profile.getGuideHistory();

        Assertions.assertEquals(0, history.size());
        history.add("Walshrus");
        Assertions.assertEquals(1, history.size());
        history.clear();
        Assertions.assertEquals(0, history.size());
    }

    @Test
    @DisplayName("Test adding a SlimefunItem to Guide History")
    void testSlimefunItem() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        GuideHistory history = profile.getGuideHistory();

        Assertions.assertThrows(IllegalArgumentException.class, () -> history.add((SlimefunItem) null));

        Assertions.assertEquals(0, history.size());

        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "HISTORIC_ITEM", new CustomItemStack(Material.DIORITE, "&4I am really running out of ideas for item names"));
        history.add(item);

        Assertions.assertEquals(1, history.size());
    }

    @Test
    @DisplayName("Test adding an ItemStack to Guide History")
    void testItem() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        GuideHistory history = profile.getGuideHistory();

        Assertions.assertThrows(IllegalArgumentException.class, () -> history.add((ItemStack) null, 1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> history.add(new ItemStack(Material.DIAMOND), -20));

        Assertions.assertEquals(0, history.size());
        history.add(new ItemStack(Material.REDSTONE), 1);
        Assertions.assertEquals(1, history.size());

        // This should not add a new entry but rather only update the page
        history.add(new ItemStack(Material.REDSTONE), 2);
        Assertions.assertEquals(1, history.size());
    }

    @Test
    @DisplayName("Test adding a Category to Guide History")
    void testCategory() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        GuideHistory history = profile.getGuideHistory();

        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "category_guide_history"), new CustomItemStack(Material.BEDROCK, "&4Can't touch this"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> history.add((ItemGroup) null, 1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> history.add(category, -20));

        Assertions.assertEquals(0, history.size());
        history.add(category, 1);
        Assertions.assertEquals(1, history.size());

        // This should not add a new entry but rather only update the page
        history.add(category, 2);
        Assertions.assertEquals(1, history.size());
    }

}
