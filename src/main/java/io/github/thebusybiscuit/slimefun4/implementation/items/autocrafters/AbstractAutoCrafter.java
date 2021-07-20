package io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.data.persistent.PersistentDataAPI;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.AutoCrafterListener;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AsyncRecipeChoiceTask;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;

/**
 * This is the abstract super class for our auto crafters.
 * 
 * @author TheBusyBiscuit
 * 
 * @see VanillaAutoCrafter
 * @see EnhancedAutoCrafter
 *
 */
public abstract class AbstractAutoCrafter extends SlimefunItem implements EnergyNetComponent {

    /**
     * The amount of energy consumed per crafting operation.
     */
    private int energyConsumed = -1;

    /**
     * The amount of energy this machine can store.
     */
    private int energyCapacity = -1;

    /**
     * The {@link NamespacedKey} used to store recipe data.
     */
    protected final NamespacedKey recipeStorageKey;

    /**
     * The {@link NamespacedKey} used to determine whether the recipe is enabled.
     */
    protected final NamespacedKey recipeEnabledKey;

    // @formatter:off
    protected final int[] background = {
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        9, 10, 14, 15, 16, 17,
        18, 19, 23, 25, 26,
        27, 28, 32, 33, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44
    };
    // @formatter:on

    @ParametersAreNonnullByDefault
    protected AbstractAutoCrafter(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        recipeStorageKey = new NamespacedKey(Slimefun.instance(), "recipe_key");
        recipeEnabledKey = new NamespacedKey(Slimefun.instance(), "recipe_enabled");

        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem item, Config data) {
                AbstractAutoCrafter.this.tick(b, data);
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        });
    }

    /**
     * This method handles our right-clicking behaviour.
     * <p>
     * Do not call this method directly, see our {@link AutoCrafterListener} for the intended
     * use case.
     * 
     * @param b
     *            The {@link Block} that was clicked
     * @param p
     *            The {@link Player} who clicked
     */
    @ParametersAreNonnullByDefault
    public void onRightClick(Block b, Player p) {
        Validate.notNull(b, "The Block must not be null!");
        Validate.notNull(p, "The Player cannot be null!");

        // Check if we have a valid chest below
        if (!isValidInventory(b.getRelative(BlockFace.DOWN))) {
            Slimefun.getLocalization().sendMessage(p, "messages.auto-crafting.missing-chest");
        } else if (Slimefun.getProtectionManager().hasPermission(p, b, Interaction.INTERACT_BLOCK)) {
            if (p.isSneaking()) {
                // Select a new recipe
                updateRecipe(b, p);
            } else {
                AbstractRecipe recipe = getSelectedRecipe(b);

                if (recipe == null) {
                    // Prompt the User to crouch
                    Slimefun.getLocalization().sendMessage(p, "messages.auto-crafting.select-a-recipe");
                } else {
                    // Show the current recipe
                    showRecipe(p, b, recipe);
                }
            }
        } else {
            Slimefun.getLocalization().sendMessage(p, "inventory.no-access");
        }
    }

    /**
     * This method performs one tick for the {@link AbstractAutoCrafter}.
     * 
     * @param b
     *            The block for this {@link AbstractAutoCrafter}
     * @param data
     *            The data stored on this block
     */
    protected void tick(@Nonnull Block b, @Nonnull Config data) {
        AbstractRecipe recipe = getSelectedRecipe(b);

        if (recipe == null || !recipe.isEnabled() || getCharge(b.getLocation(), data) < getEnergyConsumption()) {
            // No recipe / disabled recipe / no energy, abort...
            return;
        }

        // The block below where we would expect our inventory holder.
        Block targetBlock = b.getRelative(BlockFace.DOWN);

        // Make sure this is a Chest
        if (isValidInventory(targetBlock)) {
            BlockState state = PaperLib.getBlockState(targetBlock, false).getState();

            if (state instanceof InventoryHolder) {
                Inventory inv = ((InventoryHolder) state).getInventory();

                if (craft(inv, recipe)) {
                    // We are done crafting!
                    Location loc = b.getLocation().add(0.5, 0.8, 0.5);
                    b.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 6);
                    removeCharge(b.getLocation(), getEnergyConsumption());
                }
            }
        }
    }

    /**
     * This method checks if the given {@link Block} has a valid {@link Inventory}
     * where the Auto Crafter could be placed upon.
     * Right now this only supports chests and a few select tile entities but it can change or
     * be overridden in the future.
     * 
     * @param block
     *            The {@link Block} to check
     * 
     * @return Whether that {@link Block} has a valid {@link Inventory}
     */
    protected boolean isValidInventory(@Nonnull Block block) {
        Material type = block.getType();

        switch (type) {
            case CHEST:
            case TRAPPED_CHEST:
            case BARREL:
                return true;
            default:
                return SlimefunTag.SHULKER_BOXES.isTagged(type);
        }
    }

    /**
     * This method returns the currently selected {@link AbstractRecipe} for the given
     * {@link Block}.
     * 
     * @param b
     *            The {@link Block}
     * 
     * @return The currently selected {@link AbstractRecipe} or null
     */
    @Nullable
    public abstract AbstractRecipe getSelectedRecipe(@Nonnull Block b);

    /**
     * This method is called when a {@link Player} right clicks the {@link AbstractAutoCrafter}
     * while holding the shift button.
     * Use it to choose the {@link AbstractRecipe}.
     * 
     * @param b
     *            The {@link Block} which was clicked
     * @param p
     *            The {@link Player} who clicked
     */
    protected abstract void updateRecipe(@Nonnull Block b, @Nonnull Player p);

    /**
     * This method sets the selected {@link AbstractRecipe} for the given {@link Block}.
     * The recipe will be stored using the {@link PersistentDataAPI}.
     * 
     * @param b
     *            The {@link Block} to store the data on
     * @param recipe
     *            The {@link AbstractRecipe} to select
     */
    protected void setSelectedRecipe(@Nonnull Block b, @Nullable AbstractRecipe recipe) {
        Validate.notNull(b, "The Block cannot be null!");

        BlockStateSnapshotResult result = PaperLib.getBlockState(b, false);
        BlockState state = result.getState();

        if (state instanceof Skull) {
            if (recipe == null) {
                // Clear the value from persistent data storage
                PersistentDataAPI.remove((Skull) state, recipeStorageKey);

                // Also remove the "enabled" state since this should be per-recipe.
                PersistentDataAPI.remove((Skull) state, recipeEnabledKey);
            } else {
                // Store the value to persistent data storage
                PersistentDataAPI.setString((Skull) state, recipeStorageKey, recipe.toString());
            }

            // Fixes #2899 - Update the BlockState if necessary
            if (result.isSnapshot()) {
                state.update(true, false);
            }
        }
    }

    /**
     * This shows the given {@link AbstractRecipe} to the {@link Player} in a preview window.
     * 
     * @param p
     *            The {@link Player}
     * @param b
     *            The {@link Block} of the {@link AbstractAutoCrafter}
     * @param recipe
     *            The {@link AbstractRecipe} to show them
     */
    @ParametersAreNonnullByDefault
    protected void showRecipe(Player p, Block b, AbstractRecipe recipe) {
        Validate.notNull(p, "The Player should not be null");
        Validate.notNull(b, "The Block should not be null");
        Validate.notNull(recipe, "The Recipe should not be null");

        ChestMenu menu = new ChestMenu(getItemName());
        menu.setPlayerInventoryClickable(false);
        menu.setEmptySlotsClickable(false);

        ChestMenuUtils.drawBackground(menu, background);
        ChestMenuUtils.drawBackground(menu, 45, 46, 47, 48, 50, 51, 52, 53);

        if (recipe.isEnabled()) {
            menu.addItem(49, new CustomItemStack(Material.BARRIER, Slimefun.getLocalization().getMessages(p, "messages.auto-crafting.tooltips.enabled")));
            menu.addMenuClickHandler(49, (pl, item, slot, action) -> {
                if (action.isRightClicked()) {
                    deleteRecipe(pl, b);
                } else {
                    setRecipeEnabled(pl, b, false);
                }

                return false;
            });
        } else {
            menu.addItem(49, new CustomItemStack(HeadTexture.EXCLAMATION_MARK.getAsItemStack(), Slimefun.getLocalization().getMessages(p, "messages.auto-crafting.tooltips.disabled")));
            menu.addMenuClickHandler(49, (pl, item, slot, action) -> {
                if (action.isRightClicked()) {
                    deleteRecipe(pl, b);
                } else {
                    setRecipeEnabled(pl, b, true);
                }

                return false;
            });
        }

        // This makes the slots cycle through different ingredients
        AsyncRecipeChoiceTask task = new AsyncRecipeChoiceTask();
        recipe.show(menu, task);
        menu.open(p);

        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

        // Only schedule the task if necessary
        if (!task.isEmpty()) {
            task.start(menu.toInventory());
        }
    }

    @ParametersAreNonnullByDefault
    private void setRecipeEnabled(Player p, Block b, boolean enabled) {
        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        BlockState state = PaperLib.getBlockState(b, false).getState();

        // Make sure the block is still a Skull
        if (state instanceof Skull) {
            if (enabled) {
                PersistentDataAPI.remove((Skull) state, recipeEnabledKey);
                Slimefun.getLocalization().sendMessage(p, "messages.auto-crafting.re-enabled");
            } else {
                PersistentDataAPI.setByte((Skull) state, recipeEnabledKey, (byte) 1);
                Slimefun.getLocalization().sendMessage(p, "messages.auto-crafting.temporarily-disabled");
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void deleteRecipe(Player p, Block b) {
        setSelectedRecipe(b, null);
        p.closeInventory();
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        Slimefun.getLocalization().sendMessage(p, "messages.auto-crafting.recipe-removed");
    }

    /**
     * This method checks whether the given {@link Predicate} matches the provided {@link ItemStack}.
     * 
     * @param item
     *            The {@link ItemStack} to check
     * @param predicate
     *            The {@link Predicate}
     * 
     * @return Whether the {@link Predicate} matches the {@link ItemStack}
     */
    @ParametersAreNonnullByDefault
    protected boolean matches(ItemStack item, Predicate<ItemStack> predicate) {
        return predicate.test(item);
    }

    @ParametersAreNonnullByDefault
    protected boolean matchesAny(Inventory inv, Map<Integer, Integer> itemQuantities, Predicate<ItemStack> predicate) {
        ItemStack[] contents = inv.getContents();

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];

            if (item != null) {
                int amount = itemQuantities.getOrDefault(slot, item.getAmount());

                if (amount > 0 && matches(item, predicate)) {
                    // Update our local quantity map
                    itemQuantities.put(slot, amount - 1);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This method performs a crafting operation.
     * It will attempt to fulfill the provided {@link AbstractRecipe} using
     * the given {@link Inventory}.
     * This will consume items and add the result to the {@link Inventory}.
     * This method does not handle energy consumption.
     * 
     * @param inv
     *            The {@link Inventory} to take resources from
     * @param recipe
     *            The {@link AbstractRecipe} to craft
     * 
     * @return Whether this crafting operation was successful or not
     */
    public boolean craft(@Nonnull Inventory inv, @Nonnull AbstractRecipe recipe) {
        Validate.notNull(inv, "The Inventory must not be null");
        Validate.notNull(recipe, "The Recipe shall not be null");

        // Make sure that the Recipe is actually enabled
        if (!recipe.isEnabled()) {
            return false;
        }

        // Check if we have an empty slot
        if (inv.firstEmpty() != -1) {
            Map<Integer, Integer> itemQuantities = new HashMap<>();

            for (Predicate<ItemStack> predicate : recipe.getIngredients()) {
                // Check if any Item matches the Predicate
                if (!matchesAny(inv, itemQuantities, predicate)) {
                    return false;
                }
            }

            List<ItemStack> leftoverItems = new ArrayList<>();

            // Remove ingredients
            for (Map.Entry<Integer, Integer> entry : itemQuantities.entrySet()) {
                ItemStack item = inv.getItem(entry.getKey());

                // Double-check to be extra safe
                if (item != null) {
                    // Handle leftovers
                    ItemStack leftover = getLeftoverItem(item);

                    if (leftover != null) {
                        // Account for the amount of removed items
                        leftover.setAmount(item.getAmount() - entry.getValue());
                        leftoverItems.add(leftover);
                    }

                    // Update the item amount
                    item.setAmount(entry.getValue());
                }
            }

            boolean success = inv.addItem(recipe.getResult().clone()).isEmpty();

            if (success) {
                // Fixes #2926 - Push leftover items to the inventory.
                for (ItemStack leftoverItem : leftoverItems) {
                    inv.addItem(leftoverItem);
                }
            }

            return success;
        }

        return false;
    }

    /**
     * This method returns the "leftovers" from a crafting operation.
     * The method functions very similarly to {@link Material#getCraftingRemainingItem()}.
     * However we cannot use this method as it is only available in the latest 1.16 snapshots
     * of Spigot, not even on earlier 1.16 builds...
     * But this gives us more control over the leftovers anyway!
     * 
     * @param item
     *            The {@link ItemStack} that is being consumed
     * 
     * @return The leftover item or null if the item is fully consumed
     */
    @Nullable
    private ItemStack getLeftoverItem(@Nonnull ItemStack item) {
        Material type = item.getType();

        switch (type) {
            case WATER_BUCKET:
            case LAVA_BUCKET:
            case MILK_BUCKET:
                return new ItemStack(Material.BUCKET);
            case DRAGON_BREATH:
            case POTION:
                return new ItemStack(Material.GLASS_BOTTLE);
            default:
                MinecraftVersion minecraftVersion = Slimefun.getMinecraftVersion();

                // Honey does not exist in 1.14
                if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_15) && type == Material.HONEY_BOTTLE) {
                    return new ItemStack(Material.GLASS_BOTTLE);
                } else {
                    return null;
                }
        }
    }

    /**
     * This method returns the max amount of electricity this machine can hold.
     * 
     * @return The max amount of electricity this Block can store.
     */
    @Override
    public int getCapacity() {
        return energyCapacity;
    }

    /**
     * This method returns the amount of energy that is consumed per operation.
     * 
     * @return The rate of energy consumption
     */
    public int getEnergyConsumption() {
        return energyConsumed;
    }

    /**
     * This sets the energy capacity for this machine.
     * This method <strong>must</strong> be called before registering the item
     * and only before registering.
     * 
     * @param capacity
     *            The amount of energy this machine can store
     * 
     * @return This method will return the current instance of {@link AContainer}, so that it can be chained.
     */
    @Nonnull
    public final AbstractAutoCrafter setCapacity(int capacity) {
        Validate.isTrue(capacity > 0, "The capacity must be greater than zero!");

        if (getState() == ItemState.UNREGISTERED) {
            this.energyCapacity = capacity;
            return this;
        } else {
            throw new IllegalStateException("You cannot modify the capacity after the Item was registered.");
        }
    }

    /**
     * This method sets the energy consumed by this machine per tick.
     * 
     * @param energyConsumption
     *            The energy consumed per tick
     * 
     * @return This method will return the current instance of {@link AContainer}, so that it can be chained.
     */
    @Nonnull
    public final AbstractAutoCrafter setEnergyConsumption(int energyConsumption) {
        Validate.isTrue(energyConsumption > 0, "The energy consumption must be greater than zero!");
        Validate.isTrue(energyCapacity > 0, "You must specify the capacity before you can set the consumption amount.");
        Validate.isTrue(energyConsumption <= energyCapacity, "The energy consumption cannot be higher than the capacity (" + energyCapacity + ')');

        this.energyConsumed = energyConsumption;
        return this;
    }

    @Override
    public void register(@Nonnull SlimefunAddon addon) {
        Validate.notNull(addon, "A SlimefunAddon cannot be null!");
        this.addon = addon;

        if (getCapacity() <= 0) {
            warn("The capacity has not been configured correctly. The Item was disabled.");
            warn("Make sure to call '" + getClass().getSimpleName() + "#setEnergyCapacity(...)' before registering!");
        }

        if (getEnergyConsumption() <= 0) {
            warn("The energy consumption has not been configured correctly. The Item was disabled.");
            warn("Make sure to call '" + getClass().getSimpleName() + "#setEnergyConsumption(...)' before registering!");
        }

        if (getCapacity() > 0 && getEnergyConsumption() > 0) {
            super.register(addon);
        }
    }

    @Override
    public final EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }
}
