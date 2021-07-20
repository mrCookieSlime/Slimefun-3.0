package io.github.thebusybiscuit.slimefun4;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestPluginClass {

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
    @DisplayName("Verify that we are in a UNIT_TEST environment")
    void verifyTestEnvironment() {
        MinecraftVersion version = Slimefun.getMinecraftVersion();

        Assertions.assertTrue(plugin.isUnitTest());
        Assertions.assertEquals(MinecraftVersion.UNIT_TEST, version);
        Assertions.assertEquals("Unit Test Environment", version.getName());
    }

    @Test
    @DisplayName("Verify that config files were loaded")
    void testConfigs() {
        Assertions.assertNotNull(Slimefun.getCfg());
        Assertions.assertNotNull(Slimefun.getResearchCfg());
        Assertions.assertNotNull(Slimefun.getItemCfg());
    }

    @Test
    @DisplayName("Test some static Getters")
    void testGetters() {
        Assertions.assertNotNull(Slimefun.getTickerTask());
        Assertions.assertNotNull(Slimefun.getVersion());
        Assertions.assertNotNull(Slimefun.getRegistry());
        Assertions.assertNotNull(Slimefun.getCommand());
        Assertions.assertNotNull(Slimefun.getGPSNetwork());
        Assertions.assertNotNull(Slimefun.getNetworkManager());
        Assertions.assertNotNull(Slimefun.getProfiler());
    }

    @Test
    @DisplayName("Test some Services being not-null")
    void testServicesNotNull() {
        Assertions.assertNotNull(Slimefun.getLocalization());
        Assertions.assertNotNull(Slimefun.getMinecraftRecipeService());
        Assertions.assertNotNull(Slimefun.getItemDataService());
        Assertions.assertNotNull(Slimefun.getItemTextureService());
        Assertions.assertNotNull(Slimefun.getPermissionsService());
        Assertions.assertNotNull(Slimefun.getBlockDataService());
        Assertions.assertNotNull(Slimefun.getIntegrations());
        Assertions.assertNotNull(Slimefun.getWorldSettingsService());
        Assertions.assertNotNull(Slimefun.getGitHubService());
        Assertions.assertNotNull(Slimefun.getUpdater());
        Assertions.assertNotNull(Slimefun.getMetricsService());
    }

    @Test
    @DisplayName("Test some Listeners being not-null")
    void testListenersNotNull() {
        Assertions.assertNotNull(Slimefun.getGrapplingHookListener());
        Assertions.assertNotNull(Slimefun.getBackpackListener());
        Assertions.assertNotNull(Slimefun.getBowListener());
    }

}
