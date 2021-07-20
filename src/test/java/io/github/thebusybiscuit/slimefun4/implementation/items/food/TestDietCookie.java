package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;
import io.github.thebusybiscuit.slimefun4.test.presets.SlimefunItemTest;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

class TestDietCookie implements SlimefunItemTest<DietCookie> {

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

    @Override
    public DietCookie registerSlimefunItem(Slimefun plugin, String id) {
        SlimefunItemStack item = new SlimefunItemStack(id, Material.COOKIE, "&5Test Cookie");
        DietCookie cookie = new DietCookie(TestUtilities.getCategory(plugin, "diet_cookie"), item, RecipeType.NULL, new ItemStack[9]);
        cookie.register(plugin);
        return cookie;
    }

    @Test
    @DisplayName("Test Diet Cookies giving Levitation Effect")
    void testConsumptionBehaviour() {
        PlayerMock player = server.addPlayer();
        DietCookie cookie = registerSlimefunItem(plugin, "TEST_DIET_COOKIE");

        simulateConsumption(player, cookie);

        player.assertSoundHeard(Sound.ENTITY_GENERIC_EAT);
        Assertions.assertTrue(player.hasPotionEffect(PotionEffectType.LEVITATION));
    }

}
