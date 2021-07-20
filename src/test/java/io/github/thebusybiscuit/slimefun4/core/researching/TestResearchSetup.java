package io.github.thebusybiscuit.slimefun4.core.researching;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.setup.ResearchSetup;

import be.seeseemelk.mockbukkit.MockBukkit;

@TestMethodOrder(value = OrderAnnotation.class)
class TestResearchSetup {

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
    @DisplayName("Test if ResearchSetup throws an Exception")
    @Order(value = 1)
    void testForExceptions() {
        // Not really ideal but still important to test.
        // Research amount is variable, so we can't test for that.
        // We are really only concerned about any runtime exceptions here.
        Slimefun.getRegistry().setResearchingEnabled(true);
        Assertions.assertDoesNotThrow(() -> ResearchSetup.setupResearches());

        // Running it a second time should NOT be allowed.
        Assertions.assertThrows(UnsupportedOperationException.class, () -> ResearchSetup.setupResearches());
    }

    @Test
    @Order(value = 2)
    @DisplayName("Test if Researches have a translation")
    void testResearchTranslations() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/languages/en/researches.yml"), StandardCharsets.UTF_8))) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(reader);

            for (Research research : Slimefun.getRegistry().getResearches()) {
                String path = research.getKey().getNamespace() + '.' + research.getKey().getKey();
                Assertions.assertTrue(config.contains(path));
            }
        }
    }

}
