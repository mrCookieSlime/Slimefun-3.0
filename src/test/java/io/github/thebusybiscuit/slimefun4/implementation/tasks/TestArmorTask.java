package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.SlimefunArmorPiece;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

class TestArmorTask {

    private static ServerMock server;
    private static SlimefunPlugin plugin;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(SlimefunPlugin.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test Slimefun Armor Effects")
    void testSlimefunArmor() throws InterruptedException {
        Player player = server.addPlayer();
        TestUtilities.awaitProfile(player);

        // Setting the time to noon, to exclude the Solar Helmet check
        player.getWorld().setTime(16000);

        PotionEffect[] effects = { new PotionEffect(PotionEffectType.SPEED, 50, 3), new PotionEffect(PotionEffectType.SATURATION, 128, 12) };

        SlimefunItemStack helmet = new SlimefunItemStack("HELMET_FLEX", Material.IRON_HELMET, "&bSuper cool Helmet");
        SlimefunArmorPiece armor = new SlimefunArmorPiece(TestUtilities.getCategory(plugin, "armor_test"), helmet, RecipeType.NULL, new ItemStack[9], effects);
        armor.register(plugin);

        player.getInventory().setHelmet(helmet.clone());
        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        new ArmorTask().run();

        // Check if all Potion Effects were applied
        Assertions.assertTrue(player.getActivePotionEffects().containsAll(Arrays.asList(effects)));
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
