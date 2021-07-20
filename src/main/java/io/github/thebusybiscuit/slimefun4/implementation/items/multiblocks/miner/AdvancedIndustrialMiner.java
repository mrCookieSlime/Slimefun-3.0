package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;

/**
 * The {@link AdvancedIndustrialMiner} is a more advanced version of the {@link IndustrialMiner}.
 * It uses Silk Touch and has a bigger range.
 * 
 * @author TheBusyBiscuit
 * 
 * @see IndustrialMiner
 * @see MiningTask
 *
 */
public class AdvancedIndustrialMiner extends IndustrialMiner {

    public AdvancedIndustrialMiner(ItemGroup category, SlimefunItemStack item) {
        super(category, item, Material.DIAMOND_BLOCK, true, 5);
    }

    @Override
    protected void registerDefaultFuelTypes() {
        fuelTypes.add(new MachineFuel(48, new ItemStack(Material.LAVA_BUCKET)));
        fuelTypes.add(new MachineFuel(64, SlimefunItems.OIL_BUCKET));
        fuelTypes.add(new MachineFuel(128, SlimefunItems.FUEL_BUCKET));
    }

}
