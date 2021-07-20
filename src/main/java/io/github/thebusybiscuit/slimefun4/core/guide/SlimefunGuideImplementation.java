package io.github.thebusybiscuit.slimefun4.core.guide;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.options.SlimefunGuideSettings;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;

/**
 * This interface is used for the different implementations that add behaviour
 * to the {@link SlimefunGuide}.
 *
 * @author TheBusyBiscuit
 * 
 * @see SlimefunGuideMode
 * @see SurvivalSlimefunGuide
 *
 */
public interface SlimefunGuideImplementation {

    /**
     * Every {@link SlimefunGuideImplementation} can be associated with a
     * {@link SlimefunGuideMode}.
     *
     * @return The mode this {@link SlimefunGuideImplementation} represents
     */
    @Nonnull
    SlimefunGuideMode getMode();

    /**
     * Returns the {@link ItemStack} representation for this {@link SlimefunGuideImplementation}.
     * In other words: The {@link ItemStack} you hold in your hand and that you use to
     * open your {@link SlimefunGuide}
     *
     * @return The {@link ItemStack} representation for this {@link SlimefunGuideImplementation}
     */
    @Nonnull
    ItemStack getItem();

    @ParametersAreNonnullByDefault
    void openMainMenu(PlayerProfile profile, int page);

    @ParametersAreNonnullByDefault
    void openCategory(PlayerProfile profile, ItemGroup category, int page);

    @ParametersAreNonnullByDefault
    void openSearch(PlayerProfile profile, String input, boolean addToHistory);

    @ParametersAreNonnullByDefault
    void displayItem(PlayerProfile profile, ItemStack item, int index, boolean addToHistory);

    @ParametersAreNonnullByDefault
    void displayItem(PlayerProfile profile, SlimefunItem item, boolean addToHistory);

    @ParametersAreNonnullByDefault
    default void unlockItem(Player p, SlimefunItem sfitem, Consumer<Player> callback) {
        Research research = sfitem.getResearch();

        if (p.getGameMode() == GameMode.CREATIVE && Slimefun.getRegistry().isFreeCreativeResearchingEnabled()) {
            research.unlock(p, true, callback);
        } else {
            p.setLevel(p.getLevel() - research.getCost());

            boolean skipLearningAnimation = Slimefun.getRegistry().isLearningAnimationDisabled() || !SlimefunGuideSettings.hasLearningAnimationEnabled(p);
            research.unlock(p, skipLearningAnimation, callback);
        }
    }

}
