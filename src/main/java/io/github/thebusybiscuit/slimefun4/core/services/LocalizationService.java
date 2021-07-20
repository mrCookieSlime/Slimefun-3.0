package io.github.thebusybiscuit.slimefun4.core.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.core.services.localization.LanguageFile;
import io.github.thebusybiscuit.slimefun4.core.services.localization.SlimefunLocalization;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;

/**
 * As the name suggests, this Service is responsible for Localization.
 * It is used for managing the {@link Language} of a {@link Player} and the entire {@link Server}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Language
 *
 */
public class LocalizationService extends SlimefunLocalization {

    private static final String LANGUAGE_PATH = "language";

    // All supported languages are stored in this LinkedHashMap, it is Linked so we keep the order
    private final Map<String, Language> languages = new LinkedHashMap<>();
    private final boolean translationsEnabled;
    private final Slimefun plugin;
    private final String prefix;
    private final NamespacedKey languageKey;
    private final Language defaultLanguage;

    public LocalizationService(@Nonnull Slimefun plugin, @Nullable String prefix, @Nullable String serverDefaultLanguage) {
        super(plugin);

        this.plugin = plugin;
        this.prefix = prefix;
        languageKey = new NamespacedKey(plugin, LANGUAGE_PATH);

        if (serverDefaultLanguage != null) {
            translationsEnabled = Slimefun.getCfg().getBoolean("options.enable-translations");

            defaultLanguage = new Language(serverDefaultLanguage, "11b3188fd44902f72602bd7c2141f5a70673a411adb3d81862c69e536166b");
            defaultLanguage.setFile(LanguageFile.MESSAGES, getConfig().getConfiguration());

            loadEmbeddedLanguages();

            String language = getConfig().getString(LANGUAGE_PATH);

            if (language == null) {
                language = serverDefaultLanguage;
            }

            if (hasLanguage(serverDefaultLanguage)) {
                setLanguage(serverDefaultLanguage, !serverDefaultLanguage.equals(language));
            } else {
                setLanguage("en", false);
                plugin.getLogger().log(Level.WARNING, "Could not recognize the given language: \"{0}\"", serverDefaultLanguage);
            }

            Slimefun.logger().log(Level.INFO, "Available languages: {0}", String.join(", ", languages.keySet()));
            save();
        } else {
            translationsEnabled = false;
            defaultLanguage = null;
        }
    }

    /**
     * This method returns whether translations are enabled on this {@link Server}.
     * 
     * @return Whether translations are enabled
     */
    public boolean isEnabled() {
        return translationsEnabled;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    @Nonnull
    public NamespacedKey getKey() {
        return languageKey;
    }

    @Override
    @Nullable
    public Language getLanguage(@Nonnull String id) {
        Validate.notNull(id, "The language id cannot be null");
        return languages.get(id);
    }

    @Override
    @Nonnull
    public Collection<Language> getLanguages() {
        return languages.values();
    }

    @Override
    public boolean hasLanguage(@Nonnull String id) {
        Validate.notNull(id, "The language id cannot be null");

        // Checks if our jar files contains a messages.yml file for that language
        String file = LanguageFile.MESSAGES.getFilePath(id);
        return !streamConfigFile(file, null).getKeys(false).isEmpty();
    }

    /**
     * This returns whether the given {@link Language} is loaded or not.
     * 
     * @param id
     *            The id of that {@link Language}
     * 
     * @return Whether or not this {@link Language} is loaded
     */
    public boolean isLanguageLoaded(@Nonnull String id) {
        Validate.notNull(id, "The language cannot be null!");
        return languages.containsKey(id);
    }

    @Override
    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    @Override
    public Language getLanguage(@Nonnull Player p) {
        Validate.notNull(p, "Player cannot be null!");

        PersistentDataContainer container = p.getPersistentDataContainer();
        String language = container.get(languageKey, PersistentDataType.STRING);

        if (language != null) {
            Language lang = languages.get(language);

            if (lang != null) {
                return lang;
            }
        }

        return getDefaultLanguage();
    }

    private void setLanguage(@Nonnull String language, boolean reset) {
        // Clearing out the old Language (if necessary)
        if (reset) {
            getConfig().clear();
        }

        // Copy defaults
        for (LanguageFile file : LanguageFile.values()) {
            if (file != LanguageFile.MESSAGES) {
                copyToDefaultLanguage(language, file);
            }
        }

        Slimefun.logger().log(Level.INFO, "Loaded language \"{0}\"", language);
        getConfig().setValue(LANGUAGE_PATH, language);

        // Loading in the defaults from our resources folder
        String path = "/languages/" + language + "/messages.yml";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(plugin.getClass().getResourceAsStream(path), StandardCharsets.UTF_8))) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(reader);
            getConfig().getConfiguration().setDefaults(config);
        } catch (IOException e) {
            Slimefun.logger().log(Level.SEVERE, e, () -> "Failed to load language file: \"" + path + "\"");
        }

        save();
    }

    @ParametersAreNonnullByDefault
    private void copyToDefaultLanguage(String language, LanguageFile file) {
        FileConfiguration config = streamConfigFile(file.getFilePath(language), null);
        defaultLanguage.setFile(file, config);
    }

    @Override
    protected void addLanguage(@Nonnull String id, @Nonnull String texture) {
        Validate.notNull(id, "The language id cannot be null!");
        Validate.notNull(texture, "The language texture cannot be null");

        if (hasLanguage(id)) {
            Language language = new Language(id, texture);

            for (LanguageFile file : LanguageFile.values()) {
                FileConfiguration defaults = file == LanguageFile.MESSAGES ? getConfig().getConfiguration() : null;
                FileConfiguration config = streamConfigFile(file.getFilePath(language), defaults);
                language.setFile(file, config);
            }

            languages.put(id, language);
        }
    }

    /**
     * This returns the progress of translation for any given {@link Language}.
     * The progress is determined by the amount of translated strings divided by the amount
     * of strings in the english {@link Language} file and multiplied by 100.0
     * 
     * @param lang
     *            The {@link Language} to get the progress of
     * 
     * @return A percentage {@code (0.0 - 100.0)} for the progress of translation of that {@link Language}
     */
    public double calculateProgress(@Nonnull Language lang) {
        Validate.notNull(lang, "Cannot get the language progress of null");

        Set<String> defaultKeys = getTotalKeys(languages.get("en"));

        if (defaultKeys.isEmpty()) {
            return 0;
        }

        Set<String> keys = getTotalKeys(lang);
        int matches = 0;

        for (String key : defaultKeys) {
            if (keys.contains(key)) {
                matches++;
            }
        }

        return Math.min(NumberUtils.reparseDouble(100.0 * (matches / (double) defaultKeys.size())), 100.0);
    }

    @Nonnull
    private FileConfiguration streamConfigFile(@Nonnull String file, @Nullable FileConfiguration defaults) {
        InputStream inputStream = plugin.getClass().getResourceAsStream(file);

        if (inputStream == null) {
            return new YamlConfiguration();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(reader);

            if (defaults != null) {
                config.setDefaults(defaults);
            }

            return config;
        } catch (IOException e) {
            Slimefun.logger().log(Level.SEVERE, e, () -> "Failed to load language file into memory: \"" + file + "\"");
            return new YamlConfiguration();
        }
    }
}
