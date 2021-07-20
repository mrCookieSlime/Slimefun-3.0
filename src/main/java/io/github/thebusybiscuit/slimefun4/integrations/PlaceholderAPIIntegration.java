package io.github.thebusybiscuit.slimefun4.integrations;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

/**
 * This is our integration for {@link PlaceholderAPI}.
 * 
 * @author TheBusyBiscuit
 *
 */
class PlaceholderAPIIntegration extends PlaceholderExpansion {

    private final String version;
    private final String author;

    public PlaceholderAPIIntegration(@Nonnull Slimefun plugin) {
        this.version = plugin.getDescription().getVersion();
        this.author = plugin.getDescription().getAuthors().toString();
    }

    @Nonnull
    @Override
    public String getIdentifier() {
        return "slimefun";
    }

    @Nonnull
    @Override
    public String getVersion() {
        return version;
    }

    @Nonnull
    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    private boolean isPlaceholder(@Nullable OfflinePlayer p, boolean requiresProfile, @Nonnull String params, @Nonnull String placeholder) {
        if (requiresProfile) {
            if (p != null && placeholder.equals(params)) {
                PlayerProfile.request(p);
                return true;
            } else {
                return false;
            }
        } else {
            return placeholder.equals(params);
        }
    }

    @Override
    public String onRequest(@Nullable OfflinePlayer p, @Nonnull String params) {
        if (isPlaceholder(p, true, params, "researches_total_xp_levels_spent")) {
            Optional<PlayerProfile> profile = PlayerProfile.find(p);

            if (profile.isPresent()) {
                Stream<Research> stream = profile.get().getResearches().stream();
                return String.valueOf(stream.mapToInt(Research::getCost).sum());
            } else if (p instanceof Player) {
                return getProfilePlaceholder((Player) p);
            }
        }

        if (isPlaceholder(p, true, params, "researches_total_researches_unlocked")) {
            Optional<PlayerProfile> profile = PlayerProfile.find(p);

            if (profile.isPresent()) {
                Set<Research> set = profile.get().getResearches();
                return String.valueOf(set.size());
            } else if (p instanceof Player) {
                return getProfilePlaceholder((Player) p);
            }
        }

        if (isPlaceholder(p, false, params, "researches_total_researches")) {
            return String.valueOf(Slimefun.getRegistry().getResearches().size());
        }

        if (isPlaceholder(p, true, params, "researches_percentage_researches_unlocked")) {
            Optional<PlayerProfile> profile = PlayerProfile.find(p);

            if (profile.isPresent()) {
                Set<Research> set = profile.get().getResearches();
                return String.valueOf(Math.round(((set.size() * 100.0F) / Slimefun.getRegistry().getResearches().size()) * 100.0F) / 100.0F);
            } else if (p instanceof Player) {
                return getProfilePlaceholder((Player) p);
            }
        }

        if (isPlaceholder(p, true, params, "researches_title")) {
            Optional<PlayerProfile> profile = PlayerProfile.find(p);

            if (profile.isPresent()) {
                return profile.get().getTitle();
            } else if (p instanceof Player) {
                return getProfilePlaceholder((Player) p);
            }
        }

        if (isPlaceholder(p, false, params, "gps_complexity") && p != null) {
            return String.valueOf(Slimefun.getGPSNetwork().getNetworkComplexity(p.getUniqueId()));
        }

        if (isPlaceholder(p, false, params, "timings_lag")) {
            return Slimefun.getProfiler().getTime();
        }

        if (isPlaceholder(p, false, params, "language") && p instanceof Player) {
            Player player = (Player) p;
            return Slimefun.getLocalization().getLanguage(player).getName(player);
        }

        return null;
    }

    @Nonnull
    private String getProfilePlaceholder(@Nonnull Player p) {
        return Slimefun.getLocalization().getMessage(p, "placeholderapi.profile-loading");
    }

}
