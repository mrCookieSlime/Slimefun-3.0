package io.github.thebusybiscuit.slimefun4.implementation.items.weapons;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityKillHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;

/**
 * The {@link SwordOfBeheading} is a special kind of sword which allows you to obtain
 * {@link Zombie}, {@link Skeleton} and {@link Creeper} skulls when killing the respective {@link Monster}.
 * Additionally, you can also obtain the head of a {@link Player} by killing them too.
 * This sword also allows you to have a higher chance of getting the skull of a {@link WitherSkeleton} too.
 * 
 * All chances are managed by an {@link ItemSetting} and can be configured.
 * 
 * @author TheBusyBiscuit
 *
 */
public class SwordOfBeheading extends SimpleSlimefunItem<EntityKillHandler> {

    private final ItemSetting<Integer> chanceZombie = new IntRangeSetting(this, "chance.ZOMBIE", 0, 40, 100);
    private final ItemSetting<Integer> chanceSkeleton = new IntRangeSetting(this, "chance.SKELETON", 0, 40, 100);
    private final ItemSetting<Integer> chanceWitherSkeleton = new IntRangeSetting(this, "chance.WITHER_SKELETON", 0, 25, 100);
    private final ItemSetting<Integer> chanceCreeper = new IntRangeSetting(this, "chance.CREEPER", 0, 40, 100);
    private final ItemSetting<Integer> chancePlayer = new IntRangeSetting(this, "chance.PLAYER", 0, 70, 100);

    @ParametersAreNonnullByDefault
    public SwordOfBeheading(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(chanceZombie, chanceSkeleton, chanceWitherSkeleton, chanceCreeper, chancePlayer);
    }

    @Override
    public EntityKillHandler getItemHandler() {
        return (e, entity, killer, item) -> {
            Random random = ThreadLocalRandom.current();

            switch (e.getEntityType()) {
                case ZOMBIE:
                    if (random.nextInt(100) < chanceZombie.getValue()) {
                        e.getDrops().add(new ItemStack(Material.ZOMBIE_HEAD));
                    }
                    break;
                case SKELETON:
                    if (random.nextInt(100) < chanceSkeleton.getValue()) {
                        e.getDrops().add(new ItemStack(Material.SKELETON_SKULL));
                    }
                    break;
                case CREEPER:
                    if (random.nextInt(100) < chanceCreeper.getValue()) {
                        e.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
                    }
                    break;
                case WITHER_SKELETON:
                    if (random.nextInt(100) < chanceWitherSkeleton.getValue()) {
                        e.getDrops().add(new ItemStack(Material.WITHER_SKELETON_SKULL));
                    }
                    break;
                case PLAYER:
                    if (random.nextInt(100) < chancePlayer.getValue()) {
                        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

                        ItemMeta meta = skull.getItemMeta();
                        ((SkullMeta) meta).setOwningPlayer((Player) e.getEntity());
                        skull.setItemMeta(meta);

                        e.getDrops().add(skull);
                    }
                    break;
                default:
                    break;
            }
        };
    }

}
