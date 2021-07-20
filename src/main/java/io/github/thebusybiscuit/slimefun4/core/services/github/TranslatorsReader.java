package io.github.thebusybiscuit.slimefun4.core.services.github;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This class reads all translators of this project.
 * A translator is equivalent to the class {@link Contributor} as it also uses that internally.
 * 
 * @author TheBusyBiscuit
 * 
 * @see Contributor
 *
 */
final class TranslatorsReader {

    private final GitHubService github;

    // We maybe should switch to a json file in our resources folder at some point.
    TranslatorsReader(@Nonnull GitHubService github) {
        this.github = github;
    }

    public void load() {
        InputStream inputStream = Slimefun.class.getResourceAsStream("/languages/translators.json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(reader.lines().collect(Collectors.joining("")));
            JsonObject json = element.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                String languageCode = entry.getKey();

                if (entry.getValue().isJsonArray()) {
                    String role = ContributorRole.TRANSLATOR.getId() + ',' + languageCode;
                    JsonArray users = entry.getValue().getAsJsonArray();

                    for (JsonElement user : users) {
                        github.addContributor(user.getAsString(), role, 0);
                    }
                }
            }
        } catch (Exception e) {
            Slimefun.logger().log(Level.SEVERE, "Failed to load translators.json file", e);
        }
    }

}
