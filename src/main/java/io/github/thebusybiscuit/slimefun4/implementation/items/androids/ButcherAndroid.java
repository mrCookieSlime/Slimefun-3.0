package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import java.util.function.Predicate;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

public class ButcherAndroid extends ProgrammableAndroid {

    private static final String METADATA_KEY = "android_killer";

    public ButcherAndroid(ItemGroup category, int tier, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, tier, item, recipeType, recipe);
    }

    @Override
    public AndroidType getAndroidType() {
        return AndroidType.FIGHTER;
    }

    @Override
    protected void attack(Block b, BlockFace face, Predicate<LivingEntity> predicate) {
        double damage = getTier() >= 3 ? 20D : 4D * getTier();
        double radius = 4.0 + getTier();

        for (Entity n : b.getWorld().getNearbyEntities(b.getLocation(), radius, radius, radius, n -> n instanceof LivingEntity && !(n instanceof ArmorStand) && !(n instanceof Player) && n.isValid() && predicate.test((LivingEntity) n))) {
            boolean attack = false;

            switch (face) {
                case NORTH:
                    attack = n.getLocation().getZ() < b.getZ();
                    break;
                case EAST:
                    attack = n.getLocation().getX() > b.getX();
                    break;
                case SOUTH:
                    attack = n.getLocation().getZ() > b.getZ();
                    break;
                case WEST:
                    attack = n.getLocation().getX() < b.getX();
                    break;
                default:
                    break;
            }

            if (attack) {
                if (n.hasMetadata(METADATA_KEY)) {
                    n.removeMetadata(METADATA_KEY, Slimefun.instance());
                }

                n.setMetadata(METADATA_KEY, new FixedMetadataValue(Slimefun.instance(), new AndroidInstance(this, b)));

                ((LivingEntity) n).damage(damage);
                break;
            }
        }
    }

}
