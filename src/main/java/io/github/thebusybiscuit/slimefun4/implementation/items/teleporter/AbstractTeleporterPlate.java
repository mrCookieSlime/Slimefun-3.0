package io.github.thebusybiscuit.slimefun4.implementation.items.teleporter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.TeleporterListener;

/**
 * The {@link AbstractTeleporterPlate} is the abstract super class
 * for our teleporter activation plates.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Teleporter
 * @see TeleporterPylon
 * @see TeleporterListener
 * @see PersonalActivationPlate
 * @see SharedActivationPlate
 *
 */
public abstract class AbstractTeleporterPlate extends SlimefunItem {

    @ParametersAreNonnullByDefault
    protected AbstractTeleporterPlate(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    /**
     * This method checks whether the given {@link Player} is allowed to activate
     * the {@link Teleporter}.
     * 
     * @param p
     *            The {@link Player} who stepped onto this plate
     * @param block
     *            The actual {@link Block} of this activation plate
     * 
     * @return Whether the {@link Player} can access the {@link Teleporter}
     */
    public abstract boolean hasAccess(@Nonnull Player p, @Nonnull Block block);

}
