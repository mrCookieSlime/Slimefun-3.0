package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.stream.Stream;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

class TestSlimefunGuideListener {

    private static Slimefun plugin;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @DisplayName("Test Slimefun Guides being given on first join")
    @MethodSource("cartesianBooleans")
    void testFirstJoin(boolean hasPlayedBefore, boolean giveSlimefunGuide) {
        SlimefunGuideListener listener = new SlimefunGuideListener(plugin, giveSlimefunGuide);
        PlayerMock player = new PlayerMock(server, "CanIHazGuide");

        if (hasPlayedBefore) {
            player.setLastPlayed(System.currentTimeMillis());
        }

        PlayerJoinEvent event = new PlayerJoinEvent(player, "CanIHazGuide has joined and wants sum guide");
        listener.onJoin(event);

        ItemStack guide = SlimefunGuide.getItem(SlimefunGuide.getDefaultMode());
        Assertions.assertEquals(!hasPlayedBefore && giveSlimefunGuide, hasSlimefunGuide(player, guide));
    }

    private boolean hasSlimefunGuide(Player player, ItemStack guide) {
        return SlimefunUtils.isItemSimilar(player.getInventory().getItem(0), guide, true);
    }

    /**
     * This returns an {@link Arguments} {@link Stream} of boolean combinations.
     * It performs a cartesian product on two boolean sets.
     * 
     * @return a {@link Stream} of {@link Arguments}
     */
    private static Stream<Arguments> cartesianBooleans() {
        Stream<Boolean> stream = Stream.of(true, false);
        return stream.flatMap(a -> Stream.of(true, false).map(b -> Arguments.of(a, b)));
    }

}
