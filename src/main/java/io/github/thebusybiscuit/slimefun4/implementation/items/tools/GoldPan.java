package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.collections.RandomizedSet;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.EntityInteractHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricGoldPan;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.AutomatedPanningMachine;
import io.github.thebusybiscuit.slimefun4.implementation.settings.GoldPanDrop;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;



/**
 * A {@link GoldPan} is a {@link SlimefunItem} which allows you to obtain various
 * resources from Gravel.
 * 
 * @author TheBusyBiscuit
 * 
 * @see NetherGoldPan
 * @see AutomatedPanningMachine
 * @see ElectricGoldPan
 *
 */
public class GoldPan extends SimpleSlimefunItem<ItemUseHandler> implements RecipeDisplayItem {

    private final RandomizedSet<ItemStack> randomizer = new RandomizedSet<>();
    private final Set<GoldPanDrop> drops = new HashSet<>();

    @ParametersAreNonnullByDefault
    public GoldPan(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        drops.addAll(getGoldPanDrops());
        addItemSetting(drops.toArray(new GoldPanDrop[0]));
        addItemHandler(onEntityInteract());
    }

    /**
     * This method returns the target {@link Material} for this {@link GoldPan}.
     * 
     * @return The {@link Material} this {@link GoldPan} can be used on
     */
    public @Nonnull Material getInputMaterial() {
        return Material.GRAVEL;
    }

    protected @Nonnull Set<GoldPanDrop> getGoldPanDrops() {
        Set<GoldPanDrop> settings = new HashSet<>();

        settings.add(new GoldPanDrop(this, "chance.FLINT", 40, new ItemStack(Material.FLINT)));
        settings.add(new GoldPanDrop(this, "chance.CLAY", 20, new ItemStack(Material.CLAY_BALL)));
        settings.add(new GoldPanDrop(this, "chance.SIFTED_ORE", 35, SlimefunItems.SIFTED_ORE));
        settings.add(new GoldPanDrop(this, "chance.IRON_NUGGET", 5, new ItemStack(Material.IRON_NUGGET)));

        return settings;
    }

    @Override
    public void postRegister() {
        super.postRegister();
        updateRandomizer();
    }

    /**
     * <strong>Do not call this method directly</strong>.
     * 
     * This method is for internal purposes only.
     * It will update and re-calculate all weights in our {@link RandomizedSet}.
     */
    public void updateRandomizer() {
        randomizer.clear();

        for (GoldPanDrop setting : drops) {
            if (setting.getValue() > 0) {
                randomizer.add(setting.getOutput(), setting.getValue());
            }
        }
    }

    /**
     * This returns a random output {@link ItemStack} that can be obtained via
     * this {@link GoldPan}.
     * 
     * @return a random {@link ItemStack} obtained by this {@link GoldPan}
     */
    public @Nonnull ItemStack getRandomOutput() {
        ItemStack item = randomizer.getRandom();

        // Fixes #2804
        return item != null ? item : new ItemStack(Material.AIR);
    }

    @Override
    public @Nonnull String getLabelLocalPath() {
        return "guide.tooltips.recipes.gold-pan";
    }

    @Override
    public @Nonnull ItemUseHandler getItemHandler() {
        return e -> {
            Optional<Block> block = e.getClickedBlock();

            if (block.isPresent()) {
                Block b = block.get();

                // Check the clicked block type and for protections
                if (b.getType() == getInputMaterial() && Slimefun.getProtectionManager().hasPermission(e.getPlayer(), b.getLocation(), Interaction.BREAK_BLOCK)) {
                    ItemStack output = getRandomOutput();

                    b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());
                    b.setType(Material.AIR);

                    // Make sure that the randomly selected item is not air
                    if (output.getType() != Material.AIR) {
                        SlimefunUtils.spawnItem(b.getLocation(), output.clone(), ItemSpawnReason.GOLD_PAN_USE, true);
                    }
                }
            }

            e.cancel();
        };
    }

    /**
     * This method cancels {@link EntityInteractHandler} to prevent interacting {@link GoldPan}
     * with entities.
     *
     * @return the {@link EntityInteractHandler} of this {@link SlimefunItem}
     */
    public @Nonnull EntityInteractHandler onEntityInteract() {
        return (e, item, offHand) -> {
            if (!(e.getRightClicked() instanceof ItemFrame)) {
                e.setCancelled(true);
            }
        };
    }

    @Override
    public @Nonnull List<ItemStack> getDisplayRecipes() {
        List<ItemStack> recipes = new LinkedList<>();

        for (GoldPanDrop drop : drops) {
            if (drop.getValue() > 0) {
                recipes.add(new ItemStack(getInputMaterial()));
                recipes.add(drop.getOutput());
            }
        }

        return recipes;
    }

}
