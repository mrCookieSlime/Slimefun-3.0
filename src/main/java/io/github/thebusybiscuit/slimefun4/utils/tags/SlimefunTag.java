package io.github.thebusybiscuit.slimefun4.utils.tags;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.data.Waterlogged;

import io.github.thebusybiscuit.slimefun4.api.exceptions.TagMisconfigurationException;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.BlockPlacer;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators.CropGrowthAccelerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.talismans.Talisman;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner.IndustrialMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ClimbingPick;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ExplosiveShovel;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.PickaxeOfTheSeeker;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.PickaxeOfVeinMining;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.SmeltersPickaxe;

/**
 * This enum contains various implementations of the {@link Tag} interface.
 * Most of them serve some purpose within Slimefun's implementation, some are just pure
 * extensions of the default Minecraft tags.
 * The actual tag files are located in the {@literal /src/main/resources/tags} directory
 * and follow Minecraft's tags.json format.
 *
 * @author TheBusyBiscuit
 *
 * @see TagParser
 *
 */
public enum SlimefunTag implements Tag<Material> {

    /**
     * Minecraft ores.
     */
    ORES,

    /**
     * All vanilla overworld ores.
     */
    STONE_ORES,

    /**
     * All deepslate ore variants.
     */
    DEEPSLATE_ORES,

    /**
     * All nether ores.
     */
    NETHER_ORES,

    /**
     * All Shulker boxes, normal and colored.
     */
    SHULKER_BOXES,

    /**
     * All command block variants
     */
    COMMAND_BLOCKS,

    /**
     * All variants of Spawn Eggs
     */
    SPAWN_EGGS,

    /**
     * Every mushroom type, red, brown and nether ones.
     */
    MUSHROOMS,

    /**
     * All leather armor materials
     */
    LEATHER_ARMOR,

    /**
     * Every glass variant, includes both blocks and panes.
     * Also stained glass and stained glass panes.
     */
    GLASS,

    /**
     * All variants of glass, normal and stained.
     */
    GLASS_BLOCKS,

    /**
     * All variants of glass panes, normal and stained.
     */
    GLASS_PANES,

    /**
     * All variants of torches, normal, soulfire and redstone.
     */
    TORCHES,

    /**
     * All terracotta variants, does not include glazed terracotta.
     */
    TERRACOTTA,

    /**
     * All ice variants, normal, packed, blue and whatever else there is.
     */
    ICE_VARIANTS,

    /**
     * All stone variants, normal, andesite, diorite, granite and whatever else may come.
     */
    STONE_VARIANTS,

    /**
     * All dirt variants. Dirt, coarse dirt, grass, mycelium.
     * This also includes farmland and grass paths.
     */
    DIRT_VARIANTS,

    /**
     * All soil blocks for a fungus to grow on.
     * This includes all dirt variants, nylium and soul soil.
     */
    FUNGUS_SOIL,

    /**
     * All variants of concrete powder.
     * Can you believe there is no tag for this already?
     */
    CONCRETE_POWDERS,

    /**
     * All the types of pressure plates.
     */
    PRESSURE_PLATES,

    /**
     * All tall flowers because minecraft doesn't have a tag for this
     */
    TALL_FLOWERS,

    /**
     * Materials which are sensitive to break.
     * Things like Saplings or Pressure plates which break as well when you break
     * the block beneath them.
     */
    SENSITIVE_MATERIALS,

    /**
     * These Materials are sensitive to fluids, they cannot be {@link Waterlogged}
     * and would break in contact with water.
     */
    FLUID_SENSITIVE_MATERIALS,

    /**
     * These materials are just unbreakable, like bedrock for example.
     */
    UNBREAKABLE_MATERIALS,

    /**
     * Materials which cannot be reliably placed using a {@link BlockPlacer}.
     */
    BLOCK_PLACER_IGNORED_MATERIALS,

    /**
     * All materials which the {@link ExplosiveShovel} can break.
     */
    EXPLOSIVE_SHOVEL_BLOCKS,

    /**
     * All materials (ores) which the {@link PickaxeOfVeinMining} recognizes.
     */
    PICKAXE_OF_VEIN_MINING_BLOCKS,

    /**
     * All materials (ores) which the {@link PickaxeOfTheSeeker} recognizes.
     */
    PICKAXE_OF_THE_SEEKER_BLOCKS,

    /**
     * All materials which the {@link SmeltersPickaxe} will try and smelt.
     */
    SMELTERS_PICKAXE_BLOCKS,

    /**
     * All materials (ores) which the {@link IndustrialMiner} will try to mine.
     */
    INDUSTRIAL_MINER_ORES,

    /**
     * All materials (ores) which can be doubled using a Miner {@link Talisman}.
     */
    MINER_TALISMAN_TRIGGERS,

    /**
     * All materials (crops) which the {@link CropGrowthAccelerator} will recognize.
     */
    CROP_GROWTH_ACCELERATOR_BLOCKS,

    /**
     * All <strong>strong</strong> materials which the {@link ClimbingPick} is able to climb.
     */
    CLIMBING_PICK_STRONG_SURFACES,

    /**
     * All <strong>weak</strong> materials which the {@link ClimbingPick} is able to climb.
     */
    CLIMBING_PICK_WEAK_SURFACES,

    /**
     * This {@link SlimefunTag} holds all surfaces for the {@link ClimbingPick}.
     * This is an aggregation of {@code CLIMBING_PICK_STRONG_SURFACES} and {@code CLIMBING_PICK_WEAK_SURFACES}
     */
    CLIMBING_PICK_SURFACES,

    /**
     * All materials (ores) which trigger the Talisman of the Caveman.
     */
    CAVEMAN_TALISMAN_TRIGGERS;

    /**
     * Lookup table for tag names.
     */
    private static final Map<String, SlimefunTag> nameLookup = new HashMap<>();

    /**
     * Speed up lookups by caching the values instead of creating a new array
     * on every method call.
     */
    private static final SlimefunTag[] valuesCache = values();

    static {
        for (SlimefunTag tag : valuesCache) {
            nameLookup.put(tag.name(), tag);
        }
    }

    private final NamespacedKey key;
    private final Set<Material> includedMaterials = EnumSet.noneOf(Material.class);
    private final Set<Tag<Material>> additionalTags = new HashSet<>();

    /**
     * This constructs a new {@link SlimefunTag}.
     * The {@link NamespacedKey} will be automatically inferred using
     * {@link Slimefun} and {@link #name()}.
     */
    SlimefunTag() {
        key = new NamespacedKey(Slimefun.instance(), name().toLowerCase(Locale.ROOT));
    }

    /**
     * This method reloads this {@link SlimefunTag} from our resources directory.
     *
     * @throws TagMisconfigurationException
     *             This is thrown whenever a {@link SlimefunTag} could not be parsed properly
     */
    public void reload() throws TagMisconfigurationException {
        new TagParser(this).parse(this, (materials, tags) -> {
            this.includedMaterials.clear();
            this.includedMaterials.addAll(materials);

            this.additionalTags.clear();
            this.additionalTags.addAll(tags);
        });
    }

    /**
     * This method reloads every single {@link SlimefunTag} from the resources directory.
     * It is equivalent to running {@link #reload()} on every single {@link SlimefunTag} manually.
     *
     * Do keep in mind though that any misconfigured {@link SlimefunTag} will abort the entire
     * method and throw a {@link TagMisconfigurationException}. So one faulty {@link SlimefunTag}
     * will stop the reloading process.
     *
     * @throws TagMisconfigurationException
     *             This is thrown if one of the {@link SlimefunTag SlimefunTags} could not be parsed correctly
     */
    public static void reloadAll() throws TagMisconfigurationException {
        for (SlimefunTag tag : valuesCache) {
            tag.reload();
        }
    }

    @Override
    public @Nonnull NamespacedKey getKey() {
        return key;
    }

    @Override
    public boolean isTagged(@Nonnull Material item) {
        if (includedMaterials.contains(item)) {
            return true;
        } else {
            // Check if any of our additional Tags contain this Materials
            for (Tag<Material> tag : additionalTags) {
                if (tag.isTagged(item)) {
                    return true;
                }
            }

            // Now we can be sure it isn't tagged in any way
            return false;
        }
    }

    @Override
    public @Nonnull Set<Material> getValues() {
        if (additionalTags.isEmpty()) {
            return Collections.unmodifiableSet(includedMaterials);
        } else {
            Set<Material> materials = EnumSet.noneOf(Material.class);
            materials.addAll(includedMaterials);

            for (Tag<Material> tag : additionalTags) {
                materials.addAll(tag.getValues());
            }

            return materials;
        }
    }

    public boolean isEmpty() {
        if (!includedMaterials.isEmpty()) {
            /*
             * Without even needing to generate a Set we can safely
             * return false if there are directly included Materials
             */
            return false;
        } else {
            return getValues().isEmpty();
        }
    }

    /**
     * This returns a {@link Set} of {@link Tag Tags} which are children of this {@link SlimefunTag},
     * these can be other {@link SlimefunTag SlimefunTags} or regular {@link Tag Tags}.
     *
     * <strong>The returned {@link Set} is immutable</strong>
     *
     * @return An immutable {@link Set} of all sub tags.
     */
    public @Nonnull Set<Tag<Material>> getSubTags() {
        return Collections.unmodifiableSet(additionalTags);
    }

    /**
     * This method returns an Array representation for this {@link SlimefunTag}.
     *
     * @return A {@link Material} array for this {@link Tag}
     */
    public @Nonnull Material[] toArray() {
        return getValues().toArray(new Material[0]);
    }

    /**
     * This returns a {@link Stream} of {@link Material Materials} for this {@link SlimefunTag}.
     *
     * @return A {@link Stream} of {@link Material Materials}
     */
    public @Nonnull Stream<Material> stream() {
        return getValues().stream();
    }

    /**
     * Get a value from the cache map rather than calling {@link Enum#valueOf(Class, String)}.
     * This is 25-40% quicker than the standard {@link Enum#valueOf(Class, String)} depending on
     * your Java version. It also means that you can avoid an IllegalArgumentException which let's
     * face it is always good.
     *
     * @param value
     *            The value which you would like to look up.
     *
     * @return The {@link SlimefunTag} or null if it does not exist.
     */
    public static @Nullable SlimefunTag getTag(@Nonnull String value) {
        Validate.notNull(value, "A tag cannot be null!");

        return nameLookup.get(value);
    }

}
