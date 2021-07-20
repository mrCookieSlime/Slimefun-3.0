package io.github.thebusybiscuit.slimefun4.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestLoreComparison {

    private static final String IGNORED_LINE = ChatColor.GRAY + "Soulbound";

    @BeforeAll
    public static void load() {
        MockBukkit.mock();
        MockBukkit.load(Slimefun.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test if two equal lores are considered equal")
    void testEqualLists() {
        List<String> a = Arrays.asList("a", "b", "c");
        List<String> b = Arrays.asList("a", "b", "c");

        Assertions.assertTrue(SlimefunUtils.equalsLore(a, b));
        Assertions.assertTrue(SlimefunUtils.equalsLore(b, a));
    }

    @Test
    @DisplayName("Test if two not equal lores are not considered equal")
    void testNotEqualLists() {
        List<String> a = Arrays.asList("a", "b", "c");
        List<String> b = Arrays.asList("1", "2", "3");

        Assertions.assertFalse(SlimefunUtils.equalsLore(a, b));
        Assertions.assertFalse(SlimefunUtils.equalsLore(b, a));
    }

    @Test
    @DisplayName("Test if lores with different length are not considered equal")
    void testDifferentLengthLists() {
        List<String> a = Arrays.asList("a", "b", "c");
        List<String> b = Arrays.asList("a", "b", "c", "d", "e", "f", "g");

        Assertions.assertFalse(SlimefunUtils.equalsLore(a, b));
        Assertions.assertFalse(SlimefunUtils.equalsLore(b, a));
    }

    @Test
    @DisplayName("Test if lores with one ignored element are considered equal")
    void testIgnoredElementLists() {
        List<String> a = Arrays.asList("a", "b", IGNORED_LINE, "c");
        List<String> b = Arrays.asList("a", "b", "c");

        Assertions.assertTrue(SlimefunUtils.equalsLore(a, b));
        Assertions.assertTrue(SlimefunUtils.equalsLore(b, a));
    }

    @Test
    @DisplayName("Test if lores with ignored elements are considered equal")
    void testIgnoredElementsLists() {
        List<String> a = Arrays.asList("a", "b", IGNORED_LINE, "c");
        List<String> b = Arrays.asList("a", IGNORED_LINE, "b", "c");

        Assertions.assertTrue(SlimefunUtils.equalsLore(a, b));
        Assertions.assertTrue(SlimefunUtils.equalsLore(b, a));
    }

    @Test
    @DisplayName("Test if lores with ignored element at start are considered equal")
    void testIgnoredElementStartLists() {
        List<String> a = Arrays.asList(IGNORED_LINE, "a", "b", "c");
        List<String> b = Arrays.asList(IGNORED_LINE, "a", "b", "c");

        Assertions.assertTrue(SlimefunUtils.equalsLore(a, b));
        Assertions.assertTrue(SlimefunUtils.equalsLore(b, a));
    }

    @Test
    @DisplayName("Test if lores with ignored element at end are considered equal")
    void testIgnoredElementEndLists() {
        List<String> a = Arrays.asList("a", "b", "c", IGNORED_LINE);
        List<String> b = Arrays.asList("a", "b", "c", IGNORED_LINE);

        Assertions.assertTrue(SlimefunUtils.equalsLore(a, b));
        Assertions.assertTrue(SlimefunUtils.equalsLore(b, a));
    }

    @Test
    @DisplayName("Test if different lores with ignored element are not considered equal")
    void testIgnoredElementsNotEqualLists() {
        List<String> a = Arrays.asList("a", "b", "c", "d");
        List<String> b = Arrays.asList(IGNORED_LINE, "a", "b", "c", IGNORED_LINE);

        Assertions.assertFalse(SlimefunUtils.equalsLore(a, b));
        Assertions.assertFalse(SlimefunUtils.equalsLore(b, a));
    }

}
