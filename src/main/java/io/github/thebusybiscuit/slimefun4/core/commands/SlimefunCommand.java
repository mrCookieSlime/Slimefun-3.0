package io.github.thebusybiscuit.slimefun4.core.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.core.commands.subcommands.SlimefunSubCommands;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

/**
 * This {@link CommandExecutor} holds the functionality of our {@code /slimefun} command.
 * 
 * @author TheBusyBiscuit
 *
 */
public class SlimefunCommand implements CommandExecutor, Listener {

    private boolean registered = false;
    private final Slimefun plugin;
    private final List<SubCommand> commands = new LinkedList<>();
    private final Map<SubCommand, Integer> commandUsage = new HashMap<>();

    /**
     * Creates a new instance of {@link SlimefunCommand}
     * 
     * @param plugin
     *            The instance of our {@link Slimefun}
     */
    public SlimefunCommand(@Nonnull Slimefun plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Validate.isTrue(!registered, "Slimefun's subcommands have already been registered!");

        registered = true;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        plugin.getCommand("slimefun").setExecutor(this);
        plugin.getCommand("slimefun").setTabCompleter(new SlimefunTabCompleter(this));
        commands.addAll(SlimefunSubCommands.getAllCommands(this));
    }

    public @Nonnull Slimefun getPlugin() {
        return plugin;
    }

    /**
     * Returns a heatmap of how often certain commands are used.
     * 
     * @return A {@link Map} holding the amount of times each command was run
     */
    public @Nonnull Map<SubCommand, Integer> getCommandUsage() {
        return commandUsage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            for (SubCommand command : commands) {
                if (args[0].equalsIgnoreCase(command.getName())) {
                    command.recordUsage(commandUsage);
                    command.onExecute(sender, args);
                    return true;
                }
            }
        }

        sendHelp(sender);

        /*
         * We could just return true here, but if there's no subcommands,
         * then something went horribly wrong anyway.
         * This will also stop sonarcloud from nagging about
         * this always returning true...
         */
        return !commands.isEmpty();
    }

    public void sendHelp(@Nonnull CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColors.color("&aSlimefun &2v" + Slimefun.getVersion()));
        sender.sendMessage("");

        for (SubCommand cmd : commands) {
            if (!cmd.isHidden()) {
                sender.sendMessage(ChatColors.color("&3/sf " + cmd.getName() + " &b") + cmd.getDescription(sender));
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("/help slimefun")) {
            sendHelp(e.getPlayer());
            e.setCancelled(true);
        }
    }

    /**
     * This returns A {@link List} containing every possible {@link SubCommand} of this {@link Command}.
     * 
     * @return A {@link List} containing every {@link SubCommand}
     */
    public @Nonnull List<String> getSubCommandNames() {
        // @formatter:off
        return commands.stream()
                .map(SubCommand::getName)
                .collect(Collectors.toList());
        // @formatter:on
    }

}
