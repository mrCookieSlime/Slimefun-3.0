package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import io.github.thebusybiscuit.cscorelib2.chat.ChatColors;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.attributes.ProtectionType;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.RadioactiveItem;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * The {@link RadioactivityTask} handles radioactivity for
 * {@link Radioactive} items.
 *
 * @author Semisol
 *
 */
public class RadioactivityTask implements Runnable {
    private static final Symptom[] SYMPTOMS = Symptom.values();
    private static final HashMap<UUID, Integer> radioactivityLevel = new HashMap<>();
    private final int duration = SlimefunPlugin.getCfg().getOrSetDefault("options.radiation-update-interval", 1) * 20 + 20;
    //@formatter:off
    private final PotionEffect WITHER = new PotionEffect(PotionEffectType.WITHER,
            duration,
            1
    );
    
    private final PotionEffect WITHER2 = new PotionEffect(PotionEffectType.WITHER,
            duration,
            4
    );
    
    private final PotionEffect BLINDNESS = new PotionEffect(PotionEffectType.BLINDNESS,
            duration,
            0
    );
    
    private final PotionEffect SLOW = new PotionEffect(PotionEffectType.SLOW,
            duration,
            3
    );
    //@formatter:on

    public static void removePlayer(@Nonnull Player p){
        radioactivityLevel.remove(p.getUniqueId());
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isValid() || p.isDead()) {
                continue;
            }

            PlayerProfile.get(p, profile -> handleRadiation(p, profile));
        }
    }

    private void handleRadiation(@Nonnull Player p, @Nonnull PlayerProfile profile) {
        if (p.getGameMode() == GameMode.CREATIVE ||
                p.getGameMode() == GameMode.SPECTATOR) return;
        Set<SlimefunItem> radioactiveItems = SlimefunPlugin.getRegistry().getRadioactiveItems();

        int exposureTotal = 0;
        UUID uuid = p.getUniqueId();

        if (!profile.hasFullProtectionAgainst(ProtectionType.RADIATION)) {
            for (ItemStack item : p.getInventory()) {
                if (item == null ||
                        item.getType() == Material.AIR) continue;
                ItemStack tmpItem = item;

                if (!(item instanceof SlimefunItemStack) && radioactiveItems.size() > 1) {
                    // Performance optimization to reduce ItemMeta calls
                    tmpItem = ItemStackWrapper.wrap(tmpItem);
                }
                
                for (SlimefunItem i : radioactiveItems) {
                    if (i.isItem(tmpItem)) {
                        exposureTotal += tmpItem.getAmount() * ((RadioactiveItem) i).getRadioactivity().getExposureModifier();
                    }
                }
            }
        }
        
        int exposureLevelBefore = radioactivityLevel.getOrDefault(uuid, 0);
        if (exposureTotal > 0) {
            if (exposureLevelBefore == 0) {
                SlimefunPlugin.getLocalization().sendMessage(p, "messages.radiation");
            }
            radioactivityLevel.put(uuid, Math.min(exposureLevelBefore + exposureTotal, 100));
        } else if (exposureLevelBefore > 0) {
            radioactivityLevel.put(uuid, Math.max(exposureLevelBefore - 1, 0));
        }
        
        int exposureLevelAfter = radioactivityLevel.getOrDefault(uuid, 0);
        for (Symptom symptom : SYMPTOMS) {
            if (symptom.minExposure <= exposureLevelAfter) {
                applySymptom(symptom, p);
            }
        }
        
        if (exposureLevelAfter > 0 || exposureLevelBefore > 0) {
            String msg = SlimefunPlugin.getLocalization().getMessage(p, "actionbar.radiation")
                    .replace("%level%", "" + exposureLevelAfter);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new ComponentBuilder().append(ChatColors.color(msg)).create()
            );
        }
    }

    /**
     * Applies a symptom to the player.
     *
     * @param s Symptom to apply
     * @param p Player to apply to
     */
    private void applySymptom(@Nonnull Symptom s, @Nonnull Player p) {
        SlimefunPlugin.runSync(() -> {
            switch (s) {
                case SLOW: {
                    p.addPotionEffect(SLOW);
                    break;
                }
                case SLOW_DAMAGE: {
                    p.addPotionEffect(WITHER);
                    break;
                }
                case BLINDNESS: {
                    p.addPotionEffect(BLINDNESS);
                    break;
                }
                case FAST_DAMAGE: {
                    p.addPotionEffect(WITHER2);
                    break;
                }
                case IMMINENT_DEATH: {
                    p.setHealth(0);
                    break;
                }
            }
        });
    }

    private enum Symptom {
        /**
         * An enum of potential radiation symptoms.
         */
        SLOW(10),
        SLOW_DAMAGE(25),
        BLINDNESS(50),
        FAST_DAMAGE(75),
        IMMINENT_DEATH(100);

        private final int minExposure;

        Symptom(int minExposure) {
            this.minExposure = minExposure;
        }
    }
}
