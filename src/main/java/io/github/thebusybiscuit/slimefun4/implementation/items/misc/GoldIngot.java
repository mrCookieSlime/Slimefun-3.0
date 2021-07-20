package io.github.thebusybiscuit.slimefun4.implementation.items.misc;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.Smeltery;

/**
 * The {@link GoldIngot} from Slimefun is a simple resource which is divided into different
 * levels of carat ratings.
 * <p>
 * It can be obtained via gold dust and other gold ingots in a {@link Smeltery}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Smeltery
 *
 */
public class GoldIngot extends SlimefunItem {

    /**
     * The carat rating.
     */
    private final int caratRating;

    @ParametersAreNonnullByDefault
    public GoldIngot(ItemGroup category, int caratRating, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        Validate.isTrue(caratRating > 0, "Carat rating must be above zero.");
        Validate.isTrue(caratRating <= 24, "Carat rating cannot go above 24.");
        this.caratRating = caratRating;
    }

    /**
     * This returns the carat rating of this {@link GoldIngot}.
     * <p>
     * The purity of the {@link GoldIngot} is measured in carat (1-24).
     * 
     * <pre>
     * 24k = 100% gold.
     * 18k = 75% gold.
     * 12k = 50% gold.
     * </pre>
     * 
     * and so on...
     * 
     * @return The carat rating of this {@link GoldIngot}
     */
    public int getCaratRating() {
        return caratRating;
    }

}
