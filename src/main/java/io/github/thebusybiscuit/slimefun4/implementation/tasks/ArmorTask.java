package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.slimefun4.api.items.HashedArmorpiece;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectionType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.SlimefunArmorPiece;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.SolarHelmet;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

/**
 * The {@link ArmorTask} is responsible for handling {@link PotionEffect PotionEffects} for
 * {@link Radioactive} items or any {@link SlimefunArmorPiece}.
 * It also handles the prevention of radiation through a Hazmat Suit
 * 
 * @author TheBusyBiscuit
 *
 */
public class ArmorTask implements Runnable {

    private final Set<PotionEffect> radiationEffects;
    private final boolean radioactiveFire;

    /**
     * This creates a new {@link ArmorTask}.
     * 
     * @param radioactiveFire
     *            Whether radiation also causes a {@link Player} to burn
     */
    public ArmorTask(boolean radioactiveFire) {
        this.radioactiveFire = radioactiveFire;

        Set<PotionEffect> effects = new HashSet<>();
        effects.add(new PotionEffect(PotionEffectType.WITHER, 400, 2));
        effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 400, 3));
        effects.add(new PotionEffect(PotionEffectType.CONFUSION, 400, 3));
        effects.add(new PotionEffect(PotionEffectType.WEAKNESS, 400, 2));
        effects.add(new PotionEffect(PotionEffectType.SLOW, 400, 1));
        effects.add(new PotionEffect(PotionEffectType.SLOW_DIGGING, 400, 1));
        radiationEffects = Collections.unmodifiableSet(effects);
    }

    /**
     * This returns a {@link Set} of {@link PotionEffect PotionEffects} which get applied to
     * a {@link Player} when they are exposed to deadly radiation.
     * 
     * @return The {@link Set} of {@link PotionEffect PotionEffects} applied upon radioactive contact
     */
    @Nonnull
    public Set<PotionEffect> getRadiationEffects() {
        return radiationEffects;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isValid() || p.isDead()) {
                continue;
            }

            PlayerProfile.get(p, profile -> {
                ItemStack[] armor = p.getInventory().getArmorContents();
                HashedArmorpiece[] cachedArmor = profile.getArmor();

                handleSlimefunArmor(p, armor, cachedArmor);

                if (hasSunlight(p)) {
                    checkForSolarHelmet(p);
                }

                checkForRadiation(p, profile);
            });
        }
    }

    @ParametersAreNonnullByDefault
    private void handleSlimefunArmor(Player p, ItemStack[] armor, HashedArmorpiece[] cachedArmor) {
        for (int slot = 0; slot < 4; slot++) {
            ItemStack item = armor[slot];
            HashedArmorpiece armorpiece = cachedArmor[slot];

            if (armorpiece.hasDiverged(item)) {
                SlimefunItem sfItem = SlimefunItem.getByItem(item);

                if (!(sfItem instanceof SlimefunArmorPiece)) {
                    // If it isn't actually Armor, then we won't care about it.
                    sfItem = null;
                }

                armorpiece.update(item, sfItem);
            }

            if (item != null && armorpiece.getItem().isPresent()) {
                Slimefun.runSync(() -> {
                    SlimefunArmorPiece slimefunArmor = armorpiece.getItem().get();

                    if (slimefunArmor.canUse(p, true)) {
                        for (PotionEffect effect : slimefunArmor.getPotionEffects()) {
                            p.removePotionEffect(effect.getType());
                            p.addPotionEffect(effect);
                        }
                    }
                });
            }
        }
    }

    private void checkForSolarHelmet(@Nonnull Player p) {
        ItemStack helmet = p.getInventory().getHelmet();

        if (Slimefun.getRegistry().isBackwardsCompatible() && !SlimefunUtils.isItemSimilar(helmet, SlimefunItems.SOLAR_HELMET, true, false)) {
            // Performance saver for slow backwards-compatible versions of Slimefun
            return;
        }

        SlimefunItem item = SlimefunItem.getByItem(helmet);

        if (item instanceof SolarHelmet && item.canUse(p, true)) {
            ((SolarHelmet) item).rechargeItems(p);
        }
    }

    private boolean hasSunlight(@Nonnull Player p) {
        World world = p.getWorld();

        if (world.getEnvironment() != Environment.NORMAL) {
            // The End and Nether have no sunlight
            return false;
        }

        return (world.getTime() < 12300 || world.getTime() > 23850) && p.getEyeLocation().getBlock().getLightFromSky() == 15;
    }

    private void checkForRadiation(@Nonnull Player p, @Nonnull PlayerProfile profile) {
        if (!profile.hasFullProtectionAgainst(ProtectionType.RADIATION)) {
            for (ItemStack item : p.getInventory()) {
                if (checkAndApplyRadiation(p, item)) {
                    break;
                }
            }
        }
    }

    private boolean checkAndApplyRadiation(@Nonnull Player p, @Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        Set<SlimefunItem> radioactiveItems = Slimefun.getRegistry().getRadioactiveItems();
        ItemStack itemStack = item;

        if (!(item instanceof SlimefunItemStack) && radioactiveItems.size() > 1) {
            // Performance optimization to reduce ItemMeta calls
            itemStack = ItemStackWrapper.wrap(item);
        }

        for (SlimefunItem radioactiveItem : radioactiveItems) {
            if (radioactiveItem.isItem(itemStack) && !radioactiveItem.isDisabledIn(p.getWorld())) {
                // If the item is enabled in the world, then make radioactivity do its job
                Slimefun.getLocalization().sendMessage(p, "messages.radiation");

                Slimefun.runSync(() -> {
                    p.addPotionEffects(radiationEffects);

                    // if radioactive fire is enabled, set them on fire
                    if (radioactiveFire) {
                        p.setFireTicks(400);
                    }
                });

                return true;
            }
        }

        return false;
    }
}
