package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.settings.GoldPanDrop;

/**
 * The {@link NetherGoldPan} is a variant of the regular {@link GoldPan}
 * which can be used on Soul Sand.
 * 
 * @author TheBusyBiscuit
 *
 */
public class NetherGoldPan extends GoldPan {

    @ParametersAreNonnullByDefault
    public NetherGoldPan(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public @Nonnull Material getInputMaterial() {
        return Material.SOUL_SAND;
    }

    @Override
    protected @Nonnull Set<GoldPanDrop> getGoldPanDrops() {
        Set<GoldPanDrop> settings = new HashSet<>();

        settings.add(new GoldPanDrop(this, "chance.QUARTZ", 50, new ItemStack(Material.QUARTZ)));
        settings.add(new GoldPanDrop(this, "chance.GOLD_NUGGET", 25, new ItemStack(Material.GOLD_NUGGET)));
        settings.add(new GoldPanDrop(this, "chance.NETHER_WART", 10, new ItemStack(Material.NETHER_WART)));
        settings.add(new GoldPanDrop(this, "chance.BLAZE_POWDER", 8, new ItemStack(Material.BLAZE_POWDER)));
        settings.add(new GoldPanDrop(this, "chance.GLOWSTONE_DUST", 5, new ItemStack(Material.GLOWSTONE_DUST)));
        settings.add(new GoldPanDrop(this, "chance.GHAST_TEAR", 2, new ItemStack(Material.GHAST_TEAR)));

        return settings;
    }

}
