package io.github.thebusybiscuit.slimefun4.api.gps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.chat.ChatInput;
import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.events.WaypointCreateEvent;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.geo.ResourceManager;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.gps.GPSTransmitter;
import io.github.thebusybiscuit.slimefun4.implementation.items.teleporter.Teleporter;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link GPSNetwork} is a manager class for all {@link GPSTransmitter Transmitters} and waypoints.
 * There can only be one instance of this class per {@link Server}.
 * It is also responsible for teleportation and resource management.
 * 
 * @author TheBusyBiscuit
 * 
 * @see TeleportationManager
 * @see ResourceManager
 *
 */
public class GPSNetwork {

    private final int[] border = { 0, 1, 3, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53 };
    private final int[] inventory = { 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43 };

    private final Map<UUID, Set<Location>> transmitters = new HashMap<>();
    private final TeleportationManager teleportation = new TeleportationManager();
    private final ResourceManager resourceManager;

    /**
     * This constructs a new {@link GPSNetwork}.
     * Note that this network is per {@link Server} and not per {@link Player}.
     * 
     * @param plugin
     *            Our {@link Slimefun} instance
     */
    public GPSNetwork(@Nonnull Slimefun plugin) {
        resourceManager = new ResourceManager(plugin);
    }

    /**
     * This method updates the status of a {@link GPSTransmitter}.
     * 
     * @param l
     *            The {@link Location} of the {@link GPSTransmitter}
     * @param uuid
     *            The {@link UUID} who the {@link GPSTransmitter} belongs to
     * @param online
     *            Whether that {@link GPSTransmitter} is online
     */
    public void updateTransmitter(@Nonnull Location l, @Nonnull UUID uuid, boolean online) {
        Set<Location> set = transmitters.computeIfAbsent(uuid, id -> new HashSet<>());

        if (online) {
            set.add(l);
        } else {
            set.remove(l);
        }
    }

    /**
     * This method calculates the GPS complexity for the given {@link UUID}.
     * The complexity is determined by the Y level of each {@link GPSTransmitter}
     * multiplied by the multiplier of that transmitter.
     * 
     * @param uuid
     *            The {@link UUID} who to calculate it for
     * 
     * @return The network complexity for that {@link UUID}
     */
    public int getNetworkComplexity(@Nonnull UUID uuid) {
        Set<Location> locations = transmitters.get(uuid);

        if (locations == null) {
            return 0;
        }

        int level = 0;
        for (Location l : locations) {
            SlimefunItem item = BlockStorage.check(l);

            if (item instanceof GPSTransmitter) {
                level += ((GPSTransmitter) item).getMultiplier(l.getBlockY());
            }
        }

        return level;
    }

    /**
     * This method returns the amount of {@link GPSTransmitter Transmitters} for the
     * given {@link UUID}.
     * 
     * @param uuid
     *            The {@link UUID} who these transmitters belong to
     * 
     * @return The amount of transmitters
     */
    public int countTransmitters(@Nonnull UUID uuid) {
        Set<Location> locations = transmitters.get(uuid);
        return locations == null ? 0 : locations.size();
    }

    /**
     * This method opens the {@link GPSTransmitter} control panel to the given
     * {@link Player}.
     * 
     * @param p
     *            The {@link Player}
     */
    public void openTransmitterControlPanel(@Nonnull Player p) {
        ChestMenu menu = new ChestMenu(ChatColor.BLUE + Slimefun.getLocalization().getMessage(p, "machines.GPS_CONTROL_PANEL.title"));

        for (int slot : border) {
            menu.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
        }

        menu.addItem(2, new CustomItemStack(SlimefunItems.GPS_TRANSMITTER, im -> {
            im.setDisplayName(ChatColor.GRAY + Slimefun.getLocalization().getMessage(p, "machines.GPS_CONTROL_PANEL.transmitters"));
            im.setLore(null);
        }));

        menu.addMenuClickHandler(2, ChestMenuUtils.getEmptyClickHandler());

        int complexity = getNetworkComplexity(p.getUniqueId());
        menu.addItem(4, new CustomItemStack(SlimefunItems.GPS_CONTROL_PANEL, "&7Network Info", "", "&8\u21E8 &7Status: " + (complexity > 0 ? "&2&lONLINE" : "&4&lOFFLINE"), "&8\u21E8 &7Complexity: &f" + complexity));
        menu.addMenuClickHandler(4, ChestMenuUtils.getEmptyClickHandler());

        menu.addItem(6, new CustomItemStack(HeadTexture.GLOBE_OVERWORLD.getAsItemStack(), "&7" + Slimefun.getLocalization().getMessage(p, "machines.GPS_CONTROL_PANEL.waypoints"), "", ChatColor.GRAY + "\u21E8 " + Slimefun.getLocalization().getMessage(p, "guide.tooltips.open-category")));
        menu.addMenuClickHandler(6, (pl, slot, item, action) -> {
            openWaypointControlPanel(pl);
            return false;
        });

        int index = 0;
        for (Location l : getTransmitters(p.getUniqueId())) {
            if (index >= inventory.length) {
                break;
            }

            SlimefunItem sfi = BlockStorage.check(l);

            if (sfi instanceof GPSTransmitter) {
                int slot = inventory[index];

                menu.addItem(slot, new CustomItemStack(SlimefunItems.GPS_TRANSMITTER, "&bGPS Transmitter", "&8\u21E8 &7World: &f" + l.getWorld().getName(), "&8\u21E8 &7X: &f" + l.getX(), "&8\u21E8 &7Y: &f" + l.getY(), "&8\u21E8 &7Z: &f" + l.getZ(), "", "&8\u21E8 &7Signal Strength: &f" + ((GPSTransmitter) sfi).getMultiplier(l.getBlockY()), "&8\u21E8 &7Ping: &f" + NumberUtils.roundDecimalNumber(1000D / l.getY()) + "ms"));
                menu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());

                index++;
            }
        }

        menu.open(p);
    }

    /**
     * This returns an icon for the given waypoint.
     * The icon is dependent on the {@link Environment} of the waypoint's {@link World}.
     * However if the name of this waypoint indicates that this is actually a deathmarker
     * then a different texture will be used.
     * 
     * Otherwise it will return a globe, a nether or end sphere according to the {@link Environment}.
     * 
     * @param name
     *            The name of a waypoint
     * @param environment
     *            The {@link Environment} of the waypoint's {@link World}
     * 
     * @return An icon for this waypoint
     */
    @Nonnull
    public ItemStack getIcon(@Nonnull String name, @Nonnull Environment environment) {
        if (name.startsWith("player:death ")) {
            return HeadTexture.DEATHPOINT.getAsItemStack();
        } else if (environment == Environment.NETHER) {
            return HeadTexture.GLOBE_NETHER.getAsItemStack();
        } else if (environment == Environment.THE_END) {
            return HeadTexture.GLOBE_THE_END.getAsItemStack();
        } else {
            return HeadTexture.GLOBE_OVERWORLD.getAsItemStack();
        }
    }

    public void openWaypointControlPanel(@Nonnull Player p) {
        PlayerProfile.get(p, profile -> {
            ChestMenu menu = new ChestMenu(ChatColor.BLUE + Slimefun.getLocalization().getMessage(p, "machines.GPS_CONTROL_PANEL.title"));

            for (int slot : border) {
                menu.addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }

            menu.addItem(2, new CustomItemStack(SlimefunItems.GPS_TRANSMITTER, "&7" + Slimefun.getLocalization().getMessage(p, "machines.GPS_CONTROL_PANEL.transmitters"), "", ChatColor.GRAY + "\u21E8 " + Slimefun.getLocalization().getMessage(p, "guide.tooltips.open-category")));
            menu.addMenuClickHandler(2, (pl, slot, item, action) -> {
                openTransmitterControlPanel(pl);
                return false;
            });

            int complexity = getNetworkComplexity(p.getUniqueId());
            menu.addItem(4, new CustomItemStack(SlimefunItems.GPS_CONTROL_PANEL, "&7Network Info", "", "&8\u21E8 &7Status: " + (complexity > 0 ? "&2&lONLINE" : "&4&lOFFLINE"), "&8\u21E8 &7Complexity: &f" + complexity));
            menu.addMenuClickHandler(4, ChestMenuUtils.getEmptyClickHandler());

            menu.addItem(6, new CustomItemStack(HeadTexture.GLOBE_OVERWORLD.getAsItemStack(), "&7" + Slimefun.getLocalization().getMessage(p, "machines.GPS_CONTROL_PANEL.waypoints")));
            menu.addMenuClickHandler(6, ChestMenuUtils.getEmptyClickHandler());

            int index = 0;
            for (Waypoint waypoint : profile.getWaypoints()) {
                if (index >= inventory.length) {
                    break;
                }

                int slot = inventory[index];

                Location l = waypoint.getLocation();
                menu.addItem(slot, new CustomItemStack(waypoint.getIcon(), waypoint.getName().replace("player:death ", ""), "&8\u21E8 &7World: &f" + l.getWorld().getName(), "&8\u21E8 &7X: &f" + l.getX(), "&8\u21E8 &7Y: &f" + l.getY(), "&8\u21E8 &7Z: &f" + l.getZ(), "", "&8\u21E8 &cClick to delete"));
                menu.addMenuClickHandler(slot, (pl, s, item, action) -> {
                    profile.removeWaypoint(waypoint);
                    pl.playSound(pl.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1F);

                    openWaypointControlPanel(pl);
                    return false;
                });

                index++;
            }

            menu.open(p);
        });
    }

    /**
     * This method will prompt the given {@link Player} to enter a name for a waypoint.
     * After entering the name, it will be added to his waypoint list.
     * 
     * @param p
     *            The {@link Player} who should get a new waypoint
     * @param l
     *            The {@link Location} of the new waypoint
     */
    public void createWaypoint(@Nonnull Player p, @Nonnull Location l) {
        Validate.notNull(p, "Player cannot be null!");
        Validate.notNull(l, "Waypoint Location cannot be null!");

        PlayerProfile.get(p, profile -> {
            if ((profile.getWaypoints().size() + 2) > inventory.length) {
                Slimefun.getLocalization().sendMessage(p, "gps.waypoint.max", true);
                return;
            }

            Slimefun.getLocalization().sendMessage(p, "gps.waypoint.new", true);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 1F);

            ChatInput.waitForPlayer(Slimefun.instance(), p, message -> addWaypoint(p, message, l));
        });
    }

    /**
     * This method adds a new waypoint with the given name and {@link Location} for that {@link Player}.
     * 
     * @param p
     *            The {@link Player} to get the new waypoint
     * @param name
     *            The name of this waypoint
     * @param l
     *            The {@link Location} of this waypoint
     */
    public void addWaypoint(@Nonnull Player p, @Nonnull String name, @Nonnull Location l) {
        Validate.notNull(p, "Player cannot be null!");
        Validate.notNull(name, "Waypoint name cannot be null!");
        Validate.notNull(l, "Waypoint Location cannot be null!");

        PlayerProfile.get(p, profile -> {
            if ((profile.getWaypoints().size() + 2) > inventory.length) {
                Slimefun.getLocalization().sendMessage(p, "gps.waypoint.max", true);
                return;
            }

            Slimefun.runSync(() -> {
                WaypointCreateEvent event = new WaypointCreateEvent(p, name, l);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    String id = ChatColor.stripColor(ChatColors.color(event.getName())).toUpperCase(Locale.ROOT).replace(' ', '_');

                    for (Waypoint wp : profile.getWaypoints()) {
                        if (wp.getId().equals(id)) {
                            Slimefun.getLocalization().sendMessage(p, "gps.waypoint.duplicate", true, msg -> msg.replace("%waypoint%", event.getName()));
                            return;
                        }
                    }

                    profile.addWaypoint(new Waypoint(profile, id, event.getLocation(), event.getName()));

                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                    Slimefun.getLocalization().sendMessage(p, "gps.waypoint.added", true);
                }
            });
        });
    }

    /**
     * This method returns a {@link Set} of {@link Location Locations} for all {@link GPSTransmitter Transmitters}
     * owned by the given {@link UUID}.
     * 
     * @param uuid
     *            The {@link UUID} owning those transmitters
     * 
     * @return A {@link Set} with all {@link Location Locations} of transmitters for this {@link UUID}
     */
    @Nonnull
    public Set<Location> getTransmitters(@Nonnull UUID uuid) {
        return transmitters.getOrDefault(uuid, new HashSet<>());
    }

    /**
     * This returns the {@link TeleportationManager} for this {@link GPSNetwork}.
     * It is responsible for all actions that relate to the {@link Teleporter}.
     * 
     * @return The {@link TeleportationManager} for this {@link GPSNetwork}
     */
    @Nonnull
    public TeleportationManager getTeleportationManager() {
        return teleportation;
    }

    /**
     * This returns the {@link ResourceManager} for this {@link GPSNetwork}.
     * Use this to access {@link GEOResource GEOResources}.
     * 
     * @return The {@link ResourceManager} for this {@link GPSNetwork}
     */
    @Nonnull
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

}
