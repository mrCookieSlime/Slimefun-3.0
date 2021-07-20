package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.AndroidInstance;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.ButcherAndroid;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

/**
 * This {@link Listener} handles the collection of drops from an {@link Entity} that was
 * killed by a {@link ButcherAndroid}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class ButcherAndroidListener implements Listener {

    private static final String METADATA_KEY = "android_killer";

    public ButcherAndroidListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().hasMetadata(METADATA_KEY)) {
            AndroidInstance obj = (AndroidInstance) e.getEntity().getMetadata(METADATA_KEY).get(0).value();

            Slimefun.runSync(() -> {
                List<ItemStack> items = new ArrayList<>();

                // Collect any nearby dropped items
                for (Entity n : e.getEntity().getNearbyEntities(0.5D, 0.5D, 0.5D)) {
                    if (n instanceof Item && n.isValid() && !SlimefunUtils.hasNoPickupFlag((Item) n)) {
                        items.add(((Item) n).getItemStack());
                        n.remove();
                    }
                }

                addExtraDrops(items, e.getEntityType());

                obj.getAndroid().addItems(obj.getBlock(), items.toArray(new ItemStack[0]));
                ExperienceOrb exp = (ExperienceOrb) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.EXPERIENCE_ORB);
                exp.setExperience(1 + ThreadLocalRandom.current().nextInt(6));
            }, 1L);

            // Removing metadata to prevent memory leaks
            e.getEntity().removeMetadata(METADATA_KEY, Slimefun.instance());
        }
    }

    /**
     * Some items are not dropped by default.
     * Wither Skeleton Skulls but for some weird reason
     * even Blaze rods...
     * 
     * @param drops
     *            The {@link List} of item drops
     * @param entityType
     *            The {@link EntityType} of the killed entity
     */
    @ParametersAreNonnullByDefault
    private void addExtraDrops(List<ItemStack> drops, EntityType entityType) {
        Random random = ThreadLocalRandom.current();

        if (entityType == EntityType.WITHER_SKELETON && random.nextInt(250) < 2) {
            drops.add(new ItemStack(Material.WITHER_SKELETON_SKULL));
        }

        if (entityType == EntityType.BLAZE) {
            drops.add(new ItemStack(Material.BLAZE_ROD, 1 + random.nextInt(1)));
        }
    }
}
