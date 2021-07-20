package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.collections.RandomizedSet;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

public class FisherAndroid extends ProgrammableAndroid {

    private final RandomizedSet<ItemStack> fishingLoot = new RandomizedSet<>();

    public FisherAndroid(ItemGroup category, int tier, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, tier, item, recipeType, recipe);

        // Fish
        for (Material fish : Tag.ITEMS_FISHES.getValues()) {
            fishingLoot.add(new ItemStack(fish), 25);
        }

        // Junk
        fishingLoot.add(new ItemStack(Material.BONE), 10);
        fishingLoot.add(new ItemStack(Material.STRING), 10);
        fishingLoot.add(new ItemStack(Material.INK_SAC), 8);
        fishingLoot.add(new ItemStack(Material.KELP), 6);
        fishingLoot.add(new ItemStack(Material.STICK), 5);
        fishingLoot.add(new ItemStack(Material.ROTTEN_FLESH), 3);
        fishingLoot.add(new ItemStack(Material.LEATHER), 2);
        fishingLoot.add(new ItemStack(Material.BAMBOO), 3);

        // "loot"
        fishingLoot.add(new ItemStack(Material.SADDLE), 1);
        fishingLoot.add(new ItemStack(Material.NAME_TAG), 1);
        fishingLoot.add(new ItemStack(Material.NAUTILUS_SHELL), 1);
    }

    @Override
    public AndroidType getAndroidType() {
        return AndroidType.FISHERMAN;
    }

    @Override
    protected void fish(Block b, BlockMenu menu) {
        Block water = b.getRelative(BlockFace.DOWN);

        if (water.getType() == Material.WATER) {
            water.getWorld().playSound(water.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 0.3F, 0.7F);

            if (ThreadLocalRandom.current().nextInt(100) < 10 * getTier()) {
                ItemStack drop = fishingLoot.getRandom();
                menu.pushItem(drop.clone(), getOutputSlots());
            }

        }
    }

}
