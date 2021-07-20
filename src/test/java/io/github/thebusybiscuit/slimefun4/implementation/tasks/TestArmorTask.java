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

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.RadioactiveItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.SlimefunArmorPiece;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;
import io.github.thebusybiscuit.slimefun4.test.mocks.MockHazmatSuit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestArmorTask {

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
        new ArmorTask(false).run();

        // Check if all Potion Effects were applied
        Assertions.assertTrue(player.getActivePotionEffects().containsAll(Arrays.asList(effects)));
    }

    @ParameterizedTest
    @DisplayName("Test Radiation and Hazmat Suits")
    @MethodSource("cartesianBooleans")
    void testRadiactivity(boolean hazmat, boolean radioactiveFire) throws InterruptedException {
        Player player = server.addPlayer();
        TestUtilities.awaitProfile(player);

        // Setting the time to noon, to exclude the Solar Helmet check
        player.getWorld().setTime(16000);

        ItemGroup category = TestUtilities.getCategory(plugin, "hazmat_suit_test");
        SlimefunItemStack item = new SlimefunItemStack("MOCK_URANIUM_" + String.valueOf(hazmat).toUpperCase(Locale.ROOT) + "_" + String.valueOf(radioactiveFire).toUpperCase(Locale.ROOT), Material.EMERALD, "&aHi, I am deadly");
        new RadioactiveItem(category, Radioactivity.VERY_DEADLY, item, RecipeType.NULL, new ItemStack[9]).register(plugin);

        player.getInventory().setItemInMainHand(item.clone());
        player.getInventory().setItemInOffHand(new ItemStack(Material.EMERALD_ORE));

        if (hazmat) {
            SlimefunItemStack chestplate = new SlimefunItemStack("MOCK_HAZMAT_SUIT_" + String.valueOf(radioactiveFire).toUpperCase(Locale.ROOT), Material.LEATHER_CHESTPLATE, "&4Hazmat Prototype");
            MockHazmatSuit armor = new MockHazmatSuit(category, chestplate);
            armor.register(plugin);

            player.getInventory().setChestplate(chestplate.clone());
        }

        ArmorTask task = new ArmorTask(radioactiveFire);
        task.run();

        // Check if the Player is suffering from radiation
        boolean radiation = player.getActivePotionEffects().containsAll(task.getRadiationEffects());
        Assertions.assertEquals(!hazmat, radiation);
        Assertions.assertEquals(!hazmat && radioactiveFire, player.getFireTicks() > 0);
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
