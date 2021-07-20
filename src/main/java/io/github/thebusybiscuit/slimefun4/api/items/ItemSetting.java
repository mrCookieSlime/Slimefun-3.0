package io.github.thebusybiscuit.slimefun4.api.items;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This class represents a Setting for a {@link SlimefunItem} that can be modified via
 * the {@code Items.yml} {@link Config} file.
 * 
 * @author TheBusyBiscuit
 *
 * @param <T>
 *            The type of data stored under this {@link ItemSetting}
 */
public class ItemSetting<T> {

    private final SlimefunItem item;

    private final String key;
    private final T defaultValue;

    private T value;

    /**
     * This creates a new {@link ItemSetting} with the given key and default value
     * 
     * @param item
     *            The {@link SlimefunItem} this {@link ItemSetting} belongs to
     * @param key
     *            The key under which this setting will be stored (relative to the {@link SlimefunItem})
     * @param defaultValue
     *            The default value for this {@link ItemSetting}
     */
    @ParametersAreNonnullByDefault
    public ItemSetting(SlimefunItem item, String key, T defaultValue) {
        Validate.notNull(item, "The provided SlimefunItem must not be null!");
        Validate.notNull(key, "The key of an ItemSetting is not allowed to be null!");
        Validate.notNull(defaultValue, "The default value of an ItemSetting is not allowed to be null!");

        this.item = item;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * This method checks if a given input would be valid as a value for this
     * {@link ItemSetting}. You can override this method to implement your own checks.
     * 
     * @param input
     *            The input value to validate
     * 
     * @return Whether the given input was valid
     */
    public boolean validateInput(T input) {
        return input != null;
    }

    /**
     * This method updates this {@link ItemSetting} with the given value.
     * Override this method to catch changes of a value.
     * A value may never be null.
     * 
     * @param newValue
     *            The new value for this {@link ItemSetting}
     */
    public void update(@Nonnull T newValue) {
        if (validateInput(newValue)) {
            this.value = newValue;
        } else {
            throw new IllegalArgumentException("The passed value was not valid. (Maybe null?)");
        }

        // Feel free to override this as necessary.
    }

    /**
     * This returns the key of this {@link ItemSetting}.
     * 
     * @return The key under which this setting is stored (relative to the {@link SlimefunItem})
     */
    public @Nonnull String getKey() {
        return key;
    }

    /**
     * This returns the associated {@link SlimefunItem} for this {@link ItemSetting}.
     * 
     * @return The associated {@link SlimefunItem}
     */
    protected @Nonnull SlimefunItem getItem() {
        return item;
    }

    /**
     * This returns the <strong>current</strong> value of this {@link ItemSetting}.
     * 
     * @return The current value
     */
    public @Nonnull T getValue() {
        if (value != null) {
            return value;
        } else {
            item.warn("ItemSetting '" + key + "' was invoked but was not initialized yet.");
            return defaultValue;
        }
    }

    /**
     * This returns the <strong>default</strong> value of this {@link ItemSetting}.
     * 
     * @return The default value
     */
    public @Nonnull T getDefaultValue() {
        return defaultValue;
    }

    /**
     * This method checks if this {@link ItemSetting} stores the given data type.
     * 
     * @param c
     *            The class of data type you want to compare
     * 
     * @return Whether this {@link ItemSetting} stores the given type
     */
    public boolean isType(@Nonnull Class<?> c) {
        return c.isInstance(defaultValue);
    }

    /**
     * This is an error message which should provide further context on what values
     * are allowed.
     * 
     * @return An error message which is displayed when this {@link ItemSetting} is misconfigured.
     */
    protected @Nonnull String getErrorMessage() {
        return "Only '" + defaultValue.getClass().getSimpleName() + "' values are allowed!";
    }

    /**
     * This method is called by a {@link SlimefunItem} which wants to load its {@link ItemSetting}
     * from the {@link Config} file.
     * 
     */
    @SuppressWarnings("unchecked")
    public void reload() {
        Validate.notNull(item, "Cannot apply settings for a non-existing SlimefunItem");

        Slimefun.getItemCfg().setDefaultValue(item.getId() + '.' + getKey(), getDefaultValue());
        Object configuredValue = Slimefun.getItemCfg().getValue(item.getId() + '.' + getKey());

        if (defaultValue.getClass().isInstance(configuredValue) || (configuredValue instanceof List && defaultValue instanceof List)) {
            // We can do an unsafe cast here, we did an isInstance(...) check before!
            T newValue = (T) configuredValue;

            if (validateInput(newValue)) {
                this.value = newValue;
            } else {
                // @formatter:off
                item.warn(
                        "We have found an invalid config setting in your Items.yml!" +
                        "\n  at \"" + item.getId() + "." + getKey() + "\"" +
                        "\n  " + configuredValue + " is not a valid input!" +
                        "\n" + getErrorMessage()
                );
                // @formatter:on
            }
        } else {
            this.value = defaultValue;
            String found = configuredValue == null ? "null" : configuredValue.getClass().getSimpleName();

            // @formatter:off
            item.warn(
                    "We have found an invalid config setting in your Items.yml!" +
                    "\nPlease only use settings that are valid." +
                    "\n  at \"" + item.getId() + "." + getKey() + "\"" +
                    "\n  Expected \"" + defaultValue.getClass().getSimpleName() + "\" but found: \"" + found + "\""
            );
            // @formatter:on
        }
    }

    @Override
    public String toString() {
        T currentValue = this.value != null ? this.value : defaultValue;
        return getClass().getSimpleName() + " {" + getKey() + " = " + currentValue + " (default: " + getDefaultValue() + ")";
    }

    @Override
    public final int hashCode() {
        return Objects.hash(item, key);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof ItemSetting) {
            ItemSetting<?> setting = (ItemSetting<?>) obj;
            return Objects.equals(getKey(), setting.getKey()) && Objects.equals(getItem(), setting.getItem());
        } else {
            return false;
        }
    }

}
