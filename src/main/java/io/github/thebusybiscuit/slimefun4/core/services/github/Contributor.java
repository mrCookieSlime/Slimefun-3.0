package io.github.thebusybiscuit.slimefun4.core.services.github;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

import io.github.bakedlibs.dough.data.TriStateOptional;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;

/**
 * Represents a {@link Contributor} who contributed to a GitHub repository.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 * 
 * @see GitHubService
 * 
 */
public class Contributor {

    private final String githubUsername;
    private final String minecraftUsername;
    private final String profileLink;

    private final ConcurrentMap<String, Integer> contributions = new ConcurrentHashMap<>();
    private final TriStateOptional<String> headTexture = TriStateOptional.createNew();

    private Optional<UUID> uuid = Optional.empty();

    /**
     * This creates a new {@link Contributor} with the given ingame name and GitHub profile.
     * 
     * @param minecraftName
     *            The ingame name in Minecraft for this {@link Contributor}
     * @param profile
     *            A link to their GitHub profile
     */
    public Contributor(@Nonnull String minecraftName, @Nonnull String profile) {
        Validate.notNull(minecraftName, "Username must never be null!");
        Validate.notNull(profile, "The profile cannot be null!");

        githubUsername = profile.substring(profile.lastIndexOf('/') + 1);
        minecraftUsername = minecraftName;
        profileLink = profile;
    }

    /**
     * This creates a new {@link Contributor} with the given username.
     * 
     * @param username
     *            The username of this {@link Contributor}
     */
    public Contributor(@Nonnull String username) {
        Validate.notNull(username, "Username must never be null!");

        githubUsername = username;
        minecraftUsername = username;
        profileLink = null;
    }

    /**
     * This sets the amount of contributions of this {@link Contributor} for the
     * specified role.
     * 
     * @param role
     *            The role of this {@link Contributor}
     * @param commits
     *            The amount of contributions made as that role
     */
    public void setContributions(@Nonnull String role, int commits) {
        Validate.notNull(role, "The role cannot be null!");
        Validate.isTrue(commits >= 0, "Contributions cannot be negative");

        contributions.put(role, commits);
    }

    /**
     * Returns the name of this {@link Contributor}.
     *
     * @return the name of this {@link Contributor}
     */
    @Nonnull
    public String getName() {
        return githubUsername;
    }

    /**
     * Returns the Minecraft username of the {@link Contributor}.
     * This can be the same as {@link #getName()}.
     *
     * @return The Minecraft username of this {@link Contributor}.
     */
    @Nonnull
    public String getMinecraftName() {
        return minecraftUsername;
    }

    /**
     * Returns a link to the GitHub profile of this {@link Contributor}.
     *
     * @return The GitHub profile of this {@link Contributor}
     */
    @Nullable
    public String getProfile() {
        return profileLink;
    }

    /**
     * This returns a {@link List} of contributions for this {@link Contributor}.
     * Each entry consists of a {@link String} (for the role) and an {@link Integer}
     * (for the amount of commits).
     * 
     * @return A {@link List} of contributions for this {@link Contributor}
     */
    @Nonnull
    public List<Map.Entry<String, Integer>> getContributions() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(contributions.entrySet());
        list.sort(Comparator.comparingInt(entry -> -entry.getValue()));
        return list;
    }

    /**
     * This method gives you the amount of contributions this {@link Contributor}
     * has submmited in the name of the given role.
     * 
     * @param role
     *            The role for which to count the contributions.
     * 
     * @return The amount of contributions this {@link Contributor} submitted as the given role
     */
    public int getContributions(@Nonnull String role) {
        Validate.notNull(role, "The role cannot be null!");

        return contributions.getOrDefault(role, 0);
    }

    /**
     * This method sets the {@link UUID} for this {@link Contributor}.
     * 
     * @param uuid
     *            The {@link UUID} for this {@link Contributor}
     */
    public void setUniqueId(@Nullable UUID uuid) {
        this.uuid = uuid == null ? Optional.empty() : Optional.of(uuid);
    }

    /**
     * This returns the {@link UUID} for this {@link Contributor}.
     * This {@link UUID} may be loaded from a cache.
     * 
     * @return The {@link UUID} of this {@link Contributor}
     */
    @Nonnull
    public Optional<UUID> getUniqueId() {
        return uuid;
    }

    /**
     * Returns this contributor's head texture.
     * If no texture could be found, or it hasn't been pulled yet,
     * then it will return a placeholder texture.
     * 
     * @return A Base64-Head Texture
     */
    @Nonnull
    public String getTexture() {
        return getTexture(Slimefun.getGitHubService());
    }

    /**
     * Returns this contributor's head texture.
     * If no texture could be found, or it hasn't been pulled yet,
     * then it will return a placeholder texture.
     * 
     * @param github
     *            Our {@link GitHubService} instance
     * 
     * @return A Base64-Head Texture
     */
    @Nonnull
    protected String getTexture(@Nonnull GitHubService github) {
        if (!headTexture.isComputed() || !headTexture.isPresent()) {
            String cached = github.getCachedTexture(githubUsername);

            if (cached != null) {
                return cached;
            } else {
                return HeadTexture.UNKNOWN.getTexture();
            }
        } else {
            return headTexture.get();
        }
    }

    /**
     * This method will return whether this instance of {@link Contributor} has
     * pulled a texture yet.
     * 
     * @return Whether this {@link Contributor} has been assigned a texture yet
     */
    public boolean hasTexture() {
        return headTexture.isComputed();
    }

    /**
     * This sets the skin texture of this {@link Contributor} or clears it.
     * 
     * @param skin
     *            The base64 skin texture or null
     */
    public void setTexture(@Nullable String skin) {
        headTexture.compute(skin);
    }

    /**
     * This returns the total amount of contributions towards this project for this
     * {@link Contributor}.
     * 
     * @return The total amount of contributions
     */
    public int getTotalContributions() {
        return contributions.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * This returns the final display name for this {@link Contributor}.
     * The display name is basically the GitHub username but if the Minecraft username differs,
     * it will be appended in brackets behind the GitHub username.
     * 
     * @return The final display name of this {@link Contributor}.
     */
    @Nonnull
    public String getDisplayName() {
        return ChatColor.GRAY + githubUsername + (!githubUsername.equals(minecraftUsername) ? ChatColor.DARK_GRAY + " (MC: " + minecraftUsername + ")" : "");
    }

    /**
     * This returns the position on where to order this {@link Contributor}.
     * This is just a convenience method for a {@link Comparator}, it is equivalent to
     * {@link #getTotalContributions()} multiplied by minus one.
     * 
     * @return The position of this {@link Contributor} in terms for positioning and ordering.
     */
    public int getPosition() {
        return -getTotalContributions();
    }
}
