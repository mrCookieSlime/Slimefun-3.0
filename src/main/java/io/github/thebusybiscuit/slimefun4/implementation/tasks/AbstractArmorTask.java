package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.thebusybiscuit.slimefun4.api.items.HashedArmorpiece;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.SlimefunArmorPiece;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This is a base class for any ArmorTask, it checks every online player
 * and handles any armor functionality.
 *
 * @author TheBusyBiscuit
 * @author martinbrom
 *
 * @see ArmorTask
 * @see RainbowArmorTask
 */
public abstract class AbstractArmorTask implements Runnable {

    @Override
    public final void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.isValid() || p.isDead()) {
                continue;
            }

            PlayerProfile.get(p, profile -> {
                ItemStack[] armor = p.getInventory().getArmorContents();

                updateAndHandleArmor(p, armor, profile.getArmor());
                handlePlayer(p, profile);
            });
        }

        handleTick();
    }

    /**
     * Schedules this {@link AbstractArmorTask} to run every {@code tickInterval} ticks
     *
     * @param plugin The {@link SlimefunPlugin}
     * @param tickInterval Delay between two "runs" of this task in ticks
     */
    public final void schedule(SlimefunPlugin plugin, long tickInterval) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, tickInterval);
    }

    /**
     * Method to handle behavior for pieces of armor.
     * It is called per-player and per piece of armor.
     *
     * @param p The {@link Player} wearing the piece of armor
     * @param sfArmorPiece {@link SlimefunArmorPiece} Slimefun instance of the piece of armor
     * @param armorPiece The actual {@link ItemStack} of the armor piece
     */
    @ParametersAreNonnullByDefault
    protected abstract void handleArmorPiece(Player p, SlimefunArmorPiece sfArmorPiece, ItemStack armorPiece);

    /**
     * Method to handle behavior for player's armor as a whole.
     * It is called once per player.
     *
     * @param p The {@link Player} wearing the armor
     * @param profile The {@link Player}'s {@link PlayerProfile}
     */
    @ParametersAreNonnullByDefault
    protected abstract void handlePlayer(Player p, PlayerProfile profile);

    /**
     * Method to handle things related to the task itself.
     * Called once per tick (per schedule interval).
     */
    protected abstract void handleTick();

    @ParametersAreNonnullByDefault
    private void updateAndHandleArmor(Player p, ItemStack[] armor, HashedArmorpiece[] cachedArmor) {
        for (int slot = 0; slot < 4; slot++) {
            ItemStack item = armor[slot];
            HashedArmorpiece armorPiece = cachedArmor[slot];

            if (armorPiece.hasDiverged(item)) {
                SlimefunItem sfItem = SlimefunItem.getByItem(item);

                if (!(sfItem instanceof SlimefunArmorPiece)) {
                    // If it isn't actually Armor, then we won't care about it.
                    sfItem = null;
                }

                armorPiece.update(item, sfItem);
            }

            if (item != null && armorPiece.getItem().isPresent()) {
                SlimefunPlugin.runSync(() -> {
                    SlimefunArmorPiece sfArmorPiece = armorPiece.getItem().get();

                    if (sfArmorPiece.canUse(p, true)) {
                        handleArmorPiece(p, sfArmorPiece, item);
                    }
                });
            }
        }
    }

}
