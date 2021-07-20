package io.github.thebusybiscuit.slimefun4.core.services;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.plugin.Plugin;

import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

/**
 * This Class represents a Metrics Service that sends data to https://bstats.org/
 * This data is used to analyse the usage of this {@link Plugin}.
 * <p>
 * You can find more info in the README file of this Project on GitHub. <br>
 * <b>Note:</b> To start the metrics you will need to be calling {@link #start()}
 *
 * @author WalshyDev
 */
public class MetricsService {

    /**
     * The URL pointing towards the GitHub API.
     */
    private static final String API_URL = "https://api.github.com/";

    /**
     * The Name of our repository
     */
    private static final String REPO_NAME = "MetricsModule";

    /**
     * The URL pointing towards the /releases/ endpoint of our
     * Metrics repository
     */
    private static final String RELEASES_URL = API_URL + "repos/Slimefun/" + REPO_NAME + "/releases/latest";

    /**
     * The URL pointing towards the download location for a
     * GitHub release of our Metrics repository
     */
    private static final String DOWNLOAD_URL = "https://github.com/Slimefun/" + REPO_NAME + "/releases/download";

    private final Slimefun plugin;
    private final File parentFolder;
    private final File metricsModuleFile;

    private URLClassLoader moduleClassLoader;
    private String metricVersion = null;
    private boolean hasDownloadedUpdate = false;

    static {
        // @formatter:off (We want this to stay this nicely aligned :D )
        Unirest.config()
            .concurrency(2, 1)
            .setDefaultHeader("User-Agent", "MetricsModule Auto-Updater")
            .setDefaultHeader("Accept", "application/vnd.github.v3+json")
            .enableCookieManagement(false)
            .cookieSpec("ignoreCookies");
        // @formatter:on
    }

    /**
     * This constructs a new instance of our {@link MetricsService}.
     * 
     * @param plugin
     *            Our {@link Slimefun} instance
     */
    public MetricsService(@Nonnull Slimefun plugin) {
        this.plugin = plugin;
        this.parentFolder = new File(plugin.getDataFolder(), "cache" + File.separatorChar + "modules");

        if (!parentFolder.exists()) {
            parentFolder.mkdirs();
        }

        this.metricsModuleFile = new File(parentFolder, REPO_NAME + ".jar");
    }

    /**
     * This method loads the metric module and starts the metrics collection.
     */
    public void start() {
        if (!metricsModuleFile.exists()) {
            plugin.getLogger().info(REPO_NAME + " does not exist, downloading...");

            if (!download(getLatestVersion())) {
                plugin.getLogger().warning("Failed to start metrics as the file could not be downloaded.");
                return;
            }
        }

        try {
            /*
             * Load the jar file into a child class loader using the Slimefun
             * PluginClassLoader as a parent.
             */
            moduleClassLoader = URLClassLoader.newInstance(new URL[] { metricsModuleFile.toURI().toURL() }, plugin.getClass().getClassLoader());
            Class<?> metricsClass = moduleClassLoader.loadClass("dev.walshy.sfmetrics.MetricsModule");

            metricVersion = metricsClass.getPackage().getImplementationVersion();

            /*
             * If it has not been newly downloaded, auto-updates are enabled
             * AND there's a new version then cleanup, download and start
             */
            if (!hasDownloadedUpdate && hasAutoUpdates() && checkForUpdate(metricVersion)) {
                plugin.getLogger().info("Cleaned up, now re-loading Metrics-Module!");
                start();
                return;
            }

            // Finally, we're good to start this.
            Method start = metricsClass.getDeclaredMethod("start");
            String version = metricsClass.getPackage().getImplementationVersion();

            // This is required to be sync due to bStats.
            Slimefun.runSync(() -> {
                try {
                    start.invoke(null);
                    plugin.getLogger().info("Metrics build #" + version + " started.");
                } catch (Exception | LinkageError e) {
                    plugin.getLogger().log(Level.WARNING, "Failed to start metrics.", e);
                }
            });
        } catch (Exception | LinkageError e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load the metrics module. Maybe the jar is corrupt?", e);
        }
    }

    /**
     * This will close the child {@link ClassLoader} and mark all the resources held under this no longer
     * in use, they will be cleaned up the next GC run.
     */
    public void cleanUp() {
        try {
            if (moduleClassLoader != null) {
                moduleClassLoader.close();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not clean up module class loader. Some memory may have been leaked.");
        }
    }

    /**
     * Checks for a new update and compares it against the current version.
     * If there is a new version available then this returns true.
     *
     * @param currentVersion
     *            The current version which is being used.
     * 
     * @return if there is an update available.
     */
    public boolean checkForUpdate(@Nullable String currentVersion) {
        if (currentVersion == null || !CommonPatterns.NUMERIC.matcher(currentVersion).matches()) {
            return false;
        }

        int latest = getLatestVersion();

        if (latest > Integer.parseInt(currentVersion)) {
            return download(latest);
        }

        return false;
    }

    /**
     * Gets the latest version available as an int.
     * This is an internal method used by {@link #checkForUpdate(String)}.
     * If it cannot get the version for whatever reason this will return 0, effectively always
     * being behind.
     *
     * @return The latest version as an integer or -1 if it failed to fetch.
     */
    private int getLatestVersion() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(RELEASES_URL).asJson();

            if (!response.isSuccess()) {
                return -1;
            }

            JsonNode node = response.getBody();

            if (node == null) {
                return -1;
            }

            return node.getObject().getInt("tag_name");
        } catch (UnirestException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to fetch latest builds for Metrics: {0}", e.getMessage());
            return -1;
        }
    }

    /**
     * Downloads the version specified to Slimefun's data folder.
     *
     * @param version
     *            The version to download.
     */
    private boolean download(int version) {
        File file = new File(parentFolder, "Metrics-" + version + ".jar");

        try {
            plugin.getLogger().log(Level.INFO, "# Starting download of MetricsModule build: #{0}", version);

            if (file.exists()) {
                // Delete the file in case we accidentally downloaded it before
                Files.delete(file.toPath());
            }

            AtomicInteger lastPercentPosted = new AtomicInteger();
            GetRequest request = Unirest.get(DOWNLOAD_URL + "/" + version + "/" + REPO_NAME + ".jar");

            HttpResponse<File> response = request.downloadMonitor((b, fileName, bytesWritten, totalBytes) -> {
                int percent = (int) (20 * (Math.round((((double) bytesWritten / totalBytes) * 100) / 20)));

                if (percent != 0 && percent != lastPercentPosted.get()) {
                    plugin.getLogger().info("# Downloading... " + percent + "% " + "(" + bytesWritten + "/" + totalBytes + " bytes)");
                    lastPercentPosted.set(percent);
                }
            }).asFile(file.getPath());

            if (response.isSuccess()) {
                plugin.getLogger().log(Level.INFO, "Successfully downloaded {0} build: #{1}", new Object[] { REPO_NAME, version });

                // Replace the metric file with the new one
                cleanUp();
                Files.move(file.toPath(), metricsModuleFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                metricVersion = String.valueOf(version);
                hasDownloadedUpdate = true;
                return true;
            }
        } catch (UnirestException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to fetch the latest jar file from the builds page. Perhaps GitHub is down? Response: {0}", e.getMessage());
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to replace the old metric file with the new one. Please do this manually! Error: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * Returns the currently downloaded metrics version.
     * This <strong>can change</strong>! It may be null or an
     * older version before it has downloaded a newer one.
     *
     * @return The current version or null if not loaded.
     */
    @Nullable
    public String getVersion() {
        return metricVersion;
    }

    /**
     * Returns if the current server has metrics auto-updates enabled.
     *
     * @return True if the current server has metrics auto-updates enabled.
     */
    public boolean hasAutoUpdates() {
        return Slimefun.instance().getConfig().getBoolean("metrics.auto-update");
    }
}
