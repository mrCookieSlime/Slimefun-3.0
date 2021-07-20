package io.github.thebusybiscuit.slimefun4.api.items.settings;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * This variation of {@link ItemSetting} allows you to allow {@link Enum} constants to be
 * used for {@link ItemSetting} validation.
 * 
 * @param <T>
 *            The {@link Enum} type
 * 
 * @author TheBusyBiscuit
 * 
 * @see ItemSetting
 *
 */
public class EnumSetting<T extends Enum<T>> extends ItemSetting<String> {

    private final Class<T> enumClass;

    @ParametersAreNonnullByDefault
    public EnumSetting(SlimefunItem item, String key, Class<T> enumClass, T defaultValue) {
        super(item, key, defaultValue.name());

        this.enumClass = enumClass;
    }

    @Nonnull
    @Override
    protected String getErrorMessage() {
        String values = Arrays.stream(getAllowedValues()).map(Enum::name).collect(Collectors.joining(", "));
        return "The following values are valid: " + values;
    }

    /**
     * This returns an array of valid {@link Enum} values.
     * This method may be overridden to further limit the allowed values.
     * 
     * @return An array of allowed {@link Enum} constants
     */
    @Nonnull
    public T[] getAllowedValues() {
        return enumClass.getEnumConstants();
    }

    /**
     * This will attempt to get the configured value as a constant of the desired {@link Enum}.
     * 
     * @return The value as an {@link Enum} constant
     */
    @Nonnull
    public T getAsEnumConstant() {
        return Enum.valueOf(enumClass, getValue());
    }

    @Override
    public boolean validateInput(String input) {
        if (!super.validateInput(input)) {
            return false;
        } else {
            for (Enum<T> value : getAllowedValues()) {
                if (value.name().equals(input)) {
                    return true;
                }
            }

            return false;
        }
    }

}
