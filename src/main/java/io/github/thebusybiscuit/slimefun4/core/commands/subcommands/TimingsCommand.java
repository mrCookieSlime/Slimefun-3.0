package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.PerformanceInspector;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.inspectors.ConsolePerformanceInspector;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.inspectors.PlayerPerformanceInspector;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

class TimingsCommand extends SubCommand {

    private static final String FLAG_PREFIX = "--";
    private final Set<String> flags = new HashSet<>(Arrays.asList("verbose"));

    @ParametersAreNonnullByDefault
    TimingsCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "timings", false);
    }

    @Override
    protected String getDescription() {
        return "commands.timings.description";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender.hasPermission("slimefun.command.timings") || sender instanceof ConsoleCommandSender) {
            if (hasInvalidFlags(sender, args)) {
                return;
            }

            boolean verbose = hasFlag(args, "verbose");

            if (verbose && sender instanceof Player) {
                Slimefun.getLocalization().sendMessage(sender, "commands.timings.verbose-player", true);
                return;
            }

            Slimefun.getLocalization().sendMessage(sender, "commands.timings.please-wait", true);

            PerformanceInspector inspector = inspectorOf(sender, verbose);
            Slimefun.getProfiler().requestSummary(inspector);
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
        }
    }

    @ParametersAreNonnullByDefault
    private boolean hasInvalidFlags(CommandSender sender, String[] args) {
        boolean hasInvalidFlags = false;

        // We start at 1 because args[0] will be "timings".
        for (int i = 1; i < args.length; i++) {
            String argument = args[i].toLowerCase(Locale.ROOT);

            if (argument.startsWith(FLAG_PREFIX) && !flags.contains(argument.substring(2))) {
                hasInvalidFlags = true;
                Slimefun.getLocalization().sendMessage(sender, "commands.timings.unknown-flag", true, msg -> msg.replace("%flag%", argument));
            }
        }

        return hasInvalidFlags;
    }

    @ParametersAreNonnullByDefault
    private boolean hasFlag(String[] args, String flag) {
        // We start at 1 because args[0] will be "timings".
        for (int i = 1; i < args.length; i++) {
            if (args[i].equalsIgnoreCase(FLAG_PREFIX + flag)) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    private PerformanceInspector inspectorOf(@Nonnull CommandSender sender, boolean verbose) {
        if (sender instanceof Player) {
            return new PlayerPerformanceInspector((Player) sender);
        } else {
            return new ConsolePerformanceInspector(sender, verbose);
        }
    }

}
