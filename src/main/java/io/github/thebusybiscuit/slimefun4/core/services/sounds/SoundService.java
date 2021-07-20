package io.github.thebusybiscuit.slimefun4.core.services.sounds;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import io.github.thebusybiscuit.cscorelib2.config.Config;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

/**
 * The {@link SoundService} is responsible for our sound management.
 * It allows server owners to fully customize their users' sound experience.
 * 
 * @author TheBusyBiscuit
 *
 */
public class SoundService {

    /**
     * Our {@link Config} instance.
     */
    private final Config config;

    /**
     * In this map we cache the corresponding {@link SoundConfiguration} to each {@link SoundEffect}.
     */
    private final Map<SoundEffect, SoundConfiguration> soundMap = new EnumMap<>(SoundEffect.class);

    public SoundService(@Nonnull SlimefunPlugin plugin) {
        config = new Config(plugin, "sounds.yml");

        // @formatter:off
        config.getConfiguration().options().header(
            "This file is used to assign the sounds which Slimefun will play." +
            "\nYou can fully customize any sound you want and even change their pitch" +
            "\nand volume. To disable a sound, simply set the volume to zero."
        );
        // @formatter:on

        config.getConfiguration().options().copyHeader(true);
    }

    /**
     * This method reloads every {@link SoundConfiguration}.
     * 
     * @param save
     *            Whether to save the defaults to disk
     */
    public void reload(boolean save) {
        config.reload();

        for (SoundEffect sound : SoundEffect.values()) {
            try {
                reloadSound(sound);
            } catch (Exception | LinkageError x) {
                SlimefunPlugin.logger().log(Level.SEVERE, x, () -> "An exception was thrown while trying to load the configuration data for the following sound:" + sound.name());
            }
        }

        if (save) {
            config.save();
        }
    }

    private void reloadSound(@Nonnull SoundEffect sound) {
        // Set up default values
        config.setDefaultValue(sound.name() + ".sound", sound.getDefaultSoundId());
        config.setDefaultValue(sound.name() + ".volume", sound.getDefaultVolume());
        config.setDefaultValue(sound.name() + ".pitch", sound.getDefaultPitch());

        // Read the values
        String soundId = config.getString(sound.name() + ".sound");
        float volume = config.getFloat(sound.name() + ".volume");
        float pitch = config.getFloat(sound.name() + ".pitch");

        // Check whether the volume is at least 0.0
        if (volume < 0) {
            SlimefunPlugin.logger().log(Level.WARNING, "Invalid value in sounds.yml! Volume for Sound \"{0}\" was {1} (must be at least 0.0)", new Object[] { sound.name(), volume });
            volume = 0;
        }

        // Check if the pitch is at least 0.5
        if (pitch < 0.5F) {
            SlimefunPlugin.logger().log(Level.WARNING, "Invalid value in sounds.yml! Pitch for Sound \"{0}\" was {1} (must be at least 0.5)", new Object[] { sound.name(), pitch });
            pitch = 0.5F;
        }

        // Cache this configuration
        SoundConfiguration configuration = new SoundConfiguration(soundId, volume, pitch);
        soundMap.put(sound, configuration);
    }

    /**
     * This returns the currently used (immutable) {@link SoundConfiguration} for the given {@link SoundEffect}.
     * 
     * @param sound
     *            The {@link SoundEffect}
     * 
     * @return The corresponding {@link SoundConfiguration}. This may be null if something went wrong
     */
    public @Nullable SoundConfiguration getConfiguration(@Nonnull SoundEffect sound) {
        Validate.notNull(sound, "The sound must not be null!");
        return soundMap.get(sound);
    }

}
