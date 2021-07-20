package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.RainbowArmorPiece;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.SlimefunArmorPiece;

/**
 * The {@link RainbowArmorTask} is responsible for handling the change in color of any Rainbow Armor piece.
 *
 * @author martinbrom
 */
public class RainbowArmorTask extends AbstractArmorTask {

    private long currentColorIndex = 0;

    @Override
    @ParametersAreNonnullByDefault
    protected void handleArmorPiece(Player p, SlimefunArmorPiece sfArmorPiece, ItemStack armorPiece) {
        if (sfArmorPiece instanceof RainbowArmorPiece && armorPiece.hasItemMeta()) {
            RainbowArmorPiece rainbowArmorPiece = (RainbowArmorPiece) sfArmorPiece;
            Color[] colors = rainbowArmorPiece.getColors();
            Color newColor = colors[(int) (currentColorIndex % colors.length)];

            LeatherArmorMeta meta = (LeatherArmorMeta) armorPiece.getItemMeta();
            meta.setColor(newColor);
            armorPiece.setItemMeta(meta);
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void handlePlayer(Player p, PlayerProfile profile) {
    }

    @Override
    protected void handleTick() {
        currentColorIndex++;
    }

}
