package io.github.thebusybiscuit.slimefun4.implementation.registration;

import java.time.LocalDate;
import java.time.Month;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.LockedItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.SeasonalItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestCategories {

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
    @DisplayName("Test the Getters for Category")
    void testCategoryGetters() {
        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "getter_test"), new CustomItemStack(Material.DIAMOND_AXE, "&6Testing"));

        Assertions.assertEquals(3, category.getTier());
        Assertions.assertEquals(new NamespacedKey(Slimefun.instance(), "getter_test"), category.getKey());
        Assertions.assertEquals("Testing", category.getUnlocalizedName());
        Assertions.assertEquals(0, category.getItems().size());

        Assertions.assertNull(category.getAddon());
        category.register(plugin);
        Assertions.assertEquals(plugin, category.getAddon());
    }

    @Test
    @DisplayName("Test adding an item to a Category")
    void testAddItem() {
        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "items_test"), new CustomItemStack(Material.DIAMOND_AXE, "&6Testing"));
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "CATEGORY_ITEMS_TEST_ITEM", new CustomItemStack(Material.BAMBOO, "&6Test Bamboo"));
        item.setCategory(category);
        item.register(plugin);
        item.load();

        Assertions.assertTrue(category.getItems().contains(item));
        Assertions.assertEquals(1, category.getItems().size());

        // Size must still be 1 since we disallow duplicates
        item.setCategory(category);

        Assertions.assertEquals(1, category.getItems().size());
        Assertions.assertThrows(IllegalArgumentException.class, () -> category.add(null));
    }

    @Test
    @DisplayName("Test hidden Categories")
    void testHidden() {
        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "hiddenCategory"), new ItemStack(Material.BEACON));
        Player player = server.addPlayer();

        // Empty Categories are also hidden
        Assertions.assertTrue(category.isHidden(player));

        SlimefunItem disabledItem = TestUtilities.mockSlimefunItem(plugin, "DISABLED_CATEGORY_ITEM", new CustomItemStack(Material.BEETROOT, "&4Disabled"));
        Slimefun.getItemCfg().setValue("DISABLED_CATEGORY_ITEM.enabled", false);
        disabledItem.setCategory(category);
        disabledItem.register(plugin);
        disabledItem.load();

        // A disabled Item should also make the Category hide
        Assertions.assertTrue(category.isHidden(player));

        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "CATEGORY_HIDDEN_TEST", new CustomItemStack(Material.BAMBOO, "&6Test Bamboo"));
        item.setCategory(category);
        item.setHidden(true);
        item.register(plugin);
        item.load();

        // A hidden Item should also make the Category hide
        Assertions.assertTrue(category.isHidden(player));

        item.setHidden(false);
        Assertions.assertFalse(category.isHidden(player));
    }

    @Test
    @DisplayName("Test Category#contains")
    void testContains() {
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "CATEGORY_TEST_ITEM_2", new CustomItemStack(Material.BOW, "&6Test Bow"));
        item.register(plugin);
        item.load();

        ItemGroup category = item.getItemGroup();

        Assertions.assertTrue(category.contains(item));
        Assertions.assertFalse(category.contains(null));

        // Unregistered Item
        Assertions.assertFalse(category.contains(TestUtilities.mockSlimefunItem(plugin, "NULL", new ItemStack(Material.BEDROCK))));
    }

    @Test
    @DisplayName("Test LockedCategory parental locking")
    void testLockedCategoriesParents() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new LockedItemGroup(new NamespacedKey(plugin, "locked"), new CustomItemStack(Material.GOLD_NUGGET, "&6Locked Test"), (NamespacedKey) null));

        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "unlocked"), new CustomItemStack(Material.EMERALD, "&5I am SHERlocked"));
        category.register(plugin);

        ItemGroup unregistered = new ItemGroup(new NamespacedKey(plugin, "unregistered"), new CustomItemStack(Material.EMERALD, "&5I am unregistered"));

        LockedItemGroup locked = new LockedItemGroup(new NamespacedKey(plugin, "locked"), new CustomItemStack(Material.GOLD_NUGGET, "&6Locked Test"), category.getKey(), unregistered.getKey());
        locked.register(plugin);

        Assertions.assertTrue(locked.getParents().contains(category));
        Assertions.assertFalse(locked.getParents().contains(unregistered));

        locked.removeParent(category);
        Assertions.assertFalse(locked.getParents().contains(category));

        Assertions.assertThrows(IllegalArgumentException.class, () -> locked.addParent(locked));
        Assertions.assertThrows(IllegalArgumentException.class, () -> locked.addParent(null));

        locked.addParent(category);
        Assertions.assertTrue(locked.getParents().contains(category));
    }

    @Test
    @DisplayName("Test an unlocked LockedCategory")
    void testLockedCategoriesUnlocking() throws InterruptedException {
        Player player = server.addPlayer();
        PlayerProfile profile = TestUtilities.awaitProfile(player);

        Assertions.assertThrows(IllegalArgumentException.class, () -> new LockedItemGroup(new NamespacedKey(plugin, "locked"), new CustomItemStack(Material.GOLD_NUGGET, "&6Locked Test"), (NamespacedKey) null));

        ItemGroup category = new ItemGroup(new NamespacedKey(plugin, "parent"), new CustomItemStack(Material.EMERALD, "&5I am SHERlocked"));
        category.register(plugin);

        LockedItemGroup locked = new LockedItemGroup(new NamespacedKey(plugin, "locked2"), new CustomItemStack(Material.GOLD_NUGGET, "&6Locked Test"), category.getKey());
        locked.register(plugin);

        // No Items, so it should be unlocked
        Assertions.assertTrue(locked.hasUnlocked(player, profile));

        SlimefunItem item = new SlimefunItem(category, new SlimefunItemStack("LOCKED_CATEGORY_TEST", new CustomItemStack(Material.LANTERN, "&6Test Item for locked categories")), RecipeType.NULL, new ItemStack[9]);
        item.register(plugin);
        item.load();

        Slimefun.getRegistry().setResearchingEnabled(true);
        Research research = new Research(new NamespacedKey(plugin, "cant_touch_this"), 432432, "MC Hammer", 90);
        research.addItems(item);
        research.register();

        Assertions.assertFalse(profile.hasUnlocked(research));
        Assertions.assertFalse(locked.hasUnlocked(player, profile));

        profile.setResearched(research, true);

        Assertions.assertTrue(locked.hasUnlocked(player, profile));
    }

    @Test
    @DisplayName("Test a seasonal Category")
    void testSeasonalCategories() {
        // Category with current Month
        Month month = LocalDate.now().getMonth();
        SeasonalItemGroup category = new SeasonalItemGroup(new NamespacedKey(plugin, "seasonal"), month, 1, new CustomItemStack(Material.NETHER_STAR, "&cSeasonal Test"));
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "SEASONAL_ITEM", new CustomItemStack(Material.NETHER_STAR, "&dSeasonal Test Star"));
        item.setCategory(category);
        item.register(plugin);
        item.load();

        Player player = server.addPlayer();

        Assertions.assertEquals(month, category.getMonth());
        Assertions.assertFalse(category.isHidden(player));

        // Category with future Month
        SeasonalItemGroup category2 = new SeasonalItemGroup(category.getKey(), month.plus(6), 1, new CustomItemStack(Material.MILK_BUCKET, "&dSeasonal Test"));
        Assertions.assertTrue(category2.isHidden(player));
    }

    @Test
    @DisplayName("Test the FlexCategory")
    void testFlexCategory() {
        FlexItemGroup category = new FlexItemGroup(new NamespacedKey(plugin, "flex"), new CustomItemStack(Material.REDSTONE, "&4Weird flex but ok")) {

            @Override
            public void open(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
                // Nothing
            }

            @Override
            public boolean isVisible(Player p, PlayerProfile profile, SlimefunGuideMode layout) {
                return true;
            }
        };

        Player player = server.addPlayer();
        Assertions.assertFalse(category.isHidden(player));

        Assertions.assertThrows(UnsupportedOperationException.class, () -> category.add(null));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> category.contains(null));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> category.remove(null));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> category.getItems());
    }
}
