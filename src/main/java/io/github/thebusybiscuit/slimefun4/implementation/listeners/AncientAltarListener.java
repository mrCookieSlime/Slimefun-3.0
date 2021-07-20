package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.bakedlibs.dough.items.ItemUtils;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AltarRecipe;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.RepairedSpawner;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.AncientAltarTask;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * This {@link Listener} is responsible for providing the core mechanics of the {@link AncientAltar}
 * and the {@link AncientPedestal}, it also handles the crafting of items using the Altar.
 * 
 * @author Redemption198
 * @author TheBusyBiscuit
 *
 */
public class AncientAltarListener implements Listener {

    private AncientAltar altarItem;
    private AncientPedestal pedestalItem;

    private final Set<Location> altarsInUse = new HashSet<>();

    private final List<Block> altars = new ArrayList<>();
    private final Set<UUID> removedItems = new HashSet<>();

    @ParametersAreNonnullByDefault
    public AncientAltarListener(Slimefun plugin, AncientAltar altar, AncientPedestal pedestal) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.altarItem = altar;
        this.pedestalItem = pedestal;
    }

    /**
     * This returns all {@link AncientAltar Altars} that are currently in use.
     * 
     * @return A {@link Set} of every {@link AncientAltar} currently in use
     */
    public @Nonnull Set<Location> getAltarsInUse() {
        return altarsInUse;
    }

    public @Nonnull List<Block> getAltars() {
        return altars;
    }

    @EventHandler
    public void onInteract(PlayerRightClickEvent e) {
        if (altarItem == null || altarItem.isDisabled() || e.useBlock() == Result.DENY) {
            return;
        }

        Optional<Block> blockOptional = e.getClickedBlock();
        if (!blockOptional.isPresent()) {
            return;
        }

        Block b = blockOptional.get();
        if (b.getType() != Material.ENCHANTING_TABLE && b.getType() != Material.DISPENSER) {
            return;
        }

        Optional<SlimefunItem> slimefunBlock = e.getSlimefunBlock();
        if (!slimefunBlock.isPresent()) {
            return;
        }

        String id = slimefunBlock.get().getId();
        Player p = e.getPlayer();

        if (id.equals(pedestalItem.getId())) {
            e.cancel();
            usePedestal(b, p);
        } else if (id.equals(altarItem.getId())) {
            if (!altarItem.canUse(p, true) || altarsInUse.contains(b.getLocation())) {
                e.cancel();
                return;
            }

            // Make altarinuse simply because that was the last block clicked.
            altarsInUse.add(b.getLocation());
            e.cancel();

            useAltar(b, p);
        }
    }

    private void usePedestal(@Nonnull Block pedestal, @Nonnull Player p) {
        if (altarsInUse.contains(pedestal.getLocation())) {
            return;
        }

        if (!Slimefun.getProtectionManager().hasPermission(p, pedestal, Interaction.INTERACT_BLOCK)) {
            Slimefun.getLocalization().sendMessage(p, "inventory.no-access", true);
            return;
        }

        // getting the currently placed item
        Optional<Item> stack = pedestalItem.getPlacedItem(pedestal);

        if (!stack.isPresent()) {
            // Check if the Item in hand is valid
            if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
                // Check for pedestal obstructions
                if (pedestal.getRelative(0, 1, 0).getType() != Material.AIR) {
                    Slimefun.getLocalization().sendMessage(p, "machines.ANCIENT_PEDESTAL.obstructed", true);
                    return;
                }

                // place the item onto the pedestal
                pedestalItem.placeItem(p, pedestal);
            }
        } else if (!removedItems.contains(stack.get().getUniqueId())) {
            Item entity = stack.get();
            UUID uuid = entity.getUniqueId();
            removedItems.add(uuid);

            Slimefun.runSync(() -> removedItems.remove(uuid), 30L);

            entity.remove();
            p.getInventory().addItem(pedestalItem.getOriginalItemStack(entity));
            p.playSound(pedestal.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1F, 1F);
        }
    }

    private void useAltar(@Nonnull Block altar, @Nonnull Player p) {
        if (!Slimefun.getProtectionManager().hasPermission(p, altar, Interaction.INTERACT_BLOCK)) {
            Slimefun.getLocalization().sendMessage(p, "inventory.no-access", true);
            return;
        }

        ItemStack catalyst = new CustomItemStack(p.getInventory().getItemInMainHand(), 1);
        List<Block> pedestals = getPedestals(altar);

        if (!altars.contains(altar)) {
            altars.add(altar);

            if (pedestals.size() == 8) {
                pedestals.forEach(block -> altarsInUse.add(block.getLocation()));

                if (catalyst.getType() != Material.AIR) {
                    startRitual(p, altar, pedestals, catalyst);
                } else {
                    altars.remove(altar);
                    Slimefun.getLocalization().sendMessage(p, "machines.ANCIENT_ALTAR.unknown-catalyst", true);

                    for (Block block : pedestals) {
                        altarsInUse.remove(block.getLocation());
                    }

                    // Unknown catalyst, no longer in use
                    altarsInUse.remove(altar.getLocation());
                }
            } else {
                altars.remove(altar);
                Slimefun.getLocalization().sendMessage(p, "machines.ANCIENT_ALTAR.not-enough-pedestals", true, msg -> msg.replace("%pedestals%", String.valueOf(pedestals.size())));

                // Not a valid altar so remove from inuse
                altarsInUse.remove(altar.getLocation());
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void startRitual(Player p, Block b, List<Block> pedestals, ItemStack catalyst) {
        List<ItemStack> input = new ArrayList<>();

        for (Block pedestal : pedestals) {
            Optional<Item> stack = pedestalItem.getPlacedItem(pedestal);

            if (stack.isPresent()) {
                input.add(pedestalItem.getOriginalItemStack(stack.get()));
            }
        }

        Optional<ItemStack> result = getRecipeOutput(catalyst, input);

        if (result.isPresent()) {
            if (SlimefunUtils.canPlayerUseItem(p, result.get(), true)) {
                List<ItemStack> consumed = new ArrayList<>();
                consumed.add(catalyst);

                if (p.getGameMode() != GameMode.CREATIVE) {
                    ItemUtils.consumeItem(p.getInventory().getItemInMainHand(), false);
                }

                b.getWorld().playSound(b.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1, 1);

                AncientAltarTask task = new AncientAltarTask(this, b, altarItem.getStepDelay(), result.get(), pedestals, consumed, p);
                Slimefun.runSync(task, 10L);
            } else {
                altars.remove(b);

                for (Block block : pedestals) {
                    altarsInUse.remove(block.getLocation());
                }

                // Item not unlocked, no longer in use.
                altarsInUse.remove(b.getLocation());
            }
        } else {
            altars.remove(b);
            Slimefun.getLocalization().sendMessage(p, "machines.ANCIENT_ALTAR.unknown-recipe", true);

            for (Block block : pedestals) {
                altarsInUse.remove(block.getLocation());
            }

            // Bad recipe, no longer in use.
            altarsInUse.remove(b.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (altarItem == null || altarItem.isDisabled()) {
            return;
        }

        Block pedestal = e.getBlockPlaced().getRelative(BlockFace.DOWN);

        if (pedestal.getType() == Material.DISPENSER) {
            String id = BlockStorage.checkID(pedestal);

            if (id != null && id.equals(pedestalItem.getId())) {
                Slimefun.getLocalization().sendMessage(e.getPlayer(), "messages.cannot-place", true);
                e.setCancelled(true);
            }
        }
    }

    private @Nonnull List<Block> getPedestals(@Nonnull Block altar) {
        List<Block> list = new ArrayList<>();

        if (BlockStorage.check(altar.getRelative(2, 0, -2), pedestalItem.getId())) {
            list.add(altar.getRelative(2, 0, -2));
        }
        if (BlockStorage.check(altar.getRelative(3, 0, 0), pedestalItem.getId())) {
            list.add(altar.getRelative(3, 0, 0));
        }
        if (BlockStorage.check(altar.getRelative(2, 0, 2), pedestalItem.getId())) {
            list.add(altar.getRelative(2, 0, 2));
        }
        if (BlockStorage.check(altar.getRelative(0, 0, 3), pedestalItem.getId())) {
            list.add(altar.getRelative(0, 0, 3));
        }
        if (BlockStorage.check(altar.getRelative(-2, 0, 2), pedestalItem.getId())) {
            list.add(altar.getRelative(-2, 0, 2));
        }
        if (BlockStorage.check(altar.getRelative(-3, 0, 0), pedestalItem.getId())) {
            list.add(altar.getRelative(-3, 0, 0));
        }
        if (BlockStorage.check(altar.getRelative(-2, 0, -2), pedestalItem.getId())) {
            list.add(altar.getRelative(-2, 0, -2));
        }
        if (BlockStorage.check(altar.getRelative(0, 0, -3), pedestalItem.getId())) {
            list.add(altar.getRelative(0, 0, -3));
        }

        return list;
    }

    public @Nonnull Optional<ItemStack> getRecipeOutput(@Nonnull ItemStack catalyst, @Nonnull List<ItemStack> inputs) {
        if (inputs.size() != 8) {
            return Optional.empty();
        }

        ItemStackWrapper wrapper = ItemStackWrapper.wrap(catalyst);
        List<ItemStackWrapper> items = ItemStackWrapper.wrapList(inputs);

        if (SlimefunUtils.isItemSimilar(wrapper, SlimefunItems.BROKEN_SPAWNER, false, false)) {
            if (!checkRecipe(SlimefunItems.BROKEN_SPAWNER, items).isPresent()) {
                return Optional.empty();
            }

            RepairedSpawner spawner = (RepairedSpawner) SlimefunItems.REPAIRED_SPAWNER.getItem();
            return Optional.of(spawner.getItemForEntityType(spawner.getEntityType(wrapper).orElse(EntityType.PIG)));
        }

        return checkRecipe(wrapper, items);
    }

    private @Nonnull Optional<ItemStack> checkRecipe(@Nonnull ItemStack catalyst, @Nonnull List<ItemStackWrapper> items) {
        for (AltarRecipe recipe : altarItem.getRecipes()) {
            if (SlimefunUtils.isItemSimilar(catalyst, recipe.getCatalyst(), true)) {
                Optional<ItemStack> optional = checkPedestals(items, recipe);

                if (optional.isPresent()) {
                    return optional;
                }
            }
        }

        return Optional.empty();
    }

    private @Nonnull Optional<ItemStack> checkPedestals(@Nonnull List<ItemStackWrapper> items, @Nonnull AltarRecipe recipe) {
        for (int i = 0; i < 8; i++) {
            if (SlimefunUtils.isItemSimilar(items.get(i), recipe.getInput().get(0), true)) {
                for (int j = 1; j < 8; j++) {
                    if (!SlimefunUtils.isItemSimilar(items.get((i + j) % items.size()), recipe.getInput().get(j), true)) {
                        break;
                    } else if (j == 7) {
                        return Optional.of(recipe.getOutput());
                    }
                }
            }
        }

        return Optional.empty();
    }

}
