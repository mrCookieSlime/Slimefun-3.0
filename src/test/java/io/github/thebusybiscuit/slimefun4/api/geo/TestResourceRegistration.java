package io.github.thebusybiscuit.slimefun4.api.geo;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.NamespacedKey;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.resources.GEOResourcesSetup;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import be.seeseemelk.mockbukkit.MockBukkit;

@TestMethodOrder(value = OrderAnnotation.class)
class TestResourceRegistration {

    private static Slimefun plugin;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @Order(value = 1)
    @DisplayName("Test default registration for GEO resources")
    void testDefaultResources() {
        Assertions.assertDoesNotThrow(GEOResourcesSetup::setup);
    }

    @Test
    @Order(value = 2)
    @DisplayName("Test double-registration for GEO resources")
    void testDoubleRegistration() {
        Assertions.assertThrows(IllegalArgumentException.class, GEOResourcesSetup::setup);
    }

    @ParametersAreNonnullByDefault
    private @Nonnull GEOResource testResource(NamespacedKey key, String name, ItemStack item, boolean miner, int deviation) {
        Optional<GEOResource> optional = Slimefun.getRegistry().getGEOResources().get(key);
        Assertions.assertTrue(optional.isPresent());

        GEOResource resource = optional.get();
        Assertions.assertEquals(name, resource.getName());
        Assertions.assertEquals(miner, resource.isObtainableFromGEOMiner());
        Assertions.assertTrue(SlimefunUtils.isItemSimilar(resource.getItem(), item, true));
        Assertions.assertEquals(deviation, resource.getMaxDeviation());
        return resource;
    }

    @Test
    @DisplayName("Test oil generation")
    void testOilResource() {
        NamespacedKey key = new NamespacedKey(plugin, "oil");
        GEOResource resource = testResource(key, "Oil", SlimefunItems.OIL_BUCKET, false, 8);

        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.NETHER, Biome.NETHER_WASTES));

        Assertions.assertNotEquals(0, resource.getDefaultSupply(Environment.NORMAL, Biome.BEACH));
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.DESERT) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.MOUNTAINS) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.ICE_SPIKES) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.BADLANDS) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.OCEAN) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.SWAMP) > 10);
        Assertions.assertEquals(10, resource.getDefaultSupply(Environment.NORMAL, Biome.SUNFLOWER_PLAINS));
    }

    @Test
    @DisplayName("Test nether ice generation")
    void testNetherIceResource() {
        NamespacedKey key = new NamespacedKey(plugin, "nether_ice");
        GEOResource resource = testResource(key, "Nether Ice", SlimefunItems.NETHER_ICE, true, 6);

        Assertions.assertNotEquals(0, resource.getDefaultSupply(Environment.NETHER, Biome.NETHER_WASTES));
        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.NORMAL, Biome.DESERT));
    }

    @Test
    @DisplayName("Test uranium generation")
    void testUraniumResource() {
        NamespacedKey key = new NamespacedKey(plugin, "uranium");
        GEOResource resource = testResource(key, "Small Chunks of Uranium", SlimefunItems.SMALL_URANIUM, true, 2);

        Assertions.assertNotEquals(0, resource.getDefaultSupply(Environment.NORMAL, Biome.MOUNTAINS));
        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.NETHER, Biome.NETHER_WASTES));
        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.THE_END, Biome.THE_END));
    }

    @Test
    @DisplayName("Test salt generation")
    void testSaltResource() {
        NamespacedKey key = new NamespacedKey(plugin, "salt");
        GEOResource resource = testResource(key, "Salt", SlimefunItems.SALT, true, 18);

        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.NETHER, Biome.NETHER_WASTES));
        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.THE_END, Biome.THE_END));

        Assertions.assertNotEquals(0, resource.getDefaultSupply(Environment.NORMAL, Biome.MOUNTAINS));
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.BEACH) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.OCEAN) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.SWAMP) > 10);
    }
}
