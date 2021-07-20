package io.github.thebusybiscuit.slimefun4.core.services.profiler.inspectors;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.thebusybiscuit.slimefun4.core.services.profiler.PerformanceInspector;

import net.md_5.bungee.api.chat.TextComponent;

/**
 * This implementation of {@link PerformanceInspector} refers to a {@link Player}.
 * It also supports {@link TextComponent TextComponents} for rich text messages.
 * 
 * @author TheBusyBiscuit
 *
 */
public class PlayerPerformanceInspector implements PerformanceInspector {

    /**
     * Our reference to the {@link UUID} of the {@link Player}.
     */
    private final UUID uuid;

    /**
     * This creates a new {@link PlayerPerformanceInspector} for the given {@link Player}.
     * 
     * @param player
     *            The {@link Player}
     */
    public PlayerPerformanceInspector(@Nonnull Player player) {
        Validate.notNull(player, "Player cannot be null");

        this.uuid = player.getUniqueId();
    }

    @Nullable
    private Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public boolean isValid() {
        Player player = getPlayer();
        return player != null && player.isOnline();
    }

    @Override
    public boolean isVerbose() {
        return false;
    }

    @Override
    public void sendMessage(@Nonnull String msg) {
        Player player = getPlayer();

        if (player != null) {
            player.sendMessage(msg);
        }
    }

    public void sendMessage(@Nonnull TextComponent component) {
        Player player = getPlayer();

        if (player != null) {
            player.spigot().sendMessage(component);
        }
    }

}
