package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.config.Config;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;

/**
 * A {@link Script} represents runnable code for a {@link ProgrammableAndroid}.
 * 
 * @author TheBusyBiscuit
 *
 */
public final class Script {

    private final Config config;
    private final String name;
    private final String author;
    private final String code;

    /**
     * This constructs a new {@link Script} from the given {@link Config}.
     * 
     * @param config
     *            The {@link Config}
     */
    private Script(@Nonnull Config config) {
        Validate.notNull(config);

        this.config = config;
        this.name = config.getString("name");
        this.code = config.getString("code");
        String uuid = config.getString("author");

        Validate.notNull(name);
        Validate.notNull(code);
        Validate.notNull(uuid);
        Validate.notNull(config.getStringList("rating.positive"));
        Validate.notNull(config.getStringList("rating.negative"));

        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        this.author = player.getName() != null ? player.getName() : config.getString("author_name");
    }

    /**
     * This returns the name of this {@link Script}.
     * 
     * @return The name
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * This returns the author of this {@link Script}.
     * The author is the person who initially created and uploaded this {@link Script}.
     * 
     * @return The author of this {@link Script}
     */
    @Nonnull
    public String getAuthor() {
        return author;
    }

    /**
     * This method returns the actual code of this {@link Script}.
     * It is basically a {@link String} describing the order of {@link Instruction Instructions} that
     * shall be executed.
     * 
     * @return The code for this {@link Script}
     */
    @Nonnull
    public String getSourceCode() {
        return code;
    }

    /**
     * This method determines whether the given {@link OfflinePlayer} is the author of
     * this {@link Script}.
     * 
     * @param p
     *            The {@link OfflinePlayer} to check for
     * 
     * @return Whether the given {@link OfflinePlayer} is the author of this {@link Script}.
     */
    public boolean isAuthor(@Nonnull OfflinePlayer p) {
        return p.getUniqueId().equals(config.getUUID("author"));
    }

    /**
     * This method checks whether a given {@link Player} is able to leave a rating for this {@link Script}.
     * A {@link Player} is unable to rate his own {@link Script} or a {@link Script} he already rated before.
     * 
     * @param p
     *            The {@link Player} to check for
     * 
     * @return Whether the given {@link Player} is able to rate this {@link Script}
     */
    public boolean canRate(@Nonnull Player p) {
        if (isAuthor(p)) {
            return false;
        }

        List<String> upvoters = config.getStringList("rating.positive");
        List<String> downvoters = config.getStringList("rating.negative");
        return !upvoters.contains(p.getUniqueId().toString()) && !downvoters.contains(p.getUniqueId().toString());
    }

    @Nonnull
    ItemStack getAsItemStack(@Nonnull ProgrammableAndroid android, @Nonnull Player p) {
        List<String> lore = new LinkedList<>();
        lore.add("&7by &f" + getAuthor());
        lore.add("");
        lore.add("&7Downloads: &f" + getDownloads());
        lore.add("&7Rating: " + getScriptRatingPercentage());
        lore.add("&a" + getUpvotes() + " \u263A &7| &4\u2639 " + getDownvotes());
        lore.add("");
        lore.add("&eLeft Click &fto download this Script");
        lore.add("&4(This will override your current Script)");

        if (canRate(p)) {
            lore.add("");
            lore.add("&eShift + Left Click &fto leave a positive Rating");
            lore.add("&eShift + Right Click &fto leave a negative Rating");
        }

        return new CustomItemStack(android.getItem(), "&b" + getName(), lore.toArray(new String[0]));
    }

    @Nonnull
    private String getScriptRatingPercentage() {
        float percentage = getRating();
        return NumberUtils.getColorFromPercentage(percentage) + String.valueOf(percentage) + ChatColor.WHITE + "% ";
    }

    /**
     * This method returns the amount of upvotes this {@link Script} has received.
     * 
     * @return The amount of upvotes
     */
    public int getUpvotes() {
        return config.getStringList("rating.positive").size();
    }

    /**
     * This method returns the amount of downvotes this {@link Script} has received.
     * 
     * @return The amount of downvotes
     */
    public int getDownvotes() {
        return config.getStringList("rating.negative").size();
    }

    /**
     * This returns how often this {@link Script} has been downloaded.
     * 
     * @return The amount of downloads for this {@link Script}.
     */
    public int getDownloads() {
        return config.getInt("downloads");
    }

    /**
     * This returns the "rating" of this {@link Script}.
     * This value is calculated from the up- and downvotes this {@link Script} received.
     * 
     * @return The rating for this {@link Script}
     */
    public float getRating() {
        int positive = getUpvotes() + 1;
        int negative = getDownvotes();
        return Math.round((positive / (float) (positive + negative)) * 100.0F) / 100.0F;
    }

    /**
     * This method increases the amount of downloads by one.
     */
    public void download() {
        config.reload();
        config.setValue("downloads", getDownloads() + 1);
        config.save();
    }

    public void rate(@Nonnull Player p, boolean positive) {
        config.reload();

        String path = "rating." + (positive ? "positive" : "negative");
        List<String> list = config.getStringList(path);
        list.add(p.getUniqueId().toString());

        config.setValue(path, list);
        config.save();
    }

    @Nonnull
    public static List<Script> getUploadedScripts(@Nonnull AndroidType androidType) {
        List<Script> scripts = new LinkedList<>();

        loadScripts(scripts, androidType);

        if (androidType != AndroidType.NONE) {
            loadScripts(scripts, AndroidType.NONE);
        }

        Collections.sort(scripts, Comparator.comparingInt(script -> -script.getUpvotes() + 1 - script.getDownvotes()));
        return scripts;
    }

    private static void loadScripts(@Nonnull List<Script> scripts, @Nonnull AndroidType type) {
        File directory = new File("plugins/Slimefun/scripts/" + type.name());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".sfs")) {
                try {
                    Config config = new Config(file);

                    // Some older versions somehow allowed null values to slip in here sometimes
                    // So we need this check for compatibility with older scripts
                    if (config.contains("code") && config.contains("author")) {
                        scripts.add(new Script(config));
                    }
                } catch (Exception x) {
                    Slimefun.logger().log(Level.SEVERE, x, () -> "An Exception occurred while trying to load Android Script '" + file.getName() + "'");
                }
            }
        }
    }

    @ParametersAreNonnullByDefault
    public static void upload(Player p, AndroidType androidType, int id, String name, String code) {
        Config config = new Config("plugins/Slimefun/scripts/" + androidType.name() + '/' + p.getName() + ' ' + id + ".sfs");

        config.setValue("author", p.getUniqueId().toString());
        config.setValue("author_name", p.getName());
        config.setValue("name", ChatUtils.removeColorCodes(name));
        config.setValue("code", code);
        config.setValue("downloads", 0);
        config.setValue("android", androidType.name());
        config.setValue("rating.positive", new ArrayList<String>());
        config.setValue("rating.negative", new ArrayList<String>());
        config.save();
    }

}
