package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.RestoredBackpack;

/**
 * This command that allows for backpack retrieval in the event they are lost.
 * The command accepts a name and id, if those match up it spawns a Medium Backpack
 * with the correct lore set in the sender's inventory.
 * 
 * @author Sfiguz7
 * 
 * @see RestoredBackpack
 *
 */
class BackpackCommand extends SubCommand {

    @ParametersAreNonnullByDefault
    BackpackCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "backpack", false);
    }

    @Override
    protected String getDescription() {
        return "commands.backpack.description";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("slimefun.command.backpack")) {
                if (args.length != 3) {
                    Slimefun.getLocalization().sendMessage(sender, "messages.usage", true, msg -> msg.replace("%usage%", "/sf backpack <Player> <ID>"));
                    return;
                }

                if (!CommonPatterns.NUMERIC.matcher(args[2]).matches()) {
                    Slimefun.getLocalization().sendMessage(sender, "commands.backpack.invalid-id");
                    return;
                }

                @SuppressWarnings("deprecation")
                OfflinePlayer backpackOwner = Bukkit.getOfflinePlayer(args[1]);

                if (!(backpackOwner instanceof Player) && !backpackOwner.hasPlayedBefore()) {
                    Slimefun.getLocalization().sendMessage(sender, "commands.backpack.player-never-joined");
                    return;
                }

                int id = Integer.parseInt(args[2]);

                PlayerProfile.get(backpackOwner, profile -> {
                    if (!profile.getBackpack(id).isPresent()) {
                        Slimefun.getLocalization().sendMessage(sender, "commands.backpack.backpack-does-not-exist");
                        return;
                    }

                    Slimefun.runSync(() -> {
                        ItemStack item = SlimefunItems.RESTORED_BACKPACK.clone();
                        Slimefun.getBackpackListener().setBackpackId(backpackOwner, item, 2, id);
                        ((Player) sender).getInventory().addItem(item);
                        Slimefun.getLocalization().sendMessage(sender, "commands.backpack.restored-backpack-given");
                    });
                });
            } else {
                Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            }
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
        }
    }
}
