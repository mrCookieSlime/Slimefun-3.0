package io.github.thebusybiscuit.slimefun4.core.guide;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.SlimefunGuideItem;

/**
 * This is a static utility class that provides convenient access to the methods
 * of {@link SlimefunGuideImplementation} that abstracts away the actual implementation.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SlimefunGuideImplementation
 * @see SurvivalSlimefunGuide
 *
 */
public final class SlimefunGuide {

    private SlimefunGuide() {}

    @Nonnull
    public static ItemStack getItem(@Nonnull SlimefunGuideMode design) {
        return Slimefun.getRegistry().getSlimefunGuide(design).getItem();
    }

    public static void openCheatMenu(@Nonnull Player p) {
        openMainMenuAsync(p, SlimefunGuideMode.CHEAT_MODE, 1);
    }

    public static void openGuide(@Nonnull Player p, @Nullable ItemStack guide) {
        if (getItem(SlimefunGuideMode.CHEAT_MODE).equals(guide)) {
            openGuide(p, SlimefunGuideMode.CHEAT_MODE);
        } else {
            /*
             * When using /sf cheat or /sf open_guide the ItemStack is null anyway,
             * so we don't even need to check here at this point.
             */
            openGuide(p, SlimefunGuideMode.SURVIVAL_MODE);
        }
    }

    public static void openGuide(@Nonnull Player p, @Nonnull SlimefunGuideMode mode) {
        if (!Slimefun.getWorldSettingsService().isWorldEnabled(p.getWorld())) {
            return;
        }

        Optional<PlayerProfile> optional = PlayerProfile.find(p);

        if (optional.isPresent()) {
            PlayerProfile profile = optional.get();
            SlimefunGuideImplementation guide = Slimefun.getRegistry().getSlimefunGuide(mode);
            profile.getGuideHistory().openLastEntry(guide);
        } else {
            openMainMenuAsync(p, mode, 1);
        }
    }

    @ParametersAreNonnullByDefault
    private static void openMainMenuAsync(Player player, SlimefunGuideMode mode, int selectedPage) {
        if (!PlayerProfile.get(player, profile -> Slimefun.runSync(() -> openMainMenu(profile, mode, selectedPage)))) {
            Slimefun.getLocalization().sendMessage(player, "messages.opening-guide");
        }
    }

    @ParametersAreNonnullByDefault
    public static void openMainMenu(PlayerProfile profile, SlimefunGuideMode mode, int selectedPage) {
        Slimefun.getRegistry().getSlimefunGuide(mode).openMainMenu(profile, selectedPage);
    }

    @ParametersAreNonnullByDefault
    public static void openCategory(PlayerProfile profile, ItemGroup category, SlimefunGuideMode mode, int selectedPage) {
        Slimefun.getRegistry().getSlimefunGuide(mode).openCategory(profile, category, selectedPage);
    }

    @ParametersAreNonnullByDefault
    public static void openSearch(PlayerProfile profile, String input, SlimefunGuideMode mode, boolean addToHistory) {
        SlimefunGuideImplementation guide = Slimefun.getRegistry().getSlimefunGuide(mode);
        guide.openSearch(profile, input, addToHistory);
    }

    @ParametersAreNonnullByDefault
    public static void displayItem(PlayerProfile profile, ItemStack item, boolean addToHistory) {
        Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE).displayItem(profile, item, 0, addToHistory);
    }

    @ParametersAreNonnullByDefault
    public static void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory) {
        Slimefun.getRegistry().getSlimefunGuide(SlimefunGuideMode.SURVIVAL_MODE).displayItem(profile, item, addToHistory);
    }

    /**
     * This method checks if a given {@link ItemStack} is a {@link SlimefunGuide}.
     * 
     * @param item
     *            The {@link ItemStack} to check
     * 
     * @return Whether this {@link ItemStack} represents a {@link SlimefunGuide}
     */
    public static boolean isGuideItem(@Nullable ItemStack item) {
        if (item == null || item.getType() != Material.ENCHANTED_BOOK) {
            return false;
        } else if (item instanceof SlimefunGuideItem) {
            return true;
        } else {
            return SlimefunUtils.isItemSimilar(item, getItem(SlimefunGuideMode.SURVIVAL_MODE), true) || SlimefunUtils.isItemSimilar(item, getItem(SlimefunGuideMode.CHEAT_MODE), true);
        }
    }

    /**
     * Get the default mode for the Slimefun guide.
     * Currently this is only {@link SlimefunGuideMode#SURVIVAL_MODE}.
     *
     * @return The default {@link SlimefunGuideMode}.
     */
    @Nonnull
    public static SlimefunGuideMode getDefaultMode() {
        return SlimefunGuideMode.SURVIVAL_MODE;
    }
}
