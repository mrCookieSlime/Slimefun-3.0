package io.github.thebusybiscuit.slimefun4.api.geo;

import javax.annotation.Nonnull;

import org.bukkit.Chunk;
import org.bukkit.Keyed;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.GEOResourceGenerationEvent;
import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOScanner;

/**
 * A {@link GEOResource} is a virtual resource that can be thought of as world-gen.
 * However it cannot be found in a {@link World}.
 * 
 * This resource only exists in memory and can be retrieved through a {@link GEOMiner}
 * or similar devices.
 * 
 * A {@link GEOResource} can be detected via the {@link GEOScanner}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ResourceManager
 * @see GEOMiner
 * @see GEOScanner
 * @see GEOResourceGenerationEvent
 *
 */
public interface GEOResource extends Keyed {

    /**
     * Returns the default supply of this resource in that biome
     * 
     * @param environment
     *            The {@link Environment} this area is currently in (NORMAL / NETHER / THE_END)
     * @param biome
     *            The {@link Biome} this area is currently in.
     * 
     * @return The default supply found in a {@link Chunk} with the given {@link Biome}
     */
    int getDefaultSupply(@Nonnull Environment environment, @Nonnull Biome biome);

    /**
     * Returns how much the value may deviate from the default supply (positive only).
     * 
     * @return The deviation or spread of the supply
     */
    int getMaxDeviation();

    /**
     * Returns the name of this resource (e.g. "Oil")
     * 
     * @return The name of this Resource
     */
    @Nonnull
    String getName();

    /**
     * This {@link ItemStack} is used for display-purposes in the GEO Scanner.
     * But will also determine the Output of the GEO Miner, if it is applicable for that.
     * 
     * @return The {@link ItemStack} version of this Resource.
     */
    @Nonnull
    ItemStack getItem();

    /**
     * Returns whether this Resource can be obtained using a GEO Miner.
     * This will automatically add it to the GEO - Miner.
     * 
     * @return Whether you can get obtain this resource using a GEO Miner.
     */
    boolean isObtainableFromGEOMiner();

    /**
     * Registers this GEO Resource
     */
    default void register() {
        Slimefun.getGPSNetwork().getResourceManager().register(this);
    }

    /**
     * This method returns a localized name for this {@link GEOResource} in the
     * {@link Language} the given {@link Player} selected.
     * 
     * @param p
     *            The {@link Player} to localize the name for.
     * @return The localized name for this {@link GEOResource}
     */
    @Nonnull
    default String getName(@Nonnull Player p) {
        String name = Slimefun.getLocalization().getResourceString(p, "resources." + getKey().getNamespace() + "." + getKey().getKey());
        return name == null ? getName() : name;
    }

}
