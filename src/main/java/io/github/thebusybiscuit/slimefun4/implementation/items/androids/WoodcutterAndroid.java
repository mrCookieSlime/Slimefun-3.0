package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.blocks.Vein;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

public class WoodcutterAndroid extends ProgrammableAndroid {

    private static final int MAX_REACH = 160;

    @ParametersAreNonnullByDefault
    public WoodcutterAndroid(ItemGroup category, int tier, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, tier, item, recipeType, recipe);
    }

    @Override
    public AndroidType getAndroidType() {
        return AndroidType.WOODCUTTER;
    }

    @Override
    protected boolean chopTree(Block b, BlockMenu menu, BlockFace face) {
        Block target = b.getRelative(face);

        if (Tag.LOGS.isTagged(target.getType())) {
            List<Block> list = Vein.find(target, MAX_REACH, block -> Tag.LOGS.isTagged(block.getType()));

            if (!list.isEmpty()) {
                Block log = list.get(list.size() - 1);
                log.getWorld().playEffect(log.getLocation(), Effect.STEP_SOUND, log.getType());

                OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(BlockStorage.getLocationInfo(b.getLocation(), "owner")));
                if (Slimefun.getProtectionManager().hasPermission(owner, log.getLocation(), Interaction.BREAK_BLOCK)) {
                    breakLog(log, b, menu, face);
                }

                return false;
            }
        }

        return true;
    }

    @ParametersAreNonnullByDefault
    private void breakLog(Block log, Block android, BlockMenu menu, BlockFace face) {
        ItemStack drop = new ItemStack(log.getType());

        // We try to push the log into the android's inventory, but nothing happens if it does not fit
        menu.pushItem(drop, getOutputSlots());

        log.getWorld().playEffect(log.getLocation(), Effect.STEP_SOUND, log.getType());

        // If the android just chopped the bottom log, we replant the appropriate sapling
        if (log.getY() == android.getRelative(face).getY()) {
            replant(log);
        } else {
            log.setType(Material.AIR);
        }
    }

    private void replant(@Nonnull Block block) {
        Material logType = block.getType();
        Material saplingType = null;
        Predicate<Material> soilRequirement = null;

        switch (logType) {
            case OAK_LOG:
            case OAK_WOOD:
            case STRIPPED_OAK_LOG:
            case STRIPPED_OAK_WOOD:
                saplingType = Material.OAK_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
                break;
            case BIRCH_LOG:
            case BIRCH_WOOD:
            case STRIPPED_BIRCH_LOG:
            case STRIPPED_BIRCH_WOOD:
                saplingType = Material.BIRCH_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
                break;
            case JUNGLE_LOG:
            case JUNGLE_WOOD:
            case STRIPPED_JUNGLE_LOG:
            case STRIPPED_JUNGLE_WOOD:
                saplingType = Material.JUNGLE_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
                break;
            case SPRUCE_LOG:
            case SPRUCE_WOOD:
            case STRIPPED_SPRUCE_LOG:
            case STRIPPED_SPRUCE_WOOD:
                saplingType = Material.SPRUCE_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
                break;
            case ACACIA_LOG:
            case ACACIA_WOOD:
            case STRIPPED_ACACIA_LOG:
            case STRIPPED_ACACIA_WOOD:
                saplingType = Material.ACACIA_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
                break;
            case DARK_OAK_LOG:
            case DARK_OAK_WOOD:
            case STRIPPED_DARK_OAK_LOG:
            case STRIPPED_DARK_OAK_WOOD:
                saplingType = Material.DARK_OAK_SAPLING;
                soilRequirement = SlimefunTag.DIRT_VARIANTS::isTagged;
                break;
            default:
                break;
        }

        if (Slimefun.getMinecraftVersion().isAtLeast(MinecraftVersion.MINECRAFT_1_16)) {
            switch (logType) {
                case CRIMSON_STEM:
                case CRIMSON_HYPHAE:
                case STRIPPED_CRIMSON_STEM:
                case STRIPPED_CRIMSON_HYPHAE:
                    saplingType = Material.CRIMSON_FUNGUS;
                    soilRequirement = SlimefunTag.FUNGUS_SOIL::isTagged;
                    break;
                case WARPED_STEM:
                case WARPED_HYPHAE:
                case STRIPPED_WARPED_STEM:
                case STRIPPED_WARPED_HYPHAE:
                    saplingType = Material.WARPED_FUNGUS;
                    soilRequirement = SlimefunTag.FUNGUS_SOIL::isTagged;
                    break;
                default:
                    break;
            }
        }

        if (saplingType != null && soilRequirement != null) {
            if (soilRequirement.test(block.getRelative(BlockFace.DOWN).getType())) {
                // Replant the block
                block.setType(saplingType);
            } else {
                // Simply drop the sapling if the soil does not fit
                block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(saplingType));
                block.setType(Material.AIR);
            }
        }
    }

}
