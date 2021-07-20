package io.github.thebusybiscuit.slimefun4.core.networks.cargo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.common.CommonPatterns;
import io.github.thebusybiscuit.slimefun4.api.network.Network;
import io.github.thebusybiscuit.slimefun4.api.network.NetworkComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link CargoNet} is a type of {@link Network} which deals with {@link ItemStack} transportation.
 * It is also an extension of {@link AbstractItemNetwork} which provides methods to deal
 * with the addon ChestTerminal.
 * 
 * @author meiamsome
 * @author Poslovitch
 * @author John000708
 * @author BigBadE
 * @author SoSeDiK
 * @author TheBusyBiscuit
 * @author Walshy
 * @author DNx5
 *
 */
public class CargoNet extends AbstractItemNetwork implements HologramOwner {

    private static final int RANGE = 5;
    private static final int TICK_DELAY = Slimefun.getCfg().getInt("networks.cargo-ticker-delay");

    private final Set<Location> inputNodes = new HashSet<>();
    private final Set<Location> outputNodes = new HashSet<>();

    protected final Map<Location, Integer> roundRobin = new HashMap<>();
    private int tickDelayThreshold = 0;

    @Nullable
    public static CargoNet getNetworkFromLocation(@Nonnull Location l) {
        return Slimefun.getNetworkManager().getNetworkFromLocation(l, CargoNet.class).orElse(null);
    }

    @Nonnull
    public static CargoNet getNetworkFromLocationOrCreate(@Nonnull Location l) {
        Optional<CargoNet> cargoNetwork = Slimefun.getNetworkManager().getNetworkFromLocation(l, CargoNet.class);

        if (cargoNetwork.isPresent()) {
            return cargoNetwork.get();
        } else {
            CargoNet network = new CargoNet(l);
            Slimefun.getNetworkManager().registerNetwork(network);
            return network;
        }
    }

    /**
     * This constructs a new {@link CargoNet} at the given {@link Location}.
     * 
     * @param l
     *            The {@link Location} marking the manager of this {@link Network}.
     */
    protected CargoNet(@Nonnull Location l) {
        super(l);
    }

    @Override
    public String getId() {
        return "CARGO_NETWORK";
    }

    @Override
    public int getRange() {
        return RANGE;
    }

    @Override
    public NetworkComponent classifyLocation(@Nonnull Location l) {
        String id = BlockStorage.checkID(l);

        if (id == null) {
            return null;
        }

        switch (id) {
            case "CARGO_MANAGER":
                return NetworkComponent.REGULATOR;
            case "CARGO_NODE":
                return NetworkComponent.CONNECTOR;
            case "CARGO_NODE_INPUT":
            case "CARGO_NODE_OUTPUT":
            case "CARGO_NODE_OUTPUT_ADVANCED":
            case "CT_IMPORT_BUS":
            case "CT_EXPORT_BUS":
            case "CHEST_TERMINAL":
                return NetworkComponent.TERMINUS;
            default:
                return null;
        }
    }

    @Override
    public void onClassificationChange(Location l, NetworkComponent from, NetworkComponent to) {
        connectorCache.remove(l);

        if (from == NetworkComponent.TERMINUS) {
            inputNodes.remove(l);
            outputNodes.remove(l);
            terminals.remove(l);
            imports.remove(l);
            exports.remove(l);
        }

        if (to == NetworkComponent.TERMINUS) {
            String id = BlockStorage.checkID(l);
            switch (id) {
                case "CARGO_NODE_INPUT":
                    inputNodes.add(l);
                    break;
                case "CARGO_NODE_OUTPUT":
                case "CARGO_NODE_OUTPUT_ADVANCED":
                    outputNodes.add(l);
                    break;
                case "CHEST_TERMINAL":
                    terminals.add(l);
                    break;
                case "CT_IMPORT_BUS":
                    imports.add(l);
                    break;
                case "CT_EXPORT_BUS":
                    exports.add(l);
                    break;
                default:
                    break;
            }
        }
    }

    public void tick(@Nonnull Block b) {
        if (!regulator.equals(b.getLocation())) {
            updateHologram(b, "&4Multiple Cargo Regulators connected");
            return;
        }

        super.tick();

        if (connectorNodes.isEmpty() && terminusNodes.isEmpty()) {
            updateHologram(b, "&cNo Cargo Nodes found");
        } else {
            updateHologram(b, "&7Status: &a&lONLINE");

            // Skip ticking if the threshold is not reached. The delay is not same as minecraft tick,
            // but it's based on 'custom-ticker-delay' config.
            if (tickDelayThreshold < TICK_DELAY) {
                tickDelayThreshold++;
                return;
            }

            // Reset the internal threshold, so we can start skipping again
            tickDelayThreshold = 0;

            // Chest Terminal Stuff
            Set<Location> chestTerminalInputs = new HashSet<>();
            Set<Location> chestTerminalOutputs = new HashSet<>();

            Map<Location, Integer> inputs = mapInputNodes(chestTerminalInputs);
            Map<Integer, List<Location>> outputs = mapOutputNodes(chestTerminalOutputs);

            if (BlockStorage.getLocationInfo(b.getLocation(), "visualizer") == null) {
                display();
            }

            Slimefun.getProfiler().scheduleEntries((terminals.isEmpty() ? 1 : 2) + inputs.size());

            CargoNetworkTask runnable = new CargoNetworkTask(this, inputs, outputs, chestTerminalInputs, chestTerminalOutputs);
            Slimefun.runSync(runnable);
        }
    }

    @Nonnull
    private Map<Location, Integer> mapInputNodes(@Nonnull Set<Location> chestTerminalNodes) {
        Map<Location, Integer> inputs = new HashMap<>();

        for (Location node : inputNodes) {
            int frequency = getFrequency(node);

            if (frequency == 16) {
                chestTerminalNodes.add(node);
            } else if (frequency >= 0 && frequency < 16) {
                inputs.put(node, frequency);
            }
        }

        return inputs;
    }

    @Nonnull
    private Map<Integer, List<Location>> mapOutputNodes(@Nonnull Set<Location> chestTerminalOutputs) {
        Map<Integer, List<Location>> output = new HashMap<>();

        List<Location> list = new LinkedList<>();
        int lastFrequency = -1;

        for (Location node : outputNodes) {
            int frequency = getFrequency(node);

            if (frequency == 16) {
                chestTerminalOutputs.add(node);
                continue;
            }

            if (frequency != lastFrequency && lastFrequency != -1) {
                output.merge(lastFrequency, list, (prev, next) -> {
                    prev.addAll(next);
                    return prev;
                });

                list = new LinkedList<>();
            }

            list.add(node);
            lastFrequency = frequency;
        }

        if (!list.isEmpty()) {
            output.merge(lastFrequency, list, (prev, next) -> {
                prev.addAll(next);
                return prev;
            });
        }

        return output;
    }

    /**
     * This method returns the frequency a given node is set to.
     * Should there be invalid data this method it will fall back to zero in
     * order to preserve the integrity of the {@link CargoNet}.
     * 
     * @param node
     *            The {@link Location} of our cargo node
     * 
     * @return The frequency of the given node
     */
    private static int getFrequency(@Nonnull Location node) {
        String frequency = BlockStorage.getLocationInfo(node, "frequency");

        if (frequency == null) {
            return 0;
        } else if (!CommonPatterns.NUMERIC.matcher(frequency).matches()) {
            Slimefun.logger().log(Level.SEVERE, () -> "Failed to parse a Cargo Node Frequency (" + node.getWorld().getName() + " - " + node.getBlockX() + ',' + node.getBlockY() + ',' + node.getBlockZ() + "): " + frequency);
            return 0;
        } else {
            return Integer.parseInt(frequency);
        }
    }
}
