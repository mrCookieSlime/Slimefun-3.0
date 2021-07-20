package io.github.thebusybiscuit.slimefun4.api.items.settings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * This variation of {@link ItemSetting} allows you to define an {@link Double} range
 * and enforces this range using the {@link #validateInput(Double)} method.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ItemSetting
 * @see IntRangeSetting
 *
 */
public class DoubleRangeSetting extends ItemSetting<Double> {

    private final double min;
    private final double max;

    @ParametersAreNonnullByDefault
    public DoubleRangeSetting(SlimefunItem item, String key, double min, double defaultValue, double max) {
        super(item, key, defaultValue);
        Validate.isTrue(defaultValue >= min && defaultValue <= max, "The default value is not in range.");

        this.min = min;
        this.max = max;
    }

    @Nonnull
    @Override
    protected String getErrorMessage() {
        return "Only decimal numbers from " + min + '-' + max + "(inclusive) are allowed!";
    }

    @Override
    public boolean validateInput(Double input) {
        return super.validateInput(input) && input >= min && input <= max;
    }

    /**
     * This returns the minimum value of this {@link DoubleRangeSetting}.
     * 
     * @return The minimum value
     */
    public final double getMinimum() {
        return min;
    }

    /**
     * This returns the maximum value of this {@link DoubleRangeSetting}.
     * 
     * @return The maximum value
     */
    public final double getMaximum() {
        return max;
    }

}
