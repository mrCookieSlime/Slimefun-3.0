package io.github.thebusybiscuit.slimefun4.implementation.items.seasonal;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.FireworkUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

/**
 * The {@link ChristmasPresent} is a seasonal {@link SlimefunItem} that drops a random
 * gift when being placed down.
 * 
 * @author TheBusyBiscuit
 * 
 * @see EasterEgg
 *
 */
public class ChristmasPresent extends SimpleSlimefunItem<ItemUseHandler> implements NotPlaceable {

    private final ItemStack[] gifts;

    @ParametersAreNonnullByDefault
    public ChristmasPresent(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack... gifts) {
        super(category, item, recipeType, recipe);

        this.gifts = gifts;
    }

    @Override
    public @Nonnull ItemUseHandler getItemHandler() {
        return e -> {
            e.cancel();

            e.getClickedBlock().ifPresent(block -> {
                if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    ItemUtils.consumeItem(e.getItem(), false);
                }

                FireworkUtils.launchRandom(e.getPlayer(), 3);

                Block b = block.getRelative(e.getClickedFace());
                ItemStack gift = gifts[ThreadLocalRandom.current().nextInt(gifts.length)].clone();
                SlimefunUtils.spawnItem(b.getLocation(), gift, ItemSpawnReason.CHRISTMAS_PRESENT_OPENED, true);
            });
        };
    }

}
