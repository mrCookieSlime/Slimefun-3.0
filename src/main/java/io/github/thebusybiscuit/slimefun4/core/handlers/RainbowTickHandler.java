package io.github.thebusybiscuit.slimefun4.core.handlers;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.GlassPane;

import io.github.bakedlibs.dough.collections.LoopIterator;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.RainbowBlock;
import io.github.thebusybiscuit.slimefun4.utils.ColoredMaterial;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;

/**
 * This is a {@link BlockTicker} that is exclusively used for Rainbow blocks.
 * On every tick it cycles through the {@link LoopIterator} and chooses the next {@link Material}
 * and sets itself to that.
 * 
 * @author TheBusyBiscuit
 * 
 * @see RainbowBlock
 *
 */
public class RainbowTickHandler extends BlockTicker {

    private final LoopIterator<Material> iterator;
    private final boolean glassPanes;
    private Material material;

    public RainbowTickHandler(@Nonnull List<Material> materials) {
        Validate.noNullElements(materials, "A RainbowTicker cannot have a Material that is null!");

        if (materials.isEmpty()) {
            throw new IllegalArgumentException("A RainbowTicker must have at least one Material associated with it!");
        }

        glassPanes = containsGlassPanes(materials);
        iterator = new LoopIterator<>(materials);
        material = iterator.next();
    }

    public RainbowTickHandler(@Nonnull Material... materials) {
        this(Arrays.asList(materials));
    }

    public RainbowTickHandler(@Nonnull ColoredMaterial material) {
        this(material.asList());
    }

    /**
     * This method checks whether a given {@link Material} array contains any {@link Material}
     * that would result in a {@link GlassPane} {@link BlockData}.
     * This is done to save performance, so we don't have to validate {@link BlockData} at
     * runtime.
     * 
     * @param materials
     *            The {@link Material} Array to check
     * 
     * @return Whether the array contained any {@link GlassPane} materials
     */
    private boolean containsGlassPanes(@Nonnull List<Material> materials) {
        if (Slimefun.getMinecraftVersion() == MinecraftVersion.UNIT_TEST) {
            // BlockData is not available to us during Unit Tests :/
            return false;
        }

        for (Material type : materials) {
            /**
             * This BlockData is purely virtual and only created on startup, it should have
             * no impact on performance, in fact it should save performance as it preloads
             * the data but also saves heavy calls for other Materials
             */
            if (type.createBlockData() instanceof GlassPane) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void tick(Block b, SlimefunItem item, Config data) {
        if (b.getType() == Material.AIR) {
            /**
             * The block was broken, setting the Material now would result in a
             * duplication glitch
             */
            return;
        }

        if (glassPanes) {
            BlockData blockData = b.getBlockData();

            if (blockData instanceof GlassPane) {
                BlockData block = material.createBlockData(bd -> {
                    if (bd instanceof GlassPane) {
                        GlassPane previousData = (GlassPane) blockData;
                        GlassPane nextData = (GlassPane) bd;

                        nextData.setWaterlogged(previousData.isWaterlogged());

                        for (BlockFace face : previousData.getAllowedFaces()) {
                            nextData.setFace(face, previousData.hasFace(face));
                        }
                    }
                });

                b.setBlockData(block, false);
                return;
            }
        }

        b.setType(material, false);
    }

    @Override
    public void uniqueTick() {
        material = iterator.next();
    }

    @Override
    public boolean isSynchronized() {
        return true;
    }

}
