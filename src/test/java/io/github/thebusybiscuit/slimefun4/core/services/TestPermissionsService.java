package io.github.thebusybiscuit.slimefun4.core.services;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestPermissionsService {

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

    @ParameterizedTest
    @DisplayName("Test if no permission is set by default")
    @ValueSource(booleans = { false, true })
    void testDefaultPermission(boolean registered) {
        PermissionsService service = new PermissionsService(plugin);
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "PERMISSIONS_TEST", new CustomItemStack(Material.EMERALD, "&bBad omen"));

        if (registered) {
            service.register(Arrays.asList(item), false);
        }

        Optional<String> permission = service.getPermission(item);
        Assertions.assertFalse(permission.isPresent());
    }

    @Test
    @DisplayName("Test if a permission node can be set")
    void testSetPermission() {
        PermissionsService service = new PermissionsService(plugin);
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "PERMISSIONS_TEST", new CustomItemStack(Material.EMERALD, "&bBad omen"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.setPermission(null, null));

        service.setPermission(item, "slimefun.tests");

        Optional<String> permission = service.getPermission(item);
        Assertions.assertTrue(permission.isPresent());
        Assertions.assertEquals("slimefun.tests", permission.get());

        service.setPermission(item, null);
        Assertions.assertFalse(service.getPermission(item).isPresent());

        service.setPermission(item, "none");
        Assertions.assertFalse(service.getPermission(item).isPresent());
    }

    @Test
    @DisplayName("Test if the Player will pass the permissions check if no permission was set")
    void testHasPermissionTrue() {
        PermissionsService service = new PermissionsService(plugin);
        Player player = server.addPlayer();
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "PERMISSIONS_TEST", new CustomItemStack(Material.EMERALD, "&bBad omen"));

        Assertions.assertTrue(service.hasPermission(player, null));
        Assertions.assertTrue(service.hasPermission(player, item));

        service.setPermission(item, "none");
        Assertions.assertTrue(service.hasPermission(player, item));
    }

    @Test
    @DisplayName("Test Players without permissions being denied access")
    void testHasPermissionFalse() {
        PermissionsService service = new PermissionsService(plugin);
        Player player = server.addPlayer();
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "PERMISSIONS_TEST", new CustomItemStack(Material.EMERALD, "&bBad omen"));

        service.setPermission(item, "slimefun.tests");
        Assertions.assertFalse(service.hasPermission(player, item));
    }

    @Test
    @DisplayName("Test Server Operators passing permission checks")
    void testHasPermissionOp() {
        PermissionsService service = new PermissionsService(plugin);
        Player player = server.addPlayer();
        player.setOp(true);
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "PERMISSIONS_TEST", new CustomItemStack(Material.EMERALD, "&bBad omen"));

        Permission permission = new Permission("slimefun.unit.tests.op", PermissionDefault.OP);

        service.setPermission(item, permission.getName());
        Assertions.assertTrue(service.hasPermission(player, item));
    }

    @Test
    @DisplayName("Test Player with permission passing the permission check")
    void testHasPermissionSet() {
        PermissionsService service = new PermissionsService(plugin);
        Player player = server.addPlayer();
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "PERMISSIONS_TEST", new CustomItemStack(Material.EMERALD, "&bBad omen"));

        String permission = "slimefun.unit.tests.permission";
        player.addAttachment(plugin, permission, true);
        service.setPermission(item, permission);

        Assertions.assertTrue(service.hasPermission(player, item));
    }
}
