package io.github.thebusybiscuit.slimefun4.integrations;

import java.util.function.Consumer;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;

import dev.lone.itemsadder.api.ItemsAdder;
import io.github.thebusybiscuit.cscorelib2.protection.ProtectionManager;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;

/**
 * This Service holds all interactions and hooks with third-party {@link Plugin Plugins}
 * that are not necessarily a dependency or a {@link SlimefunAddon}.
 * 
 * Integration with these plugins happens inside Slimefun itself.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SlimefunPlugin
 *
 */
public class IntegrationsManager {

    /**
     * This is our instance of {@link SlimefunPlugin}.
     */
    protected final SlimefunPlugin plugin;

    /**
     * Our {@link ProtectionManager} instance.
     */
    private ProtectionManager protectionManager;

    /**
     * This boolean determines whether {@link #start()} was run.
     */
    private boolean isEnabled = false;

    // Soft dependencies
    private boolean isPlaceholderAPIInstalled = false;
    private boolean isWorldEditInstalled = false;
    private boolean isMcMMOInstalled = false;
    private boolean isClearLagInstalled = false;
    private boolean isItemsAdderInstalled = false;
    private boolean isOrebfuscatorInstalled = false;

    // Addon support
    private boolean isChestTerminalInstalled = false;

    /**
     * This initializes the {@link IntegrationsManager}
     * 
     * @param plugin
     *            Our instance of {@link SlimefunPlugin}
     */
    public IntegrationsManager(@Nonnull SlimefunPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * This method returns whether the {@link IntegrationsManager} was enabled yet.
     * 
     * @return Whether this {@link IntegrationsManager} has been enabled already.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * This method initializes all third party integrations.
     */
    public final void start() {
        if (isEnabled) {
            // Prevent double-registration
            throw new UnsupportedOperationException("All integrations have already been loaded.");
        } else {
            isEnabled = true;
        }

        // Load any soft dependencies
        onServerLoad();

        // Load any integrations which aren't dependencies (loadBefore)
        plugin.getServer().getScheduler().runTask(plugin, this::onServerStart);
    }

    /**
     * This method is called when the {@link Server} loaded its {@link Plugin Plugins}.
     * We can safely assume that any {@link Plugin} which is a soft dependency of Slimefun
     * to be enabled at this point.
     */
    private void onServerLoad() {
        // PlaceholderAPI hook to provide playerholders from Slimefun.
        load("PlaceholderAPI", integration -> {
            new PlaceholderAPIIntegration(plugin).register();
            isPlaceholderAPIInstalled = true;
        });

        // WorldEdit Hook to clear Slimefun Data upon //set 0 //cut or any other equivalent
        load("WorldEdit", integration -> {
            new WorldEditIntegration().register();
            isWorldEditInstalled = true;
        });

        // mcMMO Integration
        load("mcMMO", integration -> {
            new McMMOIntegration(plugin).register();
            isMcMMOInstalled = true;
        });

        // ClearLag integration (to prevent display items from getting deleted)
        load("ClearLag", integration -> {
            new ClearLagIntegration(plugin).register();
            isClearLagInstalled = true;
        });

        // ItemsAdder Integration (custom blocks)
        load("ItemsAdder", integration -> isItemsAdderInstalled = true);
    }

    /**
     * This method is called when the {@link Server} has finished loading all its {@link Plugin Plugins}.
     */
    private void onServerStart() {
        try {
            // Load Protection plugin integrations
            protectionManager = new ProtectionManager(plugin.getServer());
        } catch (Exception | LinkageError x) {
            SlimefunPlugin.logger().log(Level.WARNING, x, () -> "Failed to load Protection plugin integrations for Slimefun v" + SlimefunPlugin.getVersion());
        }

        // Orebfuscator Integration
        load("Orebfuscator", integration -> {
            new OrebfuscatorIntegration(plugin).register();
            isOrebfuscatorInstalled = true;
        });

        isChestTerminalInstalled = isAddonInstalled("ChestTerminal");
    }

    /**
     * This method checks if the given addon is installed.
     * 
     * @param addon
     *            The name of the addon
     * 
     * @return Whether that addon is installed on the {@link Server}
     */
    private boolean isAddonInstalled(@Nonnull String addon) {
        if (plugin.getServer().getPluginManager().isPluginEnabled(addon)) {
            SlimefunPlugin.logger().log(Level.INFO, "Hooked into Slimefun Addon: {0}", addon);
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method logs a {@link Throwable} that was caused by a {@link Plugin}
     * we integrate into.
     * Calling this method will probably log the error and provide the version of this {@link Plugin}
     * for error analysis.
     * 
     * @param name
     *            The name of the {@link Plugin}
     * @param throwable
     *            The {@link Throwable} to throw
     */
    @ParametersAreNonnullByDefault
    protected void logError(String name, Throwable throwable) {
        Plugin externalPlugin = Bukkit.getPluginManager().getPlugin(name);

        if (externalPlugin != null) {
            String version = externalPlugin.getDescription().getVersion();
            SlimefunPlugin.logger().log(Level.WARNING, "Is {0} v{1} up to date?", new Object[] { name, version });
            SlimefunPlugin.logger().log(Level.SEVERE, throwable, () -> "An unknown error was detected while interacting with \"" + name + " v" + version + "\"");
        } else {
            SlimefunPlugin.logger().log(Level.SEVERE, throwable, () -> "An unknown error was detected while interacting with the plugin \"" + name + "\"");
        }
    }

    /**
     * This method loads an integration with a {@link Plugin} of the specified name.
     * If that {@link Plugin} is installed and enabled, the provided callback will be run.
     * 
     * @param pluginName
     *            The name of this {@link Plugin}
     * @param consumer
     *            The callback to run if that {@link Plugin} is installed and enabled
     */
    private void load(@Nonnull String pluginName, @Nonnull Consumer<Plugin> consumer) {
        Plugin integration = plugin.getServer().getPluginManager().getPlugin(pluginName);

        if (integration != null && integration.isEnabled()) {
            String version = integration.getDescription().getVersion();
            SlimefunPlugin.logger().log(Level.INFO, "Hooked into Plugin: {0} v{1}", new Object[] { pluginName, version });

            try {
                // Run our callback
                consumer.accept(integration);
            } catch (Exception | LinkageError x) {
                SlimefunPlugin.logger().log(Level.WARNING, "Maybe consider updating {0} or Slimefun?", pluginName);
                SlimefunPlugin.logger().log(Level.WARNING, x, () -> "Failed to hook into " + pluginName + " v" + version);
            }
        }
    }

    /**
     * This returns out instance of the {@link ProtectionManager}.
     * This bridge is used to hook into any third-party protection {@link Plugin}.
     * 
     * @return Our instanceof of the {@link ProtectionManager}
     */
    @Nonnull
    public ProtectionManager getProtectionManager() {
        return protectionManager;
    }

    /**
     * This checks if one of our third party integrations faked an {@link Event}.
     * Faked {@link Event Events} should be ignored in our logic.
     * 
     * @param event
     *            The {@link Event} to test
     * 
     * @return Whether this is a fake event
     */
    public boolean isEventFaked(@Nonnull Event event) {
        // This can be changed to "FakeEvent" in a later version
        return isMcMMOInstalled && event instanceof FakeBlockBreakEvent;
    }

    /**
     * This checks if one of our third party integrations has placed a custom
     * {@link Block} at this {@link Location}.
     * 
     * @param block
     *            The {@link Block} to check
     * 
     * @return Whether a different custom {@link Block} exists at that {@link Location}
     */
    @SuppressWarnings("deprecation")
    public boolean isCustomBlock(@Nonnull Block block) {
        if (isItemsAdderInstalled) {
            try {
                return ItemsAdder.isCustomBlock(block);
            } catch (Exception | LinkageError x) {
                logError("ItemsAdder", x);
            }
        }

        return false;
    }

    public boolean isPlaceholderAPIInstalled() {
        return isPlaceholderAPIInstalled;
    }

    public boolean isWorldEditInstalled() {
        return isWorldEditInstalled;
    }

    public boolean isMcMMOInstalled() {
        return isMcMMOInstalled;
    }

    public boolean isClearLagInstalled() {
        return isClearLagInstalled;
    }

    public boolean isItemsAdderInstalled() {
        return isItemsAdderInstalled;
    }

    public boolean isChestTerminalInstalled() {
        return isChestTerminalInstalled;
    }

    public boolean isOrebfuscatorInstalled() {
        return isOrebfuscatorInstalled;
    }
}
