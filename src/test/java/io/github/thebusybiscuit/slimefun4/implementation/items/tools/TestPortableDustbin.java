package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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

class TestPortableDustbin implements SlimefunItemTest<PortableDustbin> {

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
    public PortableDustbin registerSlimefunItem(Slimefun plugin, String id) {
        SlimefunItemStack item = new SlimefunItemStack(id, Material.BUCKET, "&4Test Dustbin");
        PortableDustbin dustbin = new PortableDustbin(TestUtilities.getCategory(plugin, "dustbin"), item, RecipeType.NULL, new ItemStack[9]);
        dustbin.register(plugin);
        return dustbin;
    }

    @Test
    @DisplayName("Test Dustbin opening an empty Inventory")
    void testRightClickBehaviour() {
        Player player = server.addPlayer();
        PortableDustbin dustbin = registerSlimefunItem(plugin, "TEST_PORTABLE_DUSTBIN");

        simulateRightClick(player, dustbin);

        // We expect an empty Inventory to be open now
        Inventory openInventory = player.getOpenInventory().getTopInventory();
        Assertions.assertTrue(openInventory.isEmpty());
    }

}
