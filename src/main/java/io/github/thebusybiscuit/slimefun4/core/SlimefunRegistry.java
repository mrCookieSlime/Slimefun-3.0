package io.github.thebusybiscuit.slimefun4.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.collections.KeyMap;
import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideImplementation;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.CheatSheetSlimefunGuide;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;

import me.mrCookieSlime.Slimefun.api.BlockInfoConfig;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.UniversalBlockMenu;

/**
 * This class houses a lot of instances of {@link Map} and {@link List} that hold
 * various mappings and collections related to {@link SlimefunItem}.
 * 
 * @author TheBusyBiscuit
 *
 */
public final class SlimefunRegistry {

    private final Map<String, SlimefunItem> slimefunIds = new HashMap<>();
    private final List<SlimefunItem> slimefunItems = new ArrayList<>();
    private final List<SlimefunItem> enabledItems = new ArrayList<>();

    private final List<ItemGroup> categories = new ArrayList<>();
    private final List<MultiBlock> multiblocks = new LinkedList<>();

    private final List<Research> researches = new LinkedList<>();
    private final List<String> researchRanks = new ArrayList<>();
    private final Set<UUID> researchingPlayers = Collections.synchronizedSet(new HashSet<>());

    // TODO: Move this all into a proper "config cache" class
    private boolean backwardsCompatibility;
    private boolean automaticallyLoadItems;
    private boolean enableResearches;
    private boolean freeCreativeResearches;
    private boolean researchFireworks;
    private boolean disableLearningAnimation;
    private boolean logDuplicateBlockEntries;
    private boolean talismanActionBarMessages;

    private final Set<String> tickers = new HashSet<>();
    private final Set<SlimefunItem> radioactive = new HashSet<>();
    private final Set<ItemStack> barterDrops = new HashSet<>();

    private NamespacedKey soulboundKey;
    private NamespacedKey itemChargeKey;
    private NamespacedKey guideKey;

    private final KeyMap<GEOResource> geoResources = new KeyMap<>();

    private final Map<UUID, PlayerProfile> profiles = new ConcurrentHashMap<>();
    private final Map<String, BlockStorage> worlds = new ConcurrentHashMap<>();
    private final Map<String, BlockInfoConfig> chunks = new HashMap<>();
    private final Map<SlimefunGuideMode, SlimefunGuideImplementation> guides = new EnumMap<>(SlimefunGuideMode.class);
    private final Map<EntityType, Set<ItemStack>> mobDrops = new EnumMap<>(EntityType.class);

    private final Map<String, BlockMenuPreset> blockMenuPresets = new HashMap<>();
    private final Map<String, UniversalBlockMenu> universalInventories = new HashMap<>();
    private final Map<Class<? extends ItemHandler>, Set<ItemHandler>> globalItemHandlers = new HashMap<>();

    public void load(@Nonnull Slimefun plugin, @Nonnull Config cfg) {
        Validate.notNull(plugin, "The Plugin cannot be null!");
        Validate.notNull(cfg, "The Config cannot be null!");

        soulboundKey = new NamespacedKey(plugin, "soulbound");
        itemChargeKey = new NamespacedKey(plugin, "item_charge");
        guideKey = new NamespacedKey(plugin, "slimefun_guide_mode");

        boolean showVanillaRecipes = cfg.getBoolean("guide.show-vanilla-recipes");
        guides.put(SlimefunGuideMode.SURVIVAL_MODE, new SurvivalSlimefunGuide(showVanillaRecipes));
        guides.put(SlimefunGuideMode.CHEAT_MODE, new CheatSheetSlimefunGuide());

        researchRanks.addAll(cfg.getStringList("research-ranks"));

        backwardsCompatibility = cfg.getBoolean("options.backwards-compatibility");
        freeCreativeResearches = cfg.getBoolean("researches.free-in-creative-mode");
        researchFireworks = cfg.getBoolean("researches.enable-fireworks");
        disableLearningAnimation = cfg.getBoolean("researches.disable-learning-animation");
        logDuplicateBlockEntries = cfg.getBoolean("options.log-duplicate-block-entries");
        talismanActionBarMessages = cfg.getBoolean("talismans.use-actionbar");
    }

    /**
     * This returns whether auto-loading is enabled.
     * Auto-Loading will automatically call {@link SlimefunItem#load()} when the item is registered.
     * Normally that method is called after the {@link Server} finished starting up.
     * But in the unusual scenario if a {@link SlimefunItem} is registered after that, this is gonna cover that.
     * 
     * @return Whether auto-loading is enabled
     */
    public boolean isAutoLoadingEnabled() {
        return automaticallyLoadItems;
    }

    /**
     * This method returns whether backwards-compatibility is enabled.
     * Backwards compatibility allows Slimefun to recognize items from older versions but comes
     * at a huge performance cost.
     * 
     * @return Whether backwards compatibility is enabled
     */
    public boolean isBackwardsCompatible() {
        return backwardsCompatibility;
    }

    /**
     * This method sets the status of backwards compatibility.
     * Backwards compatibility allows Slimefun to recognize items from older versions but comes
     * at a huge performance cost.
     * 
     * @param compatible
     *            Whether backwards compatibility should be enabled
     */
    public void setBackwardsCompatible(boolean compatible) {
        backwardsCompatibility = compatible;
    }

    /**
     * This method will make any {@link SlimefunItem} which is registered automatically
     * call {@link SlimefunItem#load()}.
     * Normally this method call is delayed but when the {@link Server} is already running,
     * the method can be called instantaneously.
     * 
     * @param mode
     *            Whether auto-loading should be enabled
     */
    public void setAutoLoadingMode(boolean mode) {
        automaticallyLoadItems = mode;
    }

    /**
     * This returns a {@link List} containing every enabled {@link ItemGroup}.
     * 
     * @return {@link List} containing every enabled {@link ItemGroup}
     */
    @Nonnull
    public List<ItemGroup> getCategories() {
        return categories;
    }

    /**
     * This {@link List} contains every {@link SlimefunItem}, even disabled items.
     * 
     * @return A {@link List} containing every {@link SlimefunItem}
     */
    @Nonnull
    public List<SlimefunItem> getAllSlimefunItems() {
        return slimefunItems;
    }

    /**
     * This {@link List} contains every <strong>enabled</strong> {@link SlimefunItem}.
     * 
     * @return A {@link List} containing every enabled {@link SlimefunItem}
     */
    @Nonnull
    public List<SlimefunItem> getEnabledSlimefunItems() {
        return enabledItems;
    }

    /**
     * This returns a {@link List} containing every enabled {@link Research}.
     * 
     * @return A {@link List} containing every enabled {@link Research}
     */
    @Nonnull
    public List<Research> getResearches() {
        return researches;
    }

    /**
     * This method returns a {@link Set} containing the {@link UUID} of every
     * {@link Player} who is currently unlocking a {@link Research}.
     * 
     * @return A {@link Set} holding the {@link UUID} from every {@link Player}
     *         who is currently unlocking a {@link Research}
     */
    @Nonnull
    public Set<UUID> getCurrentlyResearchingPlayers() {
        return researchingPlayers;
    }

    @Nonnull
    public List<String> getResearchRanks() {
        return researchRanks;
    }

    public void setResearchingEnabled(boolean enabled) {
        enableResearches = enabled;
    }

    public boolean isResearchingEnabled() {
        return enableResearches;
    }

    public void setFreeCreativeResearchingEnabled(boolean enabled) {
        freeCreativeResearches = enabled;
    }

    public boolean isFreeCreativeResearchingEnabled() {
        return freeCreativeResearches;
    }

    public boolean isResearchFireworkEnabled() {
        return researchFireworks;
    }

    /**
     * Returns whether the research learning animations is disabled
     *
     * @return Whether the research learning animations is disabled
     */
    public boolean isLearningAnimationDisabled() {
        return disableLearningAnimation;
    }

    /**
     * This method returns a {@link List} of every enabled {@link MultiBlock}.
     * 
     * @return A {@link List} containing every enabled {@link MultiBlock}
     */
    @Nonnull
    public List<MultiBlock> getMultiBlocks() {
        return multiblocks;
    }

    /**
     * This returns the corresponding {@link SlimefunGuideImplementation} for a certain
     * {@link SlimefunGuideMode}.
     * <p>
     * This mainly only exists for internal purposes, if you want to open a certain section
     * using the {@link SlimefunGuide}, then please use the static methods provided in the
     * {@link SlimefunGuide} class.
     * 
     * @param mode
     *            The {@link SlimefunGuideMode}
     * 
     * @return The corresponding {@link SlimefunGuideImplementation}
     */
    @Nonnull
    public SlimefunGuideImplementation getSlimefunGuide(@Nonnull SlimefunGuideMode mode) {
        Validate.notNull(mode, "The Guide mode cannot be null");

        SlimefunGuideImplementation guide = guides.get(mode);

        if (guide == null) {
            throw new IllegalStateException("Slimefun Guide '" + mode + "' has no registered implementation.");
        }

        return guide;
    }

    /**
     * This returns a {@link Map} connecting the {@link EntityType} with a {@link Set}
     * of {@link ItemStack ItemStacks} which would be dropped when an {@link Entity} of that type was killed.
     * 
     * @return The {@link Map} of custom mob drops
     */
    @Nonnull
    public Map<EntityType, Set<ItemStack>> getMobDrops() {
        return mobDrops;
    }

    /**
     * This returns a {@link Set} of {@link ItemStack ItemStacks} which can be obtained by bartering
     * with {@link Piglin Piglins}.
     * 
     * @return A {@link Set} of bartering drops
     */
    @Nonnull
    public Set<ItemStack> getBarteringDrops() {
        return barterDrops;
    }

    @Nonnull
    public Set<SlimefunItem> getRadioactiveItems() {
        return radioactive;
    }

    @Nonnull
    public Set<String> getTickerBlocks() {
        return tickers;
    }

    @Nonnull
    public Map<String, SlimefunItem> getSlimefunItemIds() {
        return slimefunIds;
    }

    @Nonnull
    public Map<String, BlockMenuPreset> getMenuPresets() {
        return blockMenuPresets;
    }

    @Nonnull
    public Map<String, UniversalBlockMenu> getUniversalInventories() {
        return universalInventories;
    }

    @Nonnull
    public Map<UUID, PlayerProfile> getPlayerProfiles() {
        return profiles;
    }

    @Nonnull
    public Map<Class<? extends ItemHandler>, Set<ItemHandler>> getGlobalItemHandlers() {
        return globalItemHandlers;
    }

    @Nonnull
    public Set<ItemHandler> getGlobalItemHandlers(@Nonnull Class<? extends ItemHandler> identifier) {
        Validate.notNull(identifier, "The identifier for an ItemHandler cannot be null!");

        return globalItemHandlers.computeIfAbsent(identifier, c -> new HashSet<>());
    }

    @Nonnull
    public Map<String, BlockStorage> getWorlds() {
        return worlds;
    }

    @Nonnull
    public Map<String, BlockInfoConfig> getChunks() {
        return chunks;
    }

    @Nonnull
    public KeyMap<GEOResource> getGEOResources() {
        return geoResources;
    }

    public boolean logDuplicateBlockEntries() {
        return logDuplicateBlockEntries;
    }

    public boolean useActionbarForTalismans() {
        return talismanActionBarMessages;
    }

    @Nonnull
    public NamespacedKey getSoulboundDataKey() {
        return soulboundKey;
    }

    @Nonnull
    public NamespacedKey getItemChargeDataKey() {
        return itemChargeKey;
    }

    @Nonnull
    public NamespacedKey getGuideDataKey() {
        return guideKey;
    }

}
