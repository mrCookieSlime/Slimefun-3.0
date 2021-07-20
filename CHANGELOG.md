# Table of contents
- [Release Candidate 27 (TBD)](#release-candidate-27-tbd)
- [Release Candidate 26 (20 Jul 2021)](#release-candidate-26-20-jul-2021)
- [Release Candidate 25 (20 Jun 2021)](#release-candidate-25-20-jun-2021)
- [Release Candidate 24 (03 Jun 2021)](#release-candidate-24-03-jun-2021)
- [Release Candidate 23 (19 May 2021)](#release-candidate-23-19-may-2021)
- [Release Candidate 22 (18 Apr 2021)](#release-candidate-22-18-apr-2021)
- [Release Candidate 21 (14 Mar 2021)](#release-candidate-21-14-mar-2021)
- [Release Candidate 20 (30 Jan 2021)](#release-candidate-20-30-jan-2021)
- [Release Candidate 19 (11 Jan 2021)](#release-candidate-19-11-jan-2021)
- [Release Candidate 18 (03 Dec 2020)](#release-candidate-18-03-dec-2020)
- [Release Candidate 17 (17 Oct 2020)](#release-candidate-17-17-oct-2020)
- [Release Candidate 16 (07 Sep 2020)](#release-candidate-16-07-sep-2020)
- [Release Candidate 15 (01 Aug 2020)](#release-candidate-15-01-aug-2020)
- [Release Candidate 14 (12 Jul 2020)](#release-candidate-14-12-jul-2020)
- [Release Candidate 13 (16 Jun 2020)](#release-candidate-13-16-jun-2020)
- [Release Candidate 12 (27 May 2020)](#release-candidate-12-27-may-2020)
- [Release Candidate 11 (25 Apr 2020)](#release-candidate-11-25-apr-2020)
- [Release Candidate 10 (28 Mar 2020)](#release-candidate-10-28-mar-2020)
- [Release Candidate 9 (07 Mar 2020)](#release-candidate-9-07-mar-2020)
- [Release Candidate 8 (06 Mar 2020)](#release-candidate-8-06-mar-2020)
- [Release Candidate 7 (06 Mar 2020)](#release-candidate-7-06-mar-2020)
- [Release Candidate 6 (16 Feb 2020)](#release-candidate-6-16-feb-2020)
- [Release Candidate 5 (09 Feb 2020)](#release-candidate-5-09-feb-2020)
- [Release Candidate 4 (06 Jan 2020)](#release-candidate-4-06-jan-2020)
- [Release Candidate 3 (21 Nov 2019)](#release-candidate-3-21-nov-2019)
- [Release Candidate 2 (29 Sep 2019)](#release-candidate-2-29-sep-2019)
- [Release Candidate 1 (26 Sep 2019)](#release-candidate-1-26-sep-2019)

## Release Candidate 27 (TBD)

#### Additions

#### Changes

#### Fixes

## Release Candidate 26 (20 Jul 2021)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#26

#### Additions
* Diamonds can now be ground into Carbon using a Grind Stone
* Deepslate ores can now be doubled using an Ore Crusher
* Tridents can now be crafted
* The Industrial Miner can now mine up to the minimum world limit (previously only until y=0)
* (API) Added SlimefunItemSpawnEvent and ItemSpawnReason
* Added "Amethyst Block -> 4 Amethyst Shards" recipe to the Grind Stone
* Added an option to the IndustrialMiner to configure if they can mine deepslate ores
* Added sound effects for Slime Boots
* Added `sounds.yml` file to configure sound effects for Slimefun
* (API) Added `LimitedUseItem`

#### Changes
* The Industrial Miner now properly drops raw ores in 1.17+ instead of ore blocks

#### Fixes
* Fixed #2966
* Fixed Auto-Crafters bypassing the `doLimitedCrafting` gamerule
* Fixed "Talisman of Anvil" having issues with off-hand items
* Fixed #3136

## Release Candidate 25 (20 Jun 2021)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#25

#### Additions
* Added "4 Charcoal -> 1 Coal" recipe to the Compressor
* Added a startup warning for when CS-CoreLib is still installed
* (API) Added WeaponUseHandler
* Added support for Minecraft 1.17
* Added "Deepslate -> Lava" recipe to the Crucible
* Added "Cobbled Deepslate -> Lava" recipe to the Crucible
* Added "Tuff -> Lava" recipe to the Crucible
* Added "Copper Ingot -> Copper Dust" recipe to the Ingot Pulverizer
* Added Goats as a milk source for the Produce Collector
* Added "Raw Iron -> Iron Dust" recipe to the Ore Crusher
* Added "Raw Gold -> Gold Dust" recipe to the Ore Crusher
* Added "Raw Copper -> Copper Dust" recipe to the Ore Crusher
* Debug Fish can now read Slimefun Tags for vanilla blocks
* The Icy Bow now gives a freezing effect on 1.17

#### Changes
* (API) Removed `SlimefunItem#getID()` (renamed to `SlimefunItem#getId()`)
* (API) Removed AsyncGeneratorProcessCompleteEvent
* (API) Removed AsyncMachineProcessCompleteEvent
* (API) Removed AsyncReactorProcessCompleteEvent

#### Fixes
* Fixed #3105
* Fixed #3116

## Release Candidate 24 (03 Jun 2021)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#24

#### Additions
* (API) Added AsyncMachineOperationFinishEvent
* The speed of the Ancient Altar can now be configured in the `Items.yml` file
* The message "You do not have enough knowledge to understand this" now includes the name of the item you need to research.

#### Changes
* (API) Refactored "Machine Process" API
* (API) Deprecated AsyncGeneratorProcessCompleteEvent
* (API) Deprecated AsyncMachineProcessCompleteEvent
* (API) Deprecated AsyncReactorProcessCompleteEvent
* Error-Reports now show the date and time they were generated at
* Some performance optimizations to Cargo networks

#### Fixes
* Fixed #3064
* Fixed #2964
* Fixed #2979
* Fixed a permissions issue with `/sf charge`
* Fixed #3053
* Fixed #3075
* Fixed recipe types showing missing string message
* Fixed #3084
* Fixed #3085
* Fixed #3088
* Fixed #3087
* Fixed #3091
* Fixed #3086
* Fixed #3093
* Fixed #3095

## Release Candidate 23 (19 May 2021)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#23

#### Additions
* Added "Quartz Block -> 4 Quartz" recipe to Grind Stone
* Added "8 Coal Blocks -> 9 Carbon" recipe to Compressor
* Added "8 Coal Blocks -> 9 Carbon" recipe to Carbon Press
* Added "Magical Lump Tier 2 -> 4 Magical Lump Tier 1" recipe to Grind Stone
* Added "Magical Lump Tier 3 -> 4 Magical Lump Tier 2" recipe to Grind Stone
* Added "Ender Lump Tier 2 -> 4 Ender Lump Tier 1" recipe to Grind Stone
* Added "Ender Lump Tier 3 -> 4 Ender Lump Tier 2" recipe to Grind Stone
* Added Tier 2 Auto-Enchanter
* Added Tier 2 Auto-Disenchanter
* (API) Added AsyncAutoEnchanterProcessEvent
* (API) Added Category#setTier() to modify a category's position in the guide
* Added the ability to disable auto (dis)enchanting with a lore - `use-ignored-lores` & `ignored-lores` in Items.yml
* Added an option to turn off the "researching animation" in the `config.yml`
* Added the option to turn off the "researching animation" within your Slimefun Guide
* Added Portable Teleporter

#### Changes
* Renamed "Solar Panel" to "Photovoltaic Cell" to avoid confusions with solar generators
* Photovoltaic Cells can no longer be placed
* Batteries can no longer be placed
* Tin Cans can no longer be placed
* Magical Glass can no longer be placed
* (API) Removed deprecated "SlimefunBlockHandler"
* Removed Automated Crafting Chamber
* Memory and performance improvements for Cargo and Energy networks

#### Fixes
* Fixed #2987
* Fixed #2989
* Fixed #2977
* Fixed #2999
* Fixed #2593
* Fixed #2937
* Fixed #2927
* Fixed #3007
* Fixed #3012
* Fixed #3013
* Fixed #3027
* Fixed #2978
* Fixed #3041
* Fixed #3036
* Possibly fixed #2927
* Fixed #3060

## Release Candidate 22 (18 Apr 2021)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#22

#### Additions
* Added Vanilla Auto-Crafter
* Added Enhanced Auto-Crafter
* Added "Smart-Filling" mode to Cargo Input nodes
* Added "Netherite Ingot -> Netherite Block" recipe to Electric Press
* Added "Slimeballs -> Slime Block" recipe to Electric Press
* Added Armor Forge Auto-Crafter
* Auto-Crafters can now be turned on and off
* Added Produce Collector to automate Milk and Mushroom Stew
* Added a new message when constructing a Multiblock successfully
* Added Crafting Motor
* Block Placers can now place down cake
* Added support for the "FunnyGuilds" plugin
* Added "magma cream -> slime ball" recipe to the Freezer
* Added "2 magma blocks -> slime block" recipe to the Freezer
* Added configurable enchantment level limit for both auto enchanter and auto disenchanter
* (API) Added AutoEnchantEvent

#### Changes
* Changed item order in guide for the Villager Rune and Nether Goo (All runes are now grouped together)
* Ancient Pedestals can now be found under "Magical Gadgets"
* Removed all functionality from the old Automated Crafting Chamber
* Changed Cargo Motor texture
* Lowered "Magma block -> Sulfate" recipe to only require 1 magma block
* Small performance improvements

#### Fixes
* Fixed #1161
* Fixed #2862
* Fixed #2887
* Fixed items getting deleted when breaking enhanced furnaces
* Fixed #2895
* Fixed #2896
* Fixed #2899
* Fixed #2906
* Fixed #2903
* Fixed #2913
* Fixed #2914
* Fixed Auto-Crafters swallowing buckets when crafting cake
* Fixed Multimeter not working on Auto-Crafters
* Fixed #2650
* Fixed Slimefun items applying damage to items with an `unbreakable` tag
* Fixed #2930
* Fixed #2926
* Fixed Grappling Hook vanishing in creative mode
* Fixed #2944
* Fixed #2837
* Fixed #2942

## Release Candidate 21 (14 Mar 2021)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#21

#### Additions
* Nether Wart Blocks can now be turned into Nether Warts using a Grind Stone
* Added an option to allow Talismans to send their notifications via the Actionbar
* (API) Added support for nested Categories
* (API) Added ExplosiveToolBreakBlocksEvent
* Added an option to enable/disable water in the nether via a crucible
* /sf versions now shows the Java version and some useful tooltips

#### Changes
* Deprecated Automatic Crafting Chamber
* Performance Improvements and Optimizations for Cobblestone/Stone/Basalt generators and mining androids
* Androids operating on a Cobblestone/Stone/Basalt generator now work faster
* (API) Improvements to the BlockBreakHandler
* (API) Deprecated SlimefunBlockHandler
* (API) Improved ItemSetting API and error handling

#### Fixes
* Fixed #2794
* Fixed #2793
* Fixed #2809
* Fixed a small exception which gets thrown when Slimefun is disabled due to an invalid environment
* Fixed #2810
* Fixed #2804
* Fixed #2817
* Fixed exceptions with inventories not being printed using the logger of the addon that caused it
* Fixed #2818
* Fixed a duplication glitch with the Woodcutter Android
* Fixed #2839
* Fixed #2849
* Fixed #2851
* Fixed #2852
* Fixed some issues with the Book Binder
* Fixed #2805
* Fixed #2861
* Fixed #2856
* Fixed #2876
* Fixed #2877
* Fixed #2878
* Fixed Mining Androids being broken
* Fixed #2883

## Release Candidate 20 (30 Jan 2021)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#20

#### Additions
* Added a new language: Bulgarian
* Added a new language: Hebrew
* (API) Added AsyncProfileLoadEvent
* Added Talisman of the Wise
* Added Book Binder
* Added Tier 3 Electric Ore Grinder

#### Changes
* Massive performance improvements to holograms/armorstands
* Slimefun no longer requires CS-CoreLib to be installed

#### Fixes
* Fixed elevator floor order
* Fixed "block-explosions" (e.g. beds in Nether) not properly respecting explosion-resistant blocks
* Fixed #2560
* Fixed #2449
* Fixed #2511
* Fixed #2636
* Fixed a threading issue related to BlockStates and persistent data
* Fixed an error when the server was shutting down
* Fixed #2721
* Fixed #2662
* Fixed #2728
* Fixed some backpack opening issues
* Fixed Infused Hopper picking up items with a max pickup delay
* Fixed duplication issues related to holograms/armorstands
* Fixed #2754
* Fixed machines not respecting max size from inventories
* Fixed #2761
* Fixed #2460
* Fixed #2760
* Fixed #2771
* Fixed placeholders that did not get loaded yet not having a label
* Fixed #2679

## Release Candidate 19 (11 Jan 2021)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#19

#### Additions
* Added Bee Armor (1.15+ only)
* (API) Added AndroidFarmEvent

#### Changes
* Performance optimizations for Cargo networks
* Removed an old version of bStats
* General performance improvements
* CraftBukkit is officially no longer supported, Slimefun will now be disabled on old builds of CraftBukkit
* Removed the deprecated ItemManipulationAPI for BlockMenus
* Removed the "Written Book" variant of the Slimefun Guide
* The Elevator has an Inventory menu now

#### Fixes
* Fixed a couple of compatibility issues with ItemsAdder
* Fixed #2575
* Fixed ghost blocks to some extent (ghost blocks will now drop and be replaced)
* Fixed #2636 (hotfix)
* Fixed #2647
* Fixed #2664
* Fixed #2655
* Fixed /sf timings --verbose not working correctly
* Fixed #2675

## Release Candidate 18 (03 Dec 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#18

#### Additions
* The Smelters Pick now also works on Ancient Debris
* (API) Added PlayerPreResearchEvent
* Added a config option to disable network visualizations
* (API) Added CoolerFeedPlayerEvent
* Added a config option to delete excess cargo network items
* Added an item setting to configure the Wind Staff velocity
* Added an item setting to the Infused Hopper to toggle it with redstone
* Added an item setting to prevent Reinforced Spawners from being changed by Spawn Eggs
* Added 4 bricks -> 1 brick block recipe to the Electric Press

#### Changes
* Removed 1.13 support
* Cooling Units can no longer be placed down
* Heating Coils can no longer be placed down
* Electric Motors can no longer be placed down
* Cargo Motors can no longer be placed down
* Magnets can no longer be placed down
* Electromagnets can no longer be placed down
* Performance improvements to Cargo network visualizations
* General performance improvements
* Improved performance for radioactive items
* Memory/GC improvements for the profiler
* Performance improvements for the Fluid Pump
* Removed EmeraldEnchants integration
* Memory and performance improvements for ticking blocks

#### Fixes
* Fixed #2448
* Fixed #2470
* Fixed #2478
* Fixed #2493
* Fixed a missing slot in the contributors menu
* Fixed color codes in script downloading screen
* Fixed #2505
* Fixed contributors not showing correctly
* Fixed #2469
* Fixed #2509
* Fixed #2499
* Fixed #2527
* Fixed #2519
* Fixed #2517
* Fixed Magician Talisman sometimes drawing invalid enchantments
* Fixed id conflicts for external Enchantment sources (e.g. plugins) for the Magician Talisman settings
* Fixed network visualizers spawning particles for other player heads
* Fixed #2418
* Fixed #2446
* Fixed CoreProtect not recognizing Slimefun blocks getting broken
* Fixed #2447
* Fixed #2558
* Fixed a duplication bug with the Block Placer
* Fixed Slimefun Guide Settings showing "last activity" as a negative number
* Fixed Armor Stands getting damaged/pushed by Explosive Bow
* Fixed Sword of Beheading dropping Zombie/Skeleton Skulls from Zombie/Skeleton subvariants
* Fixed #2518
* Fixed #2421
* Fixed #2574
* Fixed color in android script downloading screen
* Fixed #2576
* Fixed #2496
* Fixed #2585
* Fixed #2583

## Release Candidate 17 (17 Oct 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#17

#### Additions
* Added /sf charge
* Added Energized Energy Capacitor
* Added various new fuel types to the Coal Generator
* Added a config option for Grappling Hooks to not be consumed on use
* Added Talisman of the Caveman
* You can now convert any gold ingot into gold dust with slightly less returns
* Magical Zombie Pills now also work on Zombified Piglins
* (API) Added SlimefunGuideOpenEvent
* (API) Added "NotConfigurable" attribute to disable configurability
* Added Elytra Cap
* Added Planks to Sticks recipe to the Table Saw
* Added "slimefun.gps.bypass" permission to open GPS devices anywhere
* (API) Added custom tags for developers
* The range of the Seeker Pickaxe is now configurable
* Added Energy Connector
* Blackstone can now be turned into lava using a Crucible
* Basalt can now be turned into lava using a Crucible
* Added "Tainted Sheep" (You can dye a Sheep using Strange Nether Goo)
* Added mcMMO support/integration

#### Changes
* Improved Auto-Updater (Multi-Threading and more)
* General performance improvements
* /sf cheat now shows seasonal categories all year through
* GPS devices now require chest-access in that area to be used

#### Fixes
* Fixed #2300
* Fixed #2296
* Fixed colors of Cheat Sheet Slimefun Guide
* Fixed Cheat Sheet Slimefun Guide being unable to open the settings menu via shift + right click
* Fixed #2320
* Fixed some issues with ChestTerminal
* Fixed #2325
* Fixed Climbing Pick having no animation in creative mode
* Fixed #2322
* Fixed some cargo incompatibilities with overflowing inventories
* Fixed #2353
* Fixed #2359
* Fixed #2356
* Fixed #2358
* Fixed #2360
* Fixed #2351
* Fixed #2357
* Fixed Auto Enchanters being unaffected by speed modifications from addons
* Fixed Auto Disenchanters being unaffected by speed modifications from addons
* Fixed radioactive items still being radioactive when disabled
* Fixed #2391
* Fixed #2403
* Fixed #2405
* Fixed #2412
* Fixed #2238
* Fixed #2439
* Fixed #2420
* Fixed #2422
* Fixed #2433
* Fixed #2455
* Fixed #2450
* Fixed Steel Thrusters being used to milk cows
* Fixed #2424
* Fixed #2468
* Fixed #2414
* Fixed #2454
* Fixed #2457
* Fixed #2411
* Fixed #2423
* Fixed #2452
* Fixed a dupe bug with mcMMO

## Release Candidate 16 (07 Sep 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#16

#### Additions
* Added an option for Industrial Miners to mine Ancient Debris
* Added a new language: Korean
* (API) Added support for adding custom Piglin Barter drops
* (API) Added BlockPlacerPlaceEvent
* (API) Added ToolUseHandler
* Added "Sand -> Sandstone" recipe to the Electric Press
* Added "Red Sand -> Red Sandstone" recipe to the Electric Press
* Industrial Miners can now also mine Gilded Blackstone
* Added a config option to disable Players from burning when exposed to radiation
* Added a config option to drop excess items when using /sf give
* Added Strange Nether Goo
* Added Villager Rune
* Added Synthetic Shulker Shells
* Added Climbing Pick
* Added item breaking sounds to some slimefun tools

#### Changes
* Performance improvement for Programmable Android rotations
* Removed Gravel -> Flint recipe from the Grind stone
* Performance improvements for miner talismans
* Performance improvements for idling Enhanced Furnaces when using Paper
* Performance improvements for Rainbow Blocks
* Crafting a Rag now yields two items
* Small performance improvements for Slimefun guides
* Small performance improvements for Cargo networks
* Small performance improvements for Miner Androids
* Small performance improvements for all machines, especially Electric Smelteries
* Small performance improvements for Holograms
* Small performance improvements for Tree Growth Accelerators
* Small performance improvements for Reactors
* Electric machines now show their tier in the Inventory name too
* Removed "Fuel efficiency" attribute for androids, since that was pretty much always at 1.0 anyway...
* Performance improvements for energy networks
* (API) Rewritten Block-Energy API
* Removed "durability" setting from cargo nodes
* Small performance improvements for radiation
* Small performance improvements for Auto Disenchanters
* Magnesium Salt in Magnesium-Salt generators now lasts longer

#### Fixes
* Fixed Programmable Androids rotating in the wrong direction
* Fixed #2176
* Fixed #2164
* Fixed #2147
* Fixed #2179
* Fixed Reinforced Spawners not working sometimes
* Fixed Explosive Pickaxe not handling normal Shulker boxes correctly
* Fixed #2103
* Fixed #2184
* Fixed #2183
* Fixed #2181
* Fixed #2180
* Fixed #2122
* Fixed #2168
* Fixed #2203
* Fixed #2205
* Fixed #2209
* Fixed #2217
* Fixed Miner Talisman sending messages when drops were not even doubled
* Fixed #2077
* Fixed #2207
* Fixed ChestTerminal timings showing up as cargo nodes
* Fixed timings reports never arriving sometimes
* Fixed #2138
* Fixed #1951 (again)
* Fixed Electric Press not working
* Fixed #2240
* Fixed #2243
* Fixed #2249
* Fixed #1022
* Fixed #2208
* Fixed Fluid Pump treating low-level fluids like stationary fluids
* Fixed Fluid Pump not working on Bubble Columns
* Fixed #2251
* Fixed #2257
* Fixed #2260
* Fixed #2263
* Fixed #2265
* Fixed #2269
* Fixed #2266
* Fixed #2275
* Fixed Multi Tools consuming hunger points when holding a Wind Staff in your off hand
* Fixed Teleports getting stuck sometimes

## Release Candidate 15 (01 Aug 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#15

#### Additions
* Added "Bone Block -> Bone meal" recipe to the Grind Stone
* Added a [Metrics module](https://github.com/Slimefun/MetricsModule) which allows us to release updates to metrics (bStats) independently from the main plugin
* Added "Compressed Carbon -> Carbon" recipe to the Ore Crusher
* Added "Carbon -> Coal" recipe to the Ore Crusher
* Added an option to disable the message "Ignoring duplicate block"
* Added Iron Golem Assembler
* Added Reinforced Cloth
* Added Bee protection to Hazmat Suit
* Added Enchantment Rune
* Added Tape Measure
* Added a permission node for /sf debug_fish

#### Changes
* Refactored and reworked the Generator API
* Small performance improvements to Energy networks
* Big performance improvements to Cargo networks when using ChestTerminal
* Slight changes to /sf timings
* Changed recipe of Hazmat Suits
* Uranium can no longer be placed down
* Huge performance improvements when using Paper
* Optimized Cargo networks for Paper
* Optimized Multiblocks for Paper
* Optimized Enhanced Furnaces for Paper
* Optimized Programmable Androids for Paper
* General performance improvements for Talismans
* General performance improvements for GPS Emergency Transmitters
* General performance improvements for Infused Magnets
* Ancient Altars now support for protection plugins
* Ancient Pedestals now support for protection plugins

#### Fixes
* Fixed Slimefun Armor sometimes not applying its effects
* Fixed #2075
* Fixed #2093
* Fixed #2086
* Fixed #1894
* Fixed #2097
* Fixed Wither Assembler requiring more items than it actually consumes
* Fixed Metrics not updating automatically
* Fixed #2143
* Fixed #2145
* Fixed #2151
* Fixed old Talismans not working
* Fixed Talismans sometimes not getting consumed properly
* Fixed old Infused Magnets not working
* Fixed old GPS Emergency Transmitters not working
* Fixed #2156
* Fixed #2165
* Fixed #2162
* Fixed #2166

## Release Candidate 14 (12 Jul 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#14

#### Additions
* Added support for Minecraft 1.16
* Added a starting sound for the Ancient Altar
* Added config option to disable backwards compatibility and improve performance
* Added ReactorExplodeEvent to the API
* Compatibility mode status is now included in /sf versions
* Added Nether Quartz Ore Crusher Recipe
* Added a new language: Tagalog
* Added Magical Zombie Pills
* Added 1.13 compatibility to the Auto Drier
* Added Corals to the fuel list for the Bio Generator
* Added Clay -> Clay blocks recipe to the Electric Press
* (1.16+) Slimefun guide can now show Smithing Table recipes
* (1.16+) Added Nether Gold Ore recipe to the Ore Crusher
* (1.16+) Added Gilded Blackstone recipe to the Ore Crusher
* (1.16+) Added Shroomlights to the fuel list for the Bio Generator
* (1.16+) Added Warped and Crimson Fungus to the fuel list for the Bio Generator
* Added an AoE damage effect to the Explosive Bow
* Added runtime deprecation warnings for ItemHandlers and Attributes used by Addons
* Added a proper lag profiler
* Added per-plugin lag info to /sf timings
* Added Indonesian translations

#### Changes
* Coolant Cells now last twice as long
* Ticking blocks will now catch more errors caused by addons
* Changed the texture for the Nuclear Reactor
* Changed the texture for the Nether Star Reactor
* Crafting Tin cans now produces 8 items instead of 4
* Multi Tool lore now says "Crouch" instead of "Hold Shift"
* Items which cannot be distributed by a Cargo Net will be dropped on the ground now instead of getting deleted
* Slimefun no longer supports CraftBukkit
* Item Energy is now also stored persistently via NBT
* Performance improvements to GPS/GEO machines, especially Oil Pump and GEO Miner
* Performance improvements for ticking blocks
* Performance improvements to the Cargo Net
* performance improvements to the Energy Net
* Performance improvements to Rainbow Blocks
* Performance improvements to Androids
* performance improvements to Generators and Electric Machines
* Cargo timings will now be attributed to the corresponding node and not the Cargo manager
* Thunderstorms now count as night time for Solar Generators
* Coolant Cells can no longer be placed on the ground
* Crafting Nether Ice Coolant Cells now results in 4 items
* Moved Soulbound Backpack to the "Magical Gadgets" Category

#### Fixes
* Fixed #2005
* Fixed #2009
* Fixed a chunk caching issue for GEO resources
* Fixed Infused Magnet working even if you haven't researched it
* Fixed Rainbow blocks duplication glitch when timing the block break right
* Fixed #1855
* Fixed some issues with AsyncWorldEdit
* Fixed some problems with unregistered or fake worlds
* Fixed a rare concurrency issue with world saving
* Fixed some contributors showing up twice
* Fixed #2062
* Fixed Grappling hooks disappearing when fired at Item frames or paintings
* Fixed Grappling hooks not getting removed when the Player leaves
* Fixed Grappling hooks making Bat sounds
* Fixed #1959
* Fixed Melon Juice requiring Melons instead of Melon Slices
* Fixed Cargo networks not showing up in /sf timings
* Fixed /sf timings reporting slightly inaccurate timings
* Fixed concurrency-related issues with the profiling
* Fixed #2066
* Fixed Rainbow Glass Panes not properly connecting to blocks
* Fixed Androids turning in the wrong direction
* Fixed contributors losing their texture after restarts
* Fixed "korean" showing up as "null"
* Fixed an issue with moving androids getting stuck
* Fixed Cargo nodes sometimes preventing chunks from unloading
* Fixed #2081
* Fixed a NullPointerException when Generators throw an Error Report

## Release Candidate 13 (16 Jun 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#13

#### Additions
* Added Dried Kelp Blocks recipe to the Electric Press
* Added Bone Blocks recipe to the Electric Press
* Added thai translations
* Added Industrial Miner
* Added Advanced Industrial Miner
* Added Cocoa Organic Food
* Added Cocoa Fertilizer
* Added a configurable limit to the Pickaxe of Vein Mining
* Added Gold Ingot to Dust recipe to the Electric Ingot Pulverizer
* Added Saddles to possible fishing loot for the Fishing Android
* Added Name tags to possible fishing loot for the Fishing Android
* Added Nautilus Shell to possible fishing loot for the Fishing Android
* Added Bamboo to possible fishing loot for the Fishing Android

#### Changes
* Removed Digital Miner
* Removed Advanced Digital Miner
* Dried Kelp Blocks can now be used in the Coal Generator
* Crafting Organic Food/Fertilizer yields more output now
* Organic Food (Melon) now uses Melon Slices instead of Melon blocks
* The Seismic Axe now skips the first two blocks to clear your field of view
* Auto Disenchanting is now a tiny bit faster
* Small performance improvements
* Dried Kelp Blocks can now be used as fuel for Tier 1 Androids
* Androids now have a separate category in the Slimefun Guide
* Android Interface recipes now require steel ingots
* Changed and unified a couple of tooltips
* Changed tooltip on jetpacks and jet boots to say "Crouch" instead of "Hold Shift"

#### Fixes
* Fixed Ore Washer recipes showing up twice
* Fixed #1942
* Fixed a few memory leaks
* Fixed #1943
* Fixed Nuclear Reactors accepting Lava as coolant
* Fixed #1971
* Fixed #1976
* Fixed #1988
* Fixed #1985
* Fixed a missing texture in the Android Script Editor
* Fixed #1992
* Possibly fixed #1951
* Fixed tab completion for /sf give showing players instead of amounts
* Fixed #1993
* Fixed #1907
* Fixed research fireworks still dealing damage sometimes

## Release Candidate 12 (27 May 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#12

#### Additions
* Added Ukrainian translations
* Added /sf backpack to restore lost backpacks
* Added automated Unit Tests
* Added WaypointCreateEvent
* Added an option to call an explosion event when using explosive tools

#### Changes
* Little performance improvements
* Bandages, Rags and Splints will no longer be consumed if your health is full and you are not on fire
* Player Profiles (researches and stuff) are now loaded completely asynchronously
* The Infused Magnet can no longer be placed down
* AncientAltar speed can now be changed internally (not available for server owners yet)
* Finished Italian translations

#### Fixes
* Fixed #1824
* Fixed #1833
* Fixed #1834
* Fixed #1843
* Fixed #1873
* Fixed Electric Smeltery not prioritising recipes
* Fixed #1851
* Fixed #1891
* Fixed #1893
* Fixed #1897
* Fixed #1908
* Fixed #1903
* Fixed Organic Food/Fertilizer not being recognized
* Fixed #1883
* Fixed #1829
* Fixed some mojang.com connection errors
* Fixed some very weird SkullMeta serialization problems in 1.15
* Fixed #1914
* Fixed file errors with PerWorldSettingsService
* Fixed ChestTerminals deleting items from Cargo networks (TheBusyBiscuit/ChestTerminal#25)
* Fixed #1926
* Fixed #1933
* Fixed random errors because of Mojang's new player heads backend (Why... Mojang... why?)
* Fixed Butcher Androids doing incorrect amounts of damage
* Fixed #1935

## Release Candidate 11 (25 Apr 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#11

#### Additions
* Added GEOResourceGenerationEvent
* Added AncientAltarCraftEvent
* Added SlimefunGuide-Options API
* Added ItemSettings API
* Added 1.13 backwards compatibility
* Added "Magma Cream to Magma Blocks" recipe to the Electric Press
* Added "Magma Blocks to Sulfate" recipe
* You can now search for items from within the book variant of the Guide
* GEO Scans now support endlessly many different resources
* Added Output Chest support to the Composter

#### Changes
* Replaced GuideHandlers with FlexCategories
* Removed support for old EmeraldEnchants versions
* Updated the book variant of the guide to use the newer API
* Removed internal /sf elevator command
* Split whitelist.yml up into individual /world-settings/worldname.yml files
* Performance improvements
* Slimefun Guide runs much faster now and can better deal with many Categories and items
* Lots of API improvements
* Faulty addons are now identified more easily and will no longer break Slimefun's main content this quickly
* You can no longer /sf give yourself a Multiblock
* Addons have no longer access to Slimefuns default categories
* Updated seasonal Categories to have better icons
* Even more performance improvements
* Changed Ignition Chamber Recipe
* GEO Miner is now 2 seconds faster
* All Generators will now stop consuming fuel if no energy is needed
* /sf teleporter will now open your own Teleporter Menu if you specify no Player
* Added counter-measures against Players who design Cargo networks in a way that intentionally lags out servers
* API requests to Mojang are now spread across a longer time period to prevent rate-limits

#### Fixes
* Fixed error message when clicking empty slots in the Slimefun Guide
* Fixed #1779
* Fixed localized messages not showing in the book guide
* Fixed empty categories showing up when items inside were hidden
* Fixed ghost pages showing up when too many categories were disabled
* Fixed debug fish not showing the correct chunk timings
* Fixed heads with missing permissions placing down
* Fixed unpermitted items still showing up in the guide if researches are disabled
* Fixed unpermitted items in the book guide triggering the search function
* Fixed #1803
* Fixed #1806
* Fixed #1807
* Fixed Coolers accepting non-Juice items
* Fixed #1813
* Fixed #1814
* Fixed GEO Scanner being unable to deal with more than 28 different resources
* Fixed #893
* Fixed #1798
* Fixed #1490
* Fixed GPS Emergency Transmitters not working

## Release Candidate 10 (28 Mar 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#10

#### Additions
* Added some new charts to bStats
* Added a new language: Turkish
* Multiblocks that use Fences or Trap doors now accept all wood types
* Added Makeshift Smeltery
* Added Tree Growth Accelerator
* Added "Glass to Glass Panes" recipe to the Electric Press
* Added "Snowballs to Snow blocks" recipe to the Electric Press
* Added "Snow blocks to Ice" recipe to the Freezer
* You can now use Cooked Salmon in an Auto Drier to craft Fish Jerky
* The Lumber Axe can now strip logs too
* The Slimefun Guide can now remember what page of a Category or Minecraft Item you were on

#### Changes
* Removed some deprecated parts of the API
* Internal clean up and further documentation
* Changed Automatic Ignition Chamber to be a Dropper
* Teleporters are now significantly faster
* Item permissions have been moved to a separate permissions.yml file
* Salt now only requires 2 blocks of Sand
* Fireworks from researching no longer damages entities
* Very slight performance improvements for Cargo networks
* 4K-carat gold ingots can now be used in a workbench by default (overridden by Items.yml)
* The project license is now included in every build
* Moved EmeraldsEnchants integration from EmeraldEnchants to Slimefun

#### Fixes
* Fixed some languages showing numbers larger than 100%
* Fixed #1570
* Fixed #1686
* Fixed #1648
* Fixed #1397
* Fixed #1706
* Fixed #1710
* Fixed #1711
* Fixed Slimefun Guide showing shaped recipes incorrectly
* Fixed #1719
* Fixed death waypoints not having the correct texture
* Fixed Androids having no texture when moving
* Fixed Androids not taking fuel from interfaces
* Fixed #1721
* Fixed #1619
* Fixed #1768

## Release Candidate 9 (07 Mar 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#9

#### Fixes
* Fixed Solar Generators not working

## Release Candidate 8 (06 Mar 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#8

#### Fixes
* Fixed bStats Metrics not sending properly

## Release Candidate 7 (06 Mar 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#7

#### Additions
* Added translations for Recipe Types
* Added Rainbow Concrete
* Added Rainbow Glazed Terracotta
* Added more internal documentation

#### Changes
* Researches now use their namespaced keys in the Researches.yml
* A lot of API changes

#### Fixes
* Fixed #1553
* Fixed #1513
* Fixed #1557
* Fixed #1558
* Fixed a translation not showing properly
* Fixed #1577
* Fixed #1597
* Fixed disabled Slimefun Addons not showing under /sf versions
* Fixed #1613

## Release Candidate 6 (16 Feb 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#6

#### Additions
* Added a new language: Japanese
* Added a new language: Swedish
* Added a new language: Czech
* Added a new language: Portuguese (Brazil)
* Added a new language: Arabic

#### Changes
* /sf research now uses namespaced keys instead of ids

#### Fixes
* Fixed #1515
* Fixed back-button in guide-settings not working via commands
* Fixed #1516
* Fixed magician talisman not being able to enchant books

## Release Candidate 5 (09 Feb 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#5

#### Additions
* Added preset messages.yml files
* Added user-configurable localization
* Added many more options to the messages.yml
* Added custom model data support for Languages
* Added Grind Stone Recipes for Prismarine
* Added String to the Bio Reactor
* Added a config setting to limit how many Nodes a Network can have
* Added support for Furnaces and Brewing Stands to Cargo Networks
* Added Organic Food/Fertilizer for Dried Kelp
* Added many more strings to the messages.yml
* Added ability to translate messages for Players
* Added the ability to translate Researches
* Added StatusEffect API
* Added translatability to categories
* Added translatability to geo-resources

#### Changes
* Removed Solar Array
* A lot of internal cleanup
* Performance improvements for GEO Miner and Oil Pump
* General performance improvements
* Changed Startup console message
* Changed GEO-Resources API

#### Fixes
* Fixed #1355
* Fixed Localization mistakes
* Fixed #1366
* Fixed GitHub cache
* Fixed #1364
* Fixed Bio Reactor not accepting melons
* Fixed Cargo Networks particles being broken
* Fixed #1379
* Fixed #1212
* Fixed #114
* Fixed #1385
* Fixed #1390
* Fixed #1394
* Fixed #1313
* Fixed #1396
* Fixed Backpacks being placeable
* Fixed wrong file encoding for translations
* Fixed Minecraft recipes not showing correctly
* Fixed #1428
* Fixed #1435
* Fixed #1438
* Fixed Multi Tool functioning as unlimited Shears
* Fixed #1383
* Fixed Android Script Component textures

## Release Candidate 4 (06 Jan 2020)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#4

#### Additions
* Added 1.15 support (1.14 and 1.15 are both supported)
* Added custom model support to Slimefun Guide and some Recipe Types
* Added Nether Gold Pan
* Added Iron Nuggets to Gold Pan drops
* Added CS-CoreLib version to the guide info
* Added AndroidMineEvent
* Added Electric Press
* Added Soulbound Trident
* Added "Andesite, Granite and Diorite to Gravel" recipes to the Grinder
* Added "nuggets to ingots and ingots to blocks" recipes to the Electric Press
* Added Salt to the GEO - Miner
* Added Magnesium Salt
* Added Magnesium-powered Generator
* Added "Gravel to Sand" recipe to the Grinder
* Added config option for circuit board drops
* Added player option to toggle research fireworks in the guide settings
* Added Kelp Cookies
* Added support for multiple recipes on vanilla items
* Added a "Craft last" button to the Automated Crafting Chamber
* Added more ore-doubling Recipes to the Ore Crusher
* Added Addons to the guide settings

#### Changes
* Revamped Guide Settings menu
* Changed some Category icons
* Changed Grappling Hook recipe
* Searching the guide now shows the Category of the item
* Contributors now also show their minecraft username (if possible)
* Changed teleporter sounds
* Electric Gold Pan now also supports Nether Gold Pan drops
* More performance improvements
* Improved Cargo performance
* Removed Nether Drill
* Tweaked Enhanced Furnace Recipes
* Changed tooltips for Radiation
* Oil Pump now shows its "Bucket -> Oil" recipe

#### Fixes
* Fixed Research Titles
* Fixed #1264
* Fixed #1261
* Fixed #1266
* Fixed #1272
* Fixed #1273
* Fixed christmas items
* Fixed Multi Tools
* Fixed credits not showing all contributors
* Fixed exception when viewing the second page of the credits
* Fixed #1269
* Fixed #1276
* Fixed GEO-Miner dupes
* Fixed Output Chest not working
* Fixed #1281
* Fixed #1280
* Fixed a lot of issues with Crucibles
* Fixed Grind Stone dupes
* Fixed #1316
* Fixed performance issues with Oil Pumps
* Fixed #1318
* Fixed #1298
* Fixed #1325
* Fixed #1295
* Fixed MultiBlocks not accepting different fence types
* Fixed #1337
* Fixed Applie Pie ID mismatch
* Fixed #1344
* Fixed #1349
* Fixed #1332
* Fixed #1356 and maybe other concurrency issues
* Fixed Ore Crusher's missing recipes
* Fixed #1354

## Release Candidate 3 (21 Nov 2019)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#3

#### Additions
* Smeltery now shows some recipes in the guide
* Added Sweet Berry Juice
* MultiBlocks that require fences will now accept all types of wooden fences
* Added craftable Totems of Undying
* Added back some wiki-pages to the guide
* Added support for all new minecraft recipes to correctly display in the guide
* Added support for custom model data for items
* Added Output Chest support to the Table Saw
* Added Output Chest support to the Automated Panning Machine
* Added Jerky recipes to the Auto-Drier
* Added AutoDisenchantEvent
* Added "Flint to Cobblestone" Recipe to the Compressor


#### Changes
* Changed Ignition Chamber Recipe
* /sf cheat no longer allows you to spawn in MultiBlocks
* Removed Heavy Armor
* Massive performance improvements with a new item-id system
* Huge performance improvements with skippable tickers
* Changed Elytra Scale Recipe
* Revamped Reactor Access Port
* Performance improvements for multi tools
* Performance improvements for armor
* Performance improvements for the Slimefun Guide

#### Fixes
* Fixed Stone Chunk -> Cobblestone Recipe not working
* Fixed #1145
* Fixed #1157
* Fixed #1180
* Fixed Backpacks not working
* Fixed /sf cheat not showing locked categories
* Fixed #1200
* Fixed #1196
* Fixed #1153
* Fixed some food items
* Fixed multi tools not working
* Fixed #1202
* Fixed #1211
* Fixed #1219
* Fixed #1226
* Fixed #1224
* Fixed repair-cost getting wiped after disenchanting
* Fixed GPS transmitters transmitting wrong locations
* Fixed Ancient Altar allowing you to craft locked items

## Release Candidate 2 (29 Sep 2019)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#2

#### Additions
* Added GEO - Miner
* Added more bStats Charts

#### Changes
* Reworked MultiBlocks
* Removed the Saw Mill

### Fixes
* Fixed Basic Machines not showing all recipes
* Fixed #1129
* Fixed #1130
* Fixed Auto-Updater for stable builds

## Release Candidate 1 (26 Sep 2019)
https://thebusybiscuit.github.io/builds/TheBusyBiscuit/Slimefun4/stable/#1

* First "stable" release since over a year. Stable builds will NOT receive support for bug reports since they are technically outdated. 
