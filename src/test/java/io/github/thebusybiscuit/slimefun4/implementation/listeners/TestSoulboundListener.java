package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.SoulboundItem;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

class TestSoulboundListener {

    private static Slimefun plugin;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        new SoulboundListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @DisplayName("Test if the soulbound item is dropped or not")
    void testItemDrop(boolean soulbound) {
        PlayerMock player = server.addPlayer();
        ItemStack item = new CustomItemStack(Material.DIAMOND_SWORD, "&4Cool Sword");
        SlimefunUtils.setSoulbound(item, soulbound);
        player.getInventory().setItem(6, item);
        player.setHealth(0);

        server.getPluginManager().assertEventFired(EntityDeathEvent.class, event -> {
            return soulbound != event.getDrops().contains(item);
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @DisplayName("Test if soulbound item is dropped if disabled")
    void testItemDropIfItemDisabled(boolean enabled) {
        PlayerMock player = server.addPlayer();

        SlimefunItemStack item = new SlimefunItemStack("SOULBOUND_ITEM_" + (enabled ? "ENABLED" : "DISABLED"), Material.DIAMOND_SWORD, "&5Soulbound Sword");
        SoulboundItem soulboundItem = new SoulboundItem(TestUtilities.getCategory(plugin, "soulbound"), item, RecipeType.NULL, new ItemStack[9]);
        soulboundItem.register(plugin);

        if (!enabled) {
            Slimefun.getWorldSettingsService().setEnabled(player.getWorld(), soulboundItem, false);
        }

        player.getInventory().setItem(0, item);
        player.setHealth(0);

        server.getPluginManager().assertEventFired(EntityDeathEvent.class, event -> {
            // If the item is enabled, we don't want it to drop.
            return enabled == !event.getDrops().contains(item);
        });
        Slimefun.getRegistry().getEnabledSlimefunItems().remove(soulboundItem);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    @DisplayName("Test if soulbound item is returned to player")
    void testItemRecover(boolean soulbound) {
        PlayerMock player = server.addPlayer();
        ItemStack item = new CustomItemStack(Material.DIAMOND_SWORD, "&4Cool Sword");
        SlimefunUtils.setSoulbound(item, soulbound);
        player.getInventory().setItem(6, item);
        player.setHealth(0);
        player.respawn();

        server.getPluginManager().assertEventFired(PlayerRespawnEvent.class, event -> {
            ItemStack stack = player.getInventory().getItem(6);
            return SlimefunUtils.isItemSimilar(stack, item, true) == soulbound;
        });
    }

}
