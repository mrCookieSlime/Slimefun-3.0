package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import io.papermc.lib.PaperLib;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

/**
 * This is our class for the /sf versions subcommand.
 * 
 * @author TheBusyBiscuit
 * @author Walshy
 *
 */
class VersionsCommand extends SubCommand {

    /**
     * This is the Java version we recommend to use.
     * Bump as necessary and adjust the warning.
     */
    private static final int RECOMMENDED_JAVA_VERSION = 16;

    /**
     * This is the notice that will be displayed when an
     * older version of Java is detected.
     */
    private static final String JAVA_VERSION_NOTICE = "As of Minecraft 1.17 Java 16 will be required!";

    @ParametersAreNonnullByDefault
    VersionsCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "versions", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender.hasPermission("slimefun.command.versions") || sender instanceof ConsoleCommandSender) {
            /*
             * After all these years... Spigot still displays as "CraftBukkit".
             * so we will just fix this inconsistency for them :)
             */
            String serverSoftware = PaperLib.isSpigot() && !PaperLib.isPaper() ? "Spigot" : Bukkit.getName();
            ComponentBuilder builder = new ComponentBuilder();

            // @formatter:off
            builder.append("This Server uses the following setup of Slimefun:\n")
                .color(ChatColor.GRAY)
                .append(serverSoftware)
                .color(ChatColor.GREEN)
                .append(" " + Bukkit.getVersion() + '\n')
                .color(ChatColor.DARK_GREEN)
                .append("Slimefun ")
                .color(ChatColor.GREEN)
                .append(Slimefun.getVersion() + '\n')
                .color(ChatColor.DARK_GREEN);
            // @formatter:on

            if (Slimefun.getMetricsService().getVersion() != null) {
                // @formatter:off
                builder.append("Metrics-Module ")
                    .color(ChatColor.GREEN)
                    .append("#" + Slimefun.getMetricsService().getVersion() + '\n')
                    .color(ChatColor.DARK_GREEN);
                // @formatter:on
            }

            addJavaVersion(builder);

            if (Slimefun.getRegistry().isBackwardsCompatible()) {
                // @formatter:off
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                    "Backwards compatibility has a negative impact on performance!\n" +
                    "We recommend you to disable this setting unless your server still " +
                    "has legacy Slimefun items (from before summer 2019) in circulation."
                ));
                // @formatter:on

                builder.append("\nBackwards compatibility enabled!\n").color(ChatColor.RED).event(hoverEvent);
            }

            builder.append("\n").event((HoverEvent) null);
            addPluginVersions(builder);

            sender.spigot().sendMessage(builder.create());
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
        }
    }

    private void addJavaVersion(@Nonnull ComponentBuilder builder) {
        int version = NumberUtils.getJavaVersion();

        if (version < RECOMMENDED_JAVA_VERSION) {
            // @formatter:off
            builder.append("Java " + version).color(ChatColor.RED)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                    "Your Java version is out of date!\n!" +
                    "You should use Java " + RECOMMENDED_JAVA_VERSION + " or higher.\n" +
                    JAVA_VERSION_NOTICE
                )))
                .append("\n")
                .event((HoverEvent) null);
            // @formatter:on
        } else {
            builder.append("Java ").color(ChatColor.GREEN).append(version + "\n").color(ChatColor.DARK_GREEN);
        }
    }

    private void addPluginVersions(@Nonnull ComponentBuilder builder) {
        Collection<Plugin> addons = Slimefun.getInstalledAddons();

        if (addons.isEmpty()) {
            builder.append("No Addons installed").color(ChatColor.GRAY).italic(true);
            return;
        }

        builder.append("Installed Addons: ").color(ChatColor.GRAY).append("(" + addons.size() + ")").color(ChatColor.DARK_GRAY);

        for (Plugin plugin : addons) {
            String version = plugin.getDescription().getVersion();

            HoverEvent hoverEvent = null;
            ClickEvent clickEvent = null;
            ChatColor primaryColor;
            ChatColor secondaryColor;

            if (Bukkit.getPluginManager().isPluginEnabled(plugin)) {
                primaryColor = ChatColor.GREEN;
                secondaryColor = ChatColor.DARK_GREEN;
                String authors = String.join(", ", plugin.getDescription().getAuthors());

                if (plugin instanceof SlimefunAddon && ((SlimefunAddon) plugin).getBugTrackerURL() != null) {
                    // @formatter:off
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder()
                        .append("Author(s): ")
                        .append(authors)
                        .color(ChatColor.YELLOW)
                        .append("\n> Click here to go to their issues tracker")
                        .color(ChatColor.GOLD)
                        .create()
                    ));
                    // @formatter:on

                    clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, ((SlimefunAddon) plugin).getBugTrackerURL());
                } else {
                    // @formatter:off
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder()
                        .append("Author(s): ")
                        .append(authors)
                        .color(ChatColor.YELLOW)
                        .create()
                    ));
                    // @formatter:on
                }
            } else {
                primaryColor = ChatColor.RED;
                secondaryColor = ChatColor.DARK_RED;

                if (plugin instanceof SlimefunAddon && ((SlimefunAddon) plugin).getBugTrackerURL() != null) {
                    // @formatter:off
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder()
                        .append("This plugin is disabled.\nCheck the console for an error message.")
                        .color(ChatColor.RED)
                        .append("\n> Click here to report on their issues tracker")
                        .color(ChatColor.DARK_RED)
                        .create()
                    ));
                    // @formatter:on

                    SlimefunAddon addon = (SlimefunAddon) plugin;

                    if (addon.getBugTrackerURL() != null) {
                        clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, addon.getBugTrackerURL());
                    }
                } else {
                    hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Plugin is disabled. Check the console for an error and report on their issues tracker."));
                }
            }

            // @formatter:off
            // We need to reset the hover event or it's added to all components
            builder.append("\n  " + plugin.getName())
                .color(primaryColor)
                .event(hoverEvent)
                .event(clickEvent)
                .append(" v" + version)
                .color(secondaryColor)
                .append("")
                .event((ClickEvent) null)
                .event((HoverEvent) null);
            // @formatter:on
        }
    }
}
