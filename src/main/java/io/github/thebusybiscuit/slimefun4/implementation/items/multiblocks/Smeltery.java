package io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.IgnitionChamber;
import io.papermc.lib.PaperLib;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

public class Smeltery extends AbstractSmeltery {

    private final BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    private final ItemSetting<Integer> fireBreakingChance = new IntRangeSetting(this, "fire-breaking-chance", 0, 34, 100);

    @ParametersAreNonnullByDefault
    public Smeltery(ItemGroup category, SlimefunItemStack item) {
        super(category, item, new ItemStack[] { null, new ItemStack(Material.NETHER_BRICK_FENCE), null, new ItemStack(Material.NETHER_BRICKS), new CustomItemStack(Material.DISPENSER, "Dispenser (Facing up)"), new ItemStack(Material.NETHER_BRICKS), null, new ItemStack(Material.FLINT_AND_STEEL), null }, BlockFace.DOWN);

        addItemSetting(fireBreakingChance);
    }

    @Override
    protected void registerDefaultRecipes(@Nonnull List<ItemStack> recipes) {
        recipes.add(SlimefunItems.IRON_DUST);
        recipes.add(new ItemStack(Material.IRON_INGOT));
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < recipes.size() - 1; i += 2) {
            if (recipes.get(i) == null || Arrays.stream(recipes.get(i)).skip(1).anyMatch(Objects::nonNull)) {
                continue;
            }

            items.add(recipes.get(i)[0]);
            items.add(recipes.get(i + 1)[0]);
        }

        return items;
    }

    @Override
    protected void craft(Player p, Block b, Inventory inv, ItemStack[] recipe, ItemStack output, Inventory outputInv) {
        super.craft(p, b, inv, recipe, output, outputInv);

        if (ThreadLocalRandom.current().nextInt(100) < fireBreakingChance.getValue()) {
            consumeFire(p, b.getRelative(BlockFace.DOWN), b);
        }
    }

    @ParametersAreNonnullByDefault
    private void consumeFire(Player p, Block dispenser, Block b) {
        Inventory chamber = findIgnitionChamber(dispenser);

        if (chamber != null) {
            if (chamber.contains(Material.FLINT_AND_STEEL)) {
                ItemStack item = chamber.getItem(chamber.first(Material.FLINT_AND_STEEL));
                ItemMeta meta = item.getItemMeta();
                ((Damageable) meta).setDamage(((Damageable) meta).getDamage() + 1);
                item.setItemMeta(meta);

                if (((Damageable) item.getItemMeta()).getDamage() >= item.getType().getMaxDurability()) {
                    item.setAmount(0);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                }

                p.getWorld().playSound(p.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 1);
            } else {
                Slimefun.getLocalization().sendMessage(p, "machines.ignition-chamber-no-flint", true);

                Block fire = b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
                fire.getWorld().playEffect(fire.getLocation(), Effect.STEP_SOUND, fire.getType());
                fire.setType(Material.AIR);
            }
        } else {
            Block fire = b.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN);
            fire.getWorld().playEffect(fire.getLocation(), Effect.STEP_SOUND, fire.getType());
            fire.setType(Material.AIR);
        }
    }

    @Nullable
    private Inventory findIgnitionChamber(@Nonnull Block b) {
        for (BlockFace face : faces) {
            Block block = b.getRelative(face);

            if (block.getType() == Material.DROPPER && BlockStorage.check(block) instanceof IgnitionChamber) {
                BlockState state = PaperLib.getBlockState(b.getRelative(face), false).getState();

                if (state instanceof Dropper) {
                    return ((Dropper) state).getInventory();
                }
            }
        }

        return null;
    }

}
