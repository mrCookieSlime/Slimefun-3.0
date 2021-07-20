package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;

/**
 * The {@link IndustrialMiner} is a {@link MultiBlockMachine} that can mine any
 * ores it finds in a given range underneath where it was placed.
 * 
 * <i>And for those of you who are wondering... yes this is the replacement for the
 * long-time deprecated Digital Miner.</i>
 * 
 * @author TheBusyBiscuit
 * 
 * @see AdvancedIndustrialMiner
 * @see MiningTask
 *
 */
public class IndustrialMiner extends MultiBlockMachine {

    protected final Map<Location, MiningTask> activeMiners = new HashMap<>();
    protected final List<MachineFuel> fuelTypes = new ArrayList<>();

    private final ItemSetting<Boolean> canMineAncientDebris = new ItemSetting<>(this, "can-mine-ancient-debris", false);
    private final ItemSetting<Boolean> canMineDeepslateOres = new ItemSetting<>(this, "can-mine-deepslate-ores", true);
    private final boolean silkTouch;
    private final int range;

    @ParametersAreNonnullByDefault
    public IndustrialMiner(ItemGroup category, SlimefunItemStack item, Material baseMaterial, boolean silkTouch, int range) {
        super(category, item, new ItemStack[] { null, null, null, new CustomItemStack(Material.PISTON, "Piston (facing up)"), new ItemStack(Material.CHEST), new CustomItemStack(Material.PISTON, "Piston (facing up)"), new ItemStack(baseMaterial), new ItemStack(Material.BLAST_FURNACE), new ItemStack(baseMaterial) }, BlockFace.UP);

        this.range = range;
        this.silkTouch = silkTouch;

        registerDefaultFuelTypes();
        addItemSetting(canMineAncientDebris);
        addItemSetting(canMineDeepslateOres);
    }

    /**
     * This returns whether this {@link IndustrialMiner} will output ores as they are.
     * Similar to the Silk Touch {@link Enchantment}.
     * 
     * @return Whether to treat ores with Silk Touch
     */
    public boolean hasSilkTouch() {
        return silkTouch;
    }

    /**
     * This method returns the range of the {@link IndustrialMiner}.
     * The total area will be determined by the range multiplied by 2 plus the actual center
     * of the machine.
     * 
     * So a range of 3 will make the {@link IndustrialMiner} affect an area of 7x7 blocks.
     * 3 on all axis, plus the center of the machine itself.
     * 
     * @return The range of this {@link IndustrialMiner}
     */
    public int getRange() {
        return range;
    }

    /**
     * This registers the various types of fuel that can be used in the
     * {@link IndustrialMiner}.
     */
    protected void registerDefaultFuelTypes() {
        // Coal & Charcoal
        fuelTypes.add(new MachineFuel(4, new ItemStack(Material.COAL)));
        fuelTypes.add(new MachineFuel(4, new ItemStack(Material.CHARCOAL)));

        fuelTypes.add(new MachineFuel(40, new ItemStack(Material.COAL_BLOCK)));
        fuelTypes.add(new MachineFuel(10, new ItemStack(Material.DRIED_KELP_BLOCK)));
        fuelTypes.add(new MachineFuel(4, new ItemStack(Material.BLAZE_ROD)));

        // Logs
        for (Material mat : Tag.LOGS.getValues()) {
            fuelTypes.add(new MachineFuel(1, new ItemStack(mat)));
        }
    }

    /**
     * This method returns the outcome that mining certain ores yields.
     * 
     * @param ore
     *            The {@link Material} of the ore that was mined
     * 
     * @return The outcome when mining this ore
     */
    public @Nonnull ItemStack getOutcome(@Nonnull Material ore) {
        if (hasSilkTouch()) {
            return new ItemStack(ore);
        }

        MinecraftVersion minecraftVersion = Slimefun.getMinecraftVersion();
        Random random = ThreadLocalRandom.current();

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_17)) {
            // In 1.17, breaking metal ores should get raw metals. Also support deepslate ores.
            switch (ore) {
                case DEEPSLATE_COAL_ORE:
                    return new ItemStack(Material.COAL);
                case DEEPSLATE_DIAMOND_ORE:
                    return new ItemStack(Material.DIAMOND);
                case DEEPSLATE_EMERALD_ORE:
                    return new ItemStack(Material.EMERALD);
                case DEEPSLATE_REDSTONE_ORE:
                    return new ItemStack(Material.REDSTONE, 4 + random.nextInt(2));
                case DEEPSLATE_LAPIS_ORE:
                    return new ItemStack(Material.LAPIS_LAZULI, 4 + random.nextInt(4));
                case COPPER_ORE:
                case DEEPSLATE_COPPER_ORE:
                    return new ItemStack(Material.RAW_COPPER);
                case IRON_ORE:
                case DEEPSLATE_IRON_ORE:
                    return new ItemStack(Material.RAW_IRON);
                case GOLD_ORE:
                case DEEPSLATE_GOLD_ORE:
                    return new ItemStack(Material.RAW_GOLD);
                default:
                    break;
            }
        }

        // In 1.16, breaking nether gold ores should get gold nuggets
        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_16) && ore == Material.NETHER_GOLD_ORE) {
            return new ItemStack(Material.GOLD_NUGGET, 2 + random.nextInt(4));
        }

        switch (ore) {
            case COAL_ORE:
                return new ItemStack(Material.COAL);
            case DIAMOND_ORE:
                return new ItemStack(Material.DIAMOND);
            case EMERALD_ORE:
                return new ItemStack(Material.EMERALD);
            case REDSTONE_ORE:
                return new ItemStack(Material.REDSTONE, 4 + random.nextInt(2));
            case LAPIS_ORE:
                return new ItemStack(Material.LAPIS_LAZULI, 4 + random.nextInt(4));
            case NETHER_QUARTZ_ORE:
                return new ItemStack(Material.QUARTZ);
            default:
                // This includes Iron and Gold ore (and Ancient Debris)
                return new ItemStack(ore);
        }
    }

    /**
     * This registers a new fuel type for this {@link IndustrialMiner}.
     * 
     * @param ores
     *            The amount of ores this allows you to mine
     * @param item
     *            The item that shall be consumed
     */
    public void addFuelType(int ores, @Nonnull ItemStack item) {
        Validate.isTrue(ores > 1 && ores % 2 == 0, "The amount of ores must be at least 2 and a multiple of 2.");
        Validate.notNull(item, "The fuel item cannot be null");

        fuelTypes.add(new MachineFuel(ores / 2, item));
    }

    @Override
    public @Nonnull String getLabelLocalPath() {
        return "guide.tooltips.recipes.generator";
    }

    @Override
    public @Nonnull List<ItemStack> getDisplayRecipes() {
        List<ItemStack> list = new ArrayList<>();

        for (MachineFuel fuel : fuelTypes) {
            ItemStack item = fuel.getInput().clone();
            ItemMeta im = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(ChatColors.color("&8\u21E8 &7Lasts for max. " + fuel.getTicks() + " Ores"));
            im.setLore(lore);
            item.setItemMeta(im);
            list.add(item);
        }

        return list;
    }

    @Override
    public void onInteract(Player p, Block b) {
        if (activeMiners.containsKey(b.getLocation())) {
            Slimefun.getLocalization().sendMessage(p, "machines.INDUSTRIAL_MINER.already-running");
            return;
        }

        Block chest = b.getRelative(BlockFace.UP);
        Block[] pistons = findPistons(chest);

        int mod = getRange();
        Block start = b.getRelative(-mod, -1, -mod);
        Block end = b.getRelative(mod, -1, mod);

        MiningTask task = new MiningTask(this, p.getUniqueId(), chest, pistons, start, end);
        task.start(b);
    }

    private @Nonnull Block[] findPistons(@Nonnull Block chest) {
        Block northern = chest.getRelative(BlockFace.NORTH);

        if (northern.getType() == Material.PISTON) {
            return new Block[] { northern, chest.getRelative(BlockFace.SOUTH) };
        } else {
            return new Block[] { chest.getRelative(BlockFace.WEST), chest.getRelative(BlockFace.EAST) };
        }
    }

    /**
     * This returns whether this {@link IndustrialMiner} can mine the given {@link Material}.
     * 
     * @param type
     *            The {@link Material} to check
     * 
     * @return Whether this {@link IndustrialMiner} is capable of mining this {@link Material}
     */
    public boolean canMine(@Nonnull Material type) {
        MinecraftVersion version = Slimefun.getMinecraftVersion();

        if (version.isAtLeast(MinecraftVersion.MINECRAFT_1_16) && type == Material.ANCIENT_DEBRIS) {
            return canMineAncientDebris.getValue();
        } else if (version.isAtLeast(MinecraftVersion.MINECRAFT_1_17) && SlimefunTag.DEEPSLATE_ORES.isTagged(type)) {
            return canMineDeepslateOres.getValue();
        } else {
            return SlimefunTag.INDUSTRIAL_MINER_ORES.isTagged(type);
        }
    }

}
