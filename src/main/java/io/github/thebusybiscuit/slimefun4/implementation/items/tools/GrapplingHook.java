package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.GrapplingHookListener;

/**
 * The {@link GrapplingHook} is a simple {@link SlimefunItem} which allows a {@link Player}
 * to launch towards a target destination via right click.
 * It also has a cool visual effect where it shows a leash following a fired {@link Arrow}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see GrapplingHookListener
 *
 */
public class GrapplingHook extends SimpleSlimefunItem<ItemUseHandler> {

    private final ItemSetting<Boolean> consumeOnUse = new ItemSetting<>(this, "consume-on-use", true);
    private final ItemSetting<Integer> despawnTicks = new IntRangeSetting(this, "despawn-seconds", 0, 60, Integer.MAX_VALUE);

    @ParametersAreNonnullByDefault
    public GrapplingHook(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(despawnTicks);
        addItemSetting(consumeOnUse);
    }

    @Override
    public @Nonnull ItemUseHandler getItemHandler() {
        return e -> {
            Player p = e.getPlayer();
            UUID uuid = p.getUniqueId();
            boolean isConsumed = consumeOnUse.getValue() && p.getGameMode() != GameMode.CREATIVE;

            if (!e.getClickedBlock().isPresent() && !Slimefun.getGrapplingHookListener().isGrappling(uuid)) {
                e.cancel();

                if (p.getInventory().getItemInOffHand().getType() == Material.BOW) {
                    // Cancel, to fix dupe #740
                    return;
                }

                ItemStack item = e.getItem();

                if (item.getType() == Material.LEAD && isConsumed) {
                    // If consume on use is enabled, consume one item
                    ItemUtils.consumeItem(item, false);
                }

                Vector direction = p.getEyeLocation().getDirection().multiply(2.0);
                Arrow arrow = p.getWorld().spawn(p.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), Arrow.class);
                arrow.setShooter(p);
                arrow.setVelocity(direction);

                Bat bat = (Bat) p.getWorld().spawnEntity(p.getLocation(), EntityType.BAT);
                bat.setCanPickupItems(false);
                bat.setAI(false);
                bat.setSilent(true);
                bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 100000));
                bat.setLeashHolder(arrow);

                boolean state = item.getType() != Material.SHEARS;
                Slimefun.getGrapplingHookListener().addGrapplingHook(p, arrow, bat, state, despawnTicks.getValue(), isConsumed);
            }
        };
    }
}
