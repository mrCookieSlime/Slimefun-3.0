package io.github.thebusybiscuit.slimefun4.api.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.api.events.AsyncProfileLoadEvent;
import io.github.thebusybiscuit.slimefun4.api.gps.Waypoint;
import io.github.thebusybiscuit.slimefun4.api.items.HashedArmorpiece;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectionType;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectiveArmor;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.SlimefunArmorPiece;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;

/**
 * A class that can store a Player's {@link Research} progress for caching purposes.
 * It also holds the backpacks of a {@link Player}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Research
 * @see Waypoint
 * @see PlayerBackpack
 * @see HashedArmorpiece
 *
 */
public class PlayerProfile {

    private final UUID uuid;
    private final String name;

    private final Config configFile;
    private final Config waypointsFile;

    private boolean dirty = false;
    private boolean markedForDeletion = false;

    private final Set<Research> researches = new HashSet<>();
    private final List<Waypoint> waypoints = new ArrayList<>();
    private final Map<Integer, PlayerBackpack> backpacks = new HashMap<>();
    private final GuideHistory guideHistory = new GuideHistory(this);

    private final HashedArmorpiece[] armor = { new HashedArmorpiece(), new HashedArmorpiece(), new HashedArmorpiece(), new HashedArmorpiece() };

    protected PlayerProfile(@Nonnull OfflinePlayer p) {
        this.uuid = p.getUniqueId();
        this.name = p.getName();

        configFile = new Config("data-storage/Slimefun/Players/" + uuid.toString() + ".yml");
        waypointsFile = new Config("data-storage/Slimefun/waypoints/" + uuid.toString() + ".yml");

        loadProfileData();
    }

    private void loadProfileData() {
        for (Research research : Slimefun.getRegistry().getResearches()) {
            if (configFile.contains("researches." + research.getID())) {
                researches.add(research);
            }
        }

        for (String key : waypointsFile.getKeys()) {
            try {
                if (waypointsFile.contains(key + ".world") && Bukkit.getWorld(waypointsFile.getString(key + ".world")) != null) {
                    String waypointName = waypointsFile.getString(key + ".name");
                    Location loc = waypointsFile.getLocation(key);
                    waypoints.add(new Waypoint(this, key, loc, waypointName));
                }
            } catch (Exception x) {
                Slimefun.logger().log(Level.WARNING, x, () -> "Could not load Waypoint \"" + key + "\" for Player \"" + name + '"');
            }
        }
    }

    /**
     * This method provides a fast way to access the armor of a {@link Player}.
     * It returns a cached version, represented by {@link HashedArmorpiece}.
     * 
     * @return The cached armor for this {@link Player}
     */
    public @Nonnull HashedArmorpiece[] getArmor() {
        return armor;
    }

    /**
     * This returns the {@link Config} which is used to store the data.
     * Only intended for internal usage.
     * 
     * @return The {@link Config} associated with this {@link PlayerProfile}
     */
    public @Nonnull Config getConfig() {
        return configFile;
    }

    /**
     * This returns the {@link UUID} this {@link PlayerProfile} is linked to.
     * 
     * @return The {@link UUID} of our {@link PlayerProfile}
     */
    public @Nonnull UUID getUUID() {
        return uuid;
    }

    /**
     * This method returns whether the {@link Player} has logged off.
     * If this is true, then the Profile can be removed from RAM.
     * 
     * @return Whether the Profile is marked for deletion
     */
    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    /**
     * This method returns whether the Profile has unsaved changes
     * 
     * @return Whether there are unsaved changes
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * This method will save the Player's Researches and Backpacks to the hard drive
     */
    public void save() {
        for (PlayerBackpack backpack : backpacks.values()) {
            backpack.save();
        }

        waypointsFile.save();
        configFile.save();
        dirty = false;
    }

    /**
     * This method sets the Player's "researched" status for this Research.
     * Use the boolean to unlock or lock the {@link Research}
     * 
     * @param research
     *            The {@link Research} that should be unlocked or locked
     * @param unlock
     *            Whether the {@link Research} should be unlocked or locked
     */
    public void setResearched(@Nonnull Research research, boolean unlock) {
        Validate.notNull(research, "Research must not be null!");
        dirty = true;

        if (unlock) {
            configFile.setValue("researches." + research.getID(), true);
            researches.add(research);
        } else {
            configFile.setValue("researches." + research.getID(), null);
            researches.remove(research);
        }
    }

    /**
     * This method returns whether the {@link Player} has unlocked the given {@link Research}
     * 
     * @param research
     *            The {@link Research} that is being queried
     * @return Whether this {@link Research} has been unlocked
     */
    public boolean hasUnlocked(@Nullable Research research) {
        if (research == null) {
            // No Research, no restriction
            return true;
        }

        return !research.isEnabled() || researches.contains(research);
    }

    /**
     * This Method will return all Researches that this {@link Player} has unlocked
     * 
     * @return A {@code Hashset<Research>} of all Researches this {@link Player} has unlocked
     */
    public @Nonnull Set<Research> getResearches() {
        return ImmutableSet.copyOf(researches);
    }

    /**
     * This returns a {@link List} of all {@link Waypoint Waypoints} belonging to this
     * {@link PlayerProfile}.
     * 
     * @return A {@link List} containing every {@link Waypoint}
     */
    public @Nonnull List<Waypoint> getWaypoints() {
        return ImmutableList.copyOf(waypoints);
    }

    /**
     * This adds the given {@link Waypoint} to the {@link List} of {@link Waypoint Waypoints}
     * of this {@link PlayerProfile}.
     * 
     * @param waypoint
     *            The {@link Waypoint} to add
     */
    public void addWaypoint(@Nonnull Waypoint waypoint) {
        Validate.notNull(waypoint, "Cannot add a 'null' waypoint!");

        for (Waypoint wp : waypoints) {
            if (wp.getId().equals(waypoint.getId())) {
                throw new IllegalArgumentException("A Waypoint with that id already exists for this Player");
            }
        }

        if (waypoints.size() < 21) {
            waypoints.add(waypoint);

            waypointsFile.setValue(waypoint.getId(), waypoint.getLocation());
            waypointsFile.setValue(waypoint.getId() + ".name", waypoint.getName());
            markDirty();
        }
    }

    /**
     * This removes the given {@link Waypoint} from the {@link List} of {@link Waypoint Waypoints}
     * of this {@link PlayerProfile}.
     * 
     * @param waypoint
     *            The {@link Waypoint} to remove
     */
    public void removeWaypoint(@Nonnull Waypoint waypoint) {
        Validate.notNull(waypoint, "Cannot remove a 'null' waypoint!");

        if (waypoints.remove(waypoint)) {
            waypointsFile.setValue(waypoint.getId(), null);
            markDirty();
        }
    }

    /**
     * Call this method if the Player has left.
     * The profile can then be removed from RAM.
     */
    public final void markForDeletion() {
        markedForDeletion = true;
    }

    /**
     * Call this method if this Profile has unsaved changes.
     */
    public final void markDirty() {
        dirty = true;
    }

    public @Nonnull PlayerBackpack createBackpack(int size) {
        IntStream stream = IntStream.iterate(0, i -> i + 1).filter(i -> !configFile.contains("backpacks." + i + ".size"));
        int id = stream.findFirst().getAsInt();

        PlayerBackpack backpack = new PlayerBackpack(this, id, size);
        backpacks.put(id, backpack);

        return backpack;
    }

    public @Nonnull Optional<PlayerBackpack> getBackpack(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Backpacks cannot have negative ids!");
        }

        PlayerBackpack backpack = backpacks.get(id);

        if (backpack != null) {
            return Optional.of(backpack);
        } else if (configFile.contains("backpacks." + id + ".size")) {
            backpack = new PlayerBackpack(this, id);
            backpacks.put(id, backpack);
            return Optional.of(backpack);
        }

        return Optional.empty();
    }

    public @Nonnull String getTitle() {
        List<String> titles = Slimefun.getRegistry().getResearchRanks();

        float fraction = (float) researches.size() / Slimefun.getRegistry().getResearches().size();
        int index = (int) (fraction * (titles.size() - 1));

        return titles.get(index);
    }

    public void sendStats(@Nonnull CommandSender sender) {
        Set<Research> unlockedResearches = getResearches();
        int levels = unlockedResearches.stream().mapToInt(Research::getCost).sum();
        int allResearches = Slimefun.getRegistry().getResearches().size();

        float progress = Math.round(((unlockedResearches.size() * 100.0F) / allResearches) * 100.0F) / 100.0F;

        sender.sendMessage("");
        sender.sendMessage(ChatColors.color("&7Statistics for Player: &b" + name));
        sender.sendMessage("");
        sender.sendMessage(ChatColors.color("&7Title: " + ChatColor.AQUA + getTitle()));
        sender.sendMessage(ChatColors.color("&7Research Progress: " + NumberUtils.getColorFromPercentage(progress) + progress + " &r% " + ChatColor.YELLOW + '(' + unlockedResearches.size() + " / " + allResearches + ')'));
        sender.sendMessage(ChatColors.color("&7Total XP Levels spent: " + ChatColor.AQUA + levels));
    }

    /**
     * This returns the {@link Player} who this {@link PlayerProfile} belongs to.
     * If the {@link Player} is offline, null will be returned.
     * 
     * @return The {@link Player} of this {@link PlayerProfile} or null
     */
    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }

    /**
     * This returns the {@link GuideHistory} of this {@link Player}.
     * It is basically that player's browsing history.
     * 
     * @return The {@link GuideHistory} of this {@link Player}
     */
    public @Nonnull GuideHistory getGuideHistory() {
        return guideHistory;
    }

    public static boolean fromUUID(@Nonnull UUID uuid, @Nonnull Consumer<PlayerProfile> callback) {
        return get(Bukkit.getOfflinePlayer(uuid), callback);
    }

    /**
     * Get the {@link PlayerProfile} for a {@link OfflinePlayer} asynchronously.
     *
     * @param p
     *            The {@link OfflinePlayer} who's {@link PlayerProfile} to retrieve
     * @param callback
     *            The callback with the {@link PlayerProfile}
     * 
     * @return If the {@link OfflinePlayer} was cached or not.
     */
    public static boolean get(@Nonnull OfflinePlayer p, @Nonnull Consumer<PlayerProfile> callback) {
        Validate.notNull(p, "Cannot get a PlayerProfile for: null!");

        UUID uuid = p.getUniqueId();
        PlayerProfile profile = Slimefun.getRegistry().getPlayerProfiles().get(uuid);

        if (profile != null) {
            callback.accept(profile);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Slimefun.instance(), () -> {
            AsyncProfileLoadEvent event = new AsyncProfileLoadEvent(new PlayerProfile(p));
            Bukkit.getPluginManager().callEvent(event);

            Slimefun.getRegistry().getPlayerProfiles().put(uuid, event.getProfile());
            callback.accept(event.getProfile());
        });

        return false;
    }

    /**
     * This requests an instance of {@link PlayerProfile} to be loaded for the given {@link OfflinePlayer}.
     * This method will return true if the {@link PlayerProfile} was already found.
     * 
     * @param p
     *            The {@link OfflinePlayer} to request the {@link PlayerProfile} for.
     * 
     * @return Whether the {@link PlayerProfile} was already loaded
     */
    public static boolean request(@Nonnull OfflinePlayer p) {
        Validate.notNull(p, "Cannot request a Profile for null");

        if (!Slimefun.getRegistry().getPlayerProfiles().containsKey(p.getUniqueId())) {
            // Should probably prevent multiple requests for the same profile in the future
            Bukkit.getScheduler().runTaskAsynchronously(Slimefun.instance(), () -> {
                PlayerProfile pp = new PlayerProfile(p);
                Slimefun.getRegistry().getPlayerProfiles().put(p.getUniqueId(), pp);
            });

            return false;
        }

        return true;
    }

    /**
     * This method tries to search for a {@link PlayerProfile} of the given {@link OfflinePlayer}.
     * The result of this method is an {@link Optional}, if no {@link PlayerProfile} was found, an empty
     * {@link Optional} will be returned.
     * 
     * @param p
     *            The {@link OfflinePlayer} to get the {@link PlayerProfile} for
     * 
     * @return An {@link Optional} describing the result
     */
    public static @Nonnull Optional<PlayerProfile> find(@Nonnull OfflinePlayer p) {
        return Optional.ofNullable(Slimefun.getRegistry().getPlayerProfiles().get(p.getUniqueId()));
    }

    public static @Nonnull Iterator<PlayerProfile> iterator() {
        return Slimefun.getRegistry().getPlayerProfiles().values().iterator();
    }

    public static void getBackpack(@Nullable ItemStack item, @Nonnull Consumer<PlayerBackpack> callback) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return;
        }

        OptionalInt id = OptionalInt.empty();
        String uuid = "";

        for (String line : item.getItemMeta().getLore()) {
            if (line.startsWith(ChatColors.color("&7ID: ")) && line.indexOf('#') != -1) {
                String[] splitLine = CommonPatterns.HASH.split(line);

                if (CommonPatterns.NUMERIC.matcher(splitLine[1]).matches()) {
                    id = OptionalInt.of(Integer.parseInt(splitLine[1]));
                    uuid = splitLine[0].replace(ChatColors.color("&7ID: "), "");
                }
            }
        }

        if (id.isPresent()) {
            int number = id.getAsInt();

            fromUUID(UUID.fromString(uuid), profile -> {
                Optional<PlayerBackpack> backpack = profile.getBackpack(number);
                backpack.ifPresent(callback);
            });
        }
    }

    public boolean hasFullProtectionAgainst(@Nonnull ProtectionType type) {
        Validate.notNull(type, "ProtectionType must not be null.");

        int armorCount = 0;
        NamespacedKey setId = null;

        for (HashedArmorpiece armorpiece : armor) {
            Optional<SlimefunArmorPiece> armorPiece = armorpiece.getItem();

            if (!armorPiece.isPresent()) {
                setId = null;
            } else if (armorPiece.get() instanceof ProtectiveArmor) {
                ProtectiveArmor protectedArmor = (ProtectiveArmor) armorPiece.get();

                if (setId == null && protectedArmor.isFullSetRequired()) {
                    setId = protectedArmor.getArmorSetId();
                }

                for (ProtectionType protectionType : protectedArmor.getProtectionTypes()) {
                    if (protectionType == type) {
                        if (setId == null) {
                            return true;
                        } else if (setId.equals(protectedArmor.getArmorSetId())) {
                            armorCount++;
                        }
                    }
                }

            }
        }

        return armorCount == 4;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerProfile && uuid.equals(((PlayerProfile) obj).uuid);
    }

    @Override
    public String toString() {
        return "PlayerProfile {" + uuid + "}";
    }

}
