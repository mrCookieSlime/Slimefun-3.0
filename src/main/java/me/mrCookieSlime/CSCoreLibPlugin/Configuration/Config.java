package me.mrCookieSlime.CSCoreLibPlugin.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * An old remnant of CS-CoreLib.
 * This will be removed once we updated everything.
 * Don't look at the code, it will be gone soon, don't worry.
 * 
 * @deprecated Do not use this. At all...
 *
 */
@Deprecated
public class Config {

    private final File file;
    private FileConfiguration config;

    /**
     * Creates a new Config Object for the specified File
     *
     * @param file
     *            The File for which the Config object is created for
     */
    public Config(File file) {
        this(file, YamlConfiguration.loadConfiguration(file));
    }

    /**
     * Creates a new Config Object for the specified File and FileConfiguration
     *
     * @param file
     *            The File to save to
     * @param config
     *            The FileConfiguration
     */
    public Config(File file, FileConfiguration config) {
        this.file = file;
        this.config = config;
    }

    /**
     * Creates a new Config Object for the File with in
     * the specified Location
     *
     * @param path
     *            The Path of the File which the Config object is created for
     */
    public Config(String path) {
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * Returns the File the Config is handling
     *
     * @return The File this Config is handling
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Converts this Config Object into a plain FileConfiguration Object
     *
     * @return The converted FileConfiguration Object
     */
    public FileConfiguration getConfiguration() {
        return this.config;
    }

    /**
     * Sets the Value for the specified Path
     *
     * @param path
     *            The path in the Config File
     * @param value
     *            The Value for that Path
     */
    public void setValue(String path, Object value) {
        this.config.set(path, value);
    }

    /**
     * Saves the Config Object to its File
     */
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {}
    }

    /**
     * Saves the Config Object to a File
     * 
     * @param file
     *            The File you are saving this Config to
     */
    public void save(File file) {
        try {
            config.save(file);
        } catch (IOException e) {}
    }

    /**
     * Sets the Value for the specified Path
     * (IF the Path does not yet exist)
     *
     * @param path
     *            The path in the Config File
     * @param value
     *            The Value for that Path
     */
    public void setDefaultValue(String path, Object value) {
        if (!contains(path)) {
            setValue(path, value);
        }
    }

    /**
     * Checks whether the Config contains the specified Path
     *
     * @param path
     *            The path in the Config File
     * @return True/false
     */
    public boolean contains(String path) {
        return config.contains(path);
    }

    /**
     * Returns the Object at the specified Path
     *
     * @param path
     *            The path in the Config File
     * @return The Value at that Path
     */
    public Object getValue(String path) {
        return config.get(path);
    }

    /**
     * Returns the String at the specified Path
     *
     * @param path
     *            The path in the Config File
     * @return The String at that Path
     */
    public String getString(String path) {
        return config.getString(path);
    }

    /**
     * Recreates the File of this Config
     */
    public void createFile() {
        try {
            this.file.createNewFile();
        } catch (IOException e) {}
    }

    /**
     * Returns all Paths in this Config
     *
     * @return All Paths in this Config
     */
    public Set<String> getKeys() {
        return config.getKeys(false);
    }

    /**
     * Returns all Sub-Paths in this Config
     *
     * @param path
     *            The path in the Config File
     * @return All Sub-Paths of the specified Path
     */
    public Set<String> getKeys(String path) {
        return config.getConfigurationSection(path).getKeys(false);
    }

    /**
     * Reloads the Configuration File
     */
    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }
}
