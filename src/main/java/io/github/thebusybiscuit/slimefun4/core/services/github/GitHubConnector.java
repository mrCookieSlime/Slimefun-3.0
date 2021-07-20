package io.github.thebusybiscuit.slimefun4.core.services.github;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONException;

/**
 * The {@link GitHubConnector} is used to connect to the GitHub API service.
 * It can be extended by subclasses, this just serves as an abstract super class for
 * other connectors.
 * 
 * @author TheBusyBiscuit
 * @author Walshy
 */
abstract class GitHubConnector {

    private static final String API_URL = "https://api.github.com/";
    private static final String USER_AGENT = "Slimefun4 (https://github.com/Slimefun)";

    protected final GitHubService github;
    private final String url;
    private File file;

    /**
     * This creates a new {@link GitHubConnector} for the given repository.
     * 
     * @param github
     *            Our instance of {@link GitHubService}
     * @param repository
     *            The repository we want to connect to
     */
    GitHubConnector(@Nonnull GitHubService github, @Nonnull String repository) {
        this.github = github;
        this.url = API_URL + "repos/" + repository + getEndpoint();
    }

    /**
     * This returns the name of our cache {@link File}.
     * 
     * @return The cache {@link File} name
     */
    @Nonnull
    public abstract String getFileName();

    /**
     * This is our {@link URL} endpoint.
     * It is the suffix of the {@link URL} we want to connect to.
     * 
     * @return Our endpoint
     */
    @Nonnull
    public abstract String getEndpoint();

    /**
     * This {@link Map} contains the query parameters for our {@link URL}.
     * 
     * @return A {@link Map} with our query parameters
     */
    @Nonnull
    public abstract Map<String, Object> getParameters();

    /**
     * This method is called when the connection finished successfully.
     * 
     * @param response
     *            The response
     */
    public abstract void onSuccess(@Nonnull JsonNode response);

    /**
     * This method is called when the connection has failed.
     */
    public void onFailure() {
        // Don't do anything by default
    }

    /**
     * This method will connect to GitHub and store the received data inside a local
     * cache {@link File}.
     * Make sure to call this method asynchronously!
     */
    void download() {
        file = new File("plugins/Slimefun/cache/github/" + getFileName() + ".json");

        if (github.isLoggingEnabled()) {
            Slimefun.logger().log(Level.INFO, "Retrieving {0}.json from GitHub...", getFileName());
        }

        try {
            // @formatter:off
            HttpResponse<JsonNode> response = Unirest.get(url)
                    .queryString(getParameters())
                    .header("User-Agent", USER_AGENT)
                    .asJson();
            // @formatter:on

            if (response.isSuccess()) {
                onSuccess(response.getBody());
                writeCacheFile(response.getBody());
            } else {
                if (github.isLoggingEnabled()) {
                    Slimefun.logger().log(Level.WARNING, "Failed to fetch {0}: {1} - {2}", new Object[] { url, response.getStatus(), response.getBody() });
                }

                // It has the cached file, let's just read that then
                if (file.exists()) {
                    JsonNode cache = readCacheFile();

                    if (cache != null) {
                        onSuccess(cache);
                    }
                }
            }
        } catch (UnirestException e) {
            if (github.isLoggingEnabled()) {
                Slimefun.logger().log(Level.WARNING, "Could not connect to GitHub in time.", e);
            }

            // It has the cached file, let's just read that then
            if (file.exists()) {
                JsonNode cache = readCacheFile();

                if (cache != null) {
                    onSuccess(cache);
                    return;
                }
            }

            // If the request failed and it failed to read the cache then call onFailure.
            onFailure();
        }
    }

    @Nullable
    private JsonNode readCacheFile() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            return new JsonNode(reader.readLine());
        } catch (IOException | JSONException e) {
            Slimefun.logger().log(Level.WARNING, "Failed to read Github cache file: {0} - {1}: {2}", new Object[] { file.getName(), e.getClass().getSimpleName(), e.getMessage() });
            return null;
        }
    }

    private void writeCacheFile(@Nonnull JsonNode node) {
        try (FileOutputStream output = new FileOutputStream(file)) {
            output.write(node.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Slimefun.logger().log(Level.WARNING, "Failed to populate GitHub cache: {0} - {1}", new Object[] { e.getClass().getSimpleName(), e.getMessage() });
        }
    }
}
