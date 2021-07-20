package io.github.thebusybiscuit.slimefun4.utils;

import java.util.Locale;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.bakedlibs.dough.chat.ChatInput;
import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This utility class contains a few static methods that are all about {@link String} manipulation
 * or sending a {@link String} to a {@link Player}.
 * 
 * @author TheBusyBiscuit
 *
 */
public final class ChatUtils {

    private ChatUtils() {}

    public static void sendURL(@Nonnull CommandSender sender, @Nonnull String url) {
        // If we get access to the URL prompt one day, we can just prompt the link to the Player that way.
        sender.sendMessage("");
        Slimefun.getLocalization().sendMessage(sender, "messages.link-prompt", false);
        sender.sendMessage(ChatColors.color("&7&o" + url));
        sender.sendMessage("");
    }

    public static @Nonnull String removeColorCodes(@Nonnull String string) {
        return ChatColor.stripColor(ChatColors.color(string));
    }

    public static @Nonnull String crop(@Nonnull ChatColor color, @Nonnull String string) {
        if (ChatColor.stripColor(color + string).length() > 19) {
            return (color + ChatColor.stripColor(string)).substring(0, 18) + "...";
        } else {
            return color + ChatColor.stripColor(string);
        }
    }

    public static @Nonnull String christmas(@Nonnull String text) {
        return ChatColors.alternating(text, ChatColor.GREEN, ChatColor.RED);
    }

    public static void awaitInput(@Nonnull Player p, @Nonnull Consumer<String> callback) {
        ChatInput.waitForPlayer(Slimefun.instance(), p, callback);
    }

    /**
     * This converts a given {@link String} to a human-friendly version.
     * This can be used to convert enum constants to easier to read words with
     * spaces and upper case word starts.
     * 
     * For example:
     * {@code ENUM_CONSTANT: Enum Constant}
     * 
     * @param string
     *            The {@link String} to convert
     * 
     * @return A human-friendly version of the given {@link String}
     */
    public static @Nonnull String humanize(@Nonnull String string) {
        StringBuilder builder = new StringBuilder();
        String[] segments = CommonPatterns.UNDERSCORE.split(string.toLowerCase(Locale.ROOT));

        builder.append(Character.toUpperCase(segments[0].charAt(0))).append(segments[0].substring(1));

        for (int i = 1; i < segments.length; i++) {
            String segment = segments[i];
            builder.append(' ').append(Character.toUpperCase(segment.charAt(0))).append(segment.substring(1));
        }

        return builder.toString();
    }

}
