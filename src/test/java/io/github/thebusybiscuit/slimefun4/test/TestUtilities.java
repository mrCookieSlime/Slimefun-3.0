package io.github.thebusybiscuit.slimefun4.test;

import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.test.mocks.MockSlimefunItem;

public final class TestUtilities {

    private TestUtilities() {}

    @ParametersAreNonnullByDefault
    public static @Nonnull Inventory mockInventory(InventoryType type, ItemStack... contents) {
        Inventory inv = Mockito.mock(Inventory.class);

        when(inv.getType()).thenReturn(type);
        when(inv.getContents()).thenReturn(contents);

        return inv;
    }

    @ParametersAreNonnullByDefault
    public static @Nonnull ItemGroup getCategory(Plugin plugin, String name) {
        return new ItemGroup(new NamespacedKey(plugin, name), new CustomItemStack(Material.NETHER_STAR, "&4Test Category"));
    }

    @ParametersAreNonnullByDefault
    public static @Nonnull SlimefunItem mockSlimefunItem(Plugin plugin, String id, ItemStack item) {
        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "test"), new CustomItemStack(Material.EMERALD, "&4Test Category"));

        return new MockSlimefunItem(category, item, id);
    }

    @ParametersAreNonnullByDefault
    public static @Nonnull VanillaItem mockVanillaItem(Plugin plugin, Material type, boolean enabled) {
        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "test"), new CustomItemStack(Material.EMERALD, "&4Test Category"));
        VanillaItem item = new VanillaItem(category, new ItemStack(type), type.name(), RecipeType.NULL, new ItemStack[9]);
        Slimefun.getItemCfg().setValue(type.name() + ".enabled", enabled);
        return item;
    }

    @ParametersAreNonnullByDefault
    public static @Nonnull PlayerProfile awaitProfile(OfflinePlayer player) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<PlayerProfile> ref = new AtomicReference<>();

        // This loads the profile asynchronously
        Assertions.assertFalse(PlayerProfile.get(player, profile -> {
            ref.set(profile);
            latch.countDown();
        }));

        latch.await(2, TimeUnit.SECONDS);
        return ref.get();
    }
}
