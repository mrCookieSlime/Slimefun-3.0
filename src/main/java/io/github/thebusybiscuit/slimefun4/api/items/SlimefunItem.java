package io.github.thebusybiscuit.slimefun4.api.items;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import io.github.bakedlibs.dough.collections.OptionalMap;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.SlimefunBranch;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IdConflictException;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.exceptions.MissingDependencyException;
import io.github.thebusybiscuit.slimefun4.api.exceptions.UnregisteredItemException;
import io.github.thebusybiscuit.slimefun4.api.exceptions.WrongItemStackException;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.SlimefunRegistry;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotConfigurable;
import io.github.thebusybiscuit.slimefun4.core.attributes.Placeable;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.core.attributes.Rechargeable;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuide;
import io.github.thebusybiscuit.slimefun4.core.handlers.GlobalItemHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoDisenchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;

/**
 * A {@link SlimefunItem} is a custom item registered by a {@link SlimefunAddon}.
 * This class defines the behaviours of the item, you can assign an {@link ItemHandler}
 * to give the item functionality.
 * In contrast to that the {@link SlimefunItemStack} defines the look and feel of the item.
 * 
 * Remember to call {@link #register(SlimefunAddon)} on your {@link SlimefunItem} for it
 * to appear in the {@link SlimefunGuide}.
 * 
 * @author TheBusyBiscuit
 * @author Poslovitch
 * 
 * @see SlimefunItemStack
 * @see SlimefunAddon
 *
 */
public class SlimefunItem implements Placeable {

    /**
     * This is our item id.
     */
    private final String id;

    /**
     * This is the original {@link ItemStack} that represents this item.
     * It is immutable and should always be cloned, never used directly.
     */
    private final ItemStack itemStackTemplate;

    /**
     * This is a reference to the {@link SlimefunAddon} that registered this
     * {@link SlimefunItem}, if the item has not been registered yet, it will be null.
     */
    protected SlimefunAddon addon;

    /**
     * This is the state of this {@link SlimefunItem}.
     */
    private ItemState state = ItemState.UNREGISTERED;

    /**
     * This is the {@link ItemGroup} in which this {@link SlimefunItem} can be found.
     */
    private ItemGroup itemGroup;

    /**
     * This is a reference to the associated {@link Research}, can be null.
     */
    private Research research;

    private ItemStack[] recipe;
    private RecipeType recipeType;
    protected ItemStack recipeOutput;

    protected boolean enchantable = true;
    protected boolean disenchantable = true;
    protected boolean hidden = false;
    protected boolean useableInWorkbench = false;

    private Optional<String> wikiURL = Optional.empty();

    private final OptionalMap<Class<? extends ItemHandler>, ItemHandler> itemhandlers = new OptionalMap<>(HashMap::new);
    private final Set<ItemSetting<?>> itemSettings = new HashSet<>();

    private boolean ticking = false;
    private BlockTicker blockTicker;

    /**
     * This creates a new {@link SlimefunItem} from the given arguments.
     * 
     * @param itemGroup
     *            The {@link ItemGroup} this {@link SlimefunItem} belongs to
     * @param item
     *            The {@link SlimefunItemStack} that describes the visual features of our {@link SlimefunItem}
     * @param recipeType
     *            the {@link RecipeType} that determines how this {@link SlimefunItem} is crafted
     * @param recipe
     *            An Array representing the recipe of this {@link SlimefunItem}
     */
    @ParametersAreNonnullByDefault
    public SlimefunItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        this(itemGroup, item, recipeType, recipe, null);
    }

    /**
     * This creates a new {@link SlimefunItem} from the given arguments.
     * 
     * @param itemGroup
     *            The {@link ItemGroup} this {@link SlimefunItem} belongs to
     * @param item
     *            The {@link SlimefunItemStack} that describes the visual features of our {@link SlimefunItem}
     * @param recipeType
     *            the {@link RecipeType} that determines how this {@link SlimefunItem} is crafted
     * @param recipe
     *            An Array representing the recipe of this {@link SlimefunItem}
     * @param recipeOutput
     *            The result of crafting this item
     */
    @ParametersAreNonnullByDefault
    public SlimefunItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, @Nullable ItemStack recipeOutput) {
        Validate.notNull(itemGroup, "'itemGroup' is not allowed to be null!");
        Validate.notNull(item, "'item' is not allowed to be null!");
        Validate.notNull(recipeType, "'recipeType' is not allowed to be null!");

        this.itemGroup = itemGroup;
        this.itemStackTemplate = item;
        this.id = item.getItemId();
        this.recipeType = recipeType;
        this.recipe = recipe;
        this.recipeOutput = recipeOutput;
    }

    // Previously deprecated constructor, now only for internal purposes
    @ParametersAreNonnullByDefault
    protected SlimefunItem(ItemGroup itemGroup, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
        Validate.notNull(itemGroup, "'itemGroup' is not allowed to be null!");
        Validate.notNull(item, "'item' is not allowed to be null!");
        Validate.notNull(id, "'id' is not allowed to be null!");
        Validate.notNull(recipeType, "'recipeType' is not allowed to be null!");

        this.itemGroup = itemGroup;
        this.itemStackTemplate = item;
        this.id = id;
        this.recipeType = recipeType;
        this.recipe = recipe;
    }

    /**
     * Returns the identifier of this {@link SlimefunItem}.
     *
     * @return the identifier of this {@link SlimefunItem}
     */
    public final @Nonnull String getId() {
        return id;
    }

    /**
     * This method returns the {@link ItemState} this {@link SlimefunItem}
     * is currently in. This can be used to determine whether a {@link SlimefunItem}
     * is enabled or disabled.
     * 
     * {@link VanillaItem} represents a special case here.
     * 
     * @return The {@link ItemState} of this {@link SlimefunItem}
     */
    public @Nonnull ItemState getState() {
        return state;
    }

    /**
     * This returns the {@link ItemStack} of this {@link SlimefunItem}.
     * The {@link ItemStack} describes the look and feel of this {@link SlimefunItem}.
     * 
     * @return The {@link ItemStack} that this {@link SlimefunItem} represents
     */
    public @Nonnull ItemStack getItem() {
        return itemStackTemplate;
    }

    /**
     * This returns the {@link ItemGroup} of our {@link SlimefunItem}, every {@link SlimefunItem}
     * is associated with exactly one {@link ItemGroup}.
     * 
     * @return The {@link ItemGroup} that this {@link SlimefunItem} belongs to
     */
    public @Nonnull ItemGroup getItemGroup() {
        return itemGroup;
    }

    public @Nonnull ItemStack[] getRecipe() {
        return recipe;
    }

    /**
     * This method returns the {@link RecipeType}.
     * The {@link RecipeType} determines how this {@link SlimefunItem} is crafted.
     * 
     * @return The {@link RecipeType} of this {@link SlimefunItem}
     */
    public @Nonnull RecipeType getRecipeType() {
        return recipeType;
    }

    /**
     * This method returns the result of crafting this {@link SlimefunItem}
     * 
     * @return The recipe output of this {@link SlimefunItem}
     */
    public @Nonnull ItemStack getRecipeOutput() {
        return recipeOutput != null ? recipeOutput.clone() : itemStackTemplate.clone();
    }

    /**
     * This method returns the {@link Research} this {@link SlimefunItem} is linked to.
     * This will be null if the item is not linked to any {@link Research}
     * 
     * @return The linked {@link Research} or null
     */
    public final @Nullable Research getResearch() {
        return research;
    }

    /**
     * This returns whether this {@link SlimefunItem} has a {@link Research}
     * assigned to it.
     * It is equivalent to a null check performed on {@link #getResearch()}.
     * 
     * @return Whether this {@link SlimefunItem} has a {@link Research}
     */
    public final boolean hasResearch() {
        return research != null;
    }

    /**
     * This returns a {@link Set} containing all instances of {@link ItemSetting} for this {@link SlimefunItem}.
     * 
     * @return A {@link Set} of every {@link ItemSetting} for this {@link SlimefunItem}
     */
    public @Nonnull Set<ItemSetting<?>> getItemSettings() {
        return itemSettings;
    }

    /**
     * This method returns an {@link Optional} holding an {@link ItemSetting} with the given
     * key and data type. Or an empty {@link Optional} if this {@link SlimefunItem} has no such {@link ItemSetting}.
     * 
     * @param <T>
     *            The Type of value stored in this {@link ItemSetting}
     * @param key
     *            The key of this {@link ItemSetting}
     * @param c
     *            The {@link Class} of the type of value stored by this setting
     * 
     * @return An {@link Optional} describing the result
     */
    @SuppressWarnings("unchecked")
    public @Nonnull <T> Optional<ItemSetting<T>> getItemSetting(@Nonnull String key, @Nonnull Class<T> c) {
        for (ItemSetting<?> setting : itemSettings) {
            if (setting.getKey().equals(key) && setting.isType(c)) {
                return Optional.of((ItemSetting<T>) setting);
            }
        }

        return Optional.empty();
    }

    /**
     * This returns whether or not this {@link SlimefunItem} is allowed to be used in
     * an {@link AutoEnchanter}.
     * 
     * @return Whether this {@link SlimefunItem} can be enchanted.
     */
    public boolean isEnchantable() {
        return enchantable;
    }

    /**
     * This returns whether or not this {@link SlimefunItem} is allowed to be used in
     * an {@link AutoDisenchanter}.
     * 
     * @return Whether this {@link SlimefunItem} can be disenchanted.
     */
    public boolean isDisenchantable() {
        return disenchantable;
    }

    /**
     * This method returns whether this {@link SlimefunItem} was hidden from the
     * {@link SlimefunGuide}.
     * 
     * @return Whether this {@link SlimefunItem} is hidden.
     */
    public final boolean isHidden() {
        return hidden;
    }

    /**
     * This method will forcefully hide this {@link SlimefunItem} from the {@link SlimefunGuide}.
     * 
     * @param hidden
     *            Whether to hide this {@link SlimefunItem} or not
     */
    public void setHidden(boolean hidden) {
        if (this.hidden != hidden) {
            this.hidden = hidden;

            if (state == ItemState.ENABLED) {
                if (hidden) {
                    itemGroup.remove(this);
                } else {
                    itemGroup.add(this);
                }
            }
        }
    }

    /**
     * This method returns whether this {@link SlimefunItem} is disabled.
     * 
     * @return Whether this {@link SlimefunItem} is disabled.
     */
    public boolean isDisabled() {
        if (state == ItemState.UNREGISTERED) {
            error("isDisabled() cannot be called before registering the item", new UnregisteredItemException(this));
            return false;
        }

        return state != ItemState.ENABLED;
    }

    /**
     * This method returns whether this {@link SlimefunItem} is disabled
     * for that specific {@link World}.
     * Note that if the item is disabled globally, this method will still return false.
     * 
     * @param world
     *            The {@link World} to check
     * 
     * @return Whether this {@link SlimefunItem} is disabled in that world (or in general).
     */
    public boolean isDisabledIn(@Nonnull World world) {
        if (state == ItemState.UNREGISTERED) {
            error("isDisabled(World) cannot be called before registering the item", new UnregisteredItemException(this));
            return false;
        }

        // Check if the Item is disabled globally or in this specific world
        return isDisabled() || !Slimefun.getWorldSettingsService().isEnabled(world, this);
    }

    /**
     * This method returns the {@link SlimefunAddon} that registered this
     * {@link SlimefunItem}. If this Item is from Slimefun itself, the current
     * instance of {@link Slimefun} will be returned.
     * Use an instanceof check to account for that.
     * 
     * @return The {@link SlimefunAddon} that registered this {@link SlimefunItem}
     */
    public final @Nonnull SlimefunAddon getAddon() {
        if (addon == null) {
            throw new UnregisteredItemException(this);
        }

        return addon;
    }

    public BlockTicker getBlockTicker() {
        return blockTicker;
    }

    /**
     * This method registers this {@link SlimefunItem}.
     * Always call this method after your {@link SlimefunItem} has been initialized.
     * Never call it more than once!
     * 
     * @param addon
     *            The {@link SlimefunAddon} that this {@link SlimefunItem} belongs to.
     */
    public void register(@Nonnull SlimefunAddon addon) {
        Validate.notNull(addon, "A SlimefunAddon cannot be null!");
        Validate.notNull(addon.getJavaPlugin(), "SlimefunAddon#getJavaPlugin() is not allowed to return null!");

        this.addon = addon;

        try {
            checkDependencies(addon);
            checkForConflicts();

            preRegister();

            if (recipe == null) {
                recipe = new ItemStack[9];
            } else if (recipe.length < 9) {
                recipe = Arrays.copyOf(recipe, 9);
            }

            Slimefun.getRegistry().getAllSlimefunItems().add(this);
            Slimefun.getRegistry().getSlimefunItemIds().put(id, this);

            // Items that are "not-configurable" cannot be configured.
            if (!(this instanceof NotConfigurable)) {
                Slimefun.getItemCfg().setDefaultValue(id + ".enabled", true);
                Slimefun.getItemCfg().setDefaultValue(id + ".can-be-used-in-workbenches", useableInWorkbench);
                Slimefun.getItemCfg().setDefaultValue(id + ".hide-in-guide", hidden);
                Slimefun.getItemCfg().setDefaultValue(id + ".allow-enchanting", enchantable);
                Slimefun.getItemCfg().setDefaultValue(id + ".allow-disenchanting", disenchantable);

                // Load all item settings
                for (ItemSetting<?> setting : itemSettings) {
                    setting.reload();
                }
            }

            if (ticking && !Slimefun.getCfg().getBoolean("URID.enable-tickers")) {
                state = ItemState.DISABLED;
                return;
            }

            if (this instanceof NotConfigurable) {
                /*
                 * Not-configurable items will be enabled.
                 * Any other settings will remain as default.
                 */
                state = ItemState.ENABLED;
            } else if (Slimefun.getItemCfg().getBoolean(id + ".enabled")) {
                // The item has been enabled.
                state = ItemState.ENABLED;
                useableInWorkbench = Slimefun.getItemCfg().getBoolean(id + ".can-be-used-in-workbenches");
                hidden = Slimefun.getItemCfg().getBoolean(id + ".hide-in-guide");
                enchantable = Slimefun.getItemCfg().getBoolean(id + ".allow-enchanting");
                disenchantable = Slimefun.getItemCfg().getBoolean(id + ".allow-disenchanting");
            } else if (this instanceof VanillaItem) {
                // This item is a vanilla "mock" but was disabled.
                state = ItemState.VANILLA_FALLBACK;
            } else {
                // The item has been disabled.
                state = ItemState.DISABLED;
            }

            // Now we can be certain this item should be enabled
            if (state == ItemState.ENABLED) {
                onEnable();
            }

            // Lock the SlimefunItemStack from any accidental manipulations
            if (itemStackTemplate instanceof SlimefunItemStack && isItemStackImmutable()) {
                ((SlimefunItemStack) itemStackTemplate).lock();
            }

            postRegister();

            // handle runtime-registrations / auto-loading
            if (Slimefun.getRegistry().isAutoLoadingEnabled() && state == ItemState.ENABLED) {
                info("Item was registered during runtime.");
                load();
            }
        } catch (Exception x) {
            error("Registering " + toString() + " has failed!", x);
        }
    }

    /**
     * This method is called when this {@link SlimefunItem} is currently being registered
     * and we are certain that it will be enabled.
     * 
     * <strong>This method is for internal purposes, like {@link ItemGroup} registration only</strong>
     */
    private final void onEnable() {
        // Register the Category too if it hasn't been registered yet
        if (!itemGroup.isRegistered()) {
            itemGroup.register(addon);
        }

        // Send out deprecation warnings for any classes or interfaces
        checkForDeprecations(getClass());

        // Check for an illegal stack size
        if (itemStackTemplate.getAmount() != 1) {
            // @formatter:off
            warn("This item has an illegal stack size: " + itemStackTemplate.getAmount()
                + ". An Item size of 1 is recommended. Please inform the author(s) of " + addon.getName()
                + " to fix this. Crafting Results with amounts of higher should be handled"
                + " via the recipeOutput parameter!");
            // @formatter:on
        }

        // Add it to the list of enabled items
        Slimefun.getRegistry().getEnabledSlimefunItems().add(this);

        // Load our Item Handlers
        loadItemHandlers();

        // Properly mark this Item as radioactive
        if (this instanceof Radioactive) {
            Slimefun.getRegistry().getRadioactiveItems().add(this);
        }
    }

    private void loadItemHandlers() {
        for (ItemHandler handler : itemhandlers.values()) {
            Optional<IncompatibleItemHandlerException> exception = handler.validate(this);

            // Check if the validation caused an exception.
            if (exception.isPresent()) {
                throw exception.get();
            } else {
                /*
                 * Make developers or at least Server admins aware that an Item
                 * is using a deprecated ItemHandler
                 */
                checkForDeprecations(handler.getClass());
            }

            /*
             * If this ItemHandler is "global" (not bound to this SlimefunItem),
             * we add it to the list of global Item handlers
             */
            if (handler instanceof GlobalItemHandler) {
                SlimefunRegistry registry = Slimefun.getRegistry();
                registry.getGlobalItemHandlers(handler.getIdentifier()).add(handler);
            }
        }
    }

    /**
     * This method returns whether the original {@link SlimefunItemStack} of this
     * {@link SlimefunItem} is immutable.
     * 
     * If <code>true</code> is returned, then any changes to the original {@link SlimefunItemStack}
     * will be rejected with a {@link WrongItemStackException}.
     * This ensures integrity so developers don't accidentally damage the wrong {@link ItemStack}.
     * 
     * @return Whether the original {@link SlimefunItemStack} is immutable.
     */
    protected boolean isItemStackImmutable() {
        return true;
    }

    /**
     * This method checks if the dependencies have been set up correctly.
     * 
     * @param addon
     *            The {@link SlimefunAddon} trying to register this {@link SlimefunItem}
     */
    private void checkDependencies(@Nonnull SlimefunAddon addon) {
        if (!addon.hasDependency("Slimefun")) {
            throw new MissingDependencyException(addon, "Slimefun");
        }
    }

    /**
     * This method checks for id conflicts.
     */
    private void checkForConflicts() {
        SlimefunItem conflictingItem = getById(id);

        if (conflictingItem != null) {
            throw new IdConflictException(this, conflictingItem);
        }
    }

    /**
     * This method checks recursively for all {@link Class} parents to look for any {@link Deprecated}
     * elements.
     * 
     * If a {@link Deprecated} element was found, a warning message will be printed.
     * 
     * @param c
     *            The {@link Class} from which to start this operation.
     */
    private void checkForDeprecations(@Nullable Class<?> c) {
        if (Slimefun.getUpdater().getBranch() == SlimefunBranch.DEVELOPMENT) {
            /*
             * This method is currently way too spammy with all the restructuring going on...
             * Since DEV builds are anyway under "development", things may be relocated.
             * So we fire these only for stable versions, since devs should update then, so
             * it's the perfect moment to tell them to act.
             */
            return;
        }

        /*
         * We do not wanna throw an Exception here since this could also mean that.
         * We have reached the end of the Class hierarchy
         */
        if (c != null) {
            // Check if this Class is deprecated
            if (c.isAnnotationPresent(Deprecated.class)) {
                warn("The inherited Class \"" + c.getName() + "\" has been deprecated. Check the documentation for more details!");
            }

            for (Class<?> parent : c.getInterfaces()) {
                // Check if this Interface is deprecated
                if (parent.isAnnotationPresent(Deprecated.class)) {
                    warn("The implemented Interface \"" + parent.getName() + "\" has been deprecated. Check the documentation for more details!");
                }
            }

            // Recursively lookup the super class
            checkForDeprecations(c.getSuperclass());
        }
    }

    /**
     * This method will set the {@link Research} of this {@link SlimefunItem}.
     * You don't have to call this method if your {@link SlimefunItem} was linked to your {@link Research}
     * using {@link Research#addItems(SlimefunItem...)}
     * 
     * @param research
     *            The new {@link Research} for this {@link SlimefunItem}, or null
     */
    public void setResearch(@Nullable Research research) {
        if (this.research != null) {
            this.research.getAffectedItems().remove(this);
        }

        if (research != null) {
            research.getAffectedItems().add(this);
        }

        this.research = research;
    }

    public void setRecipe(ItemStack[] recipe) {
        if (recipe == null || recipe.length != 9) {
            throw new IllegalArgumentException("Recipes must be of length 9");
        }

        this.recipe = recipe;
    }

    public void setRecipeType(@Nonnull RecipeType type) {
        Validate.notNull(type, "The RecipeType is not allowed to be null!");
        this.recipeType = type;
    }

    /**
     * This sets the {@link ItemGroup} in which this {@link SlimefunItem} will be displayed.
     * 
     * @param category
     *            The new {@link ItemGroup}
     */
    public void setCategory(@Nonnull ItemGroup category) {
        Validate.notNull(category, "The Category is not allowed to be null!");

        this.itemGroup.remove(this);
        category.add(this);

        this.itemGroup = category;
    }

    /**
     * This method will set the result of crafting this {@link SlimefunItem}.
     * If null is passed, then it will use the default item as the recipe result.
     * 
     * @param output
     *            The {@link ItemStack} that will be the result of crafting this {@link SlimefunItem}
     */
    public void setRecipeOutput(@Nullable ItemStack output) {
        this.recipeOutput = output;
    }

    /**
     * This method returns whether or not this {@link SlimefunItem} is allowed to
     * be used in a Crafting Table.
     * 
     * Items of type {@link VanillaItem} may be used in workbenches for example.
     * 
     * @see #setUseableInWorkbench(boolean)
     * 
     * @return Whether this {@link SlimefunItem} may be used in a Workbench.
     */
    public boolean isUseableInWorkbench() {
        return useableInWorkbench;
    }

    /**
     * This sets whether or not this {@link SlimefunItem} is allowed to be
     * used in a normal Crafting Table.
     * 
     * @param useable
     *            Whether this {@link SlimefunItem} should be useable in a workbench
     * 
     * @return This instance of {@link SlimefunItem}
     */
    public @Nonnull SlimefunItem setUseableInWorkbench(boolean useable) {
        this.useableInWorkbench = useable;

        return this;
    }

    /**
     * This method checks whether the provided {@link ItemStack} represents
     * this {@link SlimefunItem}.
     * 
     * @param item
     *            The {@link ItemStack} to compare
     * 
     * @return Whether the given {@link ItemStack} represents this {@link SlimefunItem}
     */
    public boolean isItem(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        // If the given item is a SlimefunitemStack, simply compare the id
        if (item instanceof SlimefunItemStack) {
            return getId().equals(((SlimefunItemStack) item).getItemId());
        }

        if (item.hasItemMeta()) {
            Optional<String> itemId = Slimefun.getItemDataService().getItemData(item);

            if (itemId.isPresent()) {
                return getId().equals(itemId.get());
            }
        }

        // Backwards compatibility
        if (Slimefun.getRegistry().isBackwardsCompatible()) {
            boolean loreInsensitive = this instanceof Rechargeable || this instanceof SlimefunBackpack || id.equals("BROKEN_SPAWNER") || id.equals("REINFORCED_SPAWNER");
            return SlimefunUtils.isItemSimilar(item, this.itemStackTemplate, !loreInsensitive);
        } else {
            return false;
        }
    }

    /**
     * This method is used for internal purposes only.
     */
    public void load() {
        if (!hidden) {
            itemGroup.add(this);
        }

        recipeType.register(recipe, getRecipeOutput());
    }

    /**
     * This method will add any given {@link ItemHandler} to this {@link SlimefunItem}.
     * Note that this will not work after the {@link SlimefunItem} was registered.
     * 
     * @param handlers
     *            Any {@link ItemHandler} that should be added to this {@link SlimefunItem}
     */
    public final void addItemHandler(ItemHandler... handlers) {
        Validate.notEmpty(handlers, "You cannot add zero handlers...");
        Validate.noNullElements(handlers, "You cannot add any 'null' ItemHandler!");

        // Make sure they are added before the item was registered.
        if (state != ItemState.UNREGISTERED) {
            throw new UnsupportedOperationException("You cannot add an ItemHandler after the SlimefunItem was registered.");
        }

        for (ItemHandler handler : handlers) {
            itemhandlers.put(handler.getIdentifier(), handler);

            // Tickers are a special case (at the moment at least)
            if (handler instanceof BlockTicker) {
                ticking = true;
                Slimefun.getRegistry().getTickerBlocks().add(getId());
                blockTicker = (BlockTicker) handler;
            }
        }
    }

    /**
     * This method will add any given {@link ItemSetting} to this {@link SlimefunItem}.
     * Note that this will not work after the {@link SlimefunItem} was registered.
     * 
     * @param settings
     *            Any {@link ItemSetting} that should be added to this {@link SlimefunItem}
     */
    public final void addItemSetting(ItemSetting<?>... settings) {
        Validate.notEmpty(settings, "You cannot add zero settings...");
        Validate.noNullElements(settings, "You cannot add any 'null' ItemSettings!");

        if (state != ItemState.UNREGISTERED) {
            throw new UnsupportedOperationException("You cannot add an ItemSetting after the SlimefunItem was registered.");
        }

        if (this instanceof NotConfigurable) {
            throw new UnsupportedOperationException("This Item has been marked as NotConfigurable and cannot accept Item Settings!");
        }

        for (ItemSetting<?> setting : settings) {
            if (setting != null) {
                // Prevent two Item Settings with the same key
                for (ItemSetting<?> existingSetting : itemSettings) {
                    if (existingSetting.getKey().equals(setting.getKey())) {
                        throw new IllegalArgumentException("This Item has already an ItemSetting with this key: " + setting.getKey());
                    }
                }

                itemSettings.add(setting);
            }
        }
    }

    /**
     * This method is called before {@link #register(SlimefunAddon)}.
     * Override this method to add any additional setup, adding an {@link ItemHandler} for example.
     */
    public void preRegister() {
        // Override this method to execute code before the Item has been registered
        // Useful for calls to addItemHandler(...)
    }

    /**
     * This method is called after {@link #register(SlimefunAddon)}.
     * Override this method to add any additional setup that needs to happen after
     * the original registration of this {@link SlimefunItem}.
     */
    public void postRegister() {
        // Override this method to execute code after the Item has been registered
        // Useful for calls to Slimefun.getItemValue(...)
    }

    /**
     * This method will assign the given wiki page to this Item.
     * Note that you only need to provide the page name itself,
     * the URL to our wiki is prepended automatically.
     * 
     * @param page
     *            The associated wiki page
     */
    public final void addOfficialWikipage(@Nonnull String page) {
        Validate.notNull(page, "Wiki page cannot be null.");
        wikiURL = Optional.of("https://github.com/Slimefun/Slimefun4/wiki/" + page);
    }

    /**
     * This method returns the wiki page that has been assigned to this item.
     * It will return null, if no wiki page was found.
     * 
     * @see SlimefunItem#addOfficialWikipage(String)
     * 
     * @return This item's wiki page
     */
    public @Nonnull Optional<String> getWikipage() {
        return wikiURL;
    }

    /**
     * This method will return this Item's Name (The name that is displayed when
     * hovering over this {@link ItemStack} in an {@link Inventory}).
     * 
     * @return This item's name in {@link ItemStack} form
     */
    public final @Nonnull String getItemName() {
        if (itemStackTemplate instanceof SlimefunItemStack) {
            Optional<String> name = ((SlimefunItemStack) itemStackTemplate).getItemMetaSnapshot().getDisplayName();

            if (name.isPresent()) {
                return name.get();
            }
        }

        return ItemUtils.getItemName(itemStackTemplate);
    }

    /**
     * This method returns a Set of item handlers associated with this Item.
     * 
     * @return The Set of item handlers
     */
    public @Nonnull Collection<ItemHandler> getHandlers() {
        return itemhandlers.values();
    }

    /**
     * This method calls every {@link ItemHandler} of the given {@link Class}
     * and performs the action as specified via the {@link Consumer}.
     * 
     * @param c
     *            The {@link Class} of the {@link ItemHandler} to call.
     * @param callable
     *            A {@link Consumer} that is called for any found {@link ItemHandler}.
     * @param <T>
     *            The type of {@link ItemHandler} to call.
     * 
     * @return Whether or not an {@link ItemHandler} was found.
     */
    @ParametersAreNonnullByDefault
    public <T extends ItemHandler> boolean callItemHandler(Class<T> c, Consumer<T> callable) {
        Optional<ItemHandler> handler = itemhandlers.get(c);

        if (handler.isPresent()) {
            try {
                callable.accept(c.cast(handler.get()));
            } catch (Exception | LinkageError x) {
                error("Could not pass \"" + c.getSimpleName() + "\" for " + toString(), x);
            }

            return true;
        }

        return false;
    }

    /**
     * This returns whether or not we are scheduling a ticking task for this block.
     * 
     * @return Whether this {@link SlimefunItem} is a ticking block
     */
    public boolean isTicking() {
        return ticking;
    }

    @Override
    public String toString() {
        if (addon == null) {
            return getClass().getSimpleName() + " - '" + id + "'";
        } else {
            return getClass().getSimpleName() + " - '" + id + "' (" + addon.getName() + " v" + addon.getPluginVersion() + ')';
        }
    }

    @Override
    public @Nonnull Collection<ItemStack> getDrops() {
        return Arrays.asList(itemStackTemplate.clone());
    }

    @Override
    public @Nonnull Collection<ItemStack> getDrops(Player p) {
        return getDrops();
    }

    /**
     * This will send an info message to the console and signal that this message came
     * from this {@link SlimefunItem}, the message will be sent using the {@link Logger}
     * of the {@link SlimefunAddon} which registered this {@link SlimefunItem}.
     * 
     * @param message
     *            The message to send
     */
    public void info(@Nonnull String message) {
        Validate.notNull(addon, "Cannot log a message for an unregistered item!");

        String msg = toString() + ": " + message;
        addon.getLogger().log(Level.INFO, msg);
    }

    /**
     * This will send a warning to the console and signal that this warning came from
     * this {@link SlimefunItem}, the warning will be sent using the {@link Logger}
     * of the {@link SlimefunAddon} which registered this {@link SlimefunItem}.
     * 
     * @param message
     *            The message to send
     */
    public void warn(@Nonnull String message) {
        Validate.notNull(addon, "Cannot send a warning for an unregistered item!");

        String msg = toString() + ": " + message;
        addon.getLogger().log(Level.WARNING, msg);

        if (addon.getBugTrackerURL() != null) {
            // We can prompt the server operator to report it to the addon's bug tracker
            addon.getLogger().log(Level.WARNING, "You can report this warning here: {0}", addon.getBugTrackerURL());
        }
    }

    /**
     * This will throw a {@link Throwable} to the console and signal that
     * this was caused by this {@link SlimefunItem}.
     * 
     * @param message
     *            The message to display alongside this Stacktrace
     * @param throwable
     *            The {@link Throwable} to throw as a stacktrace.
     */
    public void error(@Nonnull String message, @Nonnull Throwable throwable) {
        Validate.notNull(addon, "Cannot send an error for an unregistered item!");
        addon.getLogger().log(Level.SEVERE, "Item \"{0}\" from {1} v{2} has caused an Error!", new Object[] { id, addon.getName(), addon.getPluginVersion() });

        if (addon.getBugTrackerURL() != null) {
            // We can prompt the server operator to report it to the addon's bug tracker
            addon.getLogger().log(Level.SEVERE, "You can report it here: {0}", addon.getBugTrackerURL());
        }

        addon.getLogger().log(Level.SEVERE, message, throwable);

        // We definitely want to re-throw them during Unit Tests
        if (throwable instanceof RuntimeException && Slimefun.getMinecraftVersion() == MinecraftVersion.UNIT_TEST) {
            throw (RuntimeException) throwable;
        }
    }

    /**
     * This method checks if the given {@link Player} is able to use this {@link SlimefunItem}.
     * A {@link Player} can use it if the following conditions apply:
     * 
     * <ul>
     * <li>The {@link SlimefunItem} is not disabled
     * <li>The {@link SlimefunItem} was not disabled for that {@link Player}'s {@link World}.
     * <li>The {@link Player} has the required {@link Permission} (if present)
     * <li>The {@link Player} has unlocked the required {@link Research} (if present)
     * </ul>
     * 
     * If any of these conditions evaluate to <code>false</code>, then an optional message will be
     * sent to the {@link Player}.
     * 
     * @param p
     *            The {@link Player} to check
     * @param sendMessage
     *            Whether to send that {@link Player} a message response.
     * 
     * @return Whether this {@link Player} is able to use this {@link SlimefunItem}.
     */
    public boolean canUse(@Nonnull Player p, boolean sendMessage) {
        Validate.notNull(p, "The Player cannot be null!");

        if (getState() == ItemState.VANILLA_FALLBACK) {
            // Vanilla items (which fell back) can always be used.
            return true;
        } else if (isDisabled()) {
            // The Item has been disabled in the config
            if (sendMessage) {
                Slimefun.getLocalization().sendMessage(p, "messages.disabled-item", true);
            }

            return false;
        } else if (!Slimefun.getWorldSettingsService().isEnabled(p.getWorld(), this)) {
            // The Item was disabled in the current World
            if (sendMessage) {
                Slimefun.getLocalization().sendMessage(p, "messages.disabled-in-world", true);
            }

            return false;
        } else if (!Slimefun.getPermissionsService().hasPermission(p, this)) {
            // The Player does not have the required permission node
            if (sendMessage) {
                Slimefun.getLocalization().sendMessage(p, "messages.no-permission", true);
            }

            return false;
        } else if (hasResearch()) {
            Optional<PlayerProfile> profile = PlayerProfile.find(p);

            if (!profile.isPresent()) {
                /*
                 * We will return false since we cannot know the answer yet.
                 * But we will schedule the Profile for loading and not send
                 * any message.
                 */
                PlayerProfile.request(p);
                return false;
            } else if (!profile.get().hasUnlocked(getResearch())) {
                /*
                 * The Profile is loaded but Player has not unlocked the
                 * required Research to use this SlimefunItem.
                 */
                if (sendMessage && !(this instanceof VanillaItem)) {
                    Slimefun.getLocalization().sendMessage(p, "messages.not-researched", true, s -> s.replace("%item%", getItemName()));
                }

                return false;
            } else {
                /*
                 * The PlayerProfile is loaded and the Player has unlocked
                 * the required Research.
                 */
                return true;
            }
        } else {
            // All checks have passed, the Player can use this item.
            return true;
        }
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof SlimefunItem) {
            return ((SlimefunItem) obj).getId().equals(getId());
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return getId().hashCode();
    }

    public static @Nullable SlimefunItem getById(@Nonnull String id) {
        return Slimefun.getRegistry().getSlimefunItemIds().get(id);
    }

    public static @Nullable SlimefunItem getByItem(@Nullable ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }

        if (item instanceof SlimefunItemStack) {
            return getById(((SlimefunItemStack) item).getItemId());
        }

        Optional<String> itemID = Slimefun.getItemDataService().getItemData(item);

        if (itemID.isPresent()) {
            return getById(itemID.get());
        }

        // Backwards compatibility
        if (Slimefun.getRegistry().isBackwardsCompatible()) {
            // This wrapper improves the heavy ItemStack#getItemMeta() call by caching it.
            ItemStackWrapper wrapper = ItemStackWrapper.wrap(item);

            /*
             * Quite expensive performance-wise.
             * But necessary for supporting legacy items
             */
            for (SlimefunItem sfi : Slimefun.getRegistry().getAllSlimefunItems()) {
                if (sfi.isItem(wrapper)) {
                    /*
                     * If we have to loop all items for the given item, then at least
                     * set the id via PersistentDataAPI for future performance boosts
                     */
                    Slimefun.getItemDataService().setItemData(item, sfi.getId());

                    return sfi;
                }
            }
        }

        return null;
    }

}