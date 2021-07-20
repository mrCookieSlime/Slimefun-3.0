package io.github.thebusybiscuit.slimefun4.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This is just a simple helper class to provide static methods to the {@link Rechargeable}
 * interface.
 *
 * @author TheBusyBiscuit
 * @author WalshyDev
 *
 * @see Rechargeable
 *
 */
public final class ChargeUtils {

    private static final String LORE_PREFIX = ChatColors.color("&8\u21E8 &e\u26A1 &7");
    private static final Pattern REGEX = Pattern.compile(ChatColors.color("(&c&o)?" + LORE_PREFIX) + "[0-9.]+ / [0-9.]+ J");

    private ChargeUtils() {}

    public static void setCharge(@Nonnull ItemMeta meta, float charge, float capacity) {
        Validate.notNull(meta, "Meta cannot be null!");
        Validate.isTrue(charge >= 0, "Charge has to be equal to or greater than 0!");
        Validate.isTrue(capacity > 0, "Capacity has to be greater than 0!");
        Validate.isTrue(charge <= capacity, "Charge may not be bigger than the capacity!");

        BigDecimal decimal = BigDecimal.valueOf(charge).setScale(2, RoundingMode.HALF_UP);
        float value = decimal.floatValue();

        NamespacedKey key = Slimefun.getRegistry().getItemChargeDataKey();
        meta.getPersistentDataContainer().set(key, PersistentDataType.FLOAT, value);

        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);

            if (REGEX.matcher(line).matches()) {
                lore.set(i, LORE_PREFIX + value + " / " + capacity + " J");
                meta.setLore(lore);
                return;
            }
        }

        lore.add(LORE_PREFIX + value + " / " + capacity + " J");
        meta.setLore(lore);
    }

    public static float getCharge(@Nonnull ItemMeta meta) {
        Validate.notNull(meta, "Meta cannot be null!");

        NamespacedKey key = Slimefun.getRegistry().getItemChargeDataKey();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Float value = container.get(key, PersistentDataType.FLOAT);

        // If persistent data is available, we just return this value
        if (value != null) {
            return value;
        }

        // If no persistent data exists, we will just fall back to the lore
        if (meta.hasLore()) {
            for (String line : meta.getLore()) {
                if (REGEX.matcher(line).matches()) {
                    String data = ChatColor.stripColor(PatternUtils.SLASH_SEPARATOR.split(line)[0].replace(LORE_PREFIX, ""));

                    float loreValue = Float.parseFloat(data);
                    container.set(key, PersistentDataType.FLOAT, loreValue);
                    return loreValue;
                }
            }
        }

        return 0;
    }
}
