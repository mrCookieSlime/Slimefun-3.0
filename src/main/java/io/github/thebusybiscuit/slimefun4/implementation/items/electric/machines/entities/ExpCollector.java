package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities;

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.handlers.SimpleBlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.KnowledgeFlask;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;

/**
 * The {@link ExpCollector} is a machine which picks up any nearby {@link ExperienceOrb}
 * and produces a {@link KnowledgeFlask}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class ExpCollector extends SlimefunItem implements InventoryBlock, EnergyNetComponent {

    private final int[] border = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };

    private static final int ENERGY_CONSUMPTION = 10;
    private static final String DATA_KEY = "stored-exp";

    @ParametersAreNonnullByDefault
    public ExpCollector(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        createPreset(this, this::constructMenu);

        addItemHandler(onPlace(), onBreak());
    }

    @Nonnull
    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                BlockStorage.addBlockInfo(e.getBlock(), "owner", e.getPlayer().getUniqueId().toString());
            }
        };
    }

    @Nonnull
    private ItemHandler onBreak() {
        return new SimpleBlockBreakHandler() {

            @Override
            public void onBlockBreak(Block b) {
                BlockMenu inv = BlockStorage.getInventory(b);

                if (inv != null) {
                    inv.dropItems(b.getLocation(), getOutputSlots());
                }
            }
        };
    }

    @Override
    public int[] getInputSlots() {
        return new int[0];
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] { 12, 13, 14 };
    }

    @Override
    public EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return 1024;
    }

    protected void constructMenu(BlockMenuPreset preset) {
        for (int slot : border) {
            preset.addItem(slot, new CustomItemStack(Material.PURPLE_STAINED_GLASS_PANE, " "), (p, s, item, action) -> false);
        }
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                ExpCollector.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        });
    }

    protected void tick(Block b) {
        Iterator<Entity> iterator = b.getWorld().getNearbyEntities(b.getLocation(), 4.0, 4.0, 4.0, n -> n instanceof ExperienceOrb && n.isValid()).iterator();
        int experiencePoints = 0;

        while (iterator.hasNext() && experiencePoints == 0) {
            Entity entity = iterator.next();

            if (getCharge(b.getLocation()) < ENERGY_CONSUMPTION) {
                return;
            }

            experiencePoints = getStoredExperience(b) + ((ExperienceOrb) entity).getExperience();

            removeCharge(b.getLocation(), ENERGY_CONSUMPTION);
            entity.remove();

            int withdrawn = 0;
            BlockMenu menu = BlockStorage.getInventory(b);

            for (int level = 0; level < getStoredExperience(b); level = level + 10) {
                if (menu.fits(SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, getOutputSlots())) {
                    withdrawn = withdrawn + 10;
                    menu.pushItem(SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE.clone(), getOutputSlots());
                }
            }

            BlockStorage.addBlockInfo(b, DATA_KEY, String.valueOf(experiencePoints - withdrawn));
        }
    }

    private int getStoredExperience(Block b) {
        Config cfg = BlockStorage.getLocationInfo(b.getLocation());
        String value = cfg.getString(DATA_KEY);

        if (value != null) {
            return Integer.parseInt(value);
        } else {
            BlockStorage.addBlockInfo(b, DATA_KEY, "0");
            return 0;
        }
    }

}
