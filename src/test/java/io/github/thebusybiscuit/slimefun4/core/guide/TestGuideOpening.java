package io.github.thebusybiscuit.slimefun4.core.guide;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestGuideOpening {

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

    @ParametersAreNonnullByDefault
    private @Nonnull PlayerProfile prepare(SlimefunGuideImplementation guide, Consumer<GuideHistory> consumer) throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        GuideHistory history = profile.getGuideHistory();
        consumer.accept(history);
        history.openLastEntry(guide);
        return profile;
    }

    @Test
    @DisplayName("Test if the Slimefun Guide Main Menu can be opened from the History")
    void testOpenMainMenu() throws InterruptedException {
        SlimefunGuideImplementation guide = Mockito.mock(SlimefunGuideImplementation.class);
        PlayerProfile profile = prepare(guide, history -> {});
        Mockito.verify(guide).openMainMenu(profile, 1);
    }

    @Test
    @DisplayName("Test if a Category can be opened from the History")
    void testOpenCategory() throws InterruptedException {
        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "history_category"), new CustomItemStack(Material.BLUE_TERRACOTTA, "&9Testy test"));

        SlimefunGuideImplementation guide = Mockito.mock(SlimefunGuideImplementation.class);
        PlayerProfile profile = prepare(guide, history -> history.add(category, 1));
        Mockito.verify(guide).openCategory(profile, category, 1);
    }

    @Test
    @DisplayName("Test if a SlimefunItem can be viewed from the History")
    void testOpenSlimefunItem() throws InterruptedException {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "OPEN_SLIMEFUN_ITEM", new CustomItemStack(Material.PRISMARINE_SHARD, "&5Some Shard I guess"));

        SlimefunGuideImplementation guide = Mockito.mock(SlimefunGuideImplementation.class);
        PlayerProfile profile = prepare(guide, history -> history.add(item));
        Mockito.verify(guide).displayItem(profile, item, false);
    }

    @Test
    @DisplayName("Test if an ItemStack can be viewed from the History")
    void testOpenItemStack() throws InterruptedException {
        ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);

        SlimefunGuideImplementation guide = Mockito.mock(SlimefunGuideImplementation.class);
        PlayerProfile profile = prepare(guide, history -> history.add(item, 1));
        Mockito.verify(guide).displayItem(profile, item, 1, false);
    }

    @Test
    @DisplayName("Test if the Slimefun Search can be opened from the History")
    void testOpenSearch() throws InterruptedException {
        String query = "electric";

        SlimefunGuideImplementation guide = Mockito.mock(SlimefunGuideImplementation.class);
        PlayerProfile profile = prepare(guide, history -> history.add(query));
        Mockito.verify(guide).openSearch(profile, query, false);
    }

    @Test
    @DisplayName("Test if the Back button works")
    void testGoBack() throws InterruptedException {
        Player player = server.addPlayer();

        String query = "electric";
        String query2 = "cargo";

        SlimefunGuideImplementation guide = Mockito.mock(SlimefunGuideImplementation.class);

        PlayerProfile profile = TestUtilities.awaitProfile(player);
        GuideHistory history = profile.getGuideHistory();
        history.add(query);
        history.add(query2);
        history.goBack(guide);
        Mockito.verify(guide).openSearch(profile, query, false);
    }

}
