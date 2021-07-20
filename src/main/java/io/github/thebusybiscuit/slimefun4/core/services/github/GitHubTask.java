package io.github.thebusybiscuit.slimefun4.core.services.github;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;

import io.github.bakedlibs.dough.skins.PlayerSkin;
import io.github.bakedlibs.dough.skins.UUIDLookup;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This {@link GitHubTask} represents a {@link Runnable} that is run every X minutes.
 * It retrieves every {@link Contributor} of this project from GitHub.
 * 
 * @author TheBusyBiscuit
 * 
 * @see GitHubService
 * @see Contributor
 *
 */
class GitHubTask implements Runnable {

    private static final int MAX_REQUESTS_PER_MINUTE = 16;
    private final GitHubService gitHubService;

    GitHubTask(@Nonnull GitHubService github) {
        gitHubService = github;
    }

    @Override
    public void run() {

        if (Bukkit.isPrimaryThread()) {
            Slimefun.logger().log(Level.SEVERE, "The contributors task may never run on the main Thread!");
            return;
        }

        connectAndCache();
        grabTextures();
    }

    private void connectAndCache() {
        gitHubService.getConnectors().forEach(GitHubConnector::download);
    }

    /**
     * This method will pull the skin textures for every {@link Contributor} and store
     * the {@link UUID} and received skin inside a local cache {@link File}.
     */
    private void grabTextures() {
        /**
         * Store all queried usernames to prevent 429 responses for pinging
         * the same URL twice in one run.
         */
        Map<String, String> skins = new HashMap<>();
        int requests = 0;

        for (Contributor contributor : gitHubService.getContributors().values()) {
            int newRequests = requestTexture(contributor, skins);
            requests += newRequests;

            if (newRequests < 0 || requests >= MAX_REQUESTS_PER_MINUTE) {
                break;
            }
        }

        if (requests >= MAX_REQUESTS_PER_MINUTE && Slimefun.instance() != null && Slimefun.instance().isEnabled()) {
            // Slow down API requests and wait a minute after more than x requests were made
            Bukkit.getScheduler().runTaskLaterAsynchronously(Slimefun.instance(), this::grabTextures, 2 * 60 * 20L);
        }

        for (GitHubConnector connector : gitHubService.getConnectors()) {
            if (connector instanceof ContributionsConnector && !((ContributionsConnector) connector).hasFinished()) {
                return;
            }
        }

        /**
         * We only wanna save this if all Connectors finished already.
         * This will run multiple times but thats okay, this way we get as much
         * data as possible stored.
         */
        gitHubService.saveCache();
    }

    private int requestTexture(@Nonnull Contributor contributor, @Nonnull Map<String, String> skins) {
        if (!contributor.hasTexture()) {
            try {
                if (skins.containsKey(contributor.getMinecraftName())) {
                    contributor.setTexture(skins.get(contributor.getMinecraftName()));
                } else {
                    contributor.setTexture(pullTexture(contributor, skins));
                    return contributor.getUniqueId().isPresent() ? 1 : 2;
                }
            } catch (IllegalArgumentException x) {
                // There cannot be a texture found because it is not a valid MC username
                contributor.setTexture(null);
            } catch (Exception x) {
                // Too many requests
                Slimefun.logger().log(Level.WARNING, "Attempted to connect to mojang.com, got this response: {0}: {1}", new Object[] { x.getClass().getSimpleName(), x.getMessage() });
                Slimefun.logger().log(Level.WARNING, "This usually means mojang.com is temporarily down or started to rate-limit this connection, this is not an error message!");

                // Retry after 5 minutes if it was rate-limiting
                if (x.getMessage().contains("429")) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Slimefun.instance(), this::grabTextures, 5 * 60 * 20L);
                }

                return -1;
            }
        }

        return 0;
    }

    private @Nullable String pullTexture(@Nonnull Contributor contributor, @Nonnull Map<String, String> skins) throws InterruptedException, ExecutionException {
        Optional<UUID> uuid = contributor.getUniqueId();

        if (!uuid.isPresent()) {
            CompletableFuture<UUID> future = UUIDLookup.forUsername(Slimefun.instance(), contributor.getMinecraftName());
            uuid = Optional.ofNullable(future.get());
            uuid.ifPresent(contributor::setUniqueId);
        }

        if (uuid.isPresent()) {
            CompletableFuture<PlayerSkin> future = PlayerSkin.fromPlayerUUID(Slimefun.instance(), uuid.get());
            Optional<String> skin = Optional.of(future.get().toString());
            skins.put(contributor.getMinecraftName(), skin.orElse(""));
            return skin.orElse(null);
        } else {
            return null;
        }
    }

}
