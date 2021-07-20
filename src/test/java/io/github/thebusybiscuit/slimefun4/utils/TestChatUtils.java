package io.github.thebusybiscuit.slimefun4.utils;

import org.bukkit.ChatColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.common.ChatColors;

class TestChatUtils {

    @Test
    @DisplayName("Test ChatUtils.humanize(...)")
    void testHumanize() {
        String input = "TEST_STRING_COOL";
        String expected = "Test String Cool";
        Assertions.assertEquals(expected, ChatUtils.humanize(input));
    }

    @Test
    @DisplayName("Test ChatUtils.christmas(...)")
    void testChristmas() {
        String input = "Tis the season";
        String expected = ChatColors.color("&aT&ci&as&c &at&ch&ae&c &as&ce&aa&cs&ao&cn");
        Assertions.assertEquals(expected, ChatUtils.christmas(input));
    }

    @Test
    @DisplayName("Test ChatUtils.removeColorCodes(...)")
    void testColorCodeRemoval() {
        String expected = "Hello world";
        Assertions.assertEquals(expected, ChatUtils.removeColorCodes("&4&lHello &6world"));
        Assertions.assertEquals(expected, ChatUtils.removeColorCodes(ChatColor.GREEN + "Hello " + ChatColor.RED + "world"));
    }

}
