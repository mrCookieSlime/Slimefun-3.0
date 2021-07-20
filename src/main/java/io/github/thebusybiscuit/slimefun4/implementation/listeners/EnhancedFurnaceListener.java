package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.EnhancedFurnace;
import io.papermc.lib.PaperLib;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * This {@link Listener} is responsible for enforcing the "fuel efficiency" and "fortune" policies
 * of an {@link EnhancedFurnace}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see EnhancedFurnace
 *
 */
public class EnhancedFurnaceListener implements Listener {

    public EnhancedFurnaceListener(@Nonnull Slimefun plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFuelBurn(FurnaceBurnEvent e) {
        if (e.getBlock().getType() != Material.FURNACE) {
            // We don't care about Smokers, Blast Furnaces and all that fancy stuff
            return;
        }

        SlimefunItem furnace = BlockStorage.check(e.getBlock());

        if (furnace instanceof EnhancedFurnace && ((EnhancedFurnace) furnace).getFuelEfficiency() > 0) {
            int burnTime = e.getBurnTime();
            int newBurnTime = ((EnhancedFurnace) furnace).getFuelEfficiency() * burnTime;

            e.setBurnTime(Math.min(newBurnTime, Short.MAX_VALUE - 1));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSmelt(FurnaceSmeltEvent e) {
        if (e.getBlock().getType() != Material.FURNACE) {
            // We don't care about Smokers, Blast Furnaces and all that fancy stuff
            return;
        }

        SlimefunItem sfItem = BlockStorage.check(e.getBlock());

        if (sfItem instanceof EnhancedFurnace) {
            BlockState state = PaperLib.getBlockState(e.getBlock(), false).getState();

            if (state instanceof Furnace) {
                FurnaceInventory inventory = ((Furnace) state).getInventory();
                int amount = inventory.getSmelting().getType().toString().endsWith("_ORE") ? ((EnhancedFurnace) sfItem).getRandomOutputAmount() : 1;
                Optional<ItemStack> result = Slimefun.getMinecraftRecipeService().getFurnaceOutput(inventory.getSmelting());

                if (result.isPresent()) {
                    ItemStack item = result.get();
                    int previous = inventory.getResult() != null ? inventory.getResult().getAmount() : 0;
                    amount = Math.min(item.getMaxStackSize() - previous, amount);
                    e.setResult(new ItemStack(item.getType(), amount));
                }
            }
        }
    }

}
