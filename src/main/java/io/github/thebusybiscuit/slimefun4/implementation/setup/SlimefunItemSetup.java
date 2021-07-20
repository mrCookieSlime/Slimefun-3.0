package io.github.thebusybiscuit.slimefun4.implementation.setup;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactivity;
import io.github.thebusybiscuit.slimefun4.core.handlers.RainbowTickHandler;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.RadioactiveItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.VanillaItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.AndroidInterface;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.ButcherAndroid;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.FarmerAndroid;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.FisherAndroid;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.MinerAndroid;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.ProgrammableAndroid;
import io.github.thebusybiscuit.slimefun4.implementation.items.androids.WoodcutterAndroid;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.ElytraCap;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.EnderBoots;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.FarmerShoes;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.HazmatArmorPiece;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.LongFallBoots;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.Parachute;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.RainbowArmorPiece;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.SlimefunArmorPiece;
import io.github.thebusybiscuit.slimefun4.implementation.items.armor.StomperBoots;
import io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters.ArmorAutoCrafter;
import io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters.EnhancedAutoCrafter;
import io.github.thebusybiscuit.slimefun4.implementation.items.autocrafters.VanillaAutoCrafter;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.Cooler;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.EnderBackpack;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.RestoredBackpack;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SlimefunBackpack;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.SoulboundBackpack;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.BlockPlacer;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.BrokenSpawner;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.Composter;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.Crucible;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.EnhancedFurnace;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.HardenedGlass;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.HologramProjector;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.IgnitionChamber;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.OutputChest;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.RainbowBlock;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.RepairedSpawner;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.WitherProofBlock;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.AdvancedCargoOutputNode;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.CargoConnectorNode;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.CargoInputNode;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.CargoManager;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.CargoOutputNode;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.ReactorAccessPort;
import io.github.thebusybiscuit.slimefun4.implementation.items.cargo.TrashCan;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.Capacitor;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.EnergyConnector;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.EnergyRegulator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.JetBoots;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.Jetpack;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.MultiTool;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.Multimeter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.gadgets.SolarHelmet;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.BioGenerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.CoalGenerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.CombustionGenerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.LavaGenerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.MagnesiumGenerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.generators.SolarGenerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoAnvil;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoBrewer;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.AutoDrier;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.CarbonPress;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ChargingBench;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricDustWasher;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricFurnace;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricGoldPan;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricIngotFactory;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricIngotPulverizer;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricOreGrinder;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricPress;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectricSmeltery;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.ElectrifiedCrucible;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.FluidPump;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.FoodComposter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.FoodFabricator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.Freezer;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.HeatedPressureChamber;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.Refinery;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators.AnimalGrowthAccelerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators.CropGrowthAccelerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.accelerators.TreeGrowthAccelerator;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoDisenchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.AutoEnchanter;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.enchanting.BookBinder;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.AutoBreeder;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.ExpCollector;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.IronGolemAssembler;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.ProduceCollector;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities.WitherAssembler;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors.NetherStarReactor;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors.NuclearReactor;
import io.github.thebusybiscuit.slimefun4.implementation.items.elevator.ElevatorPlate;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.BirthdayCake;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.DietCookie;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.FortuneCookie;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.HeavyCream;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.Juice;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.MagicSugar;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.MeatJerky;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.MonsterJerky;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.GEOScanner;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.OilPump;
import io.github.thebusybiscuit.slimefun4.implementation.items.geo.PortableGEOScanner;
import io.github.thebusybiscuit.slimefun4.implementation.items.gps.GPSControlPanel;
import io.github.thebusybiscuit.slimefun4.implementation.items.gps.GPSMarkerTool;
import io.github.thebusybiscuit.slimefun4.implementation.items.gps.GPSTransmitter;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.BeeWings;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.InfernalBonemeal;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.InfusedHopper;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.InfusedMagnet;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.KnowledgeFlask;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.KnowledgeTome;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.MagicEyeOfEnder;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.MagicalZombiePills;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.SoulboundItem;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.TelepositionScroll;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.runes.ElementalRune;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.runes.EnchantmentRune;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.runes.SoulboundRune;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.runes.VillagerRune;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.staves.StormStaff;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.staves.WaterStaff;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.staves.WindStaff;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.talismans.MagicianTalisman;
import io.github.thebusybiscuit.slimefun4.implementation.items.magical.talismans.Talisman;
import io.github.thebusybiscuit.slimefun4.implementation.items.medical.Bandage;
import io.github.thebusybiscuit.slimefun4.implementation.items.medical.Medicine;
import io.github.thebusybiscuit.slimefun4.implementation.items.medical.Splint;
import io.github.thebusybiscuit.slimefun4.implementation.items.medical.Vitamins;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.AlloyIngot;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.BasicCircuitBoard;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.CoolantCell;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.GoldIngot;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.OrganicFertilizer;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.OrganicFood;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.SteelThruster;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.StrangeNetherGoo;
import io.github.thebusybiscuit.slimefun4.implementation.items.misc.SyntheticEmerald;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.ArmorForge;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.AutomatedPanningMachine;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.Compressor;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.EnhancedCraftingTable;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.GrindStone;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.Juicer;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.MagicWorkbench;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.MakeshiftSmeltery;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.OreCrusher;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.OreWasher;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.PressureChamber;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.Smeltery;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.TableSaw;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner.AdvancedIndustrialMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.multiblocks.miner.IndustrialMiner;
import io.github.thebusybiscuit.slimefun4.implementation.items.seasonal.ChristmasPresent;
import io.github.thebusybiscuit.slimefun4.implementation.items.seasonal.EasterEgg;
import io.github.thebusybiscuit.slimefun4.implementation.items.teleporter.PersonalActivationPlate;
import io.github.thebusybiscuit.slimefun4.implementation.items.teleporter.PortableTeleporter;
import io.github.thebusybiscuit.slimefun4.implementation.items.teleporter.SharedActivationPlate;
import io.github.thebusybiscuit.slimefun4.implementation.items.teleporter.Teleporter;
import io.github.thebusybiscuit.slimefun4.implementation.items.teleporter.TeleporterPylon;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ClimbingPick;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ExplosivePickaxe;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.ExplosiveShovel;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.GoldPan;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.GrapplingHook;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.HerculesPickaxe;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.LumberAxe;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.NetherGoldPan;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.PickaxeOfContainment;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.PickaxeOfTheSeeker;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.PickaxeOfVeinMining;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.PortableCrafter;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.PortableDustbin;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.SmeltersPickaxe;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.TapeMeasure;
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.ExplosiveBow;
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.IcyBow;
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.SeismicAxe;
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.SwordOfBeheading;
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.VampireBlade;
import io.github.thebusybiscuit.slimefun4.utils.ColoredMaterial;
import io.github.thebusybiscuit.slimefun4.utils.HeadTexture;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

/**
 * This class holds the recipes of all items.
 * This is the place where all items from Slimefun are registered.
 *
 */
public final class SlimefunItemSetup {

    private static boolean registeredItems = false;

    private SlimefunItemSetup() {}

    public static void setup(@Nonnull SlimefunPlugin plugin) {
        if (registeredItems) {
            throw new UnsupportedOperationException("Slimefun Items can only be registered once!");
        }

        registeredItems = true;
        DefaultCategories categories = new DefaultCategories();

        // @formatter:off (We will need to refactor this one day)
        new SlimefunItem(categories.weapons, SlimefunItems.GRANDMAS_WALKING_STICK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.OAK_LOG), null, null, new ItemStack(Material.OAK_LOG), null, null, new ItemStack(Material.OAK_LOG), null})
        .register(plugin);
        
        new SlimefunItem(categories.weapons, SlimefunItems.GRANDPAS_WALKING_STICK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.LEATHER), new ItemStack(Material.OAK_LOG), new ItemStack(Material.LEATHER), null, new ItemStack(Material.OAK_LOG), null, null, new ItemStack(Material.OAK_LOG), null})
        .register(plugin);

        new PortableCrafter(categories.usefulItems, SlimefunItems.PORTABLE_CRAFTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.BOOK), new ItemStack(Material.CRAFTING_TABLE), null, null, null, null, null, null, null})
        .register(plugin);

        new FortuneCookie(categories.food, SlimefunItems.FORTUNE_COOKIE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.COOKIE), new ItemStack(Material.PAPER), null, null, null, null, null, null, null})
        .register(plugin);

        new DietCookie(categories.food, SlimefunItems.DIET_COOKIE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {new ItemStack(Material.COOKIE), SlimefunItems.ELYTRA_SCALE, null, null, null, null, null, null, null})
        .register(plugin);

        new OutputChest(categories.basicMachines, SlimefunItems.OUTPUT_CHEST, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.LEAD_INGOT, new ItemStack(Material.HOPPER), SlimefunItems.LEAD_INGOT, SlimefunItems.LEAD_INGOT, new ItemStack(Material.CHEST), SlimefunItems.LEAD_INGOT, null, SlimefunItems.LEAD_INGOT, null})
        .register(plugin);
        
        new EnhancedCraftingTable(categories.basicMachines, SlimefunItems.ENHANCED_CRAFTING_TABLE).register(plugin);

        new PortableDustbin(categories.usefulItems, SlimefunItems.PORTABLE_DUSTBIN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), null, new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT)})
        .register(plugin);

        new MeatJerky(categories.food, SlimefunItems.BEEF_JERKY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SALT, new ItemStack(Material.COOKED_BEEF), null, null, null, null, null, null, null})
        .register(plugin);

        new MeatJerky(categories.food, SlimefunItems.PORK_JERKY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SALT, new ItemStack(Material.COOKED_PORKCHOP), null, null, null, null, null, null, null})
        .register(plugin);

        new MeatJerky(categories.food, SlimefunItems.CHICKEN_JERKY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SALT, new ItemStack(Material.COOKED_CHICKEN), null, null, null, null, null, null, null})
        .register(plugin);

        new MeatJerky(categories.food, SlimefunItems.MUTTON_JERKY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SALT, new ItemStack(Material.COOKED_MUTTON), null, null, null, null, null, null, null})
        .register(plugin);

        new MeatJerky(categories.food, SlimefunItems.RABBIT_JERKY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SALT, new ItemStack(Material.COOKED_RABBIT), null, null, null, null, null, null, null})
        .register(plugin);

        new MeatJerky(categories.food, SlimefunItems.FISH_JERKY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SALT, new ItemStack(Material.COOKED_COD), null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.food, SlimefunItems.KELP_COOKIE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.DRIED_KELP), null, new ItemStack(Material.DRIED_KELP), new ItemStack(Material.SUGAR), new ItemStack(Material.DRIED_KELP), null, new ItemStack(Material.DRIED_KELP), null},
        new SlimefunItemStack(SlimefunItems.KELP_COOKIE, 2))
        .register(plugin);

        new GrindStone(categories.basicMachines, SlimefunItems.GRIND_STONE).register(plugin);
        new ArmorForge(categories.basicMachines, SlimefunItems.ARMOR_FORGE).register(plugin);
        
        OreCrusher oreCrusher = new OreCrusher(categories.basicMachines, SlimefunItems.ORE_CRUSHER);
        oreCrusher.register(plugin);
        
        new Compressor(categories.basicMachines, SlimefunItems.COMPRESSOR).register(plugin);

        new SlimefunItem(categories.magicalResources, SlimefunItems.MAGIC_LUMP_1, RecipeType.GRIND_STONE,
        new ItemStack[] {new ItemStack(Material.NETHER_WART), null, null, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.MAGIC_LUMP_1, 2))
        .register(plugin);

        new SlimefunItem(categories.magicalResources, SlimefunItems.MAGIC_LUMP_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_1, SlimefunItems.MAGIC_LUMP_1, null, SlimefunItems.MAGIC_LUMP_1, SlimefunItems.MAGIC_LUMP_1, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.magicalResources, SlimefunItems.MAGIC_LUMP_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_2, SlimefunItems.MAGIC_LUMP_2, null, SlimefunItems.MAGIC_LUMP_2, SlimefunItems.MAGIC_LUMP_2, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.magicalResources, SlimefunItems.ENDER_LUMP_1, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, null, new ItemStack(Material.ENDER_EYE), null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.ENDER_LUMP_1, 2))
        .register(plugin);

        new SlimefunItem(categories.magicalResources, SlimefunItems.ENDER_LUMP_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_1, SlimefunItems.ENDER_LUMP_1, null, SlimefunItems.ENDER_LUMP_1, SlimefunItems.ENDER_LUMP_1, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.magicalResources, SlimefunItems.ENDER_LUMP_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_2, SlimefunItems.ENDER_LUMP_2, null, SlimefunItems.ENDER_LUMP_2, SlimefunItems.ENDER_LUMP_2, null, null, null, null})
        .register(plugin);

        new EnderBackpack(categories.magicalGadgets, SlimefunItems.ENDER_BACKPACK, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_2, new ItemStack(Material.LEATHER), SlimefunItems.ENDER_LUMP_2, new ItemStack(Material.LEATHER), new ItemStack(Material.CHEST), new ItemStack(Material.LEATHER), SlimefunItems.ENDER_LUMP_2, new ItemStack(Material.LEATHER), SlimefunItems.ENDER_LUMP_2})
        .register(plugin);

        new SlimefunItem(categories.magicalArmor, SlimefunItems.ENDER_HELMET, RecipeType.ARMOR_FORGE,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_1, new ItemStack(Material.ENDER_EYE), SlimefunItems.ENDER_LUMP_1, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), null, null, null})
        .register(plugin);

        new SlimefunItem(categories.magicalArmor, SlimefunItems.ENDER_CHESTPLATE, RecipeType.ARMOR_FORGE,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_1, null, SlimefunItems.ENDER_LUMP_1, new ItemStack(Material.OBSIDIAN), new ItemStack(Material.ENDER_EYE), new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN)})
        .register(plugin);

        new SlimefunItem(categories.magicalArmor, SlimefunItems.ENDER_LEGGINGS, RecipeType.ARMOR_FORGE,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_1, new ItemStack(Material.ENDER_EYE), SlimefunItems.ENDER_LUMP_1, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN)})
        .register(plugin);

        new EnderBoots(categories.magicalArmor, SlimefunItems.ENDER_BOOTS, RecipeType.ARMOR_FORGE,
        new ItemStack[] {null, null, null, SlimefunItems.ENDER_LUMP_1, null, SlimefunItems.ENDER_LUMP_1, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN)})
        .register(plugin);

        new MagicEyeOfEnder(categories.magicalGadgets, SlimefunItems.MAGIC_EYE_OF_ENDER, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_2, new ItemStack(Material.ENDER_PEARL), SlimefunItems.ENDER_LUMP_2, new ItemStack(Material.ENDER_PEARL), new ItemStack(Material.ENDER_EYE), new ItemStack(Material.ENDER_PEARL), SlimefunItems.ENDER_LUMP_2, new ItemStack(Material.ENDER_PEARL), SlimefunItems.ENDER_LUMP_2})
        .register(plugin);

        new MagicSugar(categories.food, SlimefunItems.MAGIC_SUGAR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.SUGAR), new ItemStack(Material.REDSTONE), new ItemStack(Material.GLOWSTONE_DUST), null, null, null, null, null, null})
        .register(plugin);

        new MonsterJerky(categories.food, SlimefunItems.MONSTER_JERKY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SALT, new ItemStack(Material.ROTTEN_FLESH), null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunArmorPiece(categories.magicalArmor, SlimefunItems.SLIME_HELMET, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.SLIME_BALL), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.SLIME_BALL), new ItemStack(Material.IRON_INGOT), null, new ItemStack(Material.IRON_INGOT), null, null, null}, null)
        .register(plugin);

        new SlimefunArmorPiece(categories.magicalArmor, SlimefunItems.SLIME_CHESTPLATE, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.SLIME_BALL), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.SLIME_BALL), new ItemStack(Material.IRON_INGOT)}, null)
        .register(plugin);

        new SlimefunArmorPiece(categories.magicalArmor, SlimefunItems.SLIME_LEGGINGS, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.SLIME_BALL), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.SLIME_BALL), new ItemStack(Material.IRON_INGOT), null, new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), null, new ItemStack(Material.IRON_INGOT)},
        new PotionEffect[] {new PotionEffect(PotionEffectType.SPEED, 300, 2)})
        .register(plugin);

        new LongFallBoots(categories.magicalArmor, SlimefunItems.SLIME_BOOTS, RecipeType.ARMOR_FORGE,
        new ItemStack[] {null, null, null, new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), new ItemStack(Material.IRON_INGOT), null, new ItemStack(Material.IRON_INGOT)},
        new PotionEffect[] {new PotionEffect(PotionEffectType.JUMP, 300, 5)})
        .register(plugin);

        new SwordOfBeheading(categories.weapons, SlimefunItems.SWORD_OF_BEHEADING, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.EMERALD), null, SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.EMERALD), SlimefunItems.MAGIC_LUMP_2, null, new ItemStack(Material.BLAZE_ROD), null})
        .register(plugin);

        new SlimefunItem(categories.magicalResources, SlimefunItems.MAGICAL_BOOK_COVER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.MAGIC_LUMP_2, null, SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.BOOK), SlimefunItems.MAGIC_LUMP_2, null, SlimefunItems.MAGIC_LUMP_2, null})
        .register(plugin);

        new UnplaceableBlock(categories.magicalResources, SlimefunItems.MAGICAL_GLASS, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_2, SlimefunItems.GOLD_DUST, SlimefunItems.MAGIC_LUMP_2, SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, new ItemStack(Material.GLASS_PANE), SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, SlimefunItems.MAGIC_LUMP_2, SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, SlimefunItems.MAGIC_LUMP_2})
        .register(plugin);

        new BasicCircuitBoard(categories.technicalComponents, SlimefunItems.BASIC_CIRCUIT_BOARD, RecipeType.MOB_DROP,
        new ItemStack[] {null, null, null, null, new CustomItem(SlimefunUtils.getCustomHead(HeadTexture.IRON_GOLEM.getTexture()), "&aIron Golem"), null, null, null, null})
        .register(plugin);

        new UnplaceableBlock(categories.technicalComponents, SlimefunItems.ADVANCED_CIRCUIT_BOARD, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.LAPIS_BLOCK), new ItemStack(Material.LAPIS_BLOCK), new ItemStack(Material.LAPIS_BLOCK), new ItemStack(Material.REDSTONE_BLOCK), SlimefunItems.BASIC_CIRCUIT_BOARD, new ItemStack(Material.REDSTONE_BLOCK), new ItemStack(Material.LAPIS_BLOCK), new ItemStack(Material.LAPIS_BLOCK), new ItemStack(Material.LAPIS_BLOCK)})
        .register(plugin);

        new GoldPan(categories.tools, SlimefunItems.GOLD_PAN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, new ItemStack(Material.STONE), new ItemStack(Material.BOWL), new ItemStack(Material.STONE), new ItemStack(Material.STONE), new ItemStack(Material.STONE), new ItemStack(Material.STONE)})
        .register(plugin);

        new NetherGoldPan(categories.tools, SlimefunItems.NETHER_GOLD_PAN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, new ItemStack(Material.NETHER_BRICK), SlimefunItems.GOLD_PAN, new ItemStack(Material.NETHER_BRICK), new ItemStack(Material.NETHER_BRICK), new ItemStack(Material.NETHER_BRICK), new ItemStack(Material.NETHER_BRICK)})
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.SIFTED_ORE, RecipeType.GOLD_PAN,
        new ItemStack[] {new ItemStack(Material.GRAVEL), null, null, null, null, null, null, null, null})
        .register(plugin);

        new MakeshiftSmeltery(categories.basicMachines, SlimefunItems.MAKESHIFT_SMELTERY).register(plugin);
        new Smeltery(categories.basicMachines, SlimefunItems.SMELTERY).register(plugin);
        
        new IgnitionChamber(categories.basicMachines, SlimefunItems.IGNITION_CHAMBER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.IRON_INGOT), new ItemStack(Material.FLINT_AND_STEEL), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), SlimefunItems.BASIC_CIRCUIT_BOARD, new ItemStack(Material.IRON_INGOT), null, new ItemStack(Material.OBSERVER), null})
        .register(plugin);
        
        new PressureChamber(categories.basicMachines, SlimefunItems.PRESSURE_CHAMBER).register(plugin);

        new UnplaceableBlock(categories.technicalComponents, SlimefunItems.BATTERY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.REDSTONE), null, SlimefunItems.ZINC_INGOT, SlimefunItems.SULFATE, SlimefunItems.COPPER_INGOT, SlimefunItems.ZINC_INGOT, SlimefunItems.SULFATE, SlimefunItems.COPPER_INGOT})
        .register(plugin);

        registerArmorSet(categories.magicalArmor, new ItemStack(Material.GLOWSTONE), new ItemStack[] {SlimefunItems.GLOWSTONE_HELMET, SlimefunItems.GLOWSTONE_CHESTPLATE, SlimefunItems.GLOWSTONE_LEGGINGS, SlimefunItems.GLOWSTONE_BOOTS}, "GLOWSTONE", false,
        new PotionEffect[][] {
            new PotionEffect[] {new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0)}, 
            new PotionEffect[] {new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0)}, 
            new PotionEffect[] {new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0)}, 
            new PotionEffect[] {new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0)}
        }, plugin);

        DyeColor[] rainbowArmorColors = {
                DyeColor.RED,
                DyeColor.ORANGE,
                DyeColor.YELLOW,
                DyeColor.LIME,
                DyeColor.LIGHT_BLUE,
                DyeColor.PURPLE,
                DyeColor.MAGENTA
        };

        new RainbowArmorPiece(categories.magicalArmor, SlimefunItems.RAINBOW_HELMET, RecipeType.ARMOR_FORGE,
        new ItemStack[] { SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, null, SlimefunItems.RAINBOW_LEATHER, null, null, null },
        rainbowArmorColors)
        .register(plugin);

        new RainbowArmorPiece(categories.magicalArmor, SlimefunItems.RAINBOW_CHESTPLATE, RecipeType.ARMOR_FORGE,
        new ItemStack[] { SlimefunItems.RAINBOW_LEATHER, null, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER },
        rainbowArmorColors)
        .register(plugin);

        new RainbowArmorPiece(categories.magicalArmor, SlimefunItems.RAINBOW_LEGGINGS, RecipeType.ARMOR_FORGE,
        new ItemStack[] { SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, null, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, null, SlimefunItems.RAINBOW_LEATHER },
        rainbowArmorColors)
        .register(plugin);

        new RainbowArmorPiece(categories.magicalArmor, SlimefunItems.RAINBOW_BOOTS, RecipeType.ARMOR_FORGE,
        new ItemStack[] { null, null, null, SlimefunItems.RAINBOW_LEATHER, null, SlimefunItems.RAINBOW_LEATHER, SlimefunItems.RAINBOW_LEATHER, null, SlimefunItems.RAINBOW_LEATHER },
        rainbowArmorColors)
        .register(plugin);

        registerArmorSet(categories.armor, SlimefunItems.DAMASCUS_STEEL_INGOT, new ItemStack[] {SlimefunItems.DAMASCUS_STEEL_HELMET, SlimefunItems.DAMASCUS_STEEL_CHESTPLATE, SlimefunItems.DAMASCUS_STEEL_LEGGINGS, SlimefunItems.DAMASCUS_STEEL_BOOTS}, "DAMASCUS_STEEL", false, new PotionEffect[0][0], plugin);

        registerArmorSet(categories.armor, SlimefunItems.REINFORCED_ALLOY_INGOT, new ItemStack[] {SlimefunItems.REINFORCED_ALLOY_HELMET, SlimefunItems.REINFORCED_ALLOY_CHESTPLATE, SlimefunItems.REINFORCED_ALLOY_LEGGINGS, SlimefunItems.REINFORCED_ALLOY_BOOTS}, "REINFORCED_ALLOY", false, new PotionEffect[0][0], plugin);

        registerArmorSet(categories.armor, new ItemStack(Material.CACTUS), new ItemStack[] {SlimefunItems.CACTUS_HELMET, SlimefunItems.CACTUS_CHESTPLATE, SlimefunItems.CACTUS_LEGGINGS, SlimefunItems.CACTUS_BOOTS}, "CACTUS", false, new PotionEffect[0][0], plugin);

        new AlloyIngot(categories.resources, SlimefunItems.REINFORCED_ALLOY_INGOT,
        new ItemStack[] {SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.BILLON_INGOT, SlimefunItems.GOLD_24K, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.HARDENED_METAL_INGOT,
        new ItemStack[] {SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.COMPRESSED_CARBON, SlimefunItems.ALUMINUM_BRONZE_INGOT, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.DAMASCUS_STEEL_INGOT,
        new ItemStack[] {SlimefunItems.STEEL_INGOT, SlimefunItems.IRON_DUST, SlimefunItems.CARBON, new ItemStack(Material.IRON_INGOT), null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.STEEL_INGOT,
        new ItemStack[] {SlimefunItems.IRON_DUST, SlimefunItems.CARBON, new ItemStack(Material.IRON_INGOT), null, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.BRONZE_INGOT,
        new ItemStack[] {SlimefunItems.COPPER_DUST, SlimefunItems.TIN_DUST, SlimefunItems.COPPER_INGOT, null, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.DURALUMIN_INGOT,
        new ItemStack[] {SlimefunItems.ALUMINUM_DUST, SlimefunItems.COPPER_DUST, SlimefunItems.ALUMINUM_INGOT, null, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.BILLON_INGOT,
        new ItemStack[] {SlimefunItems.SILVER_DUST, SlimefunItems.COPPER_DUST, SlimefunItems.SILVER_INGOT, null, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.BRASS_INGOT,
        new ItemStack[] {SlimefunItems.COPPER_DUST, SlimefunItems.ZINC_DUST, SlimefunItems.COPPER_INGOT, null, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.ALUMINUM_BRASS_INGOT,
        new ItemStack[] {SlimefunItems.ALUMINUM_DUST, SlimefunItems.BRASS_INGOT, SlimefunItems.ALUMINUM_INGOT, null, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.ALUMINUM_BRONZE_INGOT,
        new ItemStack[] {SlimefunItems.ALUMINUM_DUST, SlimefunItems.BRONZE_INGOT, SlimefunItems.ALUMINUM_INGOT, null, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.CORINTHIAN_BRONZE_INGOT,
        new ItemStack[] {SlimefunItems.SILVER_DUST, SlimefunItems.GOLD_DUST, SlimefunItems.COPPER_DUST, SlimefunItems.BRONZE_INGOT, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.SOLDER_INGOT,
        new ItemStack[] {SlimefunItems.LEAD_DUST, SlimefunItems.TIN_DUST, SlimefunItems.LEAD_INGOT, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.SYNTHETIC_SAPPHIRE, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.ALUMINUM_DUST, new ItemStack(Material.GLASS), new ItemStack(Material.GLASS_PANE), SlimefunItems.ALUMINUM_INGOT, new ItemStack(Material.LAPIS_LAZULI), null, null, null, null})
        .setUseableInWorkbench(true)
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.SYNTHETIC_DIAMOND, RecipeType.PRESSURE_CHAMBER,
        new ItemStack[] {SlimefunItems.CARBON_CHUNK, null, null, null, null, null, null, null, null})
        .setUseableInWorkbench(true)
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.RAW_CARBONADO, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.SYNTHETIC_DIAMOND, SlimefunItems.CARBON_CHUNK, new ItemStack(Material.GLASS_PANE), null, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.NICKEL_INGOT,
        new ItemStack[] {SlimefunItems.IRON_DUST, new ItemStack(Material.IRON_INGOT), SlimefunItems.COPPER_DUST, null, null, null, null, null, null})
        .register(plugin);

        new AlloyIngot(categories.resources, SlimefunItems.COBALT_INGOT,
        new ItemStack[] {SlimefunItems.IRON_DUST, SlimefunItems.COPPER_DUST, SlimefunItems.NICKEL_INGOT, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.CARBONADO, RecipeType.PRESSURE_CHAMBER,
        new ItemStack[] {SlimefunItems.RAW_CARBONADO, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.FERROSILICON, RecipeType.SMELTERY,
        new ItemStack[] {new ItemStack(Material.IRON_INGOT), SlimefunItems.IRON_DUST, SlimefunItems.SILICON, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.IRON_DUST, RecipeType.ORE_CRUSHER,
        new ItemStack[] {new ItemStack(Material.IRON_ORE), null, null, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.IRON_DUST, oreCrusher.isOreDoublingEnabled() ? 2 : 1))
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.GOLD_DUST, RecipeType.ORE_CRUSHER,
        new ItemStack[] {new ItemStack(Material.GOLD_ORE), null, null, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.GOLD_DUST, oreCrusher.isOreDoublingEnabled() ? 2 : 1))
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.COPPER_DUST, RecipeType.ORE_WASHER,
        new ItemStack[] {SlimefunItems.SIFTED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.TIN_DUST, RecipeType.ORE_WASHER,
        new ItemStack[] {SlimefunItems.SIFTED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.LEAD_DUST, RecipeType.ORE_WASHER,
        new ItemStack[] {SlimefunItems.SIFTED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);
        
        new SlimefunItem(categories.resources, SlimefunItems.SILVER_DUST, RecipeType.ORE_WASHER,
        new ItemStack[] {SlimefunItems.SIFTED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.ALUMINUM_DUST, RecipeType.ORE_WASHER,
        new ItemStack[] {SlimefunItems.SIFTED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.ZINC_DUST, RecipeType.ORE_WASHER,
        new ItemStack[] {SlimefunItems.SIFTED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.MAGNESIUM_DUST, RecipeType.ORE_WASHER,
        new ItemStack[] {SlimefunItems.SIFTED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.COPPER_INGOT, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.COPPER_DUST, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.TIN_INGOT, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.TIN_DUST, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.SILVER_INGOT, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.SILVER_DUST, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.LEAD_INGOT, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.LEAD_DUST, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.ALUMINUM_INGOT, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.ALUMINUM_DUST, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.ZINC_INGOT, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.ZINC_DUST, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.MAGNESIUM_INGOT, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.MAGNESIUM_DUST, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.SULFATE, RecipeType.ORE_CRUSHER,
        new ItemStack[] {new ItemStack(Material.NETHERRACK, 16), null, null, null, null, null, null, null, null})
        .register(plugin);

        new UnplaceableBlock(categories.resources, SlimefunItems.CARBON, RecipeType.COMPRESSOR,
        new ItemStack[] {new ItemStack(Material.COAL, 8), null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.WHEAT_FLOUR, RecipeType.GRIND_STONE,
        new ItemStack[] {new ItemStack(Material.WHEAT), null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.STEEL_PLATE, RecipeType.COMPRESSOR,
        new ItemStack[] {new SlimefunItemStack(SlimefunItems.STEEL_INGOT, 8), null, null, null, null, null, null, null, null})
        .register(plugin);

        new UnplaceableBlock(categories.resources, SlimefunItems.COMPRESSED_CARBON, RecipeType.COMPRESSOR,
        new ItemStack[] {new SlimefunItemStack(SlimefunItems.CARBON, 4), null, null, null, null, null, null, null, null})
        .register(plugin);

        new UnplaceableBlock(categories.resources, SlimefunItems.CARBON_CHUNK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.COMPRESSED_CARBON, SlimefunItems.COMPRESSED_CARBON, SlimefunItems.COMPRESSED_CARBON, SlimefunItems.COMPRESSED_CARBON, new ItemStack(Material.FLINT), SlimefunItems.COMPRESSED_CARBON, SlimefunItems.COMPRESSED_CARBON, SlimefunItems.COMPRESSED_CARBON, SlimefunItems.COMPRESSED_CARBON})
        .register(plugin);

        new SteelThruster(categories.technicalComponents, SlimefunItems.STEEL_THRUSTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.REDSTONE), null, SlimefunItems.ALUMINUM_BRONZE_INGOT, SlimefunItems.ALUMINUM_BRONZE_INGOT, SlimefunItems.ALUMINUM_BRONZE_INGOT, SlimefunItems.STEEL_PLATE, new ItemStack(Material.FIRE_CHARGE), SlimefunItems.STEEL_PLATE})
        .register(plugin);

        new SlimefunItem(categories.technicalComponents, SlimefunItems.POWER_CRYSTAL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.REDSTONE), SlimefunItems.SYNTHETIC_SAPPHIRE, new ItemStack(Material.REDSTONE), SlimefunItems.SYNTHETIC_SAPPHIRE, SlimefunItems.SYNTHETIC_DIAMOND, SlimefunItems.SYNTHETIC_SAPPHIRE, new ItemStack(Material.REDSTONE), SlimefunItems.SYNTHETIC_SAPPHIRE, new ItemStack(Material.REDSTONE)})
        .register(plugin);

        new Jetpack(categories.technicalGadgets, SlimefunItems.DURALUMIN_JETPACK,
        new ItemStack[] {SlimefunItems.DURALUMIN_INGOT, null, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.35, 20)
        .register(plugin);

        new Jetpack(categories.technicalGadgets, SlimefunItems.SOLDER_JETPACK,
        new ItemStack[] {SlimefunItems.SOLDER_INGOT, null, SlimefunItems.SOLDER_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.SOLDER_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.4, 30)
        .register(plugin);

        new Jetpack(categories.technicalGadgets, SlimefunItems.BILLON_JETPACK,
        new ItemStack[] {SlimefunItems.BILLON_INGOT, null, SlimefunItems.BILLON_INGOT, SlimefunItems.BILLON_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.BILLON_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.45, 45)
        .register(plugin);

        new Jetpack(categories.technicalGadgets, SlimefunItems.STEEL_JETPACK,
        new ItemStack[] {SlimefunItems.STEEL_INGOT, null, SlimefunItems.STEEL_INGOT, SlimefunItems.STEEL_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.STEEL_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.5, 60)
        .register(plugin);

        new Jetpack(categories.technicalGadgets, SlimefunItems.DAMASCUS_STEEL_JETPACK,
        new ItemStack[] {SlimefunItems.DAMASCUS_STEEL_INGOT, null, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.55, 75)
        .register(plugin);

        new Jetpack(categories.technicalGadgets, SlimefunItems.REINFORCED_ALLOY_JETPACK,
        new ItemStack[] {SlimefunItems.REINFORCED_ALLOY_INGOT, null, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.6, 100)
        .register(plugin);

        new Jetpack(categories.technicalGadgets, SlimefunItems.CARBONADO_JETPACK,
        new ItemStack[] {SlimefunItems.CARBON_CHUNK, null, SlimefunItems.CARBON_CHUNK, SlimefunItems.CARBONADO, SlimefunItems.POWER_CRYSTAL, SlimefunItems.CARBONADO, SlimefunItems.STEEL_THRUSTER, SlimefunItems.LARGE_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.7, 150)
        .register(plugin);

        new Parachute(categories.technicalGadgets, SlimefunItems.PARACHUTE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CLOTH, SlimefunItems.CLOTH, SlimefunItems.CLOTH, SlimefunItems.CHAIN, null, SlimefunItems.CHAIN, null, null, null})
        .register(plugin);

        new HologramProjector(categories.technicalGadgets, SlimefunItems.HOLOGRAM_PROJECTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.POWER_CRYSTAL, null, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ALUMINUM_BRASS_INGOT, null, SlimefunItems.ALUMINUM_BRASS_INGOT, null}, 
        new SlimefunItemStack(SlimefunItems.HOLOGRAM_PROJECTOR, 3))
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.CHAIN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.STEEL_INGOT, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHAIN, 8))
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.HOOK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.STEEL_INGOT, null, null, null})
        .register(plugin);

        new GrapplingHook(categories.tools, SlimefunItems.GRAPPLING_HOOK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, SlimefunItems.HOOK, null, SlimefunItems.CHAIN, null, SlimefunItems.CHAIN, null, null})
        .register(plugin);

        new MagicWorkbench(categories.basicMachines, SlimefunItems.MAGIC_WORKBENCH).register(plugin);

        new SlimefunItem(categories.magicalGadgets, SlimefunItems.STAFF_ELEMENTAL, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.MAGICAL_BOOK_COVER, SlimefunItems.MAGIC_LUMP_3, null, new ItemStack(Material.STICK), SlimefunItems.MAGICAL_BOOK_COVER, SlimefunItems.MAGIC_LUMP_3, null, null})
        .register(plugin);

        new WindStaff(categories.magicalGadgets, SlimefunItems.STAFF_WIND, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, new ItemStack(Material.FEATHER), SlimefunItems.ENDER_LUMP_3, null, SlimefunItems.STAFF_ELEMENTAL, new ItemStack(Material.FEATHER), SlimefunItems.STAFF_ELEMENTAL, null, null})
        .register(plugin);

        new WaterStaff(categories.magicalGadgets, SlimefunItems.STAFF_WATER, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, new ItemStack(Material.LILY_PAD), SlimefunItems.MAGIC_LUMP_2, null, SlimefunItems.STAFF_ELEMENTAL, new ItemStack(Material.LILY_PAD), SlimefunItems.STAFF_ELEMENTAL, null, null})
        .register(plugin);
        
        String[] multiToolItems = new String[] {"PORTABLE_CRAFTER", "MAGIC_EYE_OF_ENDER", "STAFF_ELEMENTAL_WIND", "GRAPPLING_HOOK"};

        new MultiTool(categories.technicalGadgets, SlimefunItems.DURALUMIN_MULTI_TOOL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.DURALUMIN_INGOT, null, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.DURALUMIN_INGOT, null, SlimefunItems.DURALUMIN_INGOT, null},
        20, multiToolItems)
        .register(plugin);

        new MultiTool(categories.technicalGadgets, SlimefunItems.SOLDER_MULTI_TOOL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SOLDER_INGOT, null, SlimefunItems.SOLDER_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.SOLDER_INGOT, null, SlimefunItems.SOLDER_INGOT, null},
        30, multiToolItems)
        .register(plugin);

        new MultiTool(categories.technicalGadgets, SlimefunItems.BILLON_MULTI_TOOL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.BILLON_INGOT, null, SlimefunItems.BILLON_INGOT, SlimefunItems.BILLON_INGOT, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.BILLON_INGOT, null, SlimefunItems.BILLON_INGOT, null},
        40, multiToolItems)
        .register(plugin);

        new MultiTool(categories.technicalGadgets, SlimefunItems.STEEL_MULTI_TOOL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.STEEL_INGOT, null, SlimefunItems.STEEL_INGOT, SlimefunItems.STEEL_INGOT, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_INGOT, null, SlimefunItems.STEEL_INGOT, null},
        50, multiToolItems)
        .register(plugin);

        new MultiTool(categories.technicalGadgets, SlimefunItems.DAMASCUS_STEEL_MULTI_TOOL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.DAMASCUS_STEEL_INGOT, null, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.DAMASCUS_STEEL_INGOT, null, SlimefunItems.DAMASCUS_STEEL_INGOT, null},
        60, multiToolItems)
        .register(plugin);

        new MultiTool(categories.technicalGadgets, SlimefunItems.REINFORCED_ALLOY_MULTI_TOOL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.REINFORCED_ALLOY_INGOT, null, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.REINFORCED_ALLOY_INGOT, null, SlimefunItems.REINFORCED_ALLOY_INGOT, null},
        75, multiToolItems)
        .register(plugin);

        new MultiTool(categories.technicalGadgets, SlimefunItems.CARBONADO_MULTI_TOOL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CARBONADO, null, SlimefunItems.CARBONADO, SlimefunItems.CARBONADO, SlimefunItems.LARGE_CAPACITOR, SlimefunItems.CARBONADO, null, SlimefunItems.CARBONADO, null},
        100, "PORTABLE_CRAFTER", "MAGIC_EYE_OF_ENDER", "STAFF_ELEMENTAL_WIND", "GRAPPLING_HOOK", "GOLD_PAN", "NETHER_GOLD_PAN")
        .register(plugin);

        new OreWasher(categories.basicMachines, SlimefunItems.ORE_WASHER).register(plugin);

        new GoldIngot(categories.resources, 24, SlimefunItems.GOLD_24K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, SlimefunItems.GOLD_22K, null, null, null, null, null, null, null})
        .register(plugin);

        new GoldIngot(categories.resources, 22, SlimefunItems.GOLD_22K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, SlimefunItems.GOLD_20K, null, null, null, null, null, null, null})
        .register(plugin);

        new GoldIngot(categories.resources, 20, SlimefunItems.GOLD_20K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, SlimefunItems.GOLD_18K, null, null, null, null, null, null, null})
        .register(plugin);

        new GoldIngot(categories.resources, 18, SlimefunItems.GOLD_18K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, SlimefunItems.GOLD_16K, null, null, null, null, null, null, null})
        .register(plugin);

        new GoldIngot(categories.resources, 16, SlimefunItems.GOLD_16K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, SlimefunItems.GOLD_14K, null, null, null, null, null, null, null})
        .register(plugin);

        new GoldIngot(categories.resources, 14, SlimefunItems.GOLD_14K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, SlimefunItems.GOLD_12K, null, null, null, null, null, null, null})
        .register(plugin);

        new GoldIngot(categories.resources, 12, SlimefunItems.GOLD_12K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, SlimefunItems.GOLD_10K, null, null, null, null, null, null, null})
        .register(plugin);

        new GoldIngot(categories.resources, 10, SlimefunItems.GOLD_10K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, SlimefunItems.GOLD_8K, null, null, null, null, null, null, null})
        .register(plugin);

        new GoldIngot(categories.resources, 8, SlimefunItems.GOLD_8K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, SlimefunItems.GOLD_6K, null, null, null, null, null, null, null})
        .register(plugin);

        new GoldIngot(categories.resources, 6, SlimefunItems.GOLD_6K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, SlimefunItems.GOLD_4K, null, null, null, null, null, null, null})
        .register(plugin);

        new GoldIngot(categories.resources, 4, SlimefunItems.GOLD_4K, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_DUST, null, null, null, null, null, null, null, null})
        .setUseableInWorkbench(true)
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.STONE_CHUNK, RecipeType.ORE_WASHER,
        new ItemStack[] {SlimefunItems.SIFTED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.SILICON, RecipeType.SMELTERY,
        new ItemStack[] {new ItemStack(Material.QUARTZ_BLOCK), null, null, null, null, null, null, null, null})
        .register(plugin);

        new UnplaceableBlock(categories.technicalComponents, SlimefunItems.SOLAR_PANEL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.GLASS), new ItemStack(Material.GLASS), new ItemStack(Material.GLASS), SlimefunItems.SILICON, SlimefunItems.SILICON, SlimefunItems.SILICON, SlimefunItems.FERROSILICON, SlimefunItems.FERROSILICON, SlimefunItems.FERROSILICON})
        .register(plugin);

        new SolarHelmet(categories.technicalGadgets, SlimefunItems.SOLAR_HELMET, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.SOLAR_PANEL, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, null, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.MEDIUM_CAPACITOR, null, SlimefunItems.MEDIUM_CAPACITOR},
        0.1)
        .register(plugin);

        new UnplaceableBlock(categories.magicalResources, SlimefunItems.LAVA_CRYSTAL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.BLAZE_POWDER), SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.BLAZE_POWDER), SlimefunItems.FIRE_RUNE, new ItemStack(Material.BLAZE_POWDER), SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.BLAZE_POWDER), SlimefunItems.MAGIC_LUMP_1})
        .register(plugin);

        new SlimefunItem(categories.magicalGadgets, SlimefunItems.STAFF_FIRE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, null, SlimefunItems.LAVA_CRYSTAL, null, SlimefunItems.STAFF_ELEMENTAL, null, SlimefunItems.STAFF_ELEMENTAL, null, null})
        .register(plugin);
        
        new StormStaff(categories.magicalGadgets, SlimefunItems.STAFF_STORM, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.LIGHTNING_RUNE, SlimefunItems.ENDER_LUMP_3, SlimefunItems.LIGHTNING_RUNE, SlimefunItems.STAFF_WATER, SlimefunItems.MAGIC_SUGAR, SlimefunItems.STAFF_WIND, SlimefunItems.LIGHTNING_RUNE, SlimefunItems.ENDER_LUMP_3, SlimefunItems.LIGHTNING_RUNE})
        .register(plugin);

        ItemStack weaknessPotion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) weaknessPotion.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.WEAKNESS, false, false));
        weaknessPotion.setItemMeta(meta);

        new MagicalZombiePills(categories.magicalGadgets, SlimefunItems.MAGICAL_ZOMBIE_PILLS, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {new ItemStack(Material.GOLD_INGOT), SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.APPLE), weaknessPotion, new ItemStack(Material.APPLE), new ItemStack(Material.GOLD_INGOT), SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.GOLD_INGOT)},
        new SlimefunItemStack(SlimefunItems.MAGICAL_ZOMBIE_PILLS, 2))
        .register(plugin);

        new SmeltersPickaxe(categories.tools, SlimefunItems.SMELTERS_PICKAXE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.LAVA_CRYSTAL, SlimefunItems.LAVA_CRYSTAL, SlimefunItems.LAVA_CRYSTAL, null, SlimefunItems.FERROSILICON, null, null, SlimefunItems.FERROSILICON, null})
        .register(plugin);

        new SlimefunItem(categories.magicalResources, SlimefunItems.COMMON_TALISMAN, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_2, SlimefunItems.GOLD_8K, SlimefunItems.MAGIC_LUMP_2, null, new ItemStack(Material.EMERALD), null, SlimefunItems.MAGIC_LUMP_2, SlimefunItems.GOLD_8K, SlimefunItems.MAGIC_LUMP_2})
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_ANVIL,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, new ItemStack(Material.ANVIL), SlimefunItems.COMMON_TALISMAN, new ItemStack(Material.ANVIL), SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        true, false, "anvil")
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_MINER,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.SYNTHETIC_SAPPHIRE, SlimefunItems.COMMON_TALISMAN, SlimefunItems.SIFTED_ORE, SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        false, false, "miner", 20)
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_HUNTER,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.SYNTHETIC_SAPPHIRE, SlimefunItems.COMMON_TALISMAN, SlimefunItems.MONSTER_JERKY, SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        false, false, "hunter", 20)
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_LAVA,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.LAVA_CRYSTAL, SlimefunItems.COMMON_TALISMAN, new ItemStack(Material.LAVA_BUCKET), SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        true, true, "lava", new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 3600, 4))
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_WATER,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, new ItemStack(Material.WATER_BUCKET), SlimefunItems.COMMON_TALISMAN, new ItemStack(Material.FISHING_ROD), SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        true, true, "water", new PotionEffect(PotionEffectType.WATER_BREATHING, 3600, 4))
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_ANGEL,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, new ItemStack(Material.FEATHER), SlimefunItems.COMMON_TALISMAN, new ItemStack(Material.FEATHER), SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        false, true, "angel", 75)
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_FIRE,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.LAVA_CRYSTAL, SlimefunItems.COMMON_TALISMAN, SlimefunItems.LAVA_CRYSTAL, SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        true, true, "fire", new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 3600, 4))
        .register(plugin);

        new MagicianTalisman(SlimefunItems.TALISMAN_MAGICIAN,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_3, null, SlimefunItems.ENDER_LUMP_3, new ItemStack(Material.ENCHANTING_TABLE), SlimefunItems.COMMON_TALISMAN, new ItemStack(Material.ENCHANTING_TABLE), SlimefunItems.ENDER_LUMP_3, null, SlimefunItems.ENDER_LUMP_3})
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_TRAVELLER,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.STAFF_WIND, SlimefunItems.TALISMAN_ANGEL, SlimefunItems.STAFF_WIND, SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        false, false, "traveller", 60, new PotionEffect(PotionEffectType.SPEED, 3600, 2))
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_WARRIOR,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.COMMON_TALISMAN, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        true, true, "warrior", new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3600, 2))
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_KNIGHT,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.GILDED_IRON, SlimefunItems.TALISMAN_WARRIOR, SlimefunItems.GILDED_IRON, SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        "knight", 30, new PotionEffect(PotionEffectType.REGENERATION, 100, 3))
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_CAVEMAN,
        new ItemStack[] { SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, new ItemStack(Material.GOLDEN_PICKAXE), SlimefunItems.TALISMAN_MINER, SlimefunItems.EARTH_RUNE, SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3},
        false, false, "caveman", 50, new PotionEffect(PotionEffectType.FAST_DIGGING, 800, 2))
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_WISE,
        new ItemStack[] { SlimefunItems.MAGIC_LUMP_3, SlimefunItems.MAGICAL_GLASS, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, SlimefunItems.TALISMAN_MAGICIAN, SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.MAGICAL_GLASS, SlimefunItems.MAGIC_LUMP_3},
        false, false, "wise", 20)
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.GILDED_IRON, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.GOLD_24K, SlimefunItems.IRON_DUST, null, null, null, null, null, null, null})
        .register(plugin);

        new SyntheticEmerald(categories.resources, SlimefunItems.SYNTHETIC_EMERALD, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.SYNTHETIC_SAPPHIRE, SlimefunItems.ALUMINUM_DUST, SlimefunItems.ALUMINUM_INGOT, new ItemStack(Material.GLASS_PANE), null, null, null, null, null})
        .register(plugin);

        registerArmorSet(categories.armor, SlimefunItems.CHAIN, new ItemStack[] {
            new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_BOOTS)
        }, "CHAIN", true, new PotionEffect[0][0], plugin);

        new Talisman(SlimefunItems.TALISMAN_WHIRLWIND,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.STAFF_WIND, SlimefunItems.TALISMAN_TRAVELLER, SlimefunItems.STAFF_WIND, SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3}
        , false, true, "whirlwind", 60)
        .register(plugin);

        new Talisman(SlimefunItems.TALISMAN_WIZARD,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_3, null, SlimefunItems.ENDER_LUMP_3, SlimefunItems.MAGIC_EYE_OF_ENDER, SlimefunItems.TALISMAN_MAGICIAN, SlimefunItems.MAGIC_EYE_OF_ENDER, SlimefunItems.ENDER_LUMP_3, null, SlimefunItems.ENDER_LUMP_3},
        false, false, "wizard", 60)
        .register(plugin);
        
        new LumberAxe(categories.tools, SlimefunItems.LUMBER_AXE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.SYNTHETIC_DIAMOND, SlimefunItems.SYNTHETIC_DIAMOND, null, SlimefunItems.SYNTHETIC_EMERALD, SlimefunItems.GILDED_IRON, null, null, SlimefunItems.GILDED_IRON, null})
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.SALT, RecipeType.ORE_WASHER,
        new ItemStack[] {new ItemStack(Material.SAND, 2), null, null, null, null, null, null, null, null})
        .register(plugin);

        new HeavyCream(categories.misc, SlimefunItems.HEAVY_CREAM, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.MILK_BUCKET), null, null, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.HEAVY_CREAM, 2))
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.CHEESE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.MILK_BUCKET), SlimefunItems.SALT, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.BUTTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.HEAVY_CREAM, SlimefunItems.SALT, null, null, null, null, null, null, null})
        .register(plugin);

        registerArmorSet(categories.armor, SlimefunItems.GILDED_IRON, new ItemStack[] {
             SlimefunItems.GILDED_IRON_HELMET, SlimefunItems.GILDED_IRON_CHESTPLATE, SlimefunItems.GILDED_IRON_LEGGINGS, SlimefunItems.GILDED_IRON_BOOTS
        }, "GILDED_IRON", false, new PotionEffect[0][0], plugin);
        
        new SlimefunItem(categories.technicalComponents, SlimefunItems.REINFORCED_CLOTH, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.CLOTH, null, SlimefunItems.CLOTH, SlimefunItems.LEAD_INGOT, SlimefunItems.CLOTH, null, SlimefunItems.CLOTH, null}, new SlimefunItemStack(SlimefunItems.REINFORCED_CLOTH, 2))
        .register(plugin);

        new HazmatArmorPiece(categories.armor, SlimefunItems.SCUBA_HELMET, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.ORANGE_WOOL), SlimefunItems.REINFORCED_CLOTH, new ItemStack(Material.ORANGE_WOOL), SlimefunItems.REINFORCED_CLOTH, new ItemStack(Material.GLASS_PANE), SlimefunItems.REINFORCED_CLOTH, null, null, null},
        new PotionEffect[] {new PotionEffect(PotionEffectType.WATER_BREATHING, 300, 1)})
        .register(plugin);

        new HazmatArmorPiece(categories.armor, SlimefunItems.HAZMAT_CHESTPLATE, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.ORANGE_WOOL), null, new ItemStack(Material.ORANGE_WOOL), SlimefunItems.REINFORCED_CLOTH, SlimefunItems.REINFORCED_CLOTH, SlimefunItems.REINFORCED_CLOTH, new ItemStack(Material.BLACK_WOOL), SlimefunItems.REINFORCED_CLOTH, new ItemStack(Material.BLACK_WOOL)},
        new PotionEffect[] {new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300, 1)})
        .register(plugin);

        new HazmatArmorPiece(categories.armor, SlimefunItems.HAZMAT_LEGGINGS, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.BLACK_WOOL), SlimefunItems.REINFORCED_CLOTH, new ItemStack(Material.BLACK_WOOL), SlimefunItems.REINFORCED_CLOTH, null, SlimefunItems.REINFORCED_CLOTH, SlimefunItems.REINFORCED_CLOTH, null, SlimefunItems.REINFORCED_CLOTH}, new PotionEffect[0])
        .register(plugin);

        new HazmatArmorPiece(categories.armor, SlimefunItems.HAZMAT_BOOTS, RecipeType.ARMOR_FORGE,
        new ItemStack[] {SlimefunItems.REINFORCED_CLOTH, null, SlimefunItems.REINFORCED_CLOTH, SlimefunItems.REINFORCED_CLOTH, null, SlimefunItems.REINFORCED_CLOTH, new ItemStack(Material.BLACK_WOOL), null, new ItemStack(Material.BLACK_WOOL)}, new PotionEffect[0])
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.CRUSHED_ORE, RecipeType.ORE_CRUSHER,
        new ItemStack[] {SlimefunItems.SIFTED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.PULVERIZED_ORE, RecipeType.ORE_CRUSHER,
        new ItemStack[] {SlimefunItems.CRUSHED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.PURE_ORE_CLUSTER, RecipeType.ORE_WASHER,
        new ItemStack[] {SlimefunItems.PULVERIZED_ORE, null, null, null, null, null, null, null, null})
        .register(plugin);

        new UnplaceableBlock(categories.misc, SlimefunItems.TINY_URANIUM, RecipeType.ORE_CRUSHER,
        new ItemStack[] {SlimefunItems.PURE_ORE_CLUSTER, null, null, null, null, null, null, null, null})
        .register(plugin);

        new RadioactiveItem(categories.misc, Radioactivity.MODERATE, SlimefunItems.SMALL_URANIUM, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.TINY_URANIUM, SlimefunItems.TINY_URANIUM, SlimefunItems.TINY_URANIUM, SlimefunItems.TINY_URANIUM, SlimefunItems.TINY_URANIUM, SlimefunItems.TINY_URANIUM, SlimefunItems.TINY_URANIUM, SlimefunItems.TINY_URANIUM, SlimefunItems.TINY_URANIUM})
        .register(plugin);

        new RadioactiveItem(categories.resources, Radioactivity.HIGH, SlimefunItems.URANIUM, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SMALL_URANIUM, SlimefunItems.SMALL_URANIUM, null, SlimefunItems.SMALL_URANIUM, SlimefunItems.SMALL_URANIUM, null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.REDSTONE_ALLOY, RecipeType.SMELTERY,
        new ItemStack[] {new ItemStack(Material.REDSTONE), new ItemStack(Material.REDSTONE_BLOCK), SlimefunItems.FERROSILICON, SlimefunItems.HARDENED_METAL_INGOT, null, null, null, null, null})
        .register(plugin);

        registerArmorSet(categories.armor, SlimefunItems.GOLD_12K, new ItemStack[] {
            SlimefunItems.GOLDEN_HELMET_12K, SlimefunItems.GOLDEN_CHESTPLATE_12K, SlimefunItems.GOLDEN_LEGGINGS_12K, SlimefunItems.GOLDEN_BOOTS_12K
        }, "GOLD_12K", false, new PotionEffect[0][0], plugin);

        new SlimefunItem(categories.misc, SlimefunItems.CLOTH, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.WHITE_WOOL), null, null, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CLOTH, 8))
        .register(plugin);

        new Bandage(categories.usefulItems, SlimefunItems.RAG, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CLOTH, SlimefunItems.CLOTH, SlimefunItems.CLOTH, new ItemStack(Material.STRING), null, new ItemStack(Material.STRING), SlimefunItems.CLOTH, SlimefunItems.CLOTH, SlimefunItems.CLOTH},
        new SlimefunItemStack(SlimefunItems.RAG, 2), 0)
        .register(plugin);

        new Bandage(categories.usefulItems, SlimefunItems.BANDAGE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.RAG, new ItemStack(Material.STRING), SlimefunItems.RAG, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.BANDAGE, 4), 1)
        .register(plugin);

        new Splint(categories.usefulItems, SlimefunItems.SPLINT, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.IRON_INGOT), null, new ItemStack(Material.STICK), new ItemStack(Material.STICK), new ItemStack(Material.STICK), null, new ItemStack(Material.IRON_INGOT), null}, 
        new SlimefunItemStack(SlimefunItems.SPLINT, 4))
        .register(plugin);

        new UnplaceableBlock(categories.misc, SlimefunItems.TIN_CAN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.TIN_INGOT, SlimefunItems.TIN_INGOT, SlimefunItems.TIN_INGOT, SlimefunItems.TIN_INGOT, null, SlimefunItems.TIN_INGOT, SlimefunItems.TIN_INGOT, SlimefunItems.TIN_INGOT, SlimefunItems.TIN_INGOT}, 
        new SlimefunItemStack(SlimefunItems.TIN_CAN, 8))
        .register(plugin);

        new Vitamins(categories.usefulItems, SlimefunItems.VITAMINS, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.TIN_CAN, new ItemStack(Material.APPLE), new ItemStack(Material.RED_MUSHROOM), new ItemStack(Material.SUGAR), null, null, null, null, null})
        .register(plugin);

        new Medicine(categories.usefulItems, SlimefunItems.MEDICINE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.VITAMINS, new ItemStack(Material.GLASS_BOTTLE), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null})
        .register(plugin);

        new SlimefunArmorPiece(categories.technicalGadgets, SlimefunItems.NIGHT_VISION_GOGGLES, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.COAL_BLOCK), new ItemStack(Material.COAL_BLOCK), new ItemStack(Material.COAL_BLOCK), new ItemStack(Material.LIME_STAINED_GLASS_PANE), new ItemStack(Material.COAL_BLOCK), new ItemStack(Material.LIME_STAINED_GLASS_PANE), new ItemStack(Material.COAL_BLOCK), null, new ItemStack(Material.COAL_BLOCK)},
        new PotionEffect[] {new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 20)})
        .register(plugin);

        new PickaxeOfContainment(categories.tools, SlimefunItems.PICKAXE_OF_CONTAINMENT, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.FERROSILICON, SlimefunItems.FERROSILICON, SlimefunItems.FERROSILICON, null, SlimefunItems.GILDED_IRON, null, null, SlimefunItems.GILDED_IRON, null})
        .register(plugin);

        new HerculesPickaxe(categories.tools, SlimefunItems.HERCULES_PICKAXE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.HARDENED_METAL_INGOT, null, SlimefunItems.FERROSILICON, null, null, SlimefunItems.FERROSILICON, null})
        .register(plugin);
        
        new TableSaw(categories.basicMachines, SlimefunItems.TABLE_SAW).register(plugin);

        new SlimefunArmorPiece(categories.magicalArmor, SlimefunItems.SLIME_HELMET_STEEL, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.SLIME_BALL), SlimefunItems.STEEL_PLATE, new ItemStack(Material.SLIME_BALL), new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), null, null, null}, null)
        .register(plugin);

        new SlimefunArmorPiece(categories.magicalArmor, SlimefunItems.SLIME_CHESTPLATE_STEEL, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), new ItemStack(Material.SLIME_BALL), SlimefunItems.STEEL_PLATE, new ItemStack(Material.SLIME_BALL), new ItemStack(Material.SLIME_BALL), new ItemStack(Material.SLIME_BALL), new ItemStack(Material.SLIME_BALL)}, null)
        .register(plugin);

        new SlimefunArmorPiece(categories.magicalArmor, SlimefunItems.SLIME_LEGGINGS_STEEL, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.SLIME_BALL), SlimefunItems.STEEL_PLATE, new ItemStack(Material.SLIME_BALL), new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL)},
        new PotionEffect[] {new PotionEffect(PotionEffectType.SPEED, 300, 2)})
        .register(plugin);

        new LongFallBoots(categories.magicalArmor, SlimefunItems.SLIME_BOOTS_STEEL, RecipeType.ARMOR_FORGE,
        new ItemStack[] {null, null, null, new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), new ItemStack(Material.SLIME_BALL), SlimefunItems.STEEL_PLATE, new ItemStack(Material.SLIME_BALL)},
        new PotionEffect[] {new PotionEffect(PotionEffectType.JUMP, 300, 5)})
        .register(plugin);

        new VampireBlade(categories.weapons, SlimefunItems.BLADE_OF_VAMPIRES, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, new ItemStack(Material.WITHER_SKELETON_SKULL), null, null, new ItemStack(Material.WITHER_SKELETON_SKULL), null, null, new ItemStack(Material.BLAZE_ROD), null})
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.GOLD_24K_BLOCK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GOLD_24K, SlimefunItems.GOLD_24K, SlimefunItems.GOLD_24K, SlimefunItems.GOLD_24K, SlimefunItems.GOLD_24K, SlimefunItems.GOLD_24K, SlimefunItems.GOLD_24K, SlimefunItems.GOLD_24K, SlimefunItems.GOLD_24K})
        .register(plugin);

        new Composter(categories.basicMachines, SlimefunItems.COMPOSTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.OAK_SLAB), null, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), null, new ItemStack(Material.OAK_SLAB), new ItemStack(Material.OAK_SLAB), new ItemStack(Material.CAULDRON), new ItemStack(Material.OAK_SLAB)})
        .register(plugin);

        new FarmerShoes(categories.magicalArmor, SlimefunItems.FARMER_SHOES, RecipeType.ARMOR_FORGE,
        new ItemStack[] {null, null, null, new ItemStack(Material.HAY_BLOCK), null, new ItemStack(Material.HAY_BLOCK), new ItemStack(Material.HAY_BLOCK), null, new ItemStack(Material.HAY_BLOCK)})
        .register(plugin);
        
        new ExplosivePickaxe(categories.tools, SlimefunItems.EXPLOSIVE_PICKAXE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {new ItemStack(Material.TNT), SlimefunItems.SYNTHETIC_DIAMOND, new ItemStack(Material.TNT), null, SlimefunItems.FERROSILICON, null, null, SlimefunItems.FERROSILICON, null})
        .register(plugin);

        new ExplosiveShovel(categories.tools, SlimefunItems.EXPLOSIVE_SHOVEL, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.SYNTHETIC_DIAMOND, null, null, new ItemStack(Material.TNT), null, null, SlimefunItems.FERROSILICON, null})
        .register(plugin);

        new AutomatedPanningMachine(categories.basicMachines, SlimefunItems.AUTOMATED_PANNING_MACHINE).register(plugin);
        
        new IndustrialMiner(categories.basicMachines, SlimefunItems.INDUSTRIAL_MINER, Material.IRON_BLOCK, false, 3).register(plugin);
        new AdvancedIndustrialMiner(categories.basicMachines, SlimefunItems.ADVANCED_INDUSTRIAL_MINER).register(plugin);

        new StomperBoots(categories.magicalArmor, SlimefunItems.BOOTS_OF_THE_STOMPER, RecipeType.ARMOR_FORGE,
        new ItemStack[] {null, null, null, new ItemStack(Material.YELLOW_WOOL), null, new ItemStack(Material.YELLOW_WOOL), new ItemStack(Material.PISTON), null, new ItemStack(Material.PISTON)})
        .register(plugin);

        new PickaxeOfTheSeeker(categories.tools, SlimefunItems.PICKAXE_OF_THE_SEEKER, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {new ItemStack(Material.COMPASS), SlimefunItems.SYNTHETIC_DIAMOND, new ItemStack(Material.COMPASS), null, SlimefunItems.FERROSILICON, null, null, SlimefunItems.FERROSILICON, null})
        .register(plugin);

        new SlimefunBackpack(9, categories.usefulItems, SlimefunItems.BACKPACK_SMALL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.LEATHER), null, new ItemStack(Material.LEATHER), SlimefunItems.GOLD_6K, new ItemStack(Material.CHEST), SlimefunItems.GOLD_6K, new ItemStack(Material.LEATHER), new ItemStack(Material.LEATHER), new ItemStack(Material.LEATHER)})
        .register(plugin);

        new SlimefunBackpack(18, categories.usefulItems, SlimefunItems.BACKPACK_MEDIUM, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.LEATHER), null, new ItemStack(Material.LEATHER), SlimefunItems.GOLD_10K, SlimefunItems.BACKPACK_SMALL, SlimefunItems.GOLD_10K, new ItemStack(Material.LEATHER), new ItemStack(Material.LEATHER), new ItemStack(Material.LEATHER)})
        .register(plugin);

        new SlimefunBackpack(27, categories.usefulItems, SlimefunItems.BACKPACK_LARGE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.LEATHER), null, new ItemStack(Material.LEATHER), SlimefunItems.GOLD_14K, SlimefunItems.BACKPACK_MEDIUM, SlimefunItems.GOLD_14K, new ItemStack(Material.LEATHER), new ItemStack(Material.LEATHER), new ItemStack(Material.LEATHER)})
        .register(plugin);

        new SlimefunBackpack(36, categories.usefulItems, SlimefunItems.WOVEN_BACKPACK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CLOTH, null, SlimefunItems.CLOTH, SlimefunItems.GOLD_16K, SlimefunItems.BACKPACK_LARGE, SlimefunItems.GOLD_16K, SlimefunItems.CLOTH, SlimefunItems.CLOTH, SlimefunItems.CLOTH})
        .register(plugin);

        new Crucible(categories.basicMachines, SlimefunItems.CRUCIBLE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.TERRACOTTA), null, new ItemStack(Material.TERRACOTTA), new ItemStack(Material.TERRACOTTA), null, new ItemStack(Material.TERRACOTTA), new ItemStack(Material.TERRACOTTA), new ItemStack(Material.FLINT_AND_STEEL), new ItemStack(Material.TERRACOTTA)})
        .register(plugin);

        new SlimefunBackpack(45, categories.usefulItems, SlimefunItems.GILDED_BACKPACK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GOLD_22K, null, SlimefunItems.GOLD_22K, new ItemStack(Material.LEATHER), SlimefunItems.WOVEN_BACKPACK, new ItemStack(Material.LEATHER), SlimefunItems.GOLD_22K, null, SlimefunItems.GOLD_22K})
        .register(plugin);

        new SlimefunBackpack(54, categories.usefulItems, SlimefunItems.RADIANT_BACKPACK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GOLD_24K, null, SlimefunItems.GOLD_24K, new ItemStack(Material.LEATHER), SlimefunItems.GILDED_BACKPACK, new ItemStack(Material.LEATHER), SlimefunItems.GOLD_24K, null, SlimefunItems.GOLD_24K})
        .register(plugin);

        new RestoredBackpack(categories.usefulItems).register(plugin);

        new UnplaceableBlock(categories.technicalComponents, SlimefunItems.MAGNET, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.NICKEL_INGOT, SlimefunItems.ALUMINUM_DUST, SlimefunItems.IRON_DUST, SlimefunItems.COBALT_INGOT, null, null, null, null, null})
        .register(plugin);

        new InfusedMagnet(categories.magicalGadgets, SlimefunItems.INFUSED_MAGNET, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.ENDER_LUMP_2, SlimefunItems.MAGNET, SlimefunItems.ENDER_LUMP_2, SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3})
        .register(plugin);

        new SlimefunItem(categories.tools, SlimefunItems.COBALT_PICKAXE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.COBALT_INGOT, SlimefunItems.COBALT_INGOT, SlimefunItems.COBALT_INGOT, null, SlimefunItems.NICKEL_INGOT, null, null, SlimefunItems.NICKEL_INGOT, null})
        .register(plugin);

        new UnplaceableBlock(categories.magicalResources, SlimefunItems.NECROTIC_SKULL, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3, null, new ItemStack(Material.WITHER_SKELETON_SKULL), null, SlimefunItems.MAGIC_LUMP_3, null, SlimefunItems.MAGIC_LUMP_3})
        .register(plugin);

        new SlimefunItem(categories.magicalResources, SlimefunItems.ESSENCE_OF_AFTERLIFE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_3, SlimefunItems.AIR_RUNE, SlimefunItems.ENDER_LUMP_3, SlimefunItems.EARTH_RUNE, SlimefunItems.NECROTIC_SKULL, SlimefunItems.FIRE_RUNE, SlimefunItems.ENDER_LUMP_3, SlimefunItems.WATER_RUNE, SlimefunItems.ENDER_LUMP_3})
        .register(plugin);

        new SlimefunItem(categories.magicalResources, SlimefunItems.SYNTHETIC_SHULKER_SHELL, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_3, SlimefunItems.ENDER_RUNE, SlimefunItems.ENDER_LUMP_3, SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.TURTLE_HELMET), SlimefunItems.REINFORCED_PLATE, SlimefunItems.ENDER_LUMP_3, SlimefunItems.ENDER_RUNE, SlimefunItems.ENDER_LUMP_3})
        .setUseableInWorkbench(true)
        .register(plugin);

        new SoulboundBackpack(36, categories.magicalGadgets, SlimefunItems.BOUND_BACKPACK, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_2, null, SlimefunItems.ENDER_LUMP_2, SlimefunItems.ESSENCE_OF_AFTERLIFE, SlimefunItems.WOVEN_BACKPACK, SlimefunItems.ESSENCE_OF_AFTERLIFE, SlimefunItems.ENDER_LUMP_2, null, SlimefunItems.ENDER_LUMP_2})
        .register(plugin);

        new JetBoots(categories.technicalGadgets, SlimefunItems.DURALUMIN_JETBOOTS,
        new ItemStack[] {null, null, null, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.35, 20)
        .register(plugin);

        new JetBoots(categories.technicalGadgets, SlimefunItems.SOLDER_JETBOOTS,
        new ItemStack[] {null, null, null, SlimefunItems.SOLDER_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.SOLDER_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.4, 30)
        .register(plugin);

        new JetBoots(categories.technicalGadgets, SlimefunItems.BILLON_JETBOOTS,
        new ItemStack[] {null, null, null, SlimefunItems.BILLON_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.BILLON_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.45, 40)
        .register(plugin);

        new JetBoots(categories.technicalGadgets, SlimefunItems.STEEL_JETBOOTS,
        new ItemStack[] {null, null, null, SlimefunItems.STEEL_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.STEEL_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.5, 50)
        .register(plugin);

        new JetBoots(categories.technicalGadgets, SlimefunItems.DAMASCUS_STEEL_JETBOOTS,
        new ItemStack[] {null, null, null, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.55, 75)
        .register(plugin);

        new JetBoots(categories.technicalGadgets, SlimefunItems.REINFORCED_ALLOY_JETBOOTS,
        new ItemStack[] {null, null, null, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.STEEL_THRUSTER, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.6, 100)
        .register(plugin);

        new JetBoots(categories.technicalGadgets, SlimefunItems.CARBONADO_JETBOOTS,
        new ItemStack[] {null, null, null, SlimefunItems.CARBONADO, SlimefunItems.POWER_CRYSTAL, SlimefunItems.CARBONADO, SlimefunItems.STEEL_THRUSTER, SlimefunItems.LARGE_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.7, 125)
        .register(plugin);

        new JetBoots(categories.technicalGadgets, SlimefunItems.ARMORED_JETBOOTS,
        new ItemStack[] {null, null, null, SlimefunItems.STEEL_PLATE, SlimefunItems.POWER_CRYSTAL, SlimefunItems.STEEL_PLATE, SlimefunItems.STEEL_THRUSTER, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.STEEL_THRUSTER},
        0.45, 50)
        .register(plugin);

        new SeismicAxe(categories.weapons, SlimefunItems.SEISMIC_AXE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.HARDENED_METAL_INGOT, null, SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.STAFF_ELEMENTAL, null, null, SlimefunItems.STAFF_ELEMENTAL, null})
        .register(plugin);

        new PickaxeOfVeinMining(categories.tools, SlimefunItems.PICKAXE_OF_VEIN_MINING, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {new ItemStack(Material.EMERALD_ORE), SlimefunItems.SYNTHETIC_DIAMOND, new ItemStack(Material.EMERALD_ORE), null, SlimefunItems.GILDED_IRON, null, null, SlimefunItems.GILDED_IRON, null})
        .register(plugin);
      
        new ClimbingPick(categories.tools, SlimefunItems.CLIMBING_PICK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.STEEL_INGOT, SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.STEEL_INGOT, null, new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null})
        .register(plugin);

        new SoulboundItem(categories.weapons, SlimefunItems.SOULBOUND_SWORD, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.DIAMOND_SWORD), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);
        
        new SoulboundItem(categories.weapons, SlimefunItems.SOULBOUND_TRIDENT, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.TRIDENT), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new SoulboundItem(categories.weapons, SlimefunItems.SOULBOUND_BOW, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.BOW), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new SoulboundItem(categories.tools, SlimefunItems.SOULBOUND_PICKAXE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.DIAMOND_PICKAXE), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new SoulboundItem(categories.tools, SlimefunItems.SOULBOUND_AXE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.DIAMOND_AXE), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new SoulboundItem(categories.tools, SlimefunItems.SOULBOUND_SHOVEL, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.DIAMOND_SHOVEL), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new SoulboundItem(categories.tools, SlimefunItems.SOULBOUND_HOE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.DIAMOND_HOE), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new SoulboundItem(categories.magicalArmor, SlimefunItems.SOULBOUND_HELMET, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.DIAMOND_HELMET), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new SoulboundItem(categories.magicalArmor, SlimefunItems.SOULBOUND_CHESTPLATE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.DIAMOND_CHESTPLATE), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new SoulboundItem(categories.magicalArmor, SlimefunItems.SOULBOUND_LEGGINGS, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.DIAMOND_LEGGINGS), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new SoulboundItem(categories.magicalArmor, SlimefunItems.SOULBOUND_BOOTS, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null, null, new ItemStack(Material.DIAMOND_BOOTS), null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new Juicer(categories.basicMachines, SlimefunItems.JUICER).register(plugin);

        new Juice(categories.food, SlimefunItems.APPLE_JUICE, RecipeType.JUICER,
        new ItemStack[] {new ItemStack(Material.APPLE), null, null, null, null, null, null, null, null})
        .register(plugin);

        new Juice(categories.food, SlimefunItems.CARROT_JUICE, RecipeType.JUICER,
        new ItemStack[] {new ItemStack(Material.CARROT), null, null, null, null, null, null, null, null})
        .register(plugin);

        new Juice(categories.food, SlimefunItems.MELON_JUICE, RecipeType.JUICER,
        new ItemStack[] {new ItemStack(Material.MELON_SLICE), null, null, null, null, null, null, null, null})
        .register(plugin);

        new Juice(categories.food, SlimefunItems.PUMPKIN_JUICE, RecipeType.JUICER,
        new ItemStack[] {new ItemStack(Material.PUMPKIN), null, null, null, null, null, null, null, null})
        .register(plugin);

        new Juice(categories.food, SlimefunItems.SWEET_BERRY_JUICE, RecipeType.JUICER,
        new ItemStack[] {new ItemStack(Material.SWEET_BERRIES), null, null, null, null, null, null, null, null})
        .register(plugin);

        new Juice(categories.food, SlimefunItems.GOLDEN_APPLE_JUICE, RecipeType.JUICER,
        new ItemStack[] {new ItemStack(Material.GOLDEN_APPLE), null, null, null, null, null, null, null, null})
        .register(plugin);

        new BrokenSpawner(categories.magicalResources, SlimefunItems.BROKEN_SPAWNER, new RecipeType(new NamespacedKey(plugin, "pickaxe_of_containment"), SlimefunItems.PICKAXE_OF_CONTAINMENT),
        new ItemStack[] {null, null, null, null, new ItemStack(Material.SPAWNER), null, null, null, null})
        .register(plugin);

        new RepairedSpawner(categories.magicalGadgets, SlimefunItems.REPAIRED_SPAWNER, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.ENDER_RUNE, SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, SlimefunItems.ESSENCE_OF_AFTERLIFE, SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, SlimefunItems.BROKEN_SPAWNER, SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, SlimefunItems.ESSENCE_OF_AFTERLIFE, SlimefunItems.FILLED_FLASK_OF_KNOWLEDGE, SlimefunItems.ENDER_RUNE})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 1, 1, 1, SlimefunItems.ENHANCED_FURNACE,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, new ItemStack(Material.FURNACE), SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 2, 1, 1, SlimefunItems.ENHANCED_FURNACE_2,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENHANCED_FURNACE, SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 2, 2, 1, SlimefunItems.ENHANCED_FURNACE_3,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENHANCED_FURNACE_2, SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 3, 2, 1, SlimefunItems.ENHANCED_FURNACE_4,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENHANCED_FURNACE_3, SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 3, 2, 2, SlimefunItems.ENHANCED_FURNACE_5,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENHANCED_FURNACE_4, SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 3, 3, 2, SlimefunItems.ENHANCED_FURNACE_6,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENHANCED_FURNACE_5, SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 4, 3, 2, SlimefunItems.ENHANCED_FURNACE_7,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENHANCED_FURNACE_6, SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 4, 4, 2, SlimefunItems.ENHANCED_FURNACE_8,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENHANCED_FURNACE_7, SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 5, 4, 2, SlimefunItems.ENHANCED_FURNACE_9,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENHANCED_FURNACE_8, SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 5, 5, 2, SlimefunItems.ENHANCED_FURNACE_10,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENHANCED_FURNACE_9, SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 5, 5, 3, SlimefunItems.ENHANCED_FURNACE_11,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.ENHANCED_FURNACE_10, SlimefunItems.HEATING_COIL, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 10, 10, 3, SlimefunItems.REINFORCED_FURNACE,
        new ItemStack[] {SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.HEATING_COIL, SlimefunItems.ENHANCED_FURNACE_11, SlimefunItems.HEATING_COIL, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.REINFORCED_ALLOY_INGOT})
        .register(plugin);

        new EnhancedFurnace(categories.basicMachines, 20, 10, 3, SlimefunItems.CARBONADO_EDGED_FURNACE,
        new ItemStack[] {SlimefunItems.CARBONADO, SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.CARBONADO, SlimefunItems.HEATING_COIL, SlimefunItems.REINFORCED_FURNACE, SlimefunItems.HEATING_COIL, SlimefunItems.CARBONADO, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.CARBONADO})
        .register(plugin);

        new UnplaceableBlock(categories.technicalComponents, SlimefunItems.ELECTRO_MAGNET, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.NICKEL_INGOT, SlimefunItems.MAGNET, SlimefunItems.COBALT_INGOT, null, SlimefunItems.BATTERY, null, null, null, null})
        .register(plugin);

        new UnplaceableBlock(categories.technicalComponents, SlimefunItems.ELECTRIC_MOTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.COPPER_WIRE, SlimefunItems.COPPER_WIRE, SlimefunItems.COPPER_WIRE, null, SlimefunItems.ELECTRO_MAGNET, null, SlimefunItems.COPPER_WIRE, SlimefunItems.COPPER_WIRE, SlimefunItems.COPPER_WIRE})
        .register(plugin);

        new UnplaceableBlock(categories.technicalComponents, SlimefunItems.HEATING_COIL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.COPPER_WIRE, SlimefunItems.COPPER_WIRE, SlimefunItems.COPPER_WIRE, SlimefunItems.COPPER_WIRE, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.COPPER_WIRE, SlimefunItems.COPPER_WIRE, SlimefunItems.COPPER_WIRE, SlimefunItems.COPPER_WIRE})
        .register(plugin);

        new SlimefunItem(categories.technicalComponents, SlimefunItems.COPPER_WIRE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.COPPER_WIRE, 8))
        .register(plugin);
        
        new BlockPlacer(categories.basicMachines, SlimefunItems.BLOCK_PLACER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GOLD_4K, new ItemStack(Material.PISTON), SlimefunItems.GOLD_4K, new ItemStack(Material.IRON_INGOT), SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.IRON_INGOT), SlimefunItems.GOLD_4K, new ItemStack(Material.PISTON), SlimefunItems.GOLD_4K})
        .register(plugin);

        new TelepositionScroll(categories.magicalGadgets, SlimefunItems.SCROLL_OF_DIMENSIONAL_TELEPOSITION, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, SlimefunItems.ENDER_LUMP_3, SlimefunItems.MAGIC_EYE_OF_ENDER, SlimefunItems.ENDER_LUMP_3, SlimefunItems.MAGICAL_BOOK_COVER, SlimefunItems.ENDER_LUMP_3, SlimefunItems.MAGIC_EYE_OF_ENDER, SlimefunItems.ENDER_LUMP_3, null})
        .register(plugin);

        new ExplosiveBow(categories.weapons, SlimefunItems.EXPLOSIVE_BOW,
        new ItemStack[] {null, new ItemStack(Material.STICK), new ItemStack(Material.GUNPOWDER), SlimefunItems.STAFF_FIRE, null, SlimefunItems.SULFATE, null, new ItemStack(Material.STICK), new ItemStack(Material.GUNPOWDER)})
        .register(plugin);

        new IcyBow(categories.weapons, SlimefunItems.ICY_BOW,
        new ItemStack[] {null, new ItemStack(Material.STICK), new ItemStack(Material.ICE), SlimefunItems.STAFF_WATER, null, new ItemStack(Material.PACKED_ICE), null, new ItemStack(Material.STICK), new ItemStack(Material.ICE)})
        .register(plugin);

        new KnowledgeTome(categories.magicalGadgets, SlimefunItems.TOME_OF_KNOWLEDGE_SHARING, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, new ItemStack(Material.FEATHER), null, new ItemStack(Material.INK_SAC), SlimefunItems.MAGICAL_BOOK_COVER, new ItemStack(Material.GLASS_BOTTLE), null, new ItemStack(Material.WRITABLE_BOOK), null})
        .register(plugin);

        new KnowledgeFlask(categories.magicalGadgets, SlimefunItems.FLASK_OF_KNOWLEDGE, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, null, null, SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.GLASS_PANE), SlimefunItems.MAGIC_LUMP_2, null, SlimefunItems.MAGIC_LUMP_2, null}, 
        new SlimefunItemStack(SlimefunItems.FLASK_OF_KNOWLEDGE, 8))
        .register(plugin);

        new BirthdayCake(categories.birthday, new SlimefunItemStack("BIRTHDAY_CAKE", Material.CAKE, "&bBirthday Cake"), RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.TORCH), null, new ItemStack(Material.SUGAR), new ItemStack(Material.CAKE), new ItemStack(Material.SUGAR), null, null, null})
        .register(plugin);

        new Juice(categories.christmas, SlimefunItems.CHRISTMAS_MILK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.MILK_BUCKET), new ItemStack(Material.GLASS_BOTTLE), null, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_MILK, 4))
        .register(plugin);

        new Juice(categories.christmas, SlimefunItems.CHRISTMAS_CHOCOLATE_MILK, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CHRISTMAS_MILK, new ItemStack(Material.COCOA_BEANS), null, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_CHOCOLATE_MILK, 2))
        .register(plugin);

        new Juice(categories.christmas, SlimefunItems.CHRISTMAS_EGG_NOG, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CHRISTMAS_MILK, new ItemStack(Material.EGG), null, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_EGG_NOG, 2))
        .register(plugin);

        new Juice(categories.christmas, SlimefunItems.CHRISTMAS_APPLE_CIDER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.APPLE_JUICE, new ItemStack(Material.SUGAR), null, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_APPLE_CIDER, 2))
        .register(plugin);

        new SlimefunItem(categories.christmas, SlimefunItems.CHRISTMAS_COOKIE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.COOKIE), new ItemStack(Material.SUGAR), new ItemStack(Material.LIME_DYE), null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_COOKIE, 16))
        .register(plugin);

        new SlimefunItem(categories.christmas, SlimefunItems.CHRISTMAS_FRUIT_CAKE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.EGG), new ItemStack(Material.APPLE), new ItemStack(Material.MELON), new ItemStack(Material.SUGAR), null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_FRUIT_CAKE, 4))
        .register(plugin);

        new SlimefunItem(categories.christmas, SlimefunItems.CHRISTMAS_APPLE_PIE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.SUGAR), new ItemStack(Material.APPLE), new ItemStack(Material.EGG), null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_APPLE_PIE, 2))
        .register(plugin);

        new Juice(categories.christmas, SlimefunItems.CHRISTMAS_HOT_CHOCOLATE, RecipeType.SMELTERY,
        new ItemStack[] {SlimefunItems.CHRISTMAS_CHOCOLATE_MILK, null, null, null, null, null, null, null, null}, SlimefunItems.CHRISTMAS_HOT_CHOCOLATE)
        .register(plugin);

        new SlimefunItem(categories.christmas, SlimefunItems.CHRISTMAS_CAKE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.MILK_BUCKET), null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_CAKE, 4))
        .register(plugin);

        new SlimefunItem(categories.christmas, SlimefunItems.CHRISTMAS_CARAMEL, RecipeType.SMELTERY,
        new ItemStack[] {new ItemStack(Material.SUGAR), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_CARAMEL, 4))
        .register(plugin);

        new SlimefunItem(categories.christmas, SlimefunItems.CHRISTMAS_CARAMEL_APPLE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.CHRISTMAS_CARAMEL, null, null, new ItemStack(Material.APPLE), null, null, new ItemStack(Material.STICK), null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_CARAMEL_APPLE, 2))
        .register(plugin);

        new SlimefunItem(categories.christmas, SlimefunItems.CHRISTMAS_CHOCOLATE_APPLE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.COCOA_BEANS), null, null, new ItemStack(Material.APPLE), null, null, new ItemStack(Material.STICK), null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_CHOCOLATE_APPLE, 2))
        .register(plugin);

        new ChristmasPresent(categories.christmas, SlimefunItems.CHRISTMAS_PRESENT, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, new ItemStack(Material.NAME_TAG), null, new ItemStack(Material.RED_WOOL), new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.RED_WOOL), new ItemStack(Material.GREEN_WOOL), new ItemStack(Material.RED_WOOL)},
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_HOT_CHOCOLATE, 1),
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_CHOCOLATE_APPLE, 4),
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_CARAMEL_APPLE, 4),
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_CAKE, 4),
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_COOKIE, 8),
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_PRESENT, 1),
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_EGG_NOG, 1),
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_MILK, 1),
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_APPLE_CIDER, 1),
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_FRUIT_CAKE, 4),
            new SlimefunItemStack(SlimefunItems.CHRISTMAS_APPLE_PIE, 4),
            new ItemStack(Material.EMERALD)
        ).register(plugin);

        new SlimefunItem(categories.easter, SlimefunItems.EASTER_CARROT_PIE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.SUGAR), new ItemStack(Material.CARROT), new ItemStack(Material.EGG), null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.EASTER_CARROT_PIE, 2))
        .register(plugin);

        new SlimefunItem(categories.easter, SlimefunItems.EASTER_APPLE_PIE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.SUGAR), new ItemStack(Material.APPLE), new ItemStack(Material.EGG), null, null, null, null, null, null}, 
        new SlimefunItemStack(SlimefunItems.CHRISTMAS_APPLE_PIE, 2))
        .register(plugin);

        new EasterEgg(categories.easter, SlimefunItems.EASTER_EGG, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, new ItemStack(Material.LIME_DYE), new ItemStack(Material.EGG), new ItemStack(Material.PURPLE_DYE), null, null, null}, 
        new SlimefunItemStack(SlimefunItems.EASTER_EGG, 2),
        // Gifts:
            new SlimefunItemStack(SlimefunItems.EASTER_CARROT_PIE, 4),
            new SlimefunItemStack(SlimefunItems.CARROT_JUICE, 1),
            new ItemStack(Material.EMERALD),
            new ItemStack(Material.CAKE),
            new ItemStack(Material.RABBIT_FOOT),
            new ItemStack(Material.GOLDEN_CARROT, 4)
        ).register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.REINFORCED_PLATE, RecipeType.COMPRESSOR,
        new ItemStack[] {new SlimefunItemStack(SlimefunItems.REINFORCED_ALLOY_INGOT, 8), null, null, null, null, null, null, null, null})
        .register(plugin);

        new HardenedGlass(categories.technicalComponents, SlimefunItems.HARDENED_GLASS, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.GLASS), new ItemStack(Material.GLASS), new ItemStack(Material.GLASS), new ItemStack(Material.GLASS), SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.GLASS), new ItemStack(Material.GLASS), new ItemStack(Material.GLASS), new ItemStack(Material.GLASS)},
        new SlimefunItemStack(SlimefunItems.HARDENED_GLASS, 16))
        .register(plugin);

        new UnplaceableBlock(categories.technicalComponents, SlimefunItems.COOLING_UNIT, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.ICE), new ItemStack(Material.ICE), new ItemStack(Material.ICE), SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ALUMINUM_INGOT, new ItemStack(Material.ICE), new ItemStack(Material.ICE), new ItemStack(Material.ICE)})
        .register(plugin);

        new Cooler(27, categories.usefulItems, SlimefunItems.COOLER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CLOTH, SlimefunItems.CLOTH, SlimefunItems.CLOTH, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.COOLING_UNIT, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ALUMINUM_INGOT})
        .register(plugin);

        new WitherProofBlock(categories.technicalComponents, SlimefunItems.WITHER_PROOF_OBSIDIAN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.LEAD_INGOT, new ItemStack(Material.OBSIDIAN), SlimefunItems.LEAD_INGOT, new ItemStack(Material.OBSIDIAN), SlimefunItems.HARDENED_GLASS, new ItemStack(Material.OBSIDIAN), SlimefunItems.LEAD_INGOT, new ItemStack(Material.OBSIDIAN), SlimefunItems.LEAD_INGOT},
        new SlimefunItemStack(SlimefunItems.WITHER_PROOF_OBSIDIAN, 4))
        .register(plugin);

        new AncientPedestal(categories.magicalGadgets, SlimefunItems.ANCIENT_PEDESTAL, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {new ItemStack(Material.OBSIDIAN), SlimefunItems.GOLD_8K, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.STONE), null, new ItemStack(Material.OBSIDIAN), SlimefunItems.GOLD_8K, new ItemStack(Material.OBSIDIAN)}, 
        new SlimefunItemStack(SlimefunItems.ANCIENT_PEDESTAL, 4))
        .register(plugin);

        new AncientAltar(categories.magicalGadgets, SlimefunItems.ANCIENT_ALTAR, RecipeType.MAGIC_WORKBENCH,
        new ItemStack[] {null, new ItemStack(Material.ENCHANTING_TABLE), null, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.GOLD_8K, SlimefunItems.MAGIC_LUMP_3, new ItemStack(Material.OBSIDIAN), SlimefunItems.GOLD_8K, new ItemStack(Material.OBSIDIAN)})
        .register(plugin);

        new EnergyRegulator(categories.electricity, SlimefunItems.ENERGY_REGULATOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SILVER_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.SILVER_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.SILVER_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.SILVER_INGOT})
        .register(plugin);

        new EnergyConnector(categories.electricity, SlimefunItems.ENERGY_CONNECTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CARBON, SlimefunItems.COPPER_WIRE, SlimefunItems.CARBON, SlimefunItems.COPPER_WIRE, new ItemStack(Material.REDSTONE_BLOCK), SlimefunItems.COPPER_WIRE, SlimefunItems.CARBON, SlimefunItems.COPPER_WIRE, SlimefunItems.CARBON},
        new SlimefunItemStack(SlimefunItems.ENERGY_CONNECTOR, 8))
        .register(plugin);

        new SlimefunItem(categories.misc, SlimefunItems.DUCT_TAPE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.ALUMINUM_DUST, SlimefunItems.ALUMINUM_DUST, SlimefunItems.ALUMINUM_DUST, new ItemStack(Material.SLIME_BALL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.SLIME_BALL), new ItemStack(Material.PAPER), new ItemStack(Material.PAPER), new ItemStack(Material.PAPER)}, 
        new SlimefunItemStack(SlimefunItems.DUCT_TAPE, 2))
        .register(plugin);

        new Capacitor(categories.electricity, 128, SlimefunItems.SMALL_CAPACITOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.DURALUMIN_INGOT, SlimefunItems.SULFATE, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.ENERGY_CONNECTOR, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.DURALUMIN_INGOT, new ItemStack(Material.REDSTONE), SlimefunItems.DURALUMIN_INGOT})
        .register(plugin);

        new Capacitor(categories.electricity, 512, SlimefunItems.MEDIUM_CAPACITOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.BILLON_INGOT, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.BILLON_INGOT, new ItemStack(Material.REDSTONE), SlimefunItems.SMALL_CAPACITOR, new ItemStack(Material.REDSTONE), SlimefunItems.BILLON_INGOT, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.BILLON_INGOT})
        .register(plugin);

        new Capacitor(categories.electricity, 1024, SlimefunItems.BIG_CAPACITOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.STEEL_INGOT, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.STEEL_INGOT, new ItemStack(Material.REDSTONE), SlimefunItems.MEDIUM_CAPACITOR, new ItemStack(Material.REDSTONE), SlimefunItems.STEEL_INGOT, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.STEEL_INGOT})
        .register(plugin);

        new Capacitor(categories.electricity, 8192, SlimefunItems.LARGE_CAPACITOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.REINFORCED_ALLOY_INGOT, new ItemStack(Material.REDSTONE), SlimefunItems.BIG_CAPACITOR, new ItemStack(Material.REDSTONE), SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.REINFORCED_ALLOY_INGOT})
        .register(plugin);

        new Capacitor(categories.electricity, 65536, SlimefunItems.CARBONADO_EDGED_CAPACITOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CARBONADO, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.CARBONADO, new ItemStack(Material.REDSTONE), SlimefunItems.LARGE_CAPACITOR, new ItemStack(Material.REDSTONE), SlimefunItems.CARBONADO, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.CARBONADO})
        .register(plugin);

        new Capacitor(categories.electricity, 524288, SlimefunItems.ENERGIZED_CAPACITOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CARBONADO, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.CARBONADO, new ItemStack(Material.NETHER_STAR), SlimefunItems.CARBONADO_EDGED_CAPACITOR, new ItemStack(Material.NETHER_STAR), SlimefunItems.CARBONADO, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.CARBONADO})
        .register(plugin);

        new SolarGenerator(categories.electricity, 2, 0, SlimefunItems.SOLAR_GENERATOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SOLAR_PANEL, SlimefunItems.SOLAR_PANEL, SlimefunItems.SOLAR_PANEL, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ALUMINUM_INGOT, null, SlimefunItems.ALUMINUM_INGOT, null})
        .register(plugin);

        new SolarGenerator(categories.electricity, 8, 0, SlimefunItems.SOLAR_GENERATOR_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SOLAR_GENERATOR, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.SOLAR_GENERATOR, SlimefunItems.ALUMINUM_INGOT, new ItemStack(Material.REDSTONE), SlimefunItems.ALUMINUM_INGOT, SlimefunItems.SOLAR_GENERATOR, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.SOLAR_GENERATOR})
        .register(plugin);

        new SolarGenerator(categories.electricity, 32, 0, SlimefunItems.SOLAR_GENERATOR_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SOLAR_GENERATOR_2, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.SOLAR_GENERATOR_2, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.CARBONADO, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.SOLAR_GENERATOR_2, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.SOLAR_GENERATOR_2})
        .register(plugin);

        new SolarGenerator(categories.electricity, 128, 64, SlimefunItems.SOLAR_GENERATOR_4, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SOLAR_GENERATOR_3, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.SOLAR_GENERATOR_3, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.SOLAR_GENERATOR_3, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.SOLAR_GENERATOR_3})
        .register(plugin);
        
        new ChargingBench(categories.electricity, SlimefunItems.CHARGING_BENCH, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.ELECTRO_MAGNET, null, SlimefunItems.BATTERY, new ItemStack(Material.CRAFTING_TABLE), SlimefunItems.BATTERY, null, SlimefunItems.SMALL_CAPACITOR, null})
        .setCapacity(128)
        .setEnergyConsumption(10)
        .setProcessingSpeed(1)
        .register(plugin);

        new ElectricFurnace(categories.electricity, SlimefunItems.ELECTRIC_FURNACE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.FURNACE), null, SlimefunItems.GILDED_IRON, SlimefunItems.HEATING_COIL, SlimefunItems.GILDED_IRON, SlimefunItems.GILDED_IRON, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.GILDED_IRON})
        .setCapacity(64)
        .setEnergyConsumption(2)
        .setProcessingSpeed(1)
        .register(plugin);

        new ElectricFurnace(categories.electricity, SlimefunItems.ELECTRIC_FURNACE_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.ELECTRIC_MOTOR, null, SlimefunItems.GILDED_IRON, SlimefunItems.ELECTRIC_FURNACE, SlimefunItems.GILDED_IRON, SlimefunItems.GILDED_IRON, SlimefunItems.HEATING_COIL, SlimefunItems.GILDED_IRON})
        .setCapacity(128)
        .setEnergyConsumption(3)
        .setProcessingSpeed(2)
        .register(plugin);

        new ElectricFurnace(categories.electricity, SlimefunItems.ELECTRIC_FURNACE_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.ELECTRIC_MOTOR, null, SlimefunItems.STEEL_INGOT, SlimefunItems.ELECTRIC_FURNACE_2, SlimefunItems.STEEL_INGOT, SlimefunItems.GILDED_IRON, SlimefunItems.HEATING_COIL, SlimefunItems.GILDED_IRON})
        .setCapacity(128)
        .setEnergyConsumption(5)
        .setProcessingSpeed(4)
        .register(plugin);

        new ElectricGoldPan(categories.electricity, SlimefunItems.ELECTRIC_GOLD_PAN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.GOLD_PAN, null, new ItemStack(Material.FLINT), SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.FLINT), SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.ALUMINUM_INGOT})
        .setCapacity(128)
        .setEnergyConsumption(1)
        .setProcessingSpeed(1)
        .register(plugin);

        new ElectricGoldPan(categories.electricity, SlimefunItems.ELECTRIC_GOLD_PAN_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.GOLD_PAN, null, new ItemStack(Material.IRON_INGOT), SlimefunItems.ELECTRIC_GOLD_PAN, new ItemStack(Material.IRON_INGOT), SlimefunItems.DURALUMIN_INGOT, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.DURALUMIN_INGOT})
        .setCapacity(128)
        .setEnergyConsumption(2)
        .setProcessingSpeed(3)
        .register(plugin);

        new ElectricGoldPan(categories.electricity, SlimefunItems.ELECTRIC_GOLD_PAN_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.GOLD_PAN, null, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ELECTRIC_GOLD_PAN_2, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.COBALT_INGOT, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.COBALT_INGOT})
        .setCapacity(512)
        .setEnergyConsumption(7)
        .setProcessingSpeed(10)
        .register(plugin);

        new ElectricDustWasher(categories.electricity, SlimefunItems.ELECTRIC_DUST_WASHER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.WATER_BUCKET), null, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.ELECTRIC_GOLD_PAN, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT})
        .setCapacity(128)
        .setEnergyConsumption(3)
        .setProcessingSpeed(1)
        .register(plugin);

        new ElectricDustWasher(categories.electricity, SlimefunItems.ELECTRIC_DUST_WASHER_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.WATER_BUCKET), null, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.ELECTRIC_DUST_WASHER, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT})
        .setCapacity(128)
        .setEnergyConsumption(5)
        .setProcessingSpeed(2)
        .register(plugin);

        new ElectricDustWasher(categories.electricity, SlimefunItems.ELECTRIC_DUST_WASHER_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.WATER_BUCKET), null, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.ELECTRIC_DUST_WASHER_2, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.CORINTHIAN_BRONZE_INGOT})
        .setCapacity(512)
        .setEnergyConsumption(15)
        .setProcessingSpeed(10)
        .register(plugin);

        new ElectricIngotFactory(categories.electricity, SlimefunItems.ELECTRIC_INGOT_FACTORY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.FLINT_AND_STEEL), null, SlimefunItems.HEATING_COIL, SlimefunItems.ELECTRIC_DUST_WASHER, SlimefunItems.HEATING_COIL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.DAMASCUS_STEEL_INGOT})
        .setCapacity(256)
        .setEnergyConsumption(4)
        .setProcessingSpeed(1)
        .register(plugin);

        new ElectricIngotFactory(categories.electricity, SlimefunItems.ELECTRIC_INGOT_FACTORY_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GILDED_IRON, new ItemStack(Material.FLINT_AND_STEEL), SlimefunItems.GILDED_IRON, SlimefunItems.HEATING_COIL, SlimefunItems.ELECTRIC_INGOT_FACTORY, SlimefunItems.HEATING_COIL, SlimefunItems.BRASS_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.BRASS_INGOT})
        .setCapacity(256)
        .setEnergyConsumption(7)
        .setProcessingSpeed(2)
        .register(plugin);

        new ElectricIngotFactory(categories.electricity, SlimefunItems.ELECTRIC_INGOT_FACTORY_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GILDED_IRON, new ItemStack(Material.FLINT_AND_STEEL), SlimefunItems.GILDED_IRON, SlimefunItems.HEATING_COIL, SlimefunItems.ELECTRIC_INGOT_FACTORY_2, SlimefunItems.HEATING_COIL, SlimefunItems.BRASS_INGOT, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.BRASS_INGOT})
        .setCapacity(512)
        .setEnergyConsumption(20)
        .setProcessingSpeed(8)
        .register(plugin);

        new ElectrifiedCrucible(categories.electricity, SlimefunItems.ELECTRIFIED_CRUCIBLE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.LEAD_INGOT, SlimefunItems.CRUCIBLE, SlimefunItems.LEAD_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.HEATING_COIL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.LARGE_CAPACITOR, SlimefunItems.LEAD_INGOT})
        .setCapacity(1024)
        .setEnergyConsumption(24)
        .setProcessingSpeed(1)
        .register(plugin);

        new ElectrifiedCrucible(categories.electricity, SlimefunItems.ELECTRIFIED_CRUCIBLE_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.ELECTRIFIED_CRUCIBLE, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.HEATING_COIL, SlimefunItems.LEAD_INGOT})
        .setCapacity(1024)
        .setEnergyConsumption(40)
        .setProcessingSpeed(2)
        .register(plugin);

        new ElectrifiedCrucible(categories.electricity, SlimefunItems.ELECTRIFIED_CRUCIBLE_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.ELECTRIFIED_CRUCIBLE_2, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.STEEL_PLATE, SlimefunItems.POWER_CRYSTAL, SlimefunItems.STEEL_PLATE, SlimefunItems.LEAD_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.LEAD_INGOT})
        .setCapacity(1024)
        .setEnergyConsumption(60)
        .setProcessingSpeed(4)
        .register(plugin);

        new ElectricOreGrinder(categories.electricity, SlimefunItems.ELECTRIC_ORE_GRINDER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.DIAMOND_PICKAXE), null, SlimefunItems.GILDED_IRON, SlimefunItems.HEATING_COIL, SlimefunItems.GILDED_IRON, SlimefunItems.GILDED_IRON, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.GILDED_IRON})
        .setCapacity(128)
        .setEnergyConsumption(6)
        .setProcessingSpeed(1)
        .register(plugin);

        new ElectricOreGrinder(categories.electricity, SlimefunItems.ELECTRIC_ORE_GRINDER_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.DIAMOND_PICKAXE), null, SlimefunItems.HEATING_COIL, SlimefunItems.ELECTRIC_ORE_GRINDER, SlimefunItems.HEATING_COIL, SlimefunItems.GILDED_IRON, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.GILDED_IRON})
        .setCapacity(512)
        .setEnergyConsumption(15)
        .setProcessingSpeed(4)
        .register(plugin);

        new ElectricOreGrinder(categories.electricity, SlimefunItems.ELECTRIC_ORE_GRINDER_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.REINFORCED_PLATE, SlimefunItems.HEATING_COIL, SlimefunItems.REINFORCED_PLATE, null, SlimefunItems.ELECTRIC_ORE_GRINDER_2, null, SlimefunItems.REINFORCED_PLATE, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.REINFORCED_PLATE})
        .setCapacity(1024)
        .setEnergyConsumption(45)
        .setProcessingSpeed(10)
        .register(plugin);

        new HeatedPressureChamber(categories.electricity, SlimefunItems.HEATED_PRESSURE_CHAMBER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.LEAD_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.LEAD_INGOT, SlimefunItems.LEAD_INGOT, new ItemStack(Material.GLASS), SlimefunItems.LEAD_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.HEATING_COIL, SlimefunItems.LEAD_INGOT})
        .setCapacity(128)
        .setEnergyConsumption(5)
        .setProcessingSpeed(1)
        .register(plugin);

        new HeatedPressureChamber(categories.electricity, SlimefunItems.HEATED_PRESSURE_CHAMBER_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.LEAD_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.LEAD_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.HEATED_PRESSURE_CHAMBER, SlimefunItems.LEAD_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.HEATING_COIL, SlimefunItems.REINFORCED_ALLOY_INGOT})
        .setCapacity(256)
        .setEnergyConsumption(22)
        .setProcessingSpeed(5)
        .register(plugin);

        new ElectricIngotPulverizer(categories.electricity, SlimefunItems.ELECTRIC_INGOT_PULVERIZER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.ELECTRIC_ORE_GRINDER, null, SlimefunItems.LEAD_INGOT, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.LEAD_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.HEATING_COIL, SlimefunItems.LEAD_INGOT})
        .setCapacity(512)
        .setEnergyConsumption(7)
        .setProcessingSpeed(1)
        .register(plugin);

        new CoalGenerator(categories.electricity, SlimefunItems.COAL_GENERATOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.HEATING_COIL, new ItemStack(Material.FURNACE), SlimefunItems.HEATING_COIL, SlimefunItems.NICKEL_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.NICKEL_INGOT, null, SlimefunItems.NICKEL_INGOT, null})
        .setCapacity(64)
        .setEnergyProduction(8)
        .register(plugin);

        new CoalGenerator(categories.electricity, SlimefunItems.COAL_GENERATOR_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.MAGMA_BLOCK), SlimefunItems.HEATING_COIL, new ItemStack(Material.MAGMA_BLOCK), SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.COAL_GENERATOR, SlimefunItems.HARDENED_METAL_INGOT, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .setCapacity(256)
        .setEnergyProduction(15)
        .register(plugin);

        new BioGenerator(categories.electricity, SlimefunItems.BIO_REACTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.HEATING_COIL, SlimefunItems.COMPOSTER, SlimefunItems.HEATING_COIL, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ALUMINUM_BRASS_INGOT, null, SlimefunItems.ALUMINUM_BRASS_INGOT, null})
        .setCapacity(128)
        .setEnergyProduction(4)
        .register(plugin);

        new AutoDrier(categories.electricity, SlimefunItems.AUTO_DRIER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[]{null, null, null, SlimefunItems.HEATING_COIL, new ItemStack(Material.SMOKER), SlimefunItems.HEATING_COIL, null, new ItemStack(Material.CAMPFIRE), null})
        .setCapacity(128)
        .setEnergyConsumption(5)
        .setProcessingSpeed(1)
        .register(plugin);

        new AutoBrewer(categories.electricity, SlimefunItems.AUTO_BREWER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.HEATING_COIL, null, SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.BREWING_STAND), SlimefunItems.REINFORCED_PLATE, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .setCapacity(128)
        .setEnergyConsumption(6)
        .setProcessingSpeed(1)
        .register(plugin);

        new ElectricPress(categories.electricity, SlimefunItems.ELECTRIC_PRESS, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.PISTON), SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.PISTON), null, SlimefunItems.MEDIUM_CAPACITOR, null, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT})
        .setCapacity(256)
        .setEnergyConsumption(8)
        .setProcessingSpeed(1)
        .register(plugin);

        new ElectricPress(categories.electricity, SlimefunItems.ELECTRIC_PRESS_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.STICKY_PISTON), SlimefunItems.ELECTRIC_PRESS, new ItemStack(Material.STICKY_PISTON), SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.BIG_CAPACITOR, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.DAMASCUS_STEEL_INGOT})
        .setCapacity(1024)
        .setEnergyConsumption(20)
        .setProcessingSpeed(3)
        .register(plugin);
        
        new SlimefunItem(categories.resources, SlimefunItems.MAGNESIUM_SALT, RecipeType.HEATED_PRESSURE_CHAMBER,
        new ItemStack[] {SlimefunItems.MAGNESIUM_DUST, SlimefunItems.SALT, null, null, null, null, null, null, null})
        .register(plugin);
        
        new MagnesiumGenerator(categories.electricity, SlimefunItems.MAGNESIUM_GENERATOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.ELECTRIC_MOTOR, null, SlimefunItems.COMPRESSED_CARBON, new ItemStack(Material.WATER_BUCKET), SlimefunItems.COMPRESSED_CARBON, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.DURALUMIN_INGOT, SlimefunItems.DURALUMIN_INGOT})
        .setCapacity(128)
        .setEnergyProduction(18)
        .register(plugin);

        new AutoEnchanter(categories.electricity, SlimefunItems.AUTO_ENCHANTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.ENCHANTING_TABLE), null, SlimefunItems.CARBONADO, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.CARBONADO, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.WITHER_PROOF_OBSIDIAN})
        .setCapacity(128)
        .setEnergyConsumption(9)
        .setProcessingSpeed(1)
        .register(plugin);

        new AutoEnchanter(categories.electricity, SlimefunItems.AUTO_ENCHANTER_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.REINFORCED_PLATE, SlimefunItems.BIG_CAPACITOR, SlimefunItems.REINFORCED_PLATE, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.AUTO_ENCHANTER, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.SYNTHETIC_DIAMOND, SlimefunItems.WITHER_PROOF_OBSIDIAN})
        .setCapacity(1024)
        .setEnergyConsumption(24)
        .setProcessingSpeed(3)
        .register(plugin);

        new AutoDisenchanter(categories.electricity, SlimefunItems.AUTO_DISENCHANTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.REDSTONE), new ItemStack(Material.ANVIL), new ItemStack(Material.REDSTONE), SlimefunItems.CARBONADO, SlimefunItems.AUTO_ENCHANTER, SlimefunItems.CARBONADO, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.WITHER_PROOF_OBSIDIAN})
        .setCapacity(128)
        .setEnergyConsumption(9)
        .setProcessingSpeed(1)
        .register(plugin);

        new AutoDisenchanter(categories.electricity, SlimefunItems.AUTO_DISENCHANTER_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.ANVIL), SlimefunItems.REINFORCED_PLATE, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.AUTO_DISENCHANTER, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.BIG_CAPACITOR, SlimefunItems.WITHER_PROOF_OBSIDIAN})
        .setCapacity(1024)
        .setEnergyConsumption(24)
        .setProcessingSpeed(3)
        .register(plugin);

        new AutoAnvil(categories.electricity, 10, SlimefunItems.AUTO_ANVIL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.ANVIL), null, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.REINFORCED_ALLOY_INGOT, new ItemStack(Material.IRON_BLOCK), new ItemStack(Material.IRON_BLOCK), new ItemStack(Material.IRON_BLOCK)})
        .setCapacity(128)
        .setEnergyConsumption(12)
        .setProcessingSpeed(1)
        .register(plugin);

        new AutoAnvil(categories.electricity, 25, SlimefunItems.AUTO_ANVIL_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.AUTO_ANVIL, null, SlimefunItems.STEEL_PLATE, SlimefunItems.HEATING_COIL, SlimefunItems.STEEL_PLATE, new ItemStack(Material.IRON_BLOCK), new ItemStack(Material.IRON_BLOCK), new ItemStack(Material.IRON_BLOCK)})
        .setCapacity(256)
        .setEnergyConsumption(16)
        .setProcessingSpeed(1)
        .register(plugin);

        new BookBinder(categories.electricity, SlimefunItems.BOOK_BINDER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.ENCHANTING_TABLE), null, new ItemStack(Material.BOOKSHELF), SlimefunItems.HARDENED_METAL_INGOT, new ItemStack(Material.BOOKSHELF), SlimefunItems.SYNTHETIC_SAPPHIRE, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.SYNTHETIC_SAPPHIRE})
        .setCapacity(256)
        .setEnergyConsumption(16)
        .setProcessingSpeed(1)
        .register(plugin);

        new Multimeter(categories.technicalGadgets, SlimefunItems.MULTIMETER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.COPPER_INGOT, null, SlimefunItems.COPPER_INGOT, null, SlimefunItems.REDSTONE_ALLOY, null, null, SlimefunItems.GOLD_6K, null})
        .register(plugin);

        new SlimefunItem(categories.technicalComponents, SlimefunItems.PLASTIC_SHEET, RecipeType.HEATED_PRESSURE_CHAMBER,
        new ItemStack[] {null, null, null, null, SlimefunItems.OIL_BUCKET, null, null, null, null},
        new SlimefunItemStack(SlimefunItems.PLASTIC_SHEET, 8))
        .register(plugin);

        new UnplaceableBlock(categories.technicalComponents, SlimefunItems.ANDROID_MEMORY_CORE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.BRASS_INGOT, new ItemStack(Material.ORANGE_STAINED_GLASS), SlimefunItems.BRASS_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.TIN_DUST, SlimefunItems.POWER_CRYSTAL, SlimefunItems.BRASS_INGOT, new ItemStack(Material.ORANGE_STAINED_GLASS), SlimefunItems.BRASS_INGOT})
        .register(plugin);

        new GPSTransmitter(categories.gps, 1, SlimefunItems.GPS_TRANSMITTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.STEEL_INGOT, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.STEEL_INGOT, SlimefunItems.STEEL_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.STEEL_INGOT}) {
            
            @Override
            public int getMultiplier(int y) {
                return y;
            }

            @Override
            public int getEnergyConsumption() {
                return 1;
            }

        }.register(plugin);

        new GPSTransmitter(categories.gps, 2, SlimefunItems.GPS_TRANSMITTER_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GPS_TRANSMITTER, SlimefunItems.BRONZE_INGOT, SlimefunItems.GPS_TRANSMITTER, SlimefunItems.BRONZE_INGOT, SlimefunItems.CARBON, SlimefunItems.BRONZE_INGOT, SlimefunItems.GPS_TRANSMITTER, SlimefunItems.BRONZE_INGOT, SlimefunItems.GPS_TRANSMITTER}) {
            
            @Override
            public int getMultiplier(int y) {
                return y * 4 + 100;
            }

            @Override
            public int getEnergyConsumption() {
                return 3;
            }

        }.register(plugin);

        new GPSTransmitter(categories.gps, 3, SlimefunItems.GPS_TRANSMITTER_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GPS_TRANSMITTER_2, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.GPS_TRANSMITTER_2, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.CARBONADO, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.GPS_TRANSMITTER_2, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.GPS_TRANSMITTER_2}) {
            
            @Override
            public int getMultiplier(int y) {
                return y * 16 + 500;
            }

            @Override
            public int getEnergyConsumption() {
                return 11;
            }

        }.register(plugin);

        new GPSTransmitter(categories.gps, 4, SlimefunItems.GPS_TRANSMITTER_4, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GPS_TRANSMITTER_3, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.GPS_TRANSMITTER_3, SlimefunItems.NICKEL_INGOT, SlimefunItems.CARBONADO, SlimefunItems.NICKEL_INGOT, SlimefunItems.GPS_TRANSMITTER_3, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.GPS_TRANSMITTER_3}) {
            
            @Override
            public int getMultiplier(int y) {
                return y * 64 + 600;
            }

            @Override
            public int getEnergyConsumption() {
                return 46;
            }
            
        }.register(plugin);

        new GPSControlPanel(categories.gps, SlimefunItems.GPS_CONTROL_PANEL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.COBALT_INGOT, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.COBALT_INGOT, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.ALUMINUM_BRASS_INGOT})
        .register(plugin);

        new GPSMarkerTool(categories.gps, SlimefunItems.GPS_MARKER_TOOL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.ELECTRO_MAGNET, null, new ItemStack(Material.LAPIS_LAZULI), SlimefunItems.BASIC_CIRCUIT_BOARD, new ItemStack(Material.LAPIS_LAZULI), new ItemStack(Material.REDSTONE), SlimefunItems.REDSTONE_ALLOY, new ItemStack(Material.REDSTONE)})
        .register(plugin);

        new SlimefunItem(categories.gps, SlimefunItems.GPS_EMERGENCY_TRANSMITTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.ELECTRO_MAGNET, null, null, SlimefunItems.GPS_TRANSMITTER, null, null, SlimefunItems.ESSENCE_OF_AFTERLIFE, null})
        .register(plugin);

        new AndroidInterface(categories.androids, SlimefunItems.ANDROID_INTERFACE_ITEMS, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.PLASTIC_SHEET, SlimefunItems.STEEL_INGOT, SlimefunItems.PLASTIC_SHEET, SlimefunItems.STEEL_INGOT, SlimefunItems.BASIC_CIRCUIT_BOARD, new ItemStack(Material.BLUE_STAINED_GLASS), SlimefunItems.PLASTIC_SHEET, SlimefunItems.STEEL_INGOT, SlimefunItems.PLASTIC_SHEET})
        .register(plugin);

        new AndroidInterface(categories.androids, SlimefunItems.ANDROID_INTERFACE_FUEL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.PLASTIC_SHEET, SlimefunItems.STEEL_INGOT, SlimefunItems.PLASTIC_SHEET, new ItemStack(Material.RED_STAINED_GLASS), SlimefunItems.BASIC_CIRCUIT_BOARD, SlimefunItems.STEEL_INGOT, SlimefunItems.PLASTIC_SHEET, SlimefunItems.STEEL_INGOT, SlimefunItems.PLASTIC_SHEET})
        .register(plugin);

        new ProgrammableAndroid(categories.androids, 1, SlimefunItems.PROGRAMMABLE_ANDROID, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.PLASTIC_SHEET, SlimefunItems.ANDROID_MEMORY_CORE, SlimefunItems.PLASTIC_SHEET, SlimefunItems.COAL_GENERATOR, SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.CHEST), SlimefunItems.PLASTIC_SHEET, SlimefunItems.PLASTIC_SHEET, SlimefunItems.PLASTIC_SHEET})
        .register(plugin);

        new MinerAndroid(categories.androids, 1, SlimefunItems.PROGRAMMABLE_ANDROID_MINER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, new ItemStack(Material.DIAMOND_PICKAXE), SlimefunItems.PROGRAMMABLE_ANDROID, new ItemStack(Material.DIAMOND_PICKAXE), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new FarmerAndroid(categories.androids, 1, SlimefunItems.PROGRAMMABLE_ANDROID_FARMER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, new ItemStack(Material.DIAMOND_HOE), SlimefunItems.PROGRAMMABLE_ANDROID, new ItemStack(Material.DIAMOND_HOE), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new WoodcutterAndroid(categories.androids, 1, SlimefunItems.PROGRAMMABLE_ANDROID_WOODCUTTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, new ItemStack(Material.DIAMOND_AXE), SlimefunItems.PROGRAMMABLE_ANDROID, new ItemStack(Material.DIAMOND_AXE), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new FisherAndroid(categories.androids, 1, SlimefunItems.PROGRAMMABLE_ANDROID_FISHERMAN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, new ItemStack(Material.FISHING_ROD), SlimefunItems.PROGRAMMABLE_ANDROID, new ItemStack(Material.FISHING_ROD), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new ButcherAndroid(categories.androids, 1, SlimefunItems.PROGRAMMABLE_ANDROID_BUTCHER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.GPS_TRANSMITTER, null, new ItemStack(Material.DIAMOND_SWORD), SlimefunItems.PROGRAMMABLE_ANDROID, new ItemStack(Material.DIAMOND_SWORD), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new ProgrammableAndroid(categories.androids, 2, SlimefunItems.PROGRAMMABLE_ANDROID_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.PLASTIC_SHEET, SlimefunItems.ANDROID_MEMORY_CORE, SlimefunItems.PLASTIC_SHEET, SlimefunItems.COMBUSTION_REACTOR, SlimefunItems.PROGRAMMABLE_ANDROID, new ItemStack(Material.CHEST), SlimefunItems.PLASTIC_SHEET, SlimefunItems.POWER_CRYSTAL, SlimefunItems.PLASTIC_SHEET})
        .register(plugin);

        new FisherAndroid(categories.androids, 2, SlimefunItems.PROGRAMMABLE_ANDROID_2_FISHERMAN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, new ItemStack(Material.FISHING_ROD), SlimefunItems.PROGRAMMABLE_ANDROID_2, new ItemStack(Material.FISHING_ROD), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new ButcherAndroid(categories.androids, 2, SlimefunItems.PROGRAMMABLE_ANDROID_2_BUTCHER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.GPS_TRANSMITTER, null, new ItemStack(Material.DIAMOND_SWORD), SlimefunItems.PROGRAMMABLE_ANDROID_2, new ItemStack(Material.DIAMOND_SWORD), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new FarmerAndroid(categories.androids, 2, SlimefunItems.PROGRAMMABLE_ANDROID_2_FARMER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.GPS_TRANSMITTER, null, new ItemStack(Material.DIAMOND_HOE), SlimefunItems.PROGRAMMABLE_ANDROID_2, new ItemStack(Material.DIAMOND_HOE), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new ProgrammableAndroid(categories.androids, 3, SlimefunItems.PROGRAMMABLE_ANDROID_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.PLASTIC_SHEET, SlimefunItems.ANDROID_MEMORY_CORE, SlimefunItems.PLASTIC_SHEET, SlimefunItems.NUCLEAR_REACTOR, SlimefunItems.PROGRAMMABLE_ANDROID_2, new ItemStack(Material.CHEST), SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.POWER_CRYSTAL, SlimefunItems.BLISTERING_INGOT_3})
        .register(plugin);

        new FisherAndroid(categories.androids, 3, SlimefunItems.PROGRAMMABLE_ANDROID_3_FISHERMAN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, null, new ItemStack(Material.FISHING_ROD), SlimefunItems.PROGRAMMABLE_ANDROID_3, new ItemStack(Material.FISHING_ROD), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new ButcherAndroid(categories.androids, 3, SlimefunItems.PROGRAMMABLE_ANDROID_3_BUTCHER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.GPS_TRANSMITTER_3, null, new ItemStack(Material.DIAMOND_SWORD), SlimefunItems.PROGRAMMABLE_ANDROID_3, new ItemStack(Material.DIAMOND_SWORD), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new ElementalRune(categories.magicalResources, SlimefunItems.BLANK_RUNE,
        new ItemStack[] {new ItemStack(Material.STONE), SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.STONE), SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.OBSIDIAN), SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.STONE), SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.STONE)})
        .register(plugin);

        new ElementalRune(categories.magicalResources, SlimefunItems.AIR_RUNE,
        new ItemStack[] {new ItemStack(Material.FEATHER), SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.FEATHER), new ItemStack(Material.GHAST_TEAR), SlimefunItems.BLANK_RUNE, new ItemStack(Material.GHAST_TEAR), new ItemStack(Material.FEATHER), SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.FEATHER)}, 
        new SlimefunItemStack(SlimefunItems.AIR_RUNE, 4))
        .register(plugin);

        new ElementalRune(categories.magicalResources, SlimefunItems.EARTH_RUNE,
        new ItemStack[] {new ItemStack(Material.DIRT), SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.STONE), new ItemStack(Material.OBSIDIAN), SlimefunItems.BLANK_RUNE, new ItemStack(Material.OBSIDIAN), new ItemStack(Material.STONE), SlimefunItems.MAGIC_LUMP_1, new ItemStack(Material.DIRT)}, 
        new SlimefunItemStack(SlimefunItems.EARTH_RUNE, 4))
        .register(plugin);

        new ElementalRune(categories.magicalResources, SlimefunItems.FIRE_RUNE,
        new ItemStack[] {new ItemStack(Material.FIRE_CHARGE), SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.FIRE_CHARGE), new ItemStack(Material.BLAZE_POWDER), SlimefunItems.EARTH_RUNE, new ItemStack(Material.FLINT_AND_STEEL), new ItemStack(Material.FIRE_CHARGE), SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.FIRE_CHARGE)}, 
        new SlimefunItemStack(SlimefunItems.FIRE_RUNE, 4))
        .register(plugin);

        new ElementalRune(categories.magicalResources, SlimefunItems.WATER_RUNE,
        new ItemStack[] {new ItemStack(Material.SALMON), SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.SAND), SlimefunItems.BLANK_RUNE, new ItemStack(Material.SAND), new ItemStack(Material.WATER_BUCKET), SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.COD)}, 
        new SlimefunItemStack(SlimefunItems.WATER_RUNE, 4))
        .register(plugin);

        new ElementalRune(categories.magicalResources, SlimefunItems.ENDER_RUNE,
        new ItemStack[] {new ItemStack(Material.ENDER_PEARL), SlimefunItems.ENDER_LUMP_3, new ItemStack(Material.ENDER_PEARL), new ItemStack(Material.ENDER_EYE), SlimefunItems.BLANK_RUNE, new ItemStack(Material.ENDER_EYE), new ItemStack(Material.ENDER_PEARL), SlimefunItems.ENDER_LUMP_3, new ItemStack(Material.ENDER_PEARL)}, 
        new SlimefunItemStack(SlimefunItems.ENDER_RUNE, 6))
        .register(plugin);

        new ElementalRune(categories.magicalResources, SlimefunItems.LIGHTNING_RUNE,
        new ItemStack[] {new ItemStack(Material.IRON_INGOT), SlimefunItems.MAGIC_LUMP_3, new ItemStack(Material.IRON_INGOT), SlimefunItems.AIR_RUNE, new ItemStack(Material.PHANTOM_MEMBRANE), SlimefunItems.WATER_RUNE, new ItemStack(Material.IRON_INGOT), SlimefunItems.MAGIC_LUMP_3, new ItemStack(Material.IRON_INGOT)}, 
        new SlimefunItemStack(SlimefunItems.LIGHTNING_RUNE, 4))
        .register(plugin);

        new ElementalRune(categories.magicalResources, SlimefunItems.RAINBOW_RUNE,
        new ItemStack[] {new ItemStack(Material.RED_DYE), SlimefunItems.MAGIC_LUMP_3, new ItemStack(Material.CYAN_DYE), new ItemStack(Material.WHITE_WOOL), SlimefunItems.ENDER_RUNE, new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.YELLOW_DYE), SlimefunItems.ENDER_LUMP_3, new ItemStack(Material.MAGENTA_DYE)})
        .register(plugin);

        new SoulboundRune(categories.magicalResources, SlimefunItems.SOULBOUND_RUNE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, SlimefunItems.ESSENCE_OF_AFTERLIFE, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.ENDER_LUMP_3, SlimefunItems.ENDER_RUNE, SlimefunItems.ENDER_LUMP_3, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.ESSENCE_OF_AFTERLIFE, SlimefunItems.MAGIC_LUMP_3})
        .register(plugin);

        new EnchantmentRune(categories.magicalResources, SlimefunItems.ENCHANTMENT_RUNE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, SlimefunItems.MAGICAL_GLASS, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.MAGICAL_GLASS, SlimefunItems.LIGHTNING_RUNE, SlimefunItems.MAGICAL_GLASS, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.MAGICAL_GLASS, SlimefunItems.MAGIC_LUMP_3})
        .register(plugin);

        new InfernalBonemeal(categories.magicalGadgets, SlimefunItems.INFERNAL_BONEMEAL, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.NETHER_WART), SlimefunItems.EARTH_RUNE, new ItemStack(Material.NETHER_WART), SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.BONE_MEAL), SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.NETHER_WART), new ItemStack(Material.BLAZE_POWDER), new ItemStack(Material.NETHER_WART)}, 
        new SlimefunItemStack(SlimefunItems.INFERNAL_BONEMEAL, 8))
        .register(plugin);

        new SlimefunItem(categories.magicalGadgets, SlimefunItems.ELYTRA_SCALE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.ENDER_LUMP_3, SlimefunItems.AIR_RUNE, SlimefunItems.ENDER_LUMP_3, new ItemStack(Material.PHANTOM_MEMBRANE), new ItemStack(Material.FEATHER), new ItemStack(Material.PHANTOM_MEMBRANE), SlimefunItems.ENDER_LUMP_3, SlimefunItems.AIR_RUNE, SlimefunItems.ENDER_LUMP_3})
        .register(plugin);

        new VanillaItem(categories.magicalGadgets, new ItemStack(Material.ELYTRA), "ELYTRA", RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.ELYTRA_SCALE, SlimefunItems.AIR_RUNE, SlimefunItems.ELYTRA_SCALE, SlimefunItems.AIR_RUNE, new ItemStack(Material.LEATHER_CHESTPLATE), SlimefunItems.AIR_RUNE, SlimefunItems.ELYTRA_SCALE, SlimefunItems.AIR_RUNE, SlimefunItems.ELYTRA_SCALE})
        .register(plugin);

        new SlimefunItem(categories.magicalGadgets, SlimefunItems.INFUSED_ELYTRA, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.FLASK_OF_KNOWLEDGE, SlimefunItems.ELYTRA_SCALE, SlimefunItems.FLASK_OF_KNOWLEDGE, SlimefunItems.FLASK_OF_KNOWLEDGE, new ItemStack(Material.ELYTRA), SlimefunItems.FLASK_OF_KNOWLEDGE, SlimefunItems.FLASK_OF_KNOWLEDGE, SlimefunItems.ELYTRA_SCALE, SlimefunItems.FLASK_OF_KNOWLEDGE})
        .register(plugin);

        new SoulboundItem(categories.magicalGadgets, SlimefunItems.SOULBOUND_ELYTRA, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.FLASK_OF_KNOWLEDGE, SlimefunItems.ESSENCE_OF_AFTERLIFE, SlimefunItems.FLASK_OF_KNOWLEDGE, SlimefunItems.ELYTRA_SCALE, new ItemStack(Material.ELYTRA), SlimefunItems.ELYTRA_SCALE, SlimefunItems.FLASK_OF_KNOWLEDGE, SlimefunItems.ESSENCE_OF_AFTERLIFE, SlimefunItems.FLASK_OF_KNOWLEDGE})
        .register(plugin);

        new VanillaItem(categories.magicalGadgets, new ItemStack(Material.TRIDENT), "TRIDENT", RecipeType.ANCIENT_ALTAR,
        new ItemStack[] { new ItemStack(Material.NAUTILUS_SHELL), SlimefunItems.REINFORCED_ALLOY_INGOT, new ItemStack(Material.NAUTILUS_SHELL), SlimefunItems.STAFF_WATER, new ItemStack(Material.DIAMOND_SWORD), SlimefunItems.STAFF_WATER, SlimefunItems.MAGIC_LUMP_3, new ItemStack(Material.NETHER_STAR), SlimefunItems.MAGIC_LUMP_3})
        .register(plugin);

        new VanillaItem(categories.magicalGadgets, new ItemStack(Material.TOTEM_OF_UNDYING), "TOTEM_OF_UNDYING", RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {SlimefunItems.ESSENCE_OF_AFTERLIFE, new ItemStack(Material.EMERALD_BLOCK), SlimefunItems.ESSENCE_OF_AFTERLIFE, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.COMMON_TALISMAN, SlimefunItems.MAGIC_LUMP_3, SlimefunItems.ESSENCE_OF_AFTERLIFE, new ItemStack(Material.EMERALD_BLOCK), SlimefunItems.ESSENCE_OF_AFTERLIFE})
        .register(plugin);

        new RainbowBlock(categories.magicalGadgets, SlimefunItems.RAINBOW_WOOL, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.WHITE_WOOL)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_WOOL, 8), new RainbowTickHandler(ColoredMaterial.WOOL))
        .register(plugin);

        new RainbowBlock(categories.magicalGadgets, SlimefunItems.RAINBOW_GLASS, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.WHITE_STAINED_GLASS), new ItemStack(Material.WHITE_STAINED_GLASS), new ItemStack(Material.WHITE_STAINED_GLASS), new ItemStack(Material.WHITE_STAINED_GLASS), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_STAINED_GLASS), new ItemStack(Material.WHITE_STAINED_GLASS), new ItemStack(Material.WHITE_STAINED_GLASS), new ItemStack(Material.WHITE_STAINED_GLASS)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLASS, 8), new RainbowTickHandler(ColoredMaterial.STAINED_GLASS))
        .register(plugin);

        new RainbowBlock(categories.magicalGadgets, SlimefunItems.RAINBOW_GLASS_PANE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.WHITE_STAINED_GLASS_PANE), new ItemStack(Material.WHITE_STAINED_GLASS_PANE), new ItemStack(Material.WHITE_STAINED_GLASS_PANE), new ItemStack(Material.WHITE_STAINED_GLASS_PANE), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_STAINED_GLASS_PANE), new ItemStack(Material.WHITE_STAINED_GLASS_PANE), new ItemStack(Material.WHITE_STAINED_GLASS_PANE), new ItemStack(Material.WHITE_STAINED_GLASS_PANE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLASS_PANE, 8), new RainbowTickHandler(ColoredMaterial.STAINED_GLASS_PANE))
        .register(plugin);

        new RainbowBlock(categories.magicalGadgets, SlimefunItems.RAINBOW_CLAY, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.WHITE_TERRACOTTA)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_CLAY, 8), new RainbowTickHandler(ColoredMaterial.TERRACOTTA))
        .register(plugin);

        new RainbowBlock(categories.magicalGadgets, SlimefunItems.RAINBOW_CONCRETE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.WHITE_CONCRETE), new ItemStack(Material.WHITE_CONCRETE), new ItemStack(Material.WHITE_CONCRETE), new ItemStack(Material.WHITE_CONCRETE), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_CONCRETE), new ItemStack(Material.WHITE_CONCRETE), new ItemStack(Material.WHITE_CONCRETE), new ItemStack(Material.WHITE_CONCRETE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_CONCRETE, 8), new RainbowTickHandler(ColoredMaterial.CONCRETE))
        .register(plugin);
        
        new RainbowBlock(categories.magicalGadgets, SlimefunItems.RAINBOW_GLAZED_TERRACOTTA, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), new ItemStack(Material.WHITE_GLAZED_TERRACOTTA)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLAZED_TERRACOTTA, 8), new RainbowTickHandler(ColoredMaterial.GLAZED_TERRACOTTA))
        .register(plugin);

        // Christmas

        new RainbowBlock(categories.christmas, SlimefunItems.RAINBOW_WOOL_XMAS, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.GREEN_DYE), new ItemStack(Material.WHITE_WOOL), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.GREEN_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_WOOL_XMAS, 2), new RainbowTickHandler(Material.RED_WOOL, Material.GREEN_WOOL))
        .register(plugin);

        new RainbowBlock(categories.christmas, SlimefunItems.RAINBOW_GLASS_XMAS, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.GREEN_DYE), new ItemStack(Material.WHITE_STAINED_GLASS), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_STAINED_GLASS), new ItemStack(Material.GREEN_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLASS_XMAS, 2), new RainbowTickHandler(Material.RED_STAINED_GLASS, Material.GREEN_STAINED_GLASS))
        .register(plugin);

        new RainbowBlock(categories.christmas, SlimefunItems.RAINBOW_GLASS_PANE_XMAS, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.GREEN_DYE), new ItemStack(Material.WHITE_STAINED_GLASS_PANE), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_STAINED_GLASS_PANE), new ItemStack(Material.GREEN_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLASS_PANE_XMAS, 2), new RainbowTickHandler(Material.RED_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE))
        .register(plugin);

        new RainbowBlock(categories.christmas, SlimefunItems.RAINBOW_CLAY_XMAS, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.GREEN_DYE), new ItemStack(Material.WHITE_TERRACOTTA), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.GREEN_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_CLAY_XMAS, 2), new RainbowTickHandler(Material.RED_TERRACOTTA, Material.GREEN_TERRACOTTA))
        .register(plugin);

        new RainbowBlock(categories.christmas, SlimefunItems.RAINBOW_CONCRETE_XMAS, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.GREEN_DYE), new ItemStack(Material.WHITE_CONCRETE), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_CONCRETE), new ItemStack(Material.GREEN_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_CONCRETE_XMAS, 2), new RainbowTickHandler(Material.RED_CONCRETE, Material.GREEN_CONCRETE))
        .register(plugin);
        
        new RainbowBlock(categories.christmas, SlimefunItems.RAINBOW_GLAZED_TERRACOTTA_XMAS, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.GREEN_DYE), new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), new ItemStack(Material.GREEN_DYE), SlimefunItems.CHRISTMAS_COOKIE, new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLAZED_TERRACOTTA_XMAS, 2), new RainbowTickHandler(Material.RED_GLAZED_TERRACOTTA, Material.GREEN_GLAZED_TERRACOTTA))
        .register(plugin);
        
        // Valentines Day

        new RainbowBlock(categories.valentinesDay, SlimefunItems.RAINBOW_WOOL_VALENTINE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.PINK_DYE), new ItemStack(Material.WHITE_WOOL), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.PINK_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_WOOL_VALENTINE, 2), new RainbowTickHandler(Material.MAGENTA_WOOL, Material.PINK_WOOL))
        .register(plugin);

        new RainbowBlock(categories.valentinesDay, SlimefunItems.RAINBOW_GLASS_VALENTINE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.PINK_DYE), new ItemStack(Material.WHITE_STAINED_GLASS), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_STAINED_GLASS), new ItemStack(Material.PINK_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLASS_VALENTINE, 2), new RainbowTickHandler(Material.MAGENTA_STAINED_GLASS, Material.PINK_STAINED_GLASS))
        .register(plugin);

        new RainbowBlock(categories.valentinesDay, SlimefunItems.RAINBOW_GLASS_PANE_VALENTINE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.PINK_DYE), new ItemStack(Material.WHITE_STAINED_GLASS_PANE), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_STAINED_GLASS_PANE), new ItemStack(Material.PINK_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLASS_PANE_VALENTINE, 2), new RainbowTickHandler(Material.MAGENTA_STAINED_GLASS_PANE, Material.PINK_STAINED_GLASS_PANE))
        .register(plugin);

        new RainbowBlock(categories.valentinesDay, SlimefunItems.RAINBOW_CLAY_VALENTINE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.PINK_DYE), new ItemStack(Material.WHITE_TERRACOTTA), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.PINK_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_CLAY_VALENTINE, 2), new RainbowTickHandler(Material.MAGENTA_TERRACOTTA, Material.PINK_TERRACOTTA))
        .register(plugin);
        
        new RainbowBlock(categories.valentinesDay, SlimefunItems.RAINBOW_CONCRETE_VALENTINE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.PINK_DYE), new ItemStack(Material.WHITE_CONCRETE), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_CONCRETE), new ItemStack(Material.PINK_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_CONCRETE_VALENTINE, 2), new RainbowTickHandler(Material.MAGENTA_CONCRETE, Material.PINK_CONCRETE))
        .register(plugin);
        
        new RainbowBlock(categories.valentinesDay, SlimefunItems.RAINBOW_GLAZED_TERRACOTTA_VALENTINE, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.RED_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.PINK_DYE), new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), new ItemStack(Material.PINK_DYE), new ItemStack(Material.POPPY), new ItemStack(Material.RED_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLAZED_TERRACOTTA_VALENTINE, 2), new RainbowTickHandler(Material.MAGENTA_GLAZED_TERRACOTTA, Material.PINK_GLAZED_TERRACOTTA))
        .register(plugin);

        // Halloween
        
        new RainbowBlock(categories.halloween, SlimefunItems.RAINBOW_WOOL_HALLOWEEN, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.ORANGE_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.WHITE_WOOL), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_WOOL), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.ORANGE_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_WOOL_HALLOWEEN, 2), new RainbowTickHandler(Material.ORANGE_WOOL, Material.BLACK_WOOL))
        .register(plugin);

        new RainbowBlock(categories.halloween, SlimefunItems.RAINBOW_GLASS_HALLOWEEN, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.ORANGE_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.WHITE_STAINED_GLASS), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_STAINED_GLASS), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.ORANGE_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLASS_HALLOWEEN, 2), new RainbowTickHandler(Material.ORANGE_STAINED_GLASS, Material.BLACK_STAINED_GLASS))
        .register(plugin);

        new RainbowBlock(categories.halloween, SlimefunItems.RAINBOW_GLASS_PANE_HALLOWEEN, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.ORANGE_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.WHITE_STAINED_GLASS_PANE), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_STAINED_GLASS_PANE), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.ORANGE_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLASS_PANE_HALLOWEEN, 2), new RainbowTickHandler(Material.ORANGE_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE))
        .register(plugin);

        new RainbowBlock(categories.halloween, SlimefunItems.RAINBOW_CLAY_HALLOWEEN, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.ORANGE_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.WHITE_TERRACOTTA), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_TERRACOTTA), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.ORANGE_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_CLAY_HALLOWEEN, 2), new RainbowTickHandler(Material.ORANGE_TERRACOTTA, Material.BLACK_TERRACOTTA))
        .register(plugin);
        
        new RainbowBlock(categories.halloween, SlimefunItems.RAINBOW_CONCRETE_HALLOWEEN, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.ORANGE_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.WHITE_CONCRETE), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_CONCRETE), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.ORANGE_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_CONCRETE_HALLOWEEN, 2), new RainbowTickHandler(Material.ORANGE_CONCRETE, Material.BLACK_CONCRETE))
        .register(plugin);
        
        new RainbowBlock(categories.halloween, SlimefunItems.RAINBOW_GLAZED_TERRACOTTA_HALLOWEEN, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.ORANGE_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.WHITE_GLAZED_TERRACOTTA), new ItemStack(Material.BLACK_DYE), new ItemStack(Material.PUMPKIN), new ItemStack(Material.ORANGE_DYE)}, 
        new SlimefunItemStack(SlimefunItems.RAINBOW_GLAZED_TERRACOTTA_HALLOWEEN, 2), new RainbowTickHandler(Material.ORANGE_GLAZED_TERRACOTTA, Material.BLACK_GLAZED_TERRACOTTA))
        .register(plugin);

        new WitherProofBlock(categories.technicalComponents, SlimefunItems.WITHER_PROOF_GLASS, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.LEAD_INGOT, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.LEAD_INGOT, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.HARDENED_GLASS, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.LEAD_INGOT, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.LEAD_INGOT}, 
        new SlimefunItemStack(SlimefunItems.WITHER_PROOF_GLASS, 4))
        .register(plugin);

        new GEOScanner(categories.gps, SlimefunItems.GPS_GEO_SCANNER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, null, SlimefunItems.ELECTRO_MAGNET, null, SlimefunItems.STEEL_INGOT, SlimefunItems.STEEL_INGOT, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ELECTRO_MAGNET})
        .register(plugin);

        new PortableGEOScanner(categories.gps, SlimefunItems.PORTABLE_GEO_SCANNER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.ELECTRO_MAGNET, new ItemStack(Material.COMPASS), SlimefunItems.ELECTRO_MAGNET, SlimefunItems.STEEL_INGOT, SlimefunItems.GPS_MARKER_TOOL, SlimefunItems.STEEL_INGOT, SlimefunItems.SOLDER_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.SOLDER_INGOT})
        .register(plugin);

        new OilPump(categories.gps, SlimefunItems.OIL_PUMP, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.STEEL_INGOT, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.STEEL_INGOT, SlimefunItems.STEEL_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.STEEL_INGOT, null, new ItemStack(Material.BUCKET), null})
        .setCapacity(256)
        .setEnergyConsumption(14)
        .setProcessingSpeed(1)
        .register(plugin);

        new GEOMiner(categories.gps, SlimefunItems.GEO_MINER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.DIAMOND_PICKAXE), SlimefunItems.MEDIUM_CAPACITOR, new ItemStack(Material.DIAMOND_PICKAXE), SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.OIL_PUMP, SlimefunItems.REINFORCED_ALLOY_INGOT, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .setCapacity(512)
        .setEnergyConsumption(24)
        .setProcessingSpeed(1)
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.OIL_BUCKET, new RecipeType(new NamespacedKey(plugin, "oil_pump"), SlimefunItems.OIL_PUMP),
        new ItemStack[] {null, null, null, null, new ItemStack(Material.BUCKET), null, null, null, null})
        .register(plugin);

        new SlimefunItem(categories.resources, SlimefunItems.FUEL_BUCKET, RecipeType.REFINERY,
        new ItemStack[] {null, null, null, null, SlimefunItems.OIL_BUCKET, null, null, null, null})
        .register(plugin);

        new RadioactiveItem(categories.resources, Radioactivity.MODERATE, SlimefunItems.NETHER_ICE, RecipeType.GEO_MINER,
        new ItemStack[] {null, null, null, null, null, null, null, null, null})
        .register(plugin);

        new Refinery(categories.electricity, SlimefunItems.REFINERY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.HARDENED_GLASS, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.HARDENED_GLASS, SlimefunItems.HARDENED_GLASS, SlimefunItems.REDSTONE_ALLOY, SlimefunItems.HARDENED_GLASS, new ItemStack(Material.PISTON), SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.PISTON)})
        .setCapacity(256)
        .setEnergyConsumption(16)
        .setProcessingSpeed(1)
        .register(plugin);

        new LavaGenerator(categories.electricity, SlimefunItems.LAVA_GENERATOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.GOLD_16K, null, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.HEATING_COIL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.HEATING_COIL})
        .setCapacity(512)
        .setEnergyProduction(10)
        .register(plugin);

        new LavaGenerator(categories.electricity, SlimefunItems.LAVA_GENERATOR_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.COMPRESSED_CARBON, SlimefunItems.LAVA_GENERATOR, SlimefunItems.COMPRESSED_CARBON, SlimefunItems.HEATING_COIL, SlimefunItems.COMPRESSED_CARBON, SlimefunItems.HEATING_COIL})
        .setCapacity(1024)
        .setEnergyProduction(20)
        .register(plugin);

        new CombustionGenerator(categories.electricity, SlimefunItems.COMBUSTION_REACTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.STEEL_INGOT, null, SlimefunItems.STEEL_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.STEEL_INGOT, SlimefunItems.HEATING_COIL, SlimefunItems.STEEL_INGOT, SlimefunItems.HEATING_COIL})
        .setCapacity(256)
        .setEnergyProduction(12)
        .register(plugin);

        new TeleporterPylon(categories.gps, SlimefunItems.GPS_TELEPORTER_PYLON, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.ZINC_INGOT, new ItemStack(Material.GLASS), SlimefunItems.ZINC_INGOT, new ItemStack(Material.GLASS), SlimefunItems.HEATING_COIL, new ItemStack(Material.GLASS), SlimefunItems.ZINC_INGOT, new ItemStack(Material.GLASS), SlimefunItems.ZINC_INGOT}, 
        new SlimefunItemStack(SlimefunItems.GPS_TELEPORTER_PYLON, 8))
        .register(plugin);

        new Teleporter(categories.gps, SlimefunItems.GPS_TELEPORTATION_MATRIX, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GPS_TELEPORTER_PYLON, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.GPS_TELEPORTER_PYLON, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.GPS_CONTROL_PANEL, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.GPS_TELEPORTER_PYLON, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.GPS_TELEPORTER_PYLON})
        .register(plugin);

        new PortableTeleporter(categories.gps, SlimefunItems.PORTABLE_TELEPORTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.ELECTRO_MAGNET, SlimefunItems.GPS_TRANSMITTER_3, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.REINFORCED_PLATE, SlimefunItems.GPS_TELEPORTATION_MATRIX, SlimefunItems.REINFORCED_PLATE, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.BLISTERING_INGOT_3})
        .register(plugin);

        new SharedActivationPlate(categories.gps, SlimefunItems.GPS_ACTIVATION_DEVICE_SHARED, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.STONE_PRESSURE_PLATE), null, new ItemStack(Material.REDSTONE), SlimefunItems.GPS_TRANSMITTER, new ItemStack(Material.REDSTONE), SlimefunItems.BILLON_INGOT, SlimefunItems.BILLON_INGOT, SlimefunItems.BILLON_INGOT})
        .register(plugin);

        new PersonalActivationPlate(categories.gps, SlimefunItems.GPS_ACTIVATION_DEVICE_PERSONAL, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.LEAD_INGOT, null, SlimefunItems.COBALT_INGOT, SlimefunItems.GPS_ACTIVATION_DEVICE_SHARED, SlimefunItems.COBALT_INGOT, null, SlimefunItems.LEAD_INGOT, null})
        .register(plugin);

        new InfusedHopper(categories.magicalGadgets, SlimefunItems.INFUSED_HOPPER, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] {new ItemStack(Material.OBSIDIAN), SlimefunItems.EARTH_RUNE, new ItemStack(Material.HOPPER), SlimefunItems.ENDER_RUNE, SlimefunItems.INFUSED_MAGNET, SlimefunItems.ENDER_RUNE, new ItemStack(Material.HOPPER), SlimefunItems.EARTH_RUNE, new ItemStack(Material.OBSIDIAN)})
        .register(plugin);

        new RadioactiveItem(categories.resources, Radioactivity.HIGH, SlimefunItems.BLISTERING_INGOT, RecipeType.HEATED_PRESSURE_CHAMBER,
        new ItemStack[] {SlimefunItems.GOLD_24K, SlimefunItems.URANIUM, null, null, null, null, null, null, null})
        .register(plugin);

        new RadioactiveItem(categories.resources, Radioactivity.VERY_HIGH, SlimefunItems.BLISTERING_INGOT_2, RecipeType.HEATED_PRESSURE_CHAMBER,
        new ItemStack[] {SlimefunItems.BLISTERING_INGOT, SlimefunItems.CARBONADO, null, null, null, null, null, null, null})
        .register(plugin);

        new RadioactiveItem(categories.resources, Radioactivity.VERY_HIGH, SlimefunItems.BLISTERING_INGOT_3, RecipeType.HEATED_PRESSURE_CHAMBER,
        new ItemStack[] {SlimefunItems.BLISTERING_INGOT_2, new ItemStack(Material.NETHER_STAR), null, null, null, null, null, null, null})
        .register(plugin);

        new RadioactiveItem(categories.resources, Radioactivity.VERY_HIGH, SlimefunItems.ENRICHED_NETHER_ICE, RecipeType.HEATED_PRESSURE_CHAMBER,
        new ItemStack[] {SlimefunItems.NETHER_ICE, SlimefunItems.PLUTONIUM, null, null, null, null, null, null, null},
        new SlimefunItemStack(SlimefunItems.ENRICHED_NETHER_ICE, 4))
        .register(plugin);

        new ElevatorPlate(categories.gps, SlimefunItems.ELEVATOR_PLATE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.STONE_PRESSURE_PLATE), null, new ItemStack(Material.PISTON), SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.PISTON), SlimefunItems.ALUMINUM_BRONZE_INGOT, SlimefunItems.ALUMINUM_BRONZE_INGOT, SlimefunItems.ALUMINUM_BRONZE_INGOT},
        new SlimefunItemStack(SlimefunItems.ELEVATOR_PLATE, 2))
        .register(plugin);

        new FoodFabricator(categories.electricity, SlimefunItems.FOOD_FABRICATOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.BILLON_INGOT, SlimefunItems.SILVER_INGOT, SlimefunItems.BILLON_INGOT, SlimefunItems.TIN_CAN, SlimefunItems.SMALL_CAPACITOR, SlimefunItems.TIN_CAN, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .setCapacity(256)
        .setEnergyConsumption(7)
        .setProcessingSpeed(1)
        .register(plugin);

        new FoodFabricator(categories.electricity, SlimefunItems.FOOD_FABRICATOR_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.FOOD_FABRICATOR, SlimefunItems.ELECTRIC_MOTOR, null, SlimefunItems.ELECTRO_MAGNET, null})
        .setCapacity(512)
        .setEnergyConsumption(24)
        .setProcessingSpeed(6)
        .register(plugin);

        new OrganicFood(categories.misc, SlimefunItems.WHEAT_ORGANIC_FOOD, Material.WHEAT)
        .register(plugin);

        new OrganicFood(categories.misc, SlimefunItems.CARROT_ORGANIC_FOOD, Material.CARROT)
        .register(plugin);

        new OrganicFood(categories.misc, SlimefunItems.POTATO_ORGANIC_FOOD, Material.POTATO)
        .register(plugin);

        new OrganicFood(categories.misc, SlimefunItems.SEEDS_ORGANIC_FOOD, Material.WHEAT_SEEDS)
        .register(plugin);

        new OrganicFood(categories.misc, SlimefunItems.BEETROOT_ORGANIC_FOOD, Material.BEETROOT)
        .register(plugin);

        new OrganicFood(categories.misc, SlimefunItems.MELON_ORGANIC_FOOD, Material.MELON_SLICE)
        .register(plugin);

        new OrganicFood(categories.misc, SlimefunItems.APPLE_ORGANIC_FOOD, Material.APPLE)
        .register(plugin);

        new OrganicFood(categories.misc, SlimefunItems.SWEET_BERRIES_ORGANIC_FOOD, Material.SWEET_BERRIES)
        .register(plugin);

        new OrganicFood(categories.misc, SlimefunItems.KELP_ORGANIC_FOOD, Material.DRIED_KELP)
        .register(plugin);

        new OrganicFood(categories.misc, SlimefunItems.COCOA_ORGANIC_FOOD, Material.COCOA_BEANS)
        .register(plugin);

        new AutoBreeder(categories.electricity, SlimefunItems.AUTO_BREEDER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.GOLD_18K, SlimefunItems.TIN_CAN, SlimefunItems.GOLD_18K, SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.HAY_BLOCK), SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.LEAD_INGOT, SlimefunItems.FOOD_FABRICATOR, SlimefunItems.LEAD_INGOT})
        .register(plugin);

        new AnimalGrowthAccelerator(categories.electricity, SlimefunItems.ANIMAL_GROWTH_ACCELERATOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.BLISTERING_INGOT_3, null, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.WHEAT_ORGANIC_FOOD, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.AUTO_BREEDER, SlimefunItems.REINFORCED_ALLOY_INGOT})
        .register(plugin);

        new TreeGrowthAccelerator(categories.electricity, SlimefunItems.TREE_GROWTH_ACCELERATOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.CARBONADO, null, SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.DIAMOND_AXE), SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.MAGNESIUM_SALT, SlimefunItems.BIG_CAPACITOR, SlimefunItems.MAGNESIUM_SALT})
        .register(plugin);
        
        new ExpCollector(categories.electricity, SlimefunItems.EXP_COLLECTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.BLISTERING_INGOT_3, null, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.AUTO_ENCHANTER, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.ALUMINUM_BRONZE_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ALUMINUM_BRONZE_INGOT})
        .register(plugin);

        new FoodComposter(categories.electricity, SlimefunItems.FOOD_COMPOSTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.FOOD_FABRICATOR, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.TIN_CAN, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.TIN_CAN, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .setCapacity(256)
        .setEnergyConsumption(8)
        .setProcessingSpeed(1)
        .register(plugin);

        new FoodComposter(categories.electricity, SlimefunItems.FOOD_COMPOSTER_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.HARDENED_METAL_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.FOOD_COMPOSTER, SlimefunItems.ELECTRIC_MOTOR, null, SlimefunItems.ELECTRO_MAGNET, null})
        .setCapacity(512)
        .setEnergyConsumption(26)
        .setProcessingSpeed(10)
        .register(plugin);

        new OrganicFertilizer(categories.misc, SlimefunItems.WHEAT_FERTILIZER, SlimefunItems.WHEAT_ORGANIC_FOOD)
        .register(plugin);

        new OrganicFertilizer(categories.misc, SlimefunItems.CARROT_FERTILIZER, SlimefunItems.CARROT_ORGANIC_FOOD)
        .register(plugin);

        new OrganicFertilizer(categories.misc, SlimefunItems.POTATO_FERTILIZER, SlimefunItems.POTATO_ORGANIC_FOOD)
        .register(plugin);

        new OrganicFertilizer(categories.misc, SlimefunItems.SEEDS_FERTILIZER, SlimefunItems.SEEDS_ORGANIC_FOOD)
        .register(plugin);

        new OrganicFertilizer(categories.misc, SlimefunItems.BEETROOT_FERTILIZER, SlimefunItems.BEETROOT_ORGANIC_FOOD)
        .register(plugin);

        new OrganicFertilizer(categories.misc, SlimefunItems.MELON_FERTILIZER, SlimefunItems.MELON_ORGANIC_FOOD)
        .register(plugin);

        new OrganicFertilizer(categories.misc, SlimefunItems.APPLE_FERTILIZER, SlimefunItems.APPLE_ORGANIC_FOOD)
        .register(plugin);

        new OrganicFertilizer(categories.misc, SlimefunItems.SWEET_BERRIES_FERTILIZER, SlimefunItems.SWEET_BERRIES_ORGANIC_FOOD)
        .register(plugin);

        new OrganicFertilizer(categories.misc, SlimefunItems.KELP_FERTILIZER, SlimefunItems.KELP_ORGANIC_FOOD)
        .register(plugin);

        new OrganicFertilizer(categories.misc, SlimefunItems.COCOA_FERTILIZER, SlimefunItems.COCOA_ORGANIC_FOOD)
        .register(plugin);

        new CropGrowthAccelerator(categories.electricity, SlimefunItems.CROP_GROWTH_ACCELERATOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.BLISTERING_INGOT_3, null, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.PROGRAMMABLE_ANDROID_FARMER, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.ANIMAL_GROWTH_ACCELERATOR, SlimefunItems.ELECTRO_MAGNET}) {

            @Override
            public int getEnergyConsumption() {
                return 25;
            }

            @Override
            public int getRadius() {
                return 3;
            }

            @Override
            public int getSpeed() {
                return 3;
            }

        }.register(plugin);

        new CropGrowthAccelerator(categories.electricity, SlimefunItems.CROP_GROWTH_ACCELERATOR_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.BLISTERING_INGOT_3, null, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.CROP_GROWTH_ACCELERATOR, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.ELECTRO_MAGNET}) {

            @Override
            public int getEnergyConsumption() {
                return 30;
            }

            @Override
            public int getRadius() {
                return 4;
            }

            @Override
            public int getSpeed() {
                return 4;
            }

        }.register(plugin);

        new Freezer(categories.electricity, SlimefunItems.FREEZER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.SILVER_INGOT, null, SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.PACKED_ICE), SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.COOLING_UNIT, SlimefunItems.MEDIUM_CAPACITOR, SlimefunItems.COOLING_UNIT})
        .setCapacity(256)
        .setEnergyConsumption(9)
        .setProcessingSpeed(1)
        .register(plugin);

        new Freezer(categories.electricity, SlimefunItems.FREEZER_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.SILVER_INGOT, null, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.FREEZER, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.COOLING_UNIT, SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.COOLING_UNIT})
        .setCapacity(256)
        .setEnergyConsumption(15)
        .setProcessingSpeed(2)
        .register(plugin);

        new CoolantCell(categories.technicalComponents, SlimefunItems.REACTOR_COOLANT_CELL, RecipeType.FREEZER,
        new ItemStack[] {new ItemStack(Material.BLUE_ICE), null, null, null, null, null, null, null, null})
        .register(plugin);

        new CoolantCell(categories.technicalComponents, SlimefunItems.NETHER_ICE_COOLANT_CELL, RecipeType.HEATED_PRESSURE_CHAMBER,
        new ItemStack[] {SlimefunItems.ENRICHED_NETHER_ICE, null, null, null, null, null, null, null, null},
        new SlimefunItemStack(SlimefunItems.NETHER_ICE_COOLANT_CELL, 8))
        .register(plugin);

        new RadioactiveItem(categories.resources, Radioactivity.HIGH, SlimefunItems.NEPTUNIUM, RecipeType.NUCLEAR_REACTOR,
        new ItemStack[] {SlimefunItems.URANIUM, null, null, null, null, null, null, null, null})
        .register(plugin);

        new RadioactiveItem(categories.resources, Radioactivity.VERY_HIGH, SlimefunItems.PLUTONIUM, RecipeType.NUCLEAR_REACTOR,
        new ItemStack[] {SlimefunItems.NEPTUNIUM, null, null, null, null, null, null, null, null})
        .register(plugin);

        new RadioactiveItem(categories.resources, Radioactivity.VERY_HIGH, SlimefunItems.BOOSTED_URANIUM, RecipeType.HEATED_PRESSURE_CHAMBER,
        new ItemStack[] {SlimefunItems.PLUTONIUM, SlimefunItems.URANIUM, null, null, null, null, null, null, null})
        .register(plugin);

        new NuclearReactor(categories.electricity, SlimefunItems.NUCLEAR_REACTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.CARBONADO_EDGED_CAPACITOR, SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.REINFORCED_PLATE, SlimefunItems.COOLING_UNIT, SlimefunItems.REINFORCED_PLATE, SlimefunItems.LEAD_INGOT, SlimefunItems.REINFORCED_PLATE, SlimefunItems.LEAD_INGOT}){
            
            @Override
            public int getEnergyProduction() {
                return 250;
            }

            @Override
            public int getCapacity() {
                return 16384;
            }
            
        }.register(plugin);

        new NetherStarReactor(categories.electricity, SlimefunItems.NETHER_STAR_REACTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.BOOSTED_URANIUM, SlimefunItems.CARBONADO_EDGED_CAPACITOR, SlimefunItems.BOOSTED_URANIUM, SlimefunItems.REINFORCED_PLATE, new ItemStack(Material.NETHER_STAR), SlimefunItems.REINFORCED_PLATE, SlimefunItems.CORINTHIAN_BRONZE_INGOT, SlimefunItems.REINFORCED_PLATE, SlimefunItems.CORINTHIAN_BRONZE_INGOT}){

            @Override
            public int getEnergyProduction() {
                return 512;
            }

            @Override
            public int getCapacity() {
                return 32768;
            }

        }.register(plugin);

        new UnplaceableBlock(categories.cargo, SlimefunItems.CARGO_MOTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.HARDENED_GLASS, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.HARDENED_GLASS, SlimefunItems.SILVER_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.SILVER_INGOT, SlimefunItems.HARDENED_GLASS, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.HARDENED_GLASS}, 
        new SlimefunItemStack(SlimefunItems.CARGO_MOTOR, 4))
        .register(plugin);

        new CargoManager(categories.cargo, SlimefunItems.CARGO_MANAGER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.HOLOGRAM_PROJECTOR, null, SlimefunItems.REINFORCED_PLATE, SlimefunItems.CARGO_MOTOR, SlimefunItems.REINFORCED_PLATE, SlimefunItems.ALUMINUM_BRONZE_INGOT, SlimefunItems.ANDROID_MEMORY_CORE, SlimefunItems.ALUMINUM_BRONZE_INGOT})
        .register(plugin);

        new CargoConnectorNode(categories.cargo, SlimefunItems.CARGO_CONNECTOR_NODE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.BRONZE_INGOT, SlimefunItems.SILVER_INGOT, SlimefunItems.BRONZE_INGOT, SlimefunItems.SILVER_INGOT, SlimefunItems.CARGO_MOTOR, SlimefunItems.SILVER_INGOT, SlimefunItems.BRONZE_INGOT, SlimefunItems.SILVER_INGOT, SlimefunItems.BRONZE_INGOT}, 
        new SlimefunItemStack(SlimefunItems.CARGO_CONNECTOR_NODE, 4))
        .register(plugin);

        new CargoInputNode(categories.cargo, SlimefunItems.CARGO_INPUT_NODE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.HOPPER), null, SlimefunItems.BILLON_INGOT, SlimefunItems.CARGO_CONNECTOR_NODE, SlimefunItems.BILLON_INGOT, null, new ItemStack(Material.HOPPER), null}, 
        new SlimefunItemStack(SlimefunItems.CARGO_INPUT_NODE, 2))
        .register(plugin);

        new CargoOutputNode(categories.cargo, SlimefunItems.CARGO_OUTPUT_NODE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.HOPPER), null, SlimefunItems.BRASS_INGOT, SlimefunItems.CARGO_CONNECTOR_NODE, SlimefunItems.BRASS_INGOT, null, new ItemStack(Material.HOPPER), null}, 
        new SlimefunItemStack(SlimefunItems.CARGO_OUTPUT_NODE, 2))
        .register(plugin);

        new AdvancedCargoOutputNode(categories.cargo, SlimefunItems.CARGO_OUTPUT_NODE_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.CARGO_MOTOR, null, SlimefunItems.COBALT_INGOT, SlimefunItems.CARGO_OUTPUT_NODE, SlimefunItems.COBALT_INGOT, null, SlimefunItems.CARGO_MOTOR, null})
        .register(plugin);

        new ReactorAccessPort(categories.cargo, SlimefunItems.REACTOR_ACCESS_PORT, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.BLISTERING_INGOT_3, null, SlimefunItems.LEAD_INGOT, SlimefunItems.CARGO_MOTOR, SlimefunItems.LEAD_INGOT, null, SlimefunItems.ELECTRIC_MOTOR, null})
        .register(plugin);

        new FluidPump(categories.electricity, SlimefunItems.FLUID_PUMP, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.MEDIUM_CAPACITOR, null, new ItemStack(Material.BUCKET), SlimefunItems.CARGO_MOTOR, new ItemStack(Material.BUCKET), null, SlimefunItems.OIL_PUMP, null})
        .register(plugin);

        new TrashCan(categories.cargo, SlimefunItems.TRASH_CAN, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.PORTABLE_DUSTBIN, null, SlimefunItems.LEAD_INGOT, SlimefunItems.CARGO_MOTOR, SlimefunItems.LEAD_INGOT, SlimefunItems.ALUMINUM_INGOT, SlimefunItems.LEAD_INGOT, SlimefunItems.ALUMINUM_INGOT})
        .register(plugin);

        new CarbonPress(categories.electricity, SlimefunItems.CARBON_PRESS, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CARBON, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.CARBON, SlimefunItems.CARBON, SlimefunItems.HEATED_PRESSURE_CHAMBER, SlimefunItems.CARBON, SlimefunItems.HEATING_COIL, SlimefunItems.CARBONADO, SlimefunItems.HEATING_COIL})
        .setCapacity(256)
        .setEnergyConsumption(10)
        .setProcessingSpeed(1)
        .register(plugin);

        new CarbonPress(categories.electricity, SlimefunItems.CARBON_PRESS_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CARBONADO, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.CARBONADO, SlimefunItems.CARBON, SlimefunItems.CARBON_PRESS, SlimefunItems.CARBON, SlimefunItems.HEATING_COIL, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.HEATING_COIL})
        .setCapacity(512)
        .setEnergyConsumption(25)
        .setProcessingSpeed(3)
        .register(plugin);

        new CarbonPress(categories.electricity, SlimefunItems.CARBON_PRESS_3, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.CARBONADO, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.CARBONADO, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.CARBON_PRESS_2, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.HEATING_COIL, SlimefunItems.ELECTRO_MAGNET, SlimefunItems.HEATING_COIL})
        .setCapacity(512)
        .setEnergyConsumption(90)
        .setProcessingSpeed(15)
        .register(plugin);

        new ElectricSmeltery(categories.electricity, SlimefunItems.ELECTRIC_SMELTERY, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.NETHER_BRICKS), SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.NETHER_BRICKS), SlimefunItems.HEATING_COIL, SlimefunItems.ELECTRIC_INGOT_FACTORY, SlimefunItems.HEATING_COIL, SlimefunItems.GILDED_IRON, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.GILDED_IRON})
        .setCapacity(512)
        .setEnergyConsumption(10)
        .setProcessingSpeed(1)
        .register(plugin);

        new ElectricSmeltery(categories.electricity, SlimefunItems.ELECTRIC_SMELTERY_2, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.POWER_CRYSTAL, SlimefunItems.DAMASCUS_STEEL_INGOT, SlimefunItems.HEATING_COIL, SlimefunItems.ELECTRIC_SMELTERY, SlimefunItems.HEATING_COIL, SlimefunItems.GILDED_IRON, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.GILDED_IRON})
        .setCapacity(1024)
        .setEnergyConsumption(20)
        .setProcessingSpeed(3)
        .register(plugin);

        new IronGolemAssembler(categories.electricity, SlimefunItems.IRON_GOLEM_ASSEMBLER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.ADVANCED_CIRCUIT_BOARD, SlimefunItems.BLISTERING_INGOT_3, new ItemStack(Material.IRON_BLOCK), SlimefunItems.ANDROID_MEMORY_CORE, new ItemStack(Material.IRON_BLOCK), SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.CARBONADO_EDGED_CAPACITOR})
        .register(plugin);

        new WitherAssembler(categories.electricity, SlimefunItems.WITHER_ASSEMBLER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.BLISTERING_INGOT_3, new ItemStack(Material.NETHER_STAR), SlimefunItems.BLISTERING_INGOT_3, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.ANDROID_MEMORY_CORE, SlimefunItems.WITHER_PROOF_OBSIDIAN, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.REINFORCED_ALLOY_INGOT, SlimefunItems.CARBONADO_EDGED_CAPACITOR})
        .register(plugin);
        
        new TapeMeasure(categories.usefulItems, SlimefunItems.TAPE_MEASURE, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {SlimefunItems.SILICON, new ItemStack(Material.YELLOW_DYE), SlimefunItems.SILICON, new ItemStack(Material.YELLOW_DYE), new ItemStack(Material.STRING), new ItemStack(Material.YELLOW_DYE), SlimefunItems.GILDED_IRON, new ItemStack(Material.YELLOW_DYE), SlimefunItems.SILICON})
        .register(plugin);
        
        MinecraftVersion minecraftVersion = SlimefunPlugin.getMinecraftVersion();

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_15)) {
            new SlimefunItem(categories.magicalArmor, SlimefunItems.BEE_HELMET, RecipeType.ARMOR_FORGE,
            new ItemStack[] {SlimefunItems.GOLD_8K, new ItemStack(Material.HONEY_BLOCK), SlimefunItems.GOLD_8K, new ItemStack(Material.HONEYCOMB_BLOCK), null, new ItemStack(Material.HONEYCOMB_BLOCK), null, null, null})
            .register(plugin);

            new BeeWings(categories.magicalArmor, SlimefunItems.BEE_WINGS, RecipeType.ARMOR_FORGE,
            new ItemStack[] {SlimefunItems.GOLD_8K, null, SlimefunItems.GOLD_8K, new ItemStack(Material.HONEYCOMB_BLOCK), new ItemStack(Material.ELYTRA), new ItemStack(Material.HONEYCOMB_BLOCK), new ItemStack(Material.HONEY_BLOCK), SlimefunItems.GOLD_8K, new ItemStack(Material.HONEY_BLOCK)})
            .register(plugin);

            new SlimefunItem(categories.magicalArmor, SlimefunItems.BEE_LEGGINGS, RecipeType.ARMOR_FORGE,
            new ItemStack[] {SlimefunItems.GOLD_8K, new ItemStack(Material.HONEY_BLOCK), SlimefunItems.GOLD_8K, new ItemStack(Material.HONEYCOMB_BLOCK), null, new ItemStack(Material.HONEYCOMB_BLOCK), new ItemStack(Material.HONEYCOMB_BLOCK), null, new ItemStack(Material.HONEYCOMB_BLOCK)})
            .register(plugin);

            new LongFallBoots(categories.magicalArmor, SlimefunItems.BEE_BOOTS, RecipeType.ARMOR_FORGE,
            new ItemStack[] {null, null, null, SlimefunItems.GOLD_8K, null, SlimefunItems.GOLD_8K, new ItemStack(Material.HONEY_BLOCK), null, new ItemStack(Material.HONEY_BLOCK)},
            new PotionEffect[] {new PotionEffect(PotionEffectType.JUMP, 300, 2)})
            .register(plugin);
        }

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_16)) {
            new VillagerRune(categories.magicalResources, SlimefunItems.VILLAGER_RUNE, RecipeType.ANCIENT_ALTAR, 
            new ItemStack[] {SlimefunItems.MAGIC_LUMP_3, SlimefunItems.MAGICAL_GLASS, new ItemStack(Material.CRYING_OBSIDIAN), SlimefunItems.STRANGE_NETHER_GOO, SlimefunItems.FIRE_RUNE, SlimefunItems.STRANGE_NETHER_GOO, new ItemStack(Material.CRYING_OBSIDIAN), SlimefunItems.MAGICAL_GLASS, SlimefunItems.MAGIC_LUMP_3},
            new SlimefunItemStack(SlimefunItems.VILLAGER_RUNE, 3))
            .register(plugin);

            new StrangeNetherGoo(categories.magicalResources, SlimefunItems.STRANGE_NETHER_GOO, RecipeType.BARTER_DROP,
            new ItemStack[] {null, null, null, null, new CustomItem(HeadTexture.PIGLIN_HEAD.getAsItemStack(), "&fPiglin"), null, null, null, null})
            .register(plugin);
        }

        new SlimefunItem(categories.magicalResources, SlimefunItems.RAINBOW_LEATHER, RecipeType.ANCIENT_ALTAR,
        new ItemStack[] { new ItemStack(Material.EMERALD), new ItemStack(Material.LEATHER), SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.RABBIT_HIDE), SlimefunItems.RAINBOW_RUNE, new ItemStack(Material.RABBIT_HIDE), SlimefunItems.MAGIC_LUMP_2, new ItemStack(Material.LEATHER), new ItemStack(Material.EMERALD) },
        new SlimefunItemStack(SlimefunItems.RAINBOW_LEATHER, 4))
        .register(plugin);

        new ElytraCap(categories.magicalArmor, SlimefunItems.ELYTRA_CAP, RecipeType.ARMOR_FORGE,
        new ItemStack[] {new ItemStack(Material.SLIME_BALL), new ItemStack(Material.SLIME_BALL), new ItemStack(Material.SLIME_BALL), SlimefunItems.ELYTRA_SCALE, SlimefunItems.ELYTRA_SCALE, SlimefunItems.ELYTRA_SCALE, new ItemStack(Material.SLIME_BALL), new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.SLIME_BALL)})
        .register(plugin);
        
        new UnplaceableBlock(categories.cargo, SlimefunItems.CRAFTING_MOTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {new ItemStack(Material.CRAFTING_TABLE), SlimefunItems.BLISTERING_INGOT_3, new ItemStack(Material.CRAFTING_TABLE), SlimefunItems.REDSTONE_ALLOY, SlimefunItems.CARGO_MOTOR, SlimefunItems.REDSTONE_ALLOY, new ItemStack(Material.CRAFTING_TABLE), SlimefunItems.BLISTERING_INGOT_3, new ItemStack(Material.CRAFTING_TABLE)},
        new SlimefunItemStack(SlimefunItems.CRAFTING_MOTOR, 2))
        .register(plugin);

        new VanillaAutoCrafter(categories.cargo, SlimefunItems.VANILLA_AUTO_CRAFTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.CARGO_MOTOR, null, new ItemStack(Material.CRAFTING_TABLE), SlimefunItems.CRAFTING_MOTOR, new ItemStack(Material.CRAFTING_TABLE), null, SlimefunItems.ELECTRIC_MOTOR, null})
        .setCapacity(256)
        .setEnergyConsumption(16)
        .register(plugin);

        new EnhancedAutoCrafter(categories.cargo, SlimefunItems.ENHANCED_AUTO_CRAFTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.CRAFTING_MOTOR, null, new ItemStack(Material.CRAFTING_TABLE), new ItemStack(Material.DISPENSER), new ItemStack(Material.CRAFTING_TABLE), null, SlimefunItems.CARGO_MOTOR, null})
        .setCapacity(256)
        .setEnergyConsumption(16)
        .register(plugin);

        new ArmorAutoCrafter(categories.cargo, SlimefunItems.ARMOR_AUTO_CRAFTER, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, SlimefunItems.CRAFTING_MOTOR, null, new ItemStack(Material.DISPENSER), new ItemStack(Material.ANVIL), new ItemStack(Material.DISPENSER), new ItemStack(Material.CRAFTING_TABLE), SlimefunItems.ELECTRIC_MOTOR, new ItemStack(Material.CRAFTING_TABLE)})
        .setCapacity(256)
        .setEnergyConsumption(32)
        .register(plugin);

        new ProduceCollector(categories.electricity, SlimefunItems.PRODUCE_COLLECTOR, RecipeType.ENHANCED_CRAFTING_TABLE,
        new ItemStack[] {null, new ItemStack(Material.HAY_BLOCK), null, new ItemStack(Material.BUCKET), SlimefunItems.MEDIUM_CAPACITOR, new ItemStack(Material.BUCKET), SlimefunItems.ALUMINUM_BRASS_INGOT, SlimefunItems.ELECTRIC_MOTOR, SlimefunItems.ALUMINUM_BRASS_INGOT})
        .setCapacity(256)
        .setProcessingSpeed(1)
        .setEnergyConsumption(16)
        .register(plugin);

        // @formatter:on
    }

    @ParametersAreNonnullByDefault
    private static void registerArmorSet(Category category, ItemStack baseComponent, ItemStack[] items, String idSyntax, boolean vanilla, PotionEffect[][] effects, SlimefunAddon addon) {
        String[] components = new String[] { "_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS" };
        List<ItemStack[]> recipes = new ArrayList<>();

        recipes.add(new ItemStack[] { baseComponent, baseComponent, baseComponent, baseComponent, null, baseComponent, null, null, null });
        recipes.add(new ItemStack[] { baseComponent, null, baseComponent, baseComponent, baseComponent, baseComponent, baseComponent, baseComponent, baseComponent });
        recipes.add(new ItemStack[] { baseComponent, baseComponent, baseComponent, baseComponent, null, baseComponent, baseComponent, null, baseComponent });
        recipes.add(new ItemStack[] { null, null, null, baseComponent, null, baseComponent, baseComponent, null, baseComponent });

        for (int i = 0; i < 4; i++) {
            if (vanilla) {
                new VanillaItem(category, items[i], idSyntax + components[i], RecipeType.ARMOR_FORGE, recipes.get(i)).register(addon);
            } else if (i < effects.length && effects[i].length > 0) {
                new SlimefunArmorPiece(category, new SlimefunItemStack(idSyntax + components[i], items[i]), RecipeType.ARMOR_FORGE, recipes.get(i), effects[i]).register(addon);
            } else {
                new SlimefunItem(category, new SlimefunItemStack(idSyntax + components[i], items[i]), RecipeType.ARMOR_FORGE, recipes.get(i)).register(addon);
            }
        }
    }

}
