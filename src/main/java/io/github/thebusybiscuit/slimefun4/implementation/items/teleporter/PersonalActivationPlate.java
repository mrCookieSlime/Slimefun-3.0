package io.github.thebusybiscuit.slimefun4.implementation.items.teleporter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link PersonalActivationPlate} is a teleporter activation plate
 * to which only the {@link Player} who placed it down has access.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SharedActivationPlate
 *
 */
public class PersonalActivationPlate extends AbstractTeleporterPlate {

    @ParametersAreNonnullByDefault
    public PersonalActivationPlate(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemHandler(onPlace());
    }

    @Nonnull
    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "owner", e.getPlayer().getUniqueId().toString());
            }
        };
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean hasAccess(Player p, Block b) {
        return BlockStorage.getLocationInfo(b.getLocation(), "owner").equals(p.getUniqueId().toString());
    }
}
