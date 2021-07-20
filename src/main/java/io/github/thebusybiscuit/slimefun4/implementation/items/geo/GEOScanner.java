package io.github.thebusybiscuit.slimefun4.implementation.items.geo;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

public class GEOScanner extends SimpleSlimefunItem<BlockUseHandler> {

    public GEOScanner(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public BlockUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            Player p = e.getPlayer();
            Optional<Block> block = e.getClickedBlock();

            if (block.isPresent()) {
                Block b = block.get();

                if (hasAccess(p, b.getLocation())) {
                    Slimefun.getGPSNetwork().getResourceManager().scan(p, b, 0);
                } else {
                    Slimefun.getLocalization().sendMessage(p, "inventory.no-access", true);
                }
            }
        };
    }

    @ParametersAreNonnullByDefault
    private boolean hasAccess(Player p, Location l) {
        return p.hasPermission("slimefun.gps.bypass") || (Slimefun.getProtectionManager().hasPermission(p, l, Interaction.INTERACT_BLOCK));
    }
}
