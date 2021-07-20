package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.entity.BeeListener;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestBeeListener {

    private static ServerMock server;
    private static Slimefun plugin;
    private static BeeListener listener;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new BeeListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @DisplayName("Test Bee damage protection")
    @ValueSource(booleans = { true, false })
    void testBeeDamage(boolean hasArmor) throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        if (hasArmor) {
            ItemGroup category = TestUtilities.getCategory(plugin, "bee_suit_test");
            SlimefunItemStack chestplate = new SlimefunItemStack("MOCK_BEE_SUIT", Material.LEATHER_CHESTPLATE, "&cBee Suit Prototype");
            MockBeeProtectionSuit armor = new MockBeeProtectionSuit(category, chestplate);
            armor.register(plugin);

            player.getInventory().setChestplate(chestplate.clone());
            // Force update the cached armor
            profile.getArmor()[1].update(chestplate, armor);
        }

        double damage = 7.5;

        Bee bee = Mockito.mock(Bee.class);
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(bee, player, DamageCause.ENTITY_ATTACK, damage);
        listener.onDamage(event);

        if (hasArmor) {
            Assertions.assertEquals(0, event.getDamage());
        } else {
            Assertions.assertEquals(damage, event.getDamage());
        }
    }

}
