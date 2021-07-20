package io.github.thebusybiscuit.slimefun4.core.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerBackpack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestBackpackCommand {

    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    private boolean hasBackpack(Player player, int id) {
        for (ItemStack item : player.getInventory()) {
            if (SlimefunUtils.isItemSimilar(item, SlimefunItems.RESTORED_BACKPACK, false)) {
                List<String> lore = item.getItemMeta().getLore();

                if (lore.get(2).equals(ChatColor.GRAY + "ID: " + player.getUniqueId() + "#" + id)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Test
    @DisplayName("Test /sf backpack giving a restored backpack")
    void testValidBackpack() throws InterruptedException {
        Player player = server.addPlayer();
        player.setOp(true);
        PlayerProfile profile = TestUtilities.awaitProfile(player);
        PlayerBackpack backpack = profile.createBackpack(54);

        server.execute("slimefun", player, "backpack", player.getName(), String.valueOf(backpack.getId())).assertSucceeded();

        Assertions.assertTrue(hasBackpack(player, backpack.getId()));
    }

    @ParameterizedTest
    @DisplayName("Test /sf backpack with invalid id parameters")
    @ValueSource(strings = { "", "    ", "ABC", "-100", "123456789" })
    void testNonExistentBackpacks(String id) throws InterruptedException {
        Player player = server.addPlayer();
        player.setOp(true);
        TestUtilities.awaitProfile(player);

        server.execute("slimefun", player, "backpack", player.getName(), id).assertSucceeded();

        if (CommonPatterns.NUMERIC.matcher(id).matches()) {
            Assertions.assertFalse(hasBackpack(player, Integer.parseInt(id)));
        }
    }
}
