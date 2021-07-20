package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.Color;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.entity.FireworksListener;
import io.github.thebusybiscuit.slimefun4.utils.FireworkUtils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestFireworksListener {

    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        Slimefun plugin = MockBukkit.load(Slimefun.class);
        new FireworksListener(plugin);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test if Fireworks from Research cause no damage")
    void testFireworkDamage() {
        Player player = server.addPlayer();
        Firework firework = FireworkUtils.createFirework(player.getLocation(), Color.BLUE);

        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(firework, player, DamageCause.ENTITY_EXPLOSION, 6.0);
        server.getPluginManager().callEvent(event);
        Assertions.assertTrue(event.isCancelled());
    }

}
