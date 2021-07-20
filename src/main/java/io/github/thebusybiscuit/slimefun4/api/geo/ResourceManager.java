package io.github.thebusybiscuit.slimefun4.api.geo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.bakedlibs.dough.config.Config;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.events.GEOResourceGenerationEvent;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOScanner;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link ResourceManager} is responsible for registering and managing a {@link GEOResource}.
 * You have to use the {@link ResourceManager} if you want to generate or consume a {@link GEOResource} too.
 * 
 * @author TheBusyBiscuit
 * 
 * @see GEOResource
 * @see GEOMiner
 * @see GEOScanner
 *
 */
public class ResourceManager {

    private final int[] backgroundSlots = { 0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 48, 49, 50, 52, 53 };
    private final Config config;

    /**
     * This will create a new {@link ResourceManager}.
     * 
     * @param plugin
     *            Our {@link Slimefun} instance
     */
    public ResourceManager(@Nonnull Slimefun plugin) {
        config = new Config(plugin, "resources.yml");
    }

    /**
     * This method registers the given {@link GEOResource}.
     * It may never be called directly, use {@link GEOResource#register()} instead.
     * 
     * @param resource
     *            The {@link GEOResource} to register
     */
    void register(@Nonnull GEOResource resource) {
        Validate.notNull(resource, "Cannot register null as a GEO-Resource");
        Validate.notNull(resource.getKey(), "GEO-Resources must have a NamespacedKey which is not null");

        // Resources may only be registered once
        if (Slimefun.getRegistry().getGEOResources().containsKey(resource.getKey())) {
            throw new IllegalArgumentException("GEO-Resource \"" + resource.getKey() + "\" has already been registered!");
        }

        String key = resource.getKey().getNamespace() + '.' + resource.getKey().getKey();
        boolean enabled = config.getOrSetDefault(key + ".enabled", true);

        if (enabled) {
            Slimefun.getRegistry().getGEOResources().add(resource);
        }

        if (Slimefun.getMinecraftVersion() != MinecraftVersion.UNIT_TEST) {
            config.save();
        }
    }

    /**
     * This method returns the amount of a certain {@link GEOResource} found in a given {@link Chunk}.
     * The result is an {@link OptionalInt} which will be empty if this {@link GEOResource}
     * has not been generated at that {@link Location} yet.
     * 
     * @param resource
     *            The {@link GEOResource} to query
     * @param world
     *            The {@link World} of this {@link Location}
     * @param x
     *            The {@link Chunk} x coordinate
     * @param z
     *            The {@link Chunk} z coordinate
     * 
     * @return An {@link OptionalInt}, either empty or containing the amount of the given {@link GEOResource}
     */
    @Nonnull
    public OptionalInt getSupplies(@Nonnull GEOResource resource, @Nonnull World world, int x, int z) {
        Validate.notNull(resource, "Cannot get supplies for null");
        Validate.notNull(world, "World must not be null");

        String key = resource.getKey().toString().replace(':', '-');
        String value = BlockStorage.getChunkInfo(world, x, z, key);

        if (value != null) {
            return OptionalInt.of(Integer.parseInt(value));
        } else {
            return OptionalInt.empty();
        }
    }

    /**
     * This method will set the supplies in a given {@link Chunk} to the specified value.
     * 
     * @param resource
     *            The {@link GEOResource}
     * @param world
     *            The {@link World}
     * @param x
     *            The x coordinate of that {@link Chunk}
     * @param z
     *            The z coordinate of that {@link Chunk}
     * @param value
     *            The new supply value
     */
    public void setSupplies(@Nonnull GEOResource resource, @Nonnull World world, int x, int z, int value) {
        Validate.notNull(resource, "Cannot set supplies for null");
        Validate.notNull(world, "World cannot be null");

        String key = resource.getKey().toString().replace(':', '-');
        BlockStorage.setChunkInfo(world, x, z, key, String.valueOf(value));
    }

    /**
     * This method will generate the default supplies for a given {@link GEOResource} at the
     * given {@link Chunk}.
     * <p>
     * This method will invoke {@link #setSupplies(GEOResource, World, int, int, int)} and also calls a
     * {@link GEOResourceGenerationEvent}.
     * 
     * @param resource
     *            The {@link GEOResource} to generate
     * @param world
     *            The {@link World}
     * @param x
     *            The x coordinate of that {@link Chunk}
     * @param z
     *            The z coordinate of that {@link Chunk}
     * 
     * @return The new supply value
     */
    private int generate(@Nonnull GEOResource resource, @Nonnull World world, int x, int z) {
        Validate.notNull(resource, "Cannot generate resources for null");
        Validate.notNull(world, "World cannot be null");

        // Get the corresponding Block (and Biome)
        Block block = world.getBlockAt(x << 4, 72, z << 4);
        Biome biome = block.getBiome();

        /*
         * getBiome() is marked as NotNull, but it seems like some servers ignore this entirely.
         * We have seen multiple reports on Tuinity where it has indeed returned null.
         */
        Validate.notNull(biome, "Biome appears to be null for position: " + new BlockPosition(block));

        // Make sure the value is not below zero.
        int value = Math.max(0, resource.getDefaultSupply(world.getEnvironment(), biome));

        // Check if more than zero units are to be generated.
        if (value > 0) {
            int max = resource.getMaxDeviation();

            if (max <= 0) {
                throw new IllegalStateException("GEO Resource \"" + resource.getKey() + "\" was misconfigured! getMaxDeviation() must return a value higher than zero!");
            }

            value += ThreadLocalRandom.current().nextInt(max);
        }

        // Fire an event, so that plugins can modify this.
        GEOResourceGenerationEvent event = new GEOResourceGenerationEvent(world, biome, x, z, resource, value);
        Bukkit.getPluginManager().callEvent(event);
        value = event.getValue();

        setSupplies(resource, world, x, z, value);
        return value;
    }

    /**
     * This method will start a geo-scan at the given {@link Block} and display the result
     * of that scan to the given {@link Player}.
     * 
     * Note that scans are always per {@link Chunk}, not per {@link Block}, the {@link Block}
     * parameter only determines the {@link Location} that was clicked but it will still scan
     * the entire {@link Chunk}.
     * 
     * @param p
     *            The {@link Player} who requested these results
     * @param block
     *            The {@link Block} which the scan starts at
     * @param page
     *            The page to display
     */
    public void scan(@Nonnull Player p, @Nonnull Block block, int page) {
        if (Slimefun.getGPSNetwork().getNetworkComplexity(p.getUniqueId()) < 600) {
            Slimefun.getLocalization().sendMessages(p, "gps.insufficient-complexity", true, msg -> msg.replace("%complexity%", "600"));
            return;
        }

        int x = block.getX() >> 4;
        int z = block.getZ() >> 4;

        String title = "&4" + Slimefun.getLocalization().getResourceString(p, "tooltips.results");
        ChestMenu menu = new ChestMenu(title);

        for (int slot : backgroundSlots) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        menu.addItem(4, new CustomItemStack(HeadTexture.MINECRAFT_CHUNK.getAsItemStack(), ChatColor.YELLOW + Slimefun.getLocalization().getResourceString(p, "tooltips.chunk"), "", "&8\u21E8 &7" + Slimefun.getLocalization().getResourceString(p, "tooltips.world") + ": " + block.getWorld().getName(), "&8\u21E8 &7X: " + x + " Z: " + z), ChestMenuUtils.getEmptyClickHandler());
        List<GEOResource> resources = new ArrayList<>(Slimefun.getRegistry().getGEOResources().values());
        resources.sort(Comparator.comparing(a -> a.getName(p).toLowerCase(Locale.ROOT)));

        int index = 10;
        int pages = (resources.size() - 1) / 36 + 1;

        for (int i = page * 28; i < resources.size() && i < (page + 1) * 28; i++) {
            GEOResource resource = resources.get(i);
            OptionalInt optional = getSupplies(resource, block.getWorld(), x, z);
            int supplies = optional.isPresent() ? optional.getAsInt() : generate(resource, block.getWorld(), x, z);
            String suffix = Slimefun.getLocalization().getResourceString(p, supplies == 1 ? "tooltips.unit" : "tooltips.units");

            ItemStack item = new CustomItemStack(resource.getItem(), "&f" + resource.getName(p), "&8\u21E8 &e" + supplies + ' ' + suffix);

            if (supplies > 1) {
                item.setAmount(supplies > item.getMaxStackSize() ? item.getMaxStackSize() : supplies);
            }

            menu.addItem(index, item, ChestMenuUtils.getEmptyClickHandler());
            index++;

            if (index % 9 == 8) {
                index += 2;
            }
        }

        menu.addItem(47, ChestMenuUtils.getPreviousButton(p, page + 1, pages));
        menu.addMenuClickHandler(47, (pl, slot, item, action) -> {
            if (page > 0) {
                scan(pl, block, page - 1);
            }

            return false;
        });

        menu.addItem(51, ChestMenuUtils.getNextButton(p, page + 1, pages));
        menu.addMenuClickHandler(51, (pl, slot, item, action) -> {
            if (page + 1 < pages) {
                scan(pl, block, page + 1);
            }

            return false;
        });

        menu.open(p);
    }

}
