package io.github.thebusybiscuit.slimefun4.implementation.items.weapons;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BowShootHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * The {@link IcyBow} is a special kind of bow which slows down any
 * {@link LivingEntity} it hits.
 *
 * @author TheBusyBiscuit
 * @author martinbrom
 *
 */
public class IcyBow extends SlimefunBow {

    @ParametersAreNonnullByDefault
    public IcyBow(ItemGroup category, SlimefunItemStack item, ItemStack[] recipe) {
        super(category, item, recipe);
    }

    @Nonnull
    @Override
    public BowShootHandler onShoot() {
        return (e, n) -> {
            if (n instanceof Player) {
                Player p = (Player) n;

                // Fixes #3060 - Don't apply effects if the arrow was successfully blocked.
                if (p.isBlocking() && e.getFinalDamage() <= 0) {
                    return;
                }

                if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_17)) {
                    p.setFreezeTicks(60);
                }
            }
            n.getWorld().playEffect(n.getLocation(), Effect.STEP_SOUND, Material.ICE);
            n.getWorld().playEffect(n.getEyeLocation(), Effect.STEP_SOUND, Material.ICE);
            n.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 2, 10));
            n.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 2, -10));
        };
    }

}
