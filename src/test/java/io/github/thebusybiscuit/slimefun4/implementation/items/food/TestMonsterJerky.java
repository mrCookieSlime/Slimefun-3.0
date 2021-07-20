package io.github.thebusybiscuit.slimefun4.implementation.items.food;

import org.bukkit.Material;
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

class TestMonsterJerky implements SlimefunItemTest<MonsterJerky> {

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
    public MonsterJerky registerSlimefunItem(Slimefun plugin, String id) {
        SlimefunItemStack item = new SlimefunItemStack(id, Material.ROTTEN_FLESH, "&5Test Monster Jerky");
        MonsterJerky jerky = new MonsterJerky(TestUtilities.getCategory(plugin, "monster_jerky"), item, RecipeType.NULL, new ItemStack[9]);
        jerky.register(plugin);
        return jerky;
    }

    @Test
    @DisplayName("Test Monster Jerky giving Saturation and removing Hunger")
    void testConsumptionBehaviour() {
        PlayerMock player = server.addPlayer();
        player.addPotionEffect(PotionEffectType.HUNGER.createEffect(20, 2));
        MonsterJerky jerky = registerSlimefunItem(plugin, "TEST_MONSTER_JERKY");

        simulateConsumption(player, jerky);

        Assertions.assertFalse(player.hasPotionEffect(PotionEffectType.HUNGER));
        Assertions.assertTrue(player.hasPotionEffect(PotionEffectType.SATURATION));
    }

}
