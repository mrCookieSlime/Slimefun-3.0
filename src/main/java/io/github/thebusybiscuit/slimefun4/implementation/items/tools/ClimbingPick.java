package io.github.thebusybiscuit.slimefun4.implementation.items.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.events.ClimbingPickLaunchEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.settings.ClimbableSurface;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;

/**
 * The {@link ClimbingPick} launches you 1 block upwards when you right click
 * on a ice {@link Block}.
 * Every level of efficiency {@link Enchantment} increases the launch by 0.2 blocks.
 *
 * @author Linox
 * @author TheBusyBiscuit
 *
 */
public class ClimbingPick extends SimpleSlimefunItem<ItemUseHandler> implements DamageableItem, RecipeDisplayItem {

    private static final double STRONG_SURFACE_DEFAULT = 1.0;
    private static final double WEAK_SURFACE_DEFAULT = 0.6;
    private static final double MAX_DISTANCE = 4.4;
    private static final double EFFICIENCY_MODIFIER = 0.125;
    private static final long COOLDOWN = 4;

    private final ItemSetting<Boolean> dualWielding = new ItemSetting<>(this, "dual-wielding", true);
    private final ItemSetting<Boolean> damageOnUse = new ItemSetting<>(this, "damage-on-use", true);

    private final Map<Material, ClimbableSurface> surfaces = new EnumMap<>(Material.class);
    private final Set<UUID> users = new HashSet<>();

    @ParametersAreNonnullByDefault
    public ClimbingPick(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
        addItemSetting(dualWielding, damageOnUse);
        addDefaultSurfaces();
    }

    /**
     * This method adds every surface that is climbable by default.
     */
    protected void addDefaultSurfaces() {
        // These are "strong" surfaces, they will give you the biggest boost
        for (Material surface : SlimefunTag.CLIMBING_PICK_STRONG_SURFACES.getValues()) {
            addSurface(surface, STRONG_SURFACE_DEFAULT);
        }

        // These are "weak" surfaces, you can still climb them but they don't have
        // such a high boost as the "strong" surfaces
        for (Material surface : SlimefunTag.CLIMBING_PICK_WEAK_SURFACES.getValues()) {
            addSurface(surface, WEAK_SURFACE_DEFAULT);
        }
    }

    protected void addSurface(@Nonnull Material type, double defaultValue) {
        ClimbableSurface surface = new ClimbableSurface(this, type, defaultValue);
        addItemSetting(surface);
        surfaces.put(type, surface);
    }

    /**
     * This returns whether the {@link ClimbingPick} needs to be held in both
     * arms to work.
     * 
     * @return Whether dual wielding is enabled
     */
    public boolean isDualWieldingEnabled() {
        return dualWielding.getValue();
    }

    /**
     * This method returns a {@link Collection} of every {@link ClimbableSurface} the
     * {@link ClimbingPick} can climb.
     * 
     * @return A {@link Collection} of every {@link ClimbableSurface}
     */
    @Nonnull
    public Collection<ClimbableSurface> getClimbableSurfaces() {
        return surfaces.values();
    }

    /**
     * This returns the climbing speed for a given {@link Material}.
     * 
     * @param type
     *            The {@link Material}
     * 
     * @return The climbing speed for this {@link Material} or 0.
     */
    public double getClimbingSpeed(@Nonnull Material type) {
        Validate.notNull(type, "The surface cannot be null");
        ClimbableSurface surface = surfaces.get(type);

        if (surface != null) {
            return surface.getValue();
        } else {
            return 0;
        }
    }

    /**
     * This returns the climbing speed for a given {@link Material} and the used {@link ItemStack}.
     * 
     * @param item
     *            the {@link ClimbingPick}'s {@link ItemStack}
     * @param type
     *            The {@link Material}
     * 
     * @return The climbing speed or 0.
     */
    public double getClimbingSpeed(@Nonnull ItemStack item, @Nonnull Material type) {
        double speed = getClimbingSpeed(type);

        if (speed > 0) {
            int efficiencyLevel = item.getEnchantmentLevel(Enchantment.DIG_SPEED);

            if (efficiencyLevel > 0) {
                speed += efficiencyLevel * EFFICIENCY_MODIFIER;
            }
        }

        return speed;
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            if (!e.getClickedBlock().isPresent()) {
                return;
            }

            Block block = e.getClickedBlock().get();
            Player p = e.getPlayer();

            // Check if the Player is standing close enough to the wall
            if (p.getLocation().distanceSquared(block.getLocation().add(0.5, 0.5, 0.5)) > MAX_DISTANCE) {
                return;
            }

            // Check for dual wielding
            if (isDualWieldingEnabled() && !isItem(getOtherHandItem(p, e.getHand()))) {
                Slimefun.getLocalization().sendMessage(p, "messages.climbing-pick.dual-wielding");
                return;
            }

            // Top and bottom faces won't be allowed
            if (e.getClickedFace() != BlockFace.DOWN && e.getClickedFace() != BlockFace.UP) {
                climb(p, e.getHand(), e.getItem(), block);
            }
        };
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    private ItemStack getOtherHandItem(Player p, EquipmentSlot hand) {
        if (hand == EquipmentSlot.HAND) {
            return p.getInventory().getItemInOffHand();
        } else {
            return p.getInventory().getItemInMainHand();
        }
    }

    @ParametersAreNonnullByDefault
    private void climb(Player p, EquipmentSlot hand, ItemStack item, Block block) {
        double power = getClimbingSpeed(item, block.getType());

        if (power > 0.05) {
            // Prevent players from spamming this item by enforcing a cooldown
            if (users.add(p.getUniqueId())) {
                Slimefun.runSync(() -> users.remove(p.getUniqueId()), COOLDOWN);
                Vector velocity = new Vector(0, power, 0);
                ClimbingPickLaunchEvent event = new ClimbingPickLaunchEvent(p, velocity, this, item, block);
                Bukkit.getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    p.setVelocity(event.getVelocity());
                    p.setFallDistance(0);
                    swing(p, block, hand, item);
                }
            }
        } else if (!isDualWieldingEnabled() || hand == EquipmentSlot.HAND) {
            // We don't wanna send the message twice, so we check for dual wielding
            Slimefun.getLocalization().sendMessage(p, "messages.climbing-pick.wrong-material");
        }
    }

    @ParametersAreNonnullByDefault
    private void swing(Player p, Block b, EquipmentSlot hand, ItemStack item) {
        if (isDualWieldingEnabled()) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                damageItem(p, p.getInventory().getItemInMainHand());
                playAnimation(p, b, EquipmentSlot.HAND);
            } else {
                damageItem(p, p.getInventory().getItemInOffHand());
                playAnimation(p, b, EquipmentSlot.OFF_HAND);
            }
        } else {
            damageItem(p, item);
            playAnimation(p, b, hand);
        }
    }

    @Override
    public void damageItem(@Nonnull Player p, @Nullable ItemStack item) {
        if (p.getGameMode() != GameMode.CREATIVE) {
            DamageableItem.super.damageItem(p, item);
        }
    }

    @Override
    public boolean isDamageable() {
        return damageOnUse.getValue();
    }

    @ParametersAreNonnullByDefault
    private void playAnimation(Player p, Block b, EquipmentSlot hand) {
        MinecraftVersion version = Slimefun.getMinecraftVersion();

        if (version != MinecraftVersion.UNIT_TEST) {
            p.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, b.getType());

            if (version.isAtLeast(MinecraftVersion.MINECRAFT_1_15)) {
                if (hand == EquipmentSlot.HAND) {
                    p.swingMainHand();
                } else {
                    p.swingOffHand();
                }
            }
        }
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> display = new ArrayList<>();

        for (Material mat : surfaces.keySet()) {
            display.add(new ItemStack(mat));
        }

        return display;
    }

    @Override
    public String getLabelLocalPath() {
        return "guide.tooltips.recipes.climbing-pick";
    }
}
