package io.github.thebusybiscuit.slimefun4.implementation.items.magical;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.inventory.ItemUtils;
import io.github.thebusybiscuit.cscorelib2.protection.ProtectableAction;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * This {@link SlimefunItem} allows you to convert any {@link ZombieVillager} to
 * their {@link Villager} variant. It is also one of the very few utilisations of {@link EntityInteractHandler}.
 * 
 * This item does not work on earlier versions than 1.14 as the {@link ZombieVillager} {@link EntityType}
 * did not exist back then.
 *
 * @author Linox
 *
 * @see EntityInteractHandler
 *
 */
public class MagicalZombiePills extends SimpleSlimefunItem<EntityInteractHandler> implements NotPlaceable {

    @ParametersAreNonnullByDefault
    public MagicalZombiePills(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput);

        addItemHandler(onRightClick());
    }

    @Override
    public EntityInteractHandler getItemHandler() {
        return (e, item, offhand) -> {
            Entity entity = e.getRightClicked();

            if (e.isCancelled() || !SlimefunPlugin.getProtectionManager().hasPermission(e.getPlayer(), entity.getLocation(), ProtectableAction.INTERACT_ENTITY)) {
                // They don't have permission to use it in this area
                return;
            }

            Player p = e.getPlayer();

            if (entity instanceof ZombieVillager) {
                useItem(p, item);
                healZombieVillager((ZombieVillager) entity, p);
            } else if (SlimefunPlugin.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_16) && entity instanceof PigZombie) {
                useItem(p, item);
                healZombifiedPiglin((PigZombie) entity);
            }
        };
    }

    /**
     * This method cancels {@link PlayerRightClickEvent} to prevent placing {@link MagicalZombiePills}.
     *
     * @return the {@link ItemUseHandler} of this {@link SlimefunItem}
     */
    public ItemUseHandler onRightClick() {
        return PlayerRightClickEvent::cancel;
    }

    private void useItem(@Nonnull Player p, @Nonnull ItemStack item) {
        if (p.getGameMode() != GameMode.CREATIVE) {
            ItemUtils.consumeItem(item, false);
        }

        // This is supposed to be a vanilla sound. No need for a SoundEffect
        p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1, 1);
    }

    private void healZombieVillager(@Nonnull ZombieVillager zombieVillager, @Nonnull Player p) {
        zombieVillager.setConversionTime(1);

        if (SlimefunPlugin.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_15)) {
            zombieVillager.setConversionPlayer(p);
        }
    }

    private void healZombifiedPiglin(@Nonnull PigZombie zombiePiglin) {
        Location loc = zombiePiglin.getLocation();

        zombiePiglin.remove();
        loc.getWorld().spawnEntity(loc, EntityType.PIGLIN);
    }
}
