package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import io.github.bakedlibs.dough.common.PlayerList;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

class StatsCommand extends SubCommand {

    @ParametersAreNonnullByDefault
    StatsCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "stats", false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            if (sender.hasPermission("slimefun.stats.others") || sender instanceof ConsoleCommandSender) {
                Optional<Player> player = PlayerList.findByName(args[1]);

                if (player.isPresent()) {
                    PlayerProfile.get(player.get(), profile -> profile.sendStats(sender));
                } else {
                    Slimefun.getLocalization().sendMessage(sender, "messages.not-online", true, msg -> msg.replace("%player%", args[1]));
                }
            } else {
                Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            }
        } else if (sender instanceof Player) {
            PlayerProfile.get((Player) sender, profile -> profile.sendStats(sender));
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
        }
    }

}
