package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.bakedlibs.dough.chat.ChatInput;
import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.bakedlibs.dough.skins.PlayerHead;
import io.github.bakedlibs.dough.skins.PlayerSkin;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.papermc.lib.PaperLib;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu.AdvancedMenuClickHandler;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.InventoryBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;

public class ProgrammableAndroid extends SlimefunItem implements InventoryBlock, RecipeDisplayItem {

    private static final List<BlockFace> POSSIBLE_ROTATIONS = Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private static final int[] BORDER = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 18, 24, 25, 26, 27, 33, 35, 36, 42, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53 };
    private static final int[] OUTPUT_BORDER = { 10, 11, 12, 13, 14, 19, 23, 28, 32, 37, 38, 39, 40, 41 };
    private static final String DEFAULT_SCRIPT = "START-TURN_LEFT-REPEAT";
    private static final int MAX_SCRIPT_LENGTH = 54;

    protected final List<MachineFuel> fuelTypes = new ArrayList<>();
    protected final String texture;
    private final int tier;

    @ParametersAreNonnullByDefault
    public ProgrammableAndroid(ItemGroup category, int tier, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        this.tier = tier;
        texture = item.getSkullTexture().orElse(null);
        registerDefaultFuelTypes();

        new BlockMenuPreset(getId(), "Programmable Android") {

            @Override
            public void init() {
                constructMenu(this);
            }

            @Override
            public boolean canOpen(Block b, Player p) {
                boolean open = BlockStorage.getLocationInfo(b.getLocation(), "owner").equals(p.getUniqueId().toString()) || p.hasPermission("slimefun.android.bypass");

                if (!open) {
                    Slimefun.getLocalization().sendMessage(p, "inventory.no-access", true);
                }

                return open;
            }

            @Override
            public void newInstance(BlockMenu menu, Block b) {
                menu.replaceExistingItem(15, new CustomItemStack(HeadTexture.SCRIPT_START.getAsItemStack(), "&aStart/Continue"));
                menu.addMenuClickHandler(15, (p, slot, item, action) -> {
                    Slimefun.getLocalization().sendMessage(p, "android.started", true);
                    BlockStorage.addBlockInfo(b, "paused", "false");
                    p.closeInventory();
                    return false;
                });

                menu.replaceExistingItem(17, new CustomItemStack(HeadTexture.SCRIPT_PAUSE.getAsItemStack(), "&4Pause"));
                menu.addMenuClickHandler(17, (p, slot, item, action) -> {
                    BlockStorage.addBlockInfo(b, "paused", "true");
                    Slimefun.getLocalization().sendMessage(p, "android.stopped", true);
                    return false;
                });

                menu.replaceExistingItem(16, new CustomItemStack(HeadTexture.ENERGY_REGULATOR.getAsItemStack(), "&bMemory Core", "", "&8\u21E8 &7Click to open the Script Editor"));
                menu.addMenuClickHandler(16, (p, slot, item, action) -> {
                    BlockStorage.addBlockInfo(b, "paused", "true");
                    Slimefun.getLocalization().sendMessage(p, "android.stopped", true);
                    openScriptEditor(p, b);
                    return false;
                });
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                return new int[0];
            }
        };

        addItemHandler(onPlace(), onBreak());
    }

    @Nonnull
    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                Player p = e.getPlayer();
                Block b = e.getBlock();

                BlockStorage.addBlockInfo(b, "owner", p.getUniqueId().toString());
                BlockStorage.addBlockInfo(b, "script", DEFAULT_SCRIPT);
                BlockStorage.addBlockInfo(b, "index", "0");
                BlockStorage.addBlockInfo(b, "fuel", "0");
                BlockStorage.addBlockInfo(b, "rotation", p.getFacing().getOppositeFace().toString());
                BlockStorage.addBlockInfo(b, "paused", "true");

                BlockData blockData = Material.PLAYER_HEAD.createBlockData(data -> {
                    if (data instanceof Rotatable) {
                        ((Rotatable) data).setRotation(p.getFacing());
                    }
                });

                b.setBlockData(blockData);
            }
        };
    }

    @Nonnull
    private BlockBreakHandler onBreak() {
        return new BlockBreakHandler(false, false) {

            @Override
            public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
                Block b = e.getBlock();
                String owner = BlockStorage.getLocationInfo(b.getLocation(), "owner");

                if (!e.getPlayer().hasPermission("slimefun.android.bypass") && !e.getPlayer().getUniqueId().toString().equals(owner)) {
                    // The Player is not allowed to break this android
                    e.setCancelled(true);
                    return;
                }

                BlockMenu inv = BlockStorage.getInventory(b);

                if (inv != null) {
                    inv.dropItems(b.getLocation(), 43);
                    inv.dropItems(b.getLocation(), getOutputSlots());
                }
            }
        };
    }

    /**
     * This returns the {@link AndroidType} that is associated with this {@link ProgrammableAndroid}.
     * 
     * @return The type of this {@link ProgrammableAndroid}
     */
    public AndroidType getAndroidType() {
        return AndroidType.NONE;
    }

    /**
     * This returns the {@link AndroidFuelSource} for this {@link ProgrammableAndroid}.
     * It determines what kind of fuel is required to run it.
     * 
     * @return The required type of fuel
     */
    public AndroidFuelSource getFuelSource() {
        switch (getTier()) {
            case 1:
                return AndroidFuelSource.SOLID;
            case 2:
                return AndroidFuelSource.LIQUID;
            case 3:
                return AndroidFuelSource.NUCLEAR;
            default:
                throw new IllegalStateException("Cannot convert the following Android tier to a fuel type: " + getTier());
        }
    }

    @Override
    public void preRegister() {
        super.preRegister();

        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem item, Config data) {
                if (b != null && data != null) {
                    ProgrammableAndroid.this.tick(b, data);
                }
            }

            @Override
            public boolean isSynchronized() {
                return true;
            }
        });
    }

    @ParametersAreNonnullByDefault
    public void openScript(Player p, Block b, String sourceCode) {
        ChestMenu menu = new ChestMenu(ChatColor.DARK_AQUA + Slimefun.getLocalization().getMessage(p, "android.scripts.editor"));
        menu.setEmptySlotsClickable(false);

        menu.addItem(0, new CustomItemStack(Instruction.START.getItem(), Slimefun.getLocalization().getMessage(p, "android.scripts.instructions.START"), "", "&7\u21E8 &eLeft Click &7to return to the Android's interface"));
        menu.addMenuClickHandler(0, (pl, slot, item, action) -> {
            BlockMenu inv = BlockStorage.getInventory(b);
            // Fixes #2937
            if (inv != null) {
                inv.open(pl);
            } else {
                pl.closeInventory();
            }
            return false;
        });

        String[] script = CommonPatterns.DASH.split(sourceCode);

        for (int i = 1; i < script.length; i++) {
            int index = i;

            if (i == script.length - 1) {
                boolean hasFreeSlot = script.length < 54;

                if (hasFreeSlot) {
                    menu.addItem(i, new CustomItemStack(HeadTexture.SCRIPT_NEW.getAsItemStack(), "&7> Add new Command"));
                    menu.addMenuClickHandler(i, (pl, slot, item, action) -> {
                        editInstruction(pl, b, script, index);
                        return false;
                    });
                }

                int slot = i + (hasFreeSlot ? 1 : 0);
                menu.addItem(slot, new CustomItemStack(Instruction.REPEAT.getItem(), Slimefun.getLocalization().getMessage(p, "android.scripts.instructions.REPEAT"), "", "&7\u21E8 &eLeft Click &7to return to the Android's interface"));
                menu.addMenuClickHandler(slot, (pl, s, item, action) -> {
                    BlockMenu inv = BlockStorage.getInventory(b);
                    // Fixes #2937
                    if (inv != null) {
                        inv.open(pl);
                    } else {
                        pl.closeInventory();
                    }
                    return false;
                });
            } else {
                Instruction instruction = Instruction.getInstruction(script[i]);

                if (instruction == null) {
                    Slimefun.instance().getLogger().log(Level.WARNING, "Failed to parse Android instruction: {0}, maybe your server is out of date?", script[i]);
                    return;
                }

                ItemStack stack = instruction.getItem();
                menu.addItem(i, new CustomItemStack(stack, Slimefun.getLocalization().getMessage(p, "android.scripts.instructions." + instruction.name()), "", "&7\u21E8 &eLeft Click &7to edit", "&7\u21E8 &eRight Click &7to delete", "&7\u21E8 &eShift + Right Click &7to duplicate"));
                menu.addMenuClickHandler(i, (pl, slot, item, action) -> {
                    if (action.isRightClicked() && action.isShiftClicked()) {
                        if (script.length == 54) {
                            return false;
                        }

                        String code = duplicateInstruction(script, index);
                        setScript(b.getLocation(), code);
                        openScript(pl, b, code);
                    } else if (action.isRightClicked()) {
                        String code = deleteInstruction(script, index);
                        setScript(b.getLocation(), code);
                        openScript(pl, b, code);
                    } else {
                        editInstruction(pl, b, script, index);
                    }

                    return false;
                });
            }
        }

        menu.open(p);
    }

    @ParametersAreNonnullByDefault
    private String addInstruction(String[] script, int index, Instruction instruction) {
        int i = 0;
        StringBuilder builder = new StringBuilder(Instruction.START.name() + '-');

        for (String current : script) {
            if (i > 0) {
                if (i == index) {
                    builder.append(instruction).append('-');
                } else if (i < script.length - 1) {
                    builder.append(current).append('-');
                }
            }
            i++;
        }

        builder.append(Instruction.REPEAT.name());
        return builder.toString();
    }

    private String duplicateInstruction(@Nonnull String[] script, int index) {
        int i = 0;
        StringBuilder builder = new StringBuilder(Instruction.START + "-");

        for (String instruction : script) {
            if (i > 0) {
                if (i == index) {
                    builder.append(script[i]).append('-').append(script[i]).append('-');
                } else if (i < script.length - 1) {
                    builder.append(instruction).append('-');
                }
            }
            i++;
        }

        builder.append(Instruction.REPEAT.name());
        return builder.toString();
    }

    private String deleteInstruction(String[] script, int index) {
        int i = 0;
        StringBuilder builder = new StringBuilder(Instruction.START.name() + '-');

        for (String instruction : script) {
            if (i != index && i > 0 && i < script.length - 1) {
                builder.append(instruction).append('-');
            }

            i++;
        }

        builder.append(Instruction.REPEAT.name());
        return builder.toString();
    }

    protected void openScriptDownloader(Player p, Block b, int page) {
        ChestMenu menu = new ChestMenu("Android Scripts");

        menu.setEmptySlotsClickable(false);
        menu.addMenuOpeningHandler(pl -> pl.playSound(pl.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.7F, 0.7F));

        List<Script> scripts = Script.getUploadedScripts(getAndroidType());
        int pages = (scripts.size() / 45) + 1;

        for (int i = 45; i < 54; i++) {
            menu.addItem(i, new CustomItemStack(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), " "));
            menu.addMenuClickHandler(i, (pl, slot, item, action) -> false);
        }

        menu.addItem(46, ChestMenuUtils.getPreviousButton(p, page, pages));
        menu.addMenuClickHandler(46, (pl, slot, item, action) -> {
            int next = page - 1;
            if (next < 1) {
                next = pages;
            }
            if (next != page) {
                openScriptDownloader(pl, b, next);
            }
            return false;
        });

        menu.addItem(48, new CustomItemStack(HeadTexture.SCRIPT_UP.getAsItemStack(), "&eUpload a Script", "", "&6Click &7to upload your Android's Script", "&7to the Server's database"));
        menu.addMenuClickHandler(48, (pl, slot, item, action) -> {
            uploadScript(pl, b, page);
            return false;
        });

        menu.addItem(50, ChestMenuUtils.getNextButton(p, page, pages));
        menu.addMenuClickHandler(50, (pl, slot, item, action) -> {
            int next = page + 1;
            if (next > pages) {
                next = 1;
            }
            if (next != page) {
                openScriptDownloader(pl, b, next);
            }
            return false;
        });

        menu.addItem(53, new CustomItemStack(HeadTexture.SCRIPT_LEFT.getAsItemStack(), "&6> Back", "", "&7Return to the Android's interface"));
        menu.addMenuClickHandler(53, (pl, slot, item, action) -> {
            openScriptEditor(pl, b);
            return false;
        });

        int index = 0;
        int categoryIndex = 45 * (page - 1);

        for (int i = 0; i < 45; i++) {
            int target = categoryIndex + i;

            if (target >= scripts.size()) {
                break;
            } else {
                Script script = scripts.get(target);
                menu.addItem(index, script.getAsItemStack(this, p), (player, slot, stack, action) -> {
                    try {
                        if (action.isShiftClicked()) {
                            if (script.isAuthor(player)) {
                                Slimefun.getLocalization().sendMessage(player, "android.scripts.rating.own", true);
                            } else if (script.canRate(player)) {
                                script.rate(player, !action.isRightClicked());
                                openScriptDownloader(player, b, page);
                            } else {
                                Slimefun.getLocalization().sendMessage(player, "android.scripts.rating.already", true);
                            }
                        } else if (!action.isRightClicked()) {
                            script.download();
                            setScript(b.getLocation(), script.getSourceCode());
                            openScriptEditor(player, b);
                        }
                    } catch (Exception x) {
                        Slimefun.logger().log(Level.SEVERE, "An Exception was thrown when a User tried to download a Script!", x);
                    }

                    return false;
                });

                index++;
            }
        }

        menu.open(p);
    }

    @ParametersAreNonnullByDefault
    private void uploadScript(Player p, Block b, int page) {
        String code = getScript(b.getLocation());
        int nextId = 1;

        for (Script script : Script.getUploadedScripts(getAndroidType())) {
            if (script.isAuthor(p)) {
                nextId++;
            }

            if (script.getSourceCode().equals(code)) {
                Slimefun.getLocalization().sendMessage(p, "android.scripts.already-uploaded", true);
                return;
            }
        }

        p.closeInventory();
        Slimefun.getLocalization().sendMessages(p, "android.scripts.enter-name");
        int id = nextId;

        ChatInput.waitForPlayer(Slimefun.instance(), p, msg -> {
            Script.upload(p, getAndroidType(), id, msg, code);
            Slimefun.getLocalization().sendMessages(p, "android.scripts.uploaded");
            openScriptDownloader(p, b, page);
        });
    }

    public void openScriptEditor(Player p, Block b) {
        ChestMenu menu = new ChestMenu(ChatColor.DARK_AQUA + Slimefun.getLocalization().getMessage(p, "android.scripts.editor"));
        menu.setEmptySlotsClickable(false);

        menu.addItem(1, new CustomItemStack(HeadTexture.SCRIPT_FORWARD.getAsItemStack(), "&2> Edit Script", "", "&aEdits your current Script"));
        menu.addMenuClickHandler(1, (pl, slot, item, action) -> {
            String script = BlockStorage.getLocationInfo(b.getLocation()).getString("script");
            // Fixes #2937
            if (script != null) {
                if (CommonPatterns.DASH.split(script).length <= MAX_SCRIPT_LENGTH) {
                    openScript(pl, b, getScript(b.getLocation()));
                } else {
                    pl.closeInventory();
                    Slimefun.getLocalization().sendMessage(pl, "android.scripts.too-long");
                }
            } else {
                pl.closeInventory();
            }
            return false;
        });

        menu.addItem(3, new CustomItemStack(HeadTexture.SCRIPT_NEW.getAsItemStack(), "&4> Create new Script", "", "&cDeletes your current Script", "&cand creates a blank one"));
        menu.addMenuClickHandler(3, (pl, slot, item, action) -> {
            openScript(pl, b, DEFAULT_SCRIPT);
            return false;
        });

        menu.addItem(5, new CustomItemStack(HeadTexture.SCRIPT_DOWN.getAsItemStack(), "&6> Download a Script", "", "&eDownload a Script from the Server", "&eYou can edit or simply use it"));
        menu.addMenuClickHandler(5, (pl, slot, item, action) -> {
            openScriptDownloader(pl, b, 1);
            return false;
        });

        menu.addItem(8, new CustomItemStack(HeadTexture.SCRIPT_LEFT.getAsItemStack(), "&6> Back", "", "&7Return to the Android's interface"));
        menu.addMenuClickHandler(8, (pl, slot, item, action) -> {
            BlockMenu inv = BlockStorage.getInventory(b);
            // Fixes #2937
            if (inv != null) {
                inv.open(pl);
            } else {
                pl.closeInventory();
            }
            return false;
        });

        menu.open(p);
    }

    @Nonnull
    protected List<Instruction> getValidScriptInstructions() {
        List<Instruction> list = new ArrayList<>();

        for (Instruction part : Instruction.valuesCache) {
            if (part == Instruction.START || part == Instruction.REPEAT) {
                continue;
            }

            if (getAndroidType().isType(part.getRequiredType())) {
                list.add(part);
            }
        }

        return list;
    }

    protected void editInstruction(Player p, Block b, String[] script, int index) {
        ChestMenu menu = new ChestMenu(ChatColor.DARK_AQUA + Slimefun.getLocalization().getMessage(p, "android.scripts.editor"));
        ChestMenuUtils.drawBackground(menu, 0, 1, 2, 3, 4, 5, 6, 7, 8);

        menu.setEmptySlotsClickable(false);
        menu.addItem(9, new CustomItemStack(HeadTexture.SCRIPT_PAUSE.getAsItemStack(), "&fDo nothing"), (pl, slot, item, action) -> {
            String code = deleteInstruction(script, index);
            setScript(b.getLocation(), code);
            openScript(p, b, code);
            return false;
        });

        int i = 10;
        for (Instruction instruction : getValidScriptInstructions()) {
            menu.addItem(i, new CustomItemStack(instruction.getItem(), Slimefun.getLocalization().getMessage(p, "android.scripts.instructions." + instruction.name())), (pl, slot, item, action) -> {
                String code = addInstruction(script, index, instruction);
                setScript(b.getLocation(), code);
                openScript(p, b, code);
                return false;
            });

            i++;
        }

        menu.open(p);
    }

    @Nonnull
    public String getScript(@Nonnull Location l) {
        Validate.notNull(l, "Location for android not specified");
        String script = BlockStorage.getLocationInfo(l, "script");
        return script != null ? script : DEFAULT_SCRIPT;
    }

    public void setScript(@Nonnull Location l, @Nonnull String script) {
        Validate.notNull(l, "Location for android not specified");
        Validate.notNull(script, "No script given");
        Validate.isTrue(script.startsWith(Instruction.START.name() + '-'), "A script must begin with a 'START' token.");
        Validate.isTrue(script.endsWith('-' + Instruction.REPEAT.name()), "A script must end with a 'REPEAT' token.");
        Validate.isTrue(CommonPatterns.DASH.split(script).length <= MAX_SCRIPT_LENGTH, "Scripts may not have more than " + MAX_SCRIPT_LENGTH + " segments");

        BlockStorage.addBlockInfo(l, "script", script);
    }

    private void registerDefaultFuelTypes() {
        switch (getFuelSource()) {
            case SOLID:
                registerFuelType(new MachineFuel(80, new ItemStack(Material.COAL_BLOCK)));
                registerFuelType(new MachineFuel(45, new ItemStack(Material.BLAZE_ROD)));
                registerFuelType(new MachineFuel(70, new ItemStack(Material.DRIED_KELP_BLOCK)));

                // Coal & Charcoal
                registerFuelType(new MachineFuel(8, new ItemStack(Material.COAL)));
                registerFuelType(new MachineFuel(8, new ItemStack(Material.CHARCOAL)));

                // Logs
                for (Material mat : Tag.LOGS.getValues()) {
                    registerFuelType(new MachineFuel(2, new ItemStack(mat)));
                }

                // Wooden Planks
                for (Material mat : Tag.PLANKS.getValues()) {
                    registerFuelType(new MachineFuel(1, new ItemStack(mat)));
                }

                break;
            case LIQUID:
                registerFuelType(new MachineFuel(100, new ItemStack(Material.LAVA_BUCKET)));
                registerFuelType(new MachineFuel(200, SlimefunItems.OIL_BUCKET));
                registerFuelType(new MachineFuel(500, SlimefunItems.FUEL_BUCKET));
                break;
            case NUCLEAR:
                registerFuelType(new MachineFuel(2500, SlimefunItems.URANIUM));
                registerFuelType(new MachineFuel(1200, SlimefunItems.NEPTUNIUM));
                registerFuelType(new MachineFuel(3000, SlimefunItems.BOOSTED_URANIUM));
                break;
            default:
                throw new IllegalStateException("Unhandled Fuel Source: " + getFuelSource());
        }
    }

    public void registerFuelType(@Nonnull MachineFuel fuel) {
        Validate.notNull(fuel, "Cannot register null as a Fuel type");

        fuelTypes.add(fuel);
    }

    @Override
    public String getLabelLocalPath() {
        return "guide.tooltips.recipes.generator";
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> list = new ArrayList<>();

        for (MachineFuel fuel : fuelTypes) {
            ItemStack item = fuel.getInput().clone();
            ItemMeta im = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(ChatColors.color("&8\u21E8 &7Lasts " + NumberUtils.getTimeLeft(fuel.getTicks() / 2)));
            im.setLore(lore);
            item.setItemMeta(im);
            list.add(item);
        }

        return list;
    }

    @Override
    public int[] getInputSlots() {
        return new int[0];
    }

    @Override
    public int[] getOutputSlots() {
        return new int[] { 20, 21, 22, 29, 30, 31 };
    }

    public int getTier() {
        return tier;
    }

    protected void tick(Block b, Config data) {
        if (b.getType() != Material.PLAYER_HEAD) {
            // The Android was destroyed or moved.
            return;
        }

        if ("false".equals(data.getString("paused"))) {
            BlockMenu menu = BlockStorage.getInventory(b);

            String fuelData = data.getString("fuel");
            float fuel = fuelData == null ? 0 : Float.parseFloat(fuelData);

            if (fuel < 0.001) {
                consumeFuel(b, menu);
            } else {
                String code = data.getString("script");
                String[] script = CommonPatterns.DASH.split(code == null ? DEFAULT_SCRIPT : code);

                String indexData = data.getString("index");
                int index = (indexData == null ? 0 : Integer.parseInt(indexData)) + 1;

                if (index >= script.length) {
                    index = 0;
                }

                BlockStorage.addBlockInfo(b, "fuel", String.valueOf(fuel - 1));
                Instruction instruction = Instruction.getInstruction(script[index]);

                if (instruction == null) {
                    Slimefun.instance().getLogger().log(Level.WARNING, "Failed to parse Android instruction: {0}, maybe your server is out of date?", script[index]);
                    return;
                }

                executeInstruction(instruction, b, menu, data, index);
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void executeInstruction(Instruction instruction, Block b, BlockMenu inv, Config data, int index) {
        if (getAndroidType().isType(instruction.getRequiredType())) {
            String rotationData = data.getString("rotation");
            BlockFace face = rotationData == null ? BlockFace.NORTH : BlockFace.valueOf(rotationData);

            switch (instruction) {
                case START:
                case WAIT:
                    // We are "waiting" here, so we only move a step forward
                    BlockStorage.addBlockInfo(b, "index", String.valueOf(index));
                    break;
                case REPEAT:
                    // "repeat" just means, we reset our index
                    BlockStorage.addBlockInfo(b, "index", String.valueOf(0));
                    break;
                case CHOP_TREE:
                    // We only move to the next step if we finished chopping wood
                    if (chopTree(b, inv, face)) {
                        BlockStorage.addBlockInfo(b, "index", String.valueOf(index));
                    }
                    break;
                default:
                    // We set the index here in advance to fix moving android issues
                    BlockStorage.addBlockInfo(b, "index", String.valueOf(index));
                    instruction.execute(this, b, inv, face);
                    break;
            }
        }
    }

    protected void rotate(Block b, BlockFace current, int mod) {
        int index = POSSIBLE_ROTATIONS.indexOf(current) + mod;

        if (index == POSSIBLE_ROTATIONS.size()) {
            index = 0;
        } else if (index < 0) {
            index = POSSIBLE_ROTATIONS.size() - 1;
        }

        BlockFace rotation = POSSIBLE_ROTATIONS.get(index);

        BlockData blockData = Material.PLAYER_HEAD.createBlockData(data -> {
            if (data instanceof Rotatable) {
                Rotatable rotatable = ((Rotatable) data);
                rotatable.setRotation(rotation.getOppositeFace());
            }
        });

        b.setBlockData(blockData);
        BlockStorage.addBlockInfo(b, "rotation", rotation.name());
    }

    protected void depositItems(BlockMenu menu, Block facedBlock) {
        if (facedBlock.getType() == Material.DISPENSER && BlockStorage.check(facedBlock, "ANDROID_INTERFACE_ITEMS")) {
            BlockState state = PaperLib.getBlockState(facedBlock, false).getState();

            if (state instanceof Dispenser) {
                Dispenser d = (Dispenser) state;

                for (int slot : getOutputSlots()) {
                    ItemStack stack = menu.getItemInSlot(slot);

                    if (stack != null) {
                        Optional<ItemStack> optional = d.getInventory().addItem(stack).values().stream().findFirst();

                        if (optional.isPresent()) {
                            menu.replaceExistingItem(slot, optional.get());
                        } else {
                            menu.replaceExistingItem(slot, null);
                        }
                    }
                }
            }
        }
    }

    protected void refuel(BlockMenu menu, Block facedBlock) {
        if (facedBlock.getType() == Material.DISPENSER && BlockStorage.check(facedBlock, "ANDROID_INTERFACE_FUEL")) {
            BlockState state = PaperLib.getBlockState(facedBlock, false).getState();

            if (state instanceof Dispenser) {
                Dispenser d = (Dispenser) state;

                for (int slot = 0; slot < 9; slot++) {
                    ItemStack item = d.getInventory().getItem(slot);

                    if (item != null) {
                        insertFuel(menu, d.getInventory(), slot, menu.getItemInSlot(43), item);
                    }
                }
            }
        }
    }

    private boolean insertFuel(BlockMenu menu, Inventory dispenser, int slot, ItemStack currentFuel, ItemStack newFuel) {
        if (currentFuel == null) {
            menu.replaceExistingItem(43, newFuel);
            dispenser.setItem(slot, null);
            return true;
        } else if (SlimefunUtils.isItemSimilar(newFuel, currentFuel, true, false)) {
            int rest = newFuel.getType().getMaxStackSize() - currentFuel.getAmount();

            if (rest > 0) {
                int amount = newFuel.getAmount() > rest ? rest : newFuel.getAmount();
                menu.replaceExistingItem(43, new CustomItemStack(newFuel, currentFuel.getAmount() + amount));
                ItemUtils.consumeItem(newFuel, amount, false);
            }

            return true;
        }

        return false;
    }

    @ParametersAreNonnullByDefault
    private void consumeFuel(Block b, BlockMenu menu) {
        ItemStack item = menu.getItemInSlot(43);

        if (item != null && item.getType() != Material.AIR) {
            for (MachineFuel fuel : fuelTypes) {
                if (fuel.test(item)) {
                    menu.consumeItem(43);

                    if (getFuelSource() == AndroidFuelSource.LIQUID) {
                        menu.pushItem(new ItemStack(Material.BUCKET), getOutputSlots());
                    }

                    int fuelLevel = fuel.getTicks();
                    BlockStorage.addBlockInfo(b, "fuel", String.valueOf(fuelLevel));
                    break;
                }
            }
        }
    }

    private void constructMenu(@Nonnull BlockMenuPreset preset) {
        preset.drawBackground(BORDER);
        preset.drawBackground(ChestMenuUtils.getOutputSlotTexture(), OUTPUT_BORDER);

        for (int i : getOutputSlots()) {
            preset.addMenuClickHandler(i, new AdvancedMenuClickHandler() {

                @Override
                public boolean onClick(Player p, int slot, ItemStack cursor, ClickAction action) {
                    return false;
                }

                @Override
                public boolean onClick(InventoryClickEvent e, Player p, int slot, ItemStack cursor, ClickAction action) {
                    return cursor == null || cursor.getType() == null || cursor.getType() == Material.AIR;
                }
            });
        }

        preset.addItem(34, getFuelSource().getItem(), ChestMenuUtils.getEmptyClickHandler());
    }

    @ParametersAreNonnullByDefault
    public void addItems(Block b, ItemStack... items) {
        Validate.notNull(b, "The Block cannot be null.");

        BlockMenu inv = BlockStorage.getInventory(b);

        if (inv != null) {
            for (ItemStack item : items) {
                inv.pushItem(item, getOutputSlots());
            }
        }
    }

    @ParametersAreNonnullByDefault
    protected void move(Block b, BlockFace face, Block block) {
        if (block.getY() > 0 && block.getY() < block.getWorld().getMaxHeight() && block.isEmpty()) {
            BlockData blockData = Material.PLAYER_HEAD.createBlockData(data -> {
                if (data instanceof Rotatable) {
                    Rotatable rotatable = ((Rotatable) data);
                    rotatable.setRotation(face.getOppositeFace());
                }
            });

            block.setBlockData(blockData);

            Slimefun.runSync(() -> {
                PlayerSkin skin = PlayerSkin.fromBase64(texture);
                PlayerHead.setSkin(block, skin, true);
            });

            b.setType(Material.AIR);
            BlockStorage.moveBlockInfo(b.getLocation(), block.getLocation());
        }
    }

    protected void attack(Block b, BlockFace face, Predicate<LivingEntity> predicate) {
        throw new UnsupportedOperationException("Non-butcher Android tried to butcher!");
    }

    protected void fish(Block b, BlockMenu menu) {
        throw new UnsupportedOperationException("Non-fishing Android tried to fish!");
    }

    protected void dig(Block b, BlockMenu menu, Block block) {
        throw new UnsupportedOperationException("Non-mining Android tried to mine!");
    }

    protected void moveAndDig(Block b, BlockMenu menu, BlockFace face, Block block) {
        throw new UnsupportedOperationException("Non-mining Android tried to mine!");
    }

    protected boolean chopTree(Block b, BlockMenu menu, BlockFace face) {
        throw new UnsupportedOperationException("Non-woodcutter Android tried to chop a Tree!");
    }

    protected void farm(Block b, BlockMenu menu, Block block, boolean isAdvanced) {
        throw new UnsupportedOperationException("Non-farming Android tried to farm!");
    }

}
