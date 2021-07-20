package io.github.thebusybiscuit.slimefun4.core.services.github;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import io.github.bakedlibs.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;

/**
 * This Service is responsible for grabbing every {@link Contributor} to this project
 * from GitHub and holding data associated to the project repository, such
 * as open issues or pending pull requests.
 * 
 * @author TheBusyBiscuit
 *
 */
public class GitHubService {

    private final String repository;
    private final Set<GitHubConnector> connectors;
    private final ConcurrentMap<String, Contributor> contributors;

    private final Config uuidCache = new Config("plugins/Slimefun/cache/github/uuids.yml");
    private final Config texturesCache = new Config("plugins/Slimefun/cache/github/skins.yml");

    private boolean logging = false;

    private LocalDateTime lastUpdate = LocalDateTime.now();

    private int openIssues = 0;
    private int pendingPullRequests = 0;
    private int publicForks = 0;
    private int stargazers = 0;

    /**
     * This creates a new {@link GitHubService} for the given repository.
     * 
     * @param repository
     *            The repository to create this {@link GitHubService} for
     */
    public GitHubService(@Nonnull String repository) {
        this.repository = repository;

        connectors = new HashSet<>();
        contributors = new ConcurrentHashMap<>();
        loadConnectors(false);
    }

    /**
     * This will start the {@link GitHubService} and run the asynchronous {@link GitHubTask}
     * every so often to update its data.
     * 
     * @param plugin
     *            Our instance of {@link Slimefun}
     */
    public void start(@Nonnull Slimefun plugin) {
        long period = TimeUnit.HOURS.toMillis(1);
        GitHubTask task = new GitHubTask(this);

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, 80L, period);
    }

    /**
     * This method adds a few default {@link Contributor Contributors}.
     * Think of them like honorable mentions that aren't listed through
     * the usual methods.
     */
    private void addDefaultContributors() {
        // Artists
        addContributor("Fuffles_", "&dArtist");
        addContributor("IMS_Art", "https://github.com/IAmSorryArt", "&dArtist", 0);

        // Addon Jam winners
        addContributor("nahkd123", "&aWinner of the 2020 Addon Jam");

        // Translators
        try {
            TranslatorsReader translators = new TranslatorsReader(this);
            translators.load();
        } catch (Exception x) {
            Slimefun.logger().log(Level.SEVERE, "Failed to read 'translators.json'", x);
        }
    }

    private void addContributor(@Nonnull String name, @Nonnull String role) {
        Contributor contributor = new Contributor(name);
        contributor.setContributions(role, 0);
        contributor.setUniqueId(uuidCache.getUUID(name));
        contributors.put(name, contributor);
    }

    @Nonnull
    public Contributor addContributor(@Nonnull String minecraftName, @Nonnull String profileURL, @Nonnull String role, int commits) {
        Validate.notNull(minecraftName, "Minecraft username must not be null.");
        Validate.notNull(profileURL, "GitHub profile url must not be null.");
        Validate.notNull(role, "Role should not be null.");
        Validate.isTrue(commits >= 0, "Commit count cannot be negative.");

        String username = profileURL.substring(profileURL.lastIndexOf('/') + 1);

        Contributor contributor = contributors.computeIfAbsent(username, key -> new Contributor(minecraftName, profileURL));
        contributor.setContributions(role, commits);
        contributor.setUniqueId(uuidCache.getUUID(minecraftName));
        return contributor;
    }

    @Nonnull
    public Contributor addContributor(@Nonnull String username, @Nonnull String role, int commits) {
        Validate.notNull(username, "Username must not be null.");
        Validate.notNull(role, "Role should not be null.");
        Validate.isTrue(commits >= 0, "Commit count cannot be negative.");

        Contributor contributor = contributors.computeIfAbsent(username, key -> new Contributor(username));
        contributor.setContributions(role, commits);
        return contributor;
    }

    private void loadConnectors(boolean logging) {
        this.logging = logging;
        addDefaultContributors();

        // TheBusyBiscuit/Slimefun4 (multiple times because there may me multiple pages)
        connectors.add(new ContributionsConnector(this, "code", 1, repository, ContributorRole.DEVELOPER));
        connectors.add(new ContributionsConnector(this, "code2", 2, repository, ContributorRole.DEVELOPER));
        connectors.add(new ContributionsConnector(this, "code3", 3, repository, ContributorRole.DEVELOPER));

        // TheBusyBiscuit/Slimefun4-Wiki
        connectors.add(new ContributionsConnector(this, "wiki", 1, "Slimefun/Wiki", ContributorRole.WIKI_EDITOR));

        // TheBusyBiscuit/Slimefun4-Resourcepack
        connectors.add(new ContributionsConnector(this, "resourcepack", 1, "Slimefun/Resourcepack", ContributorRole.RESOURCEPACK_ARTIST));

        // Issues and Pull Requests
        connectors.add(new GitHubIssuesConnector(this, repository, (issues, pullRequests) -> {
            this.openIssues = issues;
            this.pendingPullRequests = pullRequests;
        }));

        // Forks, star count and last commit date
        connectors.add(new GitHubActivityConnector(this, repository, (forks, stars, date) -> {
            this.publicForks = forks;
            this.stargazers = stars;
            this.lastUpdate = date;
        }));
    }

    @Nonnull
    protected Set<GitHubConnector> getConnectors() {
        return connectors;
    }

    protected boolean isLoggingEnabled() {
        return logging;
    }

    /**
     * This returns the {@link Contributor Contributors} to this project.
     * 
     * @return A {@link ConcurrentMap} containing all {@link Contributor Contributors}
     */
    @Nonnull
    public ConcurrentMap<String, Contributor> getContributors() {
        return contributors;
    }

    /**
     * This returns the amount of forks of our repository
     * 
     * @return The amount of forks
     */
    public int getForks() {
        return publicForks;
    }

    /**
     * This method returns the amount of stargazers of the repository.
     * 
     * @return The amount of people who starred the repository
     */
    public int getStars() {
        return stargazers;
    }

    /**
     * This returns the amount of open Issues on our repository.
     * 
     * @return The amount of open issues
     */
    public int getOpenIssues() {
        return openIssues;
    }

    /**
     * Returns the id of Slimefun's GitHub Repository. (e.g. "TheBusyBiscuit/Slimefun4").
     * 
     * @return The id of our GitHub Repository
     */
    @Nonnull
    public String getRepository() {
        return repository;
    }

    /**
     * This method returns the amount of pending pull requests.
     * 
     * @return The amount of pending pull requests
     */
    public int getPendingPullRequests() {
        return pendingPullRequests;
    }

    /**
     * This returns the date and time of the last commit to this repository.
     * 
     * @return A {@link LocalDateTime} object representing the date and time of the latest commit
     */
    @Nonnull
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * This will store the {@link UUID} and texture of all {@link Contributor Contributors}
     * in memory in a {@link File} to save requests the next time we iterate over them.
     */
    protected void saveCache() {
        for (Contributor contributor : contributors.values()) {
            Optional<UUID> uuid = contributor.getUniqueId();
            uuid.ifPresent(value -> uuidCache.setValue(contributor.getName(), value));

            if (contributor.hasTexture()) {
                String texture = contributor.getTexture(this);

                if (!texture.equals(HeadTexture.UNKNOWN.getTexture())) {
                    texturesCache.setValue(contributor.getName(), texture);
                }
            }
        }

        uuidCache.save();
        texturesCache.save();
    }

    /**
     * This returns the cached skin texture for a given username.
     * 
     * @param username
     *            The minecraft username
     * 
     * @return The cached skin texture for that user (or null)
     */
    @Nullable
    protected String getCachedTexture(@Nonnull String username) {
        return texturesCache.getString(username);
    }
}
