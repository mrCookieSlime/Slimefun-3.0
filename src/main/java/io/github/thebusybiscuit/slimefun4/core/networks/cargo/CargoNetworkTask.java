package io.github.thebusybiscuit.slimefun4.core.networks.cargo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSpawnReason;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.networks.NetworkManager;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;

/**
 * The {@link CargoNetworkTask} is the actual {@link Runnable} responsible for moving {@link ItemStack ItemStacks}
 * around the {@link CargoNet}.
 * 
 * Inbefore this was just a method in the {@link CargoNet} class.
 * However for aesthetic reasons but mainly to prevent the Cargo Task from showing up as
 * "lambda:xyz-123" in timing reports... this was moved.
 * 
 * @see CargoNet
 * @see CargoUtils
 * @see AbstractItemNetwork
 *
 */
class CargoNetworkTask implements Runnable {

    private final NetworkManager manager;
    private final CargoNet network;
    private final Map<Location, Inventory> inventories = new HashMap<>();

    private final Map<Location, Integer> inputs;
    private final Map<Integer, List<Location>> outputs;

    private final Set<Location> chestTerminalInputs;
    private final Set<Location> chestTerminalOutputs;

    @ParametersAreNonnullByDefault
    CargoNetworkTask(CargoNet network, Map<Location, Integer> inputs, Map<Integer, List<Location>> outputs, Set<Location> chestTerminalInputs, Set<Location> chestTerminalOutputs) {
        this.network = network;
        this.manager = Slimefun.getNetworkManager();

        this.inputs = inputs;
        this.outputs = outputs;
        this.chestTerminalInputs = chestTerminalInputs;
        this.chestTerminalOutputs = chestTerminalOutputs;
    }

    @Override
    public void run() {
        long timestamp = System.nanoTime();

        try {
            // Chest Terminal Code
            if (Slimefun.getIntegrations().isChestTerminalInstalled()) {
                network.handleItemRequests(inventories, chestTerminalInputs, chestTerminalOutputs);
            }

            /**
             * All operations happen here: Everything gets iterated from the Input Nodes.
             * (Apart from ChestTerminal Buses)
             */
            SlimefunItem inputNode = SlimefunItems.CARGO_INPUT_NODE.getItem();
            for (Map.Entry<Location, Integer> entry : inputs.entrySet()) {
                long nodeTimestamp = System.nanoTime();
                Location input = entry.getKey();
                Optional<Block> attachedBlock = network.getAttachedBlock(input);

                attachedBlock.ifPresent(block -> routeItems(input, block, entry.getValue(), outputs));

                // This will prevent this timings from showing up for the Cargo Manager
                timestamp += Slimefun.getProfiler().closeEntry(entry.getKey(), inputNode, nodeTimestamp);
            }

            // Chest Terminal Code
            if (Slimefun.getIntegrations().isChestTerminalInstalled()) {
                // This will deduct any CT timings and attribute them towards the actual terminal
                timestamp += network.updateTerminals(chestTerminalInputs);
            }
        } catch (Exception | LinkageError x) {
            Slimefun.logger().log(Level.SEVERE, x, () -> "An Exception was caught while ticking a Cargo network @ " + new BlockPosition(network.getRegulator()));
        }

        // Submit a timings report
        Slimefun.getProfiler().closeEntry(network.getRegulator(), SlimefunItems.CARGO_MANAGER.getItem(), timestamp);
    }

    @ParametersAreNonnullByDefault
    private void routeItems(Location inputNode, Block inputTarget, int frequency, Map<Integer, List<Location>> outputNodes) {
        ItemStackAndInteger slot = CargoUtils.withdraw(network, inventories, inputNode.getBlock(), inputTarget);

        if (slot == null) {
            return;
        }

        ItemStack stack = slot.getItem();
        int previousSlot = slot.getInt();
        List<Location> destinations = outputNodes.get(frequency);

        if (destinations != null) {
            stack = distributeItem(stack, inputNode, destinations);
        }

        if (stack != null) {
            insertItem(inputTarget, previousSlot, stack);
        }
    }

    @ParametersAreNonnullByDefault
    private void insertItem(Block inputTarget, int previousSlot, ItemStack item) {
        Inventory inv = inventories.get(inputTarget.getLocation());

        if (inv != null) {
            // Check if the original slot hasn't been occupied in the meantime
            if (inv.getItem(previousSlot) == null) {
                inv.setItem(previousSlot, item);
            } else {
                // Try to add the item into another available slot then
                ItemStack rest = inv.addItem(item).get(0);

                if (rest != null && !manager.isItemDeletionEnabled()) {
                    // If the item still couldn't be inserted, simply drop it on the ground
                    SlimefunUtils.spawnItem(inputTarget.getLocation().add(0, 1, 0), rest, ItemSpawnReason.CARGO_OVERFLOW);
                }
            }
        } else {
            DirtyChestMenu menu = CargoUtils.getChestMenu(inputTarget);

            if (menu != null) {
                if (menu.getItemInSlot(previousSlot) == null) {
                    menu.replaceExistingItem(previousSlot, item);
                } else if (!manager.isItemDeletionEnabled()) {
                    SlimefunUtils.spawnItem(inputTarget.getLocation().add(0, 1, 0), item, ItemSpawnReason.CARGO_OVERFLOW);
                }
            }
        }
    }

    @Nullable
    @ParametersAreNonnullByDefault
    private ItemStack distributeItem(ItemStack stack, Location inputNode, List<Location> outputNodes) {
        ItemStack item = stack;

        Config cfg = BlockStorage.getLocationInfo(inputNode);
        boolean roundrobin = Objects.equals(cfg.getString("round-robin"), "true");
        boolean smartFill = Objects.equals(cfg.getString("smart-fill"), "true");

        Collection<Location> destinations;
        if (roundrobin) {
            // Use an ArrayDeque to perform round-robin sorting
            // Since the impl for roundRobinSort just does Deque.addLast(Deque#removeFirst)
            // An ArrayDequeue is preferable as opposed to a LinkedList:
            // - The number of elements does not change.
            // - ArrayDequeue has better iterative performance
            Deque<Location> tempDestinations = new ArrayDeque<>(outputNodes);
            roundRobinSort(inputNode, tempDestinations);
            destinations = tempDestinations;
        } else {
            // Using an ArrayList here since we won't need to sort the destinations
            // The ArrayList has the best performance for iteration bar a primitive array
            destinations = new ArrayList<>(outputNodes);
        }

        for (Location output : destinations) {
            Optional<Block> target = network.getAttachedBlock(output);

            if (target.isPresent()) {
                ItemStackWrapper wrapper = ItemStackWrapper.wrap(item);
                item = CargoUtils.insert(network, inventories, output.getBlock(), target.get(), smartFill, item, wrapper);

                if (item == null) {
                    break;
                }
            }
        }

        return item;
    }

    /**
     * This method sorts a given {@link Deque} of output node locations using a semi-accurate
     * round-robin method.
     * 
     * @param inputNode
     *            The {@link Location} of the input node
     * @param outputNodes
     *            A {@link Deque} of {@link Location Locations} of the output nodes
     */
    private void roundRobinSort(Location inputNode, Deque<Location> outputNodes) {
        int index = network.roundRobin.getOrDefault(inputNode, 0);

        if (index < outputNodes.size()) {
            // Not ideal but actually not bad performance-wise over more elegant alternatives
            for (int i = 0; i < index; i++) {
                Location temp = outputNodes.removeFirst();
                outputNodes.add(temp);
            }

            index++;
        } else {
            index = 1;
        }

        network.roundRobin.put(inputNode, index);
    }

}
