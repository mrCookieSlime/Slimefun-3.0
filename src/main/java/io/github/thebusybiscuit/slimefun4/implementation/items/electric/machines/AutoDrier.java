package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotHopperable;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;

/**
 * The {@link AutoDrier} is an implementation of {@link AContainer} that features recipes
 * related to "drying out" items.
 * It also allows you to convert Rotten Flesh into Leather.
 * 
 * @author Linox
 *
 */
public class AutoDrier extends AContainer implements RecipeDisplayItem, NotHopperable {

    private List<ItemStack> recipeList;

    @ParametersAreNonnullByDefault
    public AutoDrier(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    protected void registerDefaultRecipes() {
        recipeList = new ArrayList<>();
        recipeList.add(new ItemStack(Material.ROTTEN_FLESH));
        recipeList.add(new ItemStack(Material.LEATHER));

        recipeList.add(new ItemStack(Material.WET_SPONGE));
        recipeList.add(new ItemStack(Material.SPONGE));

        recipeList.add(new ItemStack(Material.KELP));
        recipeList.add(new ItemStack(Material.DRIED_KELP));

        recipeList.add(new ItemStack(Material.POTION));
        recipeList.add(new ItemStack(Material.GLASS_BOTTLE));

        recipeList.add(new ItemStack(Material.SPLASH_POTION));
        recipeList.add(new ItemStack(Material.GLASS_BOTTLE));

        recipeList.add(new ItemStack(Material.LINGERING_POTION));
        recipeList.add(new ItemStack(Material.GLASS_BOTTLE));

        recipeList.add(new ItemStack(Material.WATER_BUCKET));
        recipeList.add(new ItemStack(Material.BUCKET));

        recipeList.add(new ItemStack(Material.COOKED_BEEF));
        recipeList.add(SlimefunItems.BEEF_JERKY);

        recipeList.add(new ItemStack(Material.COOKED_PORKCHOP));
        recipeList.add(SlimefunItems.PORK_JERKY);

        recipeList.add(new ItemStack(Material.COOKED_CHICKEN));
        recipeList.add(SlimefunItems.CHICKEN_JERKY);

        recipeList.add(new ItemStack(Material.COOKED_MUTTON));
        recipeList.add(SlimefunItems.MUTTON_JERKY);

        recipeList.add(new ItemStack(Material.COOKED_RABBIT));
        recipeList.add(SlimefunItems.RABBIT_JERKY);

        recipeList.add(new ItemStack(Material.COOKED_COD));
        recipeList.add(SlimefunItems.FISH_JERKY);

        recipeList.add(new ItemStack(Material.COOKED_SALMON));
        recipeList.add(SlimefunItems.FISH_JERKY);

        for (Material sapling : Tag.SAPLINGS.getValues()) {
            recipeList.add(new ItemStack(sapling));
            recipeList.add(new ItemStack(Material.STICK, 2));
        }

        for (Material leaves : Tag.LEAVES.getValues()) {
            recipeList.add(new ItemStack(leaves));
            recipeList.add(new ItemStack(Material.STICK));
        }

        // Now convert them to machine recipes
        for (int i = 0; i < recipeList.size(); i += 2) {
            registerRecipe(6, recipeList.get(i), recipeList.get(i + 1));
        }
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.FLINT_AND_STEEL);
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        return recipeList;
    }

    @Override
    public String getMachineIdentifier() {
        return "AUTO_DRIER";
    }
}
