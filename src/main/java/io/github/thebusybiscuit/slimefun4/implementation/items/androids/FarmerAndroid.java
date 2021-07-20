package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.AndroidFarmEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

public class FarmerAndroid extends ProgrammableAndroid {

    @ParametersAreNonnullByDefault
    public FarmerAndroid(ItemGroup category, int tier, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, tier, item, recipeType, recipe);
    }

    @Override
    public AndroidType getAndroidType() {
        return getTier() == 1 ? AndroidType.FARMER : AndroidType.ADVANCED_FARMER;
    }

    @Override
    protected void farm(Block b, BlockMenu menu, Block block, boolean isAdvanced) {
        Material blockType = block.getType();
        BlockData data = block.getBlockData();
        ItemStack drop = null;

        if (data instanceof Ageable && ((Ageable) data).getAge() >= ((Ageable) data).getMaximumAge()) {
            drop = getDropFromCrop(blockType);
        }

        AndroidInstance instance = new AndroidInstance(this, b);

        AndroidFarmEvent event = new AndroidFarmEvent(block, instance, isAdvanced, drop);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            drop = event.getDrop();

            if (drop != null && menu.pushItem(drop, getOutputSlots()) == null) {
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, blockType);

                if (data instanceof Ageable) {
                    ((Ageable) data).setAge(0);
                    block.setBlockData(data);
                }
            }
        }
    }

    private ItemStack getDropFromCrop(Material crop) {
        Random random = ThreadLocalRandom.current();

        switch (crop) {
            case WHEAT:
                return new ItemStack(Material.WHEAT, random.nextInt(2) + 1);
            case POTATOES:
                return new ItemStack(Material.POTATO, random.nextInt(3) + 1);
            case CARROTS:
                return new ItemStack(Material.CARROT, random.nextInt(3) + 1);
            case BEETROOTS:
                return new ItemStack(Material.BEETROOT, random.nextInt(3) + 1);
            case COCOA:
                return new ItemStack(Material.COCOA_BEANS, random.nextInt(3) + 1);
            case NETHER_WART:
                return new ItemStack(Material.NETHER_WART, random.nextInt(3) + 1);
            case SWEET_BERRY_BUSH:
                return new ItemStack(Material.SWEET_BERRIES, random.nextInt(3) + 1);
            default:
                return null;
        }
    }

}
