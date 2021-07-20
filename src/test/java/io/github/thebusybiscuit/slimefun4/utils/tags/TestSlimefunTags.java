package io.github.thebusybiscuit.slimefun4.utils.tags;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.thebusybiscuit.slimefun4.api.exceptions.TagMisconfigurationException;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import be.seeseemelk.mockbukkit.MockBukkit;

class TestSlimefunTags {

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
    @DisplayName("Test for Exceptions with Slimefun Tags")
    void testTags() {
        for (SlimefunTag tag : SlimefunTag.values()) {
            Assertions.assertDoesNotThrow(tag::reload);
        }
    }

    @Test
    @DisplayName("Test for infinite loops with Slimefun Tags")
    void testForInfiniteLoops() throws TagMisconfigurationException {
        SlimefunTag.reloadAll();

        for (SlimefunTag tag : SlimefunTag.values()) {
            assertNotCyclic(tag);
        }
    }

    @Test
    @DisplayName("Test SlimefunTag#isTagged()")
    void testIsTagged() throws TagMisconfigurationException {
        SlimefunTag.reloadAll();

        // Direct inclusion
        Assertions.assertTrue(SlimefunTag.SENSITIVE_MATERIALS.isTagged(Material.CAKE));

        // Inclusion through a Minecraft Tag
        Assertions.assertTrue(SlimefunTag.SENSITIVE_MATERIALS.isTagged(Material.OAK_SAPLING));

        // Inclusion through a Slimefun Tag
        Assertions.assertTrue(SlimefunTag.SENSITIVE_MATERIALS.isTagged(Material.TORCH));
        Assertions.assertTrue(SlimefunTag.SENSITIVE_MATERIALS.isTagged(Material.OAK_PRESSURE_PLATE));
    }

    @Test
    @DisplayName("Test SlimefunTag#toArray()")
    void testToArray() throws TagMisconfigurationException {
        SlimefunTag.reloadAll();

        for (SlimefunTag tag : SlimefunTag.values()) {
            Set<Material> values = tag.getValues();
            Assertions.assertArrayEquals(values.toArray(new Material[0]), tag.toArray());
        }
    }

    @Test
    @DisplayName("Test SlimefunTag#getValues()")
    void testGetValues() throws TagMisconfigurationException {
        SlimefunTag.reloadAll();

        for (SlimefunTag tag : SlimefunTag.values()) {
            Set<Material> values = tag.getValues();

            Assertions.assertFalse(values.isEmpty());

            for (Material value : tag.getValues()) {
                // All values of our tag must be tagged
                Assertions.assertTrue(tag.isTagged(value));
            }

            for (Tag<Material> sub : tag.getSubTags()) {
                for (Material value : sub.getValues()) {
                    // All values of sub tags should be tagged by our tag too
                    Assertions.assertTrue(tag.isTagged(value));
                }
            }
        }
    }

    @Test
    @DisplayName("Test SlimefunTag#stream()")
    void testStream() throws TagMisconfigurationException {
        SlimefunTag.reloadAll();

        for (SlimefunTag tag : SlimefunTag.values()) {
            Set<Material> values = tag.getValues();
            Stream<Material> stream = tag.stream();

            Assertions.assertEquals(values, stream.collect(Collectors.toSet()));
        }
    }

    @Test
    @DisplayName("Test static SlimefunTag accessors")
    void testGetTag() {
        Assertions.assertEquals(SlimefunTag.GLASS_BLOCKS, SlimefunTag.getTag("GLASS_BLOCKS"));
        Assertions.assertEquals(SlimefunTag.ORES, SlimefunTag.getTag("ORES"));
        Assertions.assertEquals(SlimefunTag.SHULKER_BOXES, SlimefunTag.getTag("SHULKER_BOXES"));
        Assertions.assertNull(SlimefunTag.getTag("hello"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> SlimefunTag.getTag(null));
    }

    private void assertNotCyclic(@Nonnull SlimefunTag tag) {
        Set<SlimefunTag> visiting = new HashSet<>();
        Set<SlimefunTag> visited = new HashSet<>();

        if (isCyclic(visiting, visited, tag)) {
            System.out.println("Currently visiting: " + visiting);
            System.out.println("Previously visited" + visiting);
            Assertions.fail("Tag '" + tag.getKey() + "' is cyclic!");
        }
    }

    @ParametersAreNonnullByDefault
    private boolean isCyclic(Set<SlimefunTag> visiting, Set<SlimefunTag> visited, SlimefunTag tag) {
        visiting.add(tag);

        for (Tag<Material> sub : tag.getSubTags()) {
            if (sub instanceof SlimefunTag) {
                if (visiting.contains(sub)) {
                    return true;
                } else if (!visited.contains(sub) && isCyclic(visiting, visited, (SlimefunTag) sub)) {
                    return true;
                }
            }
        }

        visiting.remove(tag);
        visited.add(tag);
        return false;
    }

}
