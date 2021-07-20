package io.github.thebusybiscuit.slimefun4.api.items.settings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * This variation of {@link ItemSetting} allows you to define an {@link Integer} range
 * and enforces this range using the {@link #validateInput(Integer)} method.
 * 
 * @author TheBusyBiscuit
 * 
 * @see ItemSetting
 * @see DoubleRangeSetting
 *
 */
public class IntRangeSetting extends ItemSetting<Integer> {

    private final int min;
    private final int max;

    @ParametersAreNonnullByDefault
    public IntRangeSetting(SlimefunItem item, String key, int min, int defaultValue, int max) {
        super(item, key, defaultValue);
        Validate.isTrue(defaultValue >= min && defaultValue <= max, "The default value is not in range.");

        this.min = min;
        this.max = max;
    }

    @Nonnull
    @Override
    protected String getErrorMessage() {
        return "Only whole numbers from " + min + '-' + max + "(inclusive) are allowed!";
    }

    @Override
    public boolean validateInput(Integer input) {
        return super.validateInput(input) && input >= min && input <= max;
    }

    /**
     * This returns the minimum value of this {@link IntRangeSetting}.
     * 
     * @return The minimum value
     */
    public final int getMinimum() {
        return min;
    }

    /**
     * This returns the maximum value of this {@link IntRangeSetting}.
     * 
     * @return The maximum value
     */
    public final int getMaximum() {
        return max;
    }

}
