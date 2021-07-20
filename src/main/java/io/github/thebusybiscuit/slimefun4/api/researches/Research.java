package io.github.thebusybiscuit.slimefun4.api.researches;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerPreResearchEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ResearchUnlockEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.setup.ResearchSetup;

/**
 * Represents a research, which is bound to one
 * {@link SlimefunItem} or more and requires XP levels to unlock said item(s).
 * 
 * @author TheBusyBiscuit
 * 
 * @see ResearchSetup
 * @see ResearchUnlockEvent
 * 
 */
public class Research implements Keyed {

    private final NamespacedKey key;
    private final int id;
    private final String name;
    private boolean enabled = true;
    private int cost;

    private final List<SlimefunItem> items = new LinkedList<>();

    /**
     * The constructor for a {@link Research}.
     * 
     * Create a new research, then bind this research to the Slimefun items you want by calling
     * {@link #addItems(SlimefunItem...)}. Once you're finished, call {@link #register()}
     * to register it.
     * 
     * @param key
     *            A unique identifier for this {@link Research}
     * @param id
     *            old way of identifying researches
     * @param defaultName
     *            The displayed name of this {@link Research}
     * @param defaultCost
     *            The Cost in XP levels to unlock this {@link Research}
     * 
     */
    public Research(@Nonnull NamespacedKey key, int id, @Nonnull String defaultName, int defaultCost) {
        Validate.notNull(key, "A NamespacedKey must be provided");
        Validate.notNull(defaultName, "A default name must be specified");

        this.key = key;
        this.id = id;
        this.name = defaultName;
        this.cost = defaultCost;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * This method returns whether this {@link Research} is enabled.
     * {@code false} can mean that this particular {@link Research} was disabled or that
     * researches altogether have been disabled.
     * 
     * @return Whether this {@link Research} is enabled or not
     */
    public boolean isEnabled() {
        return Slimefun.getRegistry().isResearchingEnabled() && enabled;
    }

    /**
     * Gets the ID of this {@link Research}.
     * This is the old way of identifying Researches, use a {@link NamespacedKey} in the future.
     * 
     * @deprecated Numeric Ids for Researches are deprecated, use {@link #getKey()} for identification instead.
     * 
     * @return The ID of this {@link Research}
     */
    @Deprecated
    public int getID() {
        return id;
    }

    /**
     * This method gives you a localized name for this {@link Research}.
     * The name is automatically taken from the currently selected {@link Language} of
     * the specified {@link Player}.
     * 
     * @param p
     *            The {@link Player} to translate this name for.
     * 
     * @return The localized Name of this {@link Research}.
     */
    @Nonnull
    public String getName(@Nonnull Player p) {
        String localized = Slimefun.getLocalization().getResearchName(p, key);
        return localized != null ? localized : name;
    }

    /**
     * Gets the cost in XP levels to unlock this {@link Research}.
     * 
     * @return The cost in XP levels for this {@link Research}
     */
    public int getCost() {
        return cost;
    }

    /**
     * Sets the cost in XP levels to unlock this {@link Research}.
     * 
     * @param cost
     *            The cost in XP levels
     */
    public void setCost(int cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Research cost must be zero or greater!");
        }

        this.cost = cost;
    }

    /**
     * Bind the specified {@link SlimefunItem SlimefunItems} to this {@link Research}.
     * 
     * @param items
     *            Instances of {@link SlimefunItem} to bind to this {@link Research}
     */
    public void addItems(SlimefunItem... items) {
        for (SlimefunItem item : items) {
            if (item != null) {
                item.setResearch(this);
            }
        }
    }

    /**
     * Bind the specified ItemStacks to this {@link Research}.
     * 
     * @param items
     *            Instances of {@link ItemStack} to bind to this {@link Research}
     * 
     * @return The current instance of {@link Research}
     */
    @Nonnull
    public Research addItems(ItemStack... items) {
        for (ItemStack item : items) {
            SlimefunItem sfItem = SlimefunItem.getByItem(item);

            if (sfItem != null) {
                sfItem.setResearch(this);
            }
        }

        return this;
    }

    /**
     * Lists every {@link SlimefunItem} that is bound to this {@link Research}.
     * 
     * @return The Slimefun items bound to this {@link Research}.
     */
    @Nonnull
    public List<SlimefunItem> getAffectedItems() {
        return items;
    }

    /**
     * Handle what to do when a {@link Player} clicks on an un-researched item in
     * a {@link SlimefunGuideImplementation}.
     *
     * @param guide
     *            The {@link SlimefunGuideImplementation} used.
     * @param player
     *            The {@link Player} who clicked on the item.
     * @param profile
     *            The {@link PlayerProfile} of that {@link Player}.
     * @param sfItem
     *            The {@link SlimefunItem} on which the {@link Player} clicked.
     * @param category
     *            The {@link ItemGroup} where the {@link Player} was.
     * @param page
     *            The page number of where the {@link Player} was in the {@link ItemGroup};
     *
     */
    @ParametersAreNonnullByDefault
    public void unlockFromGuide(SlimefunGuideImplementation guide, Player player, PlayerProfile profile, SlimefunItem sfItem, ItemGroup category, int page) {
        if (!Slimefun.getRegistry().getCurrentlyResearchingPlayers().contains(player.getUniqueId())) {
            if (profile.hasUnlocked(this)) {
                guide.openCategory(profile, category, page);
            } else {
                PlayerPreResearchEvent event = new PlayerPreResearchEvent(player, this, sfItem);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    if (this.canUnlock(player)) {
                        guide.unlockItem(player, sfItem, pl -> guide.openCategory(profile, category, page));
                    } else {
                        Slimefun.getLocalization().sendMessage(player, "messages.not-enough-xp", true);
                    }
                }
            }
        }
    }

    /**
     * Checks if the {@link Player} can unlock this {@link Research}.
     * 
     * @param p
     *            The {@link Player} to check
     * 
     * @return Whether that {@link Player} can unlock this {@link Research}
     */
    public boolean canUnlock(@Nonnull Player p) {
        if (!isEnabled()) {
            return true;
        }

        boolean creativeResearch = p.getGameMode() == GameMode.CREATIVE && Slimefun.getRegistry().isFreeCreativeResearchingEnabled();
        return creativeResearch || p.getLevel() >= cost;
    }

    /**
     * This unlocks this {@link Research} for the given {@link Player} without any form of callback.
     * 
     * @param p
     *            The {@link Player} who should unlock this {@link Research}
     * @param instant
     *            Whether to unlock it instantly
     */
    public void unlock(@Nonnull Player p, boolean instant) {
        unlock(p, instant, null);
    }

    /**
     * Unlocks this {@link Research} for the specified {@link Player}.
     * 
     * @param p
     *            The {@link Player} for which to unlock this {@link Research}
     * @param isInstant
     *            Whether to unlock this {@link Research} instantly
     * @param callback
     *            A callback which will be run when the {@link Research} animation completed
     */
    public void unlock(@Nonnull Player p, boolean isInstant, @Nullable Consumer<Player> callback) {
        PlayerProfile.get(p, new PlayerResearchTask(this, isInstant, callback));
    }

    /**
     * Registers this {@link Research}.
     */
    public void register() {
        Slimefun.getResearchCfg().setDefaultValue("enable-researching", true);
        String path = key.getNamespace() + '.' + key.getKey();

        if (Slimefun.getResearchCfg().contains(path + ".enabled") && !Slimefun.getResearchCfg().getBoolean(path + ".enabled")) {
            for (SlimefunItem item : new ArrayList<>(items)) {
                if (item != null) {
                    item.setResearch(null);
                }
            }

            enabled = false;
            return;
        }

        Slimefun.getResearchCfg().setDefaultValue(path + ".cost", getCost());
        Slimefun.getResearchCfg().setDefaultValue(path + ".enabled", true);

        setCost(Slimefun.getResearchCfg().getInt(path + ".cost"));
        enabled = true;

        Slimefun.getRegistry().getResearches().add(this);
    }

    /**
     * Attempts to get a {@link Research} with the given {@link NamespacedKey}.
     * 
     * @param key
     *            the {@link NamespacedKey} of the {@link Research} you are looking for
     * 
     * @return An {@link Optional} with or without the found {@link Research}
     */
    @Nonnull
    public static Optional<Research> getResearch(@Nullable NamespacedKey key) {
        if (key == null) {
            return Optional.empty();
        }

        for (Research research : Slimefun.getRegistry().getResearches()) {
            if (research.getKey().equals(key)) {
                return Optional.of(research);
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Research (" + getKey() + ')';
    }
}