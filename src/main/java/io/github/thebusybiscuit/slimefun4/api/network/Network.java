package io.github.thebusybiscuit.slimefun4.api.network;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import io.github.bakedlibs.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.core.networks.NetworkManager;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.NetworkListener;

/**
 * An abstract Network class to manage networks in a stateful way
 * 
 * @author meiamsome
 * @author TheBusyBiscuit
 * 
 * @see NetworkListener
 * @see NetworkManager
 *
 */
public abstract class Network {

    /**
     * Our {@link NetworkManager} instance.
     */
    private final NetworkManager manager;

    /**
     * The {@link Location} of the regulator of this {@link Network}.
     */
    protected Location regulator;

    /**
     * The {@link UUID} of the {@link World} this {@link Network} exists within.
     */
    private final UUID worldId;

    /**
     * This {@link Set} holds all {@link Network} positions that are part of this {@link Network}.
     * The {@link World} should be equal for all positions, therefore we can save memory by simply
     * storing {@link BlockPosition#getAsLong(int, int, int)}.
     */
    private final Set<Long> positions = new HashSet<>();

    private final Queue<Location> nodeQueue = new ArrayDeque<>();
    protected final Set<Location> regulatorNodes = new HashSet<>();
    protected final Set<Location> connectorNodes = new HashSet<>();
    protected final Set<Location> terminusNodes = new HashSet<>();

    /**
     * This constructs a new {@link Network} at the given {@link Location}.
     * 
     * @param manager
     *            The {@link NetworkManager} instance
     * @param regulator
     *            The {@link Location} marking the regulator of this {@link Network}.
     */
    protected Network(@Nonnull NetworkManager manager, @Nonnull Location regulator) {
        Validate.notNull(manager, "A NetworkManager must be provided");
        Validate.notNull(regulator, "No regulator was specified");

        this.manager = manager;
        this.regulator = regulator;
        this.worldId = regulator.getWorld().getUID();

        positions.add(BlockPosition.getAsLong(regulator));
        nodeQueue.add(regulator.clone());
    }

    /**
     * This method returns the range of the {@link Network}.
     * The range determines how far the {@link Network} will search for
     * nearby nodes from any given node.
     * 
     * It basically translates to the maximum distance between nodes.
     * 
     * @return the range of this {@link Network}
     */
    public abstract int getRange();

    /**
     * This method assigns the given {@link Location} a type of {@link NetworkComponent}
     * for classification.
     * 
     * @param l
     *            The {@link Location} to classify
     * 
     * @return The assigned type of {@link NetworkComponent} for this {@link Location}
     */
    public abstract @Nullable NetworkComponent classifyLocation(@Nonnull Location l);

    /**
     * This method is called whenever a {@link Location} in this {@link Network} changes
     * its classification.
     * 
     * @param l
     *            The {@link Location} that is changing its classification
     * @param from
     *            The {@link NetworkComponent} this {@link Location} was previously classified as
     * @param to
     *            The {@link NetworkComponent} this {@link Location} is changing to
     */
    public abstract void onClassificationChange(Location l, NetworkComponent from, NetworkComponent to);

    /**
     * This returns the size of this {@link Network}. It is equivalent to the amount
     * of {@link Location Locations} connected to this {@link Network}.
     * 
     * @return The size of this {@link Network}
     */
    public int getSize() {
        return regulatorNodes.size() + connectorNodes.size() + terminusNodes.size();
    }

    /**
     * This method adds the given {@link Location} to this {@link Network}.
     * 
     * @param l
     *            The {@link Location} to add
     */
    protected void addLocationToNetwork(@Nonnull Location l) {
        Validate.notNull(l, "You cannot add a Location to a Network which is null!");
        Validate.isTrue(l.getWorld().getUID().equals(worldId), "Networks cannot exist in multiple worlds!");

        if (positions.add(BlockPosition.getAsLong(l))) {
            markDirty(l);
        }
    }

    /**
     * This method marks the given {@link Location} as dirty and adds it to a {@link Queue}
     * to handle this update.
     * 
     * @param l
     *            The {@link Location} to update
     */
    public void markDirty(@Nonnull Location l) {
        if (regulator.equals(l)) {
            manager.unregisterNetwork(this);
        } else {
            nodeQueue.add(l.clone());
        }
    }

    /**
     * This method checks whether the given {@link Location} is part of this {@link Network}.
     * 
     * @param l
     *            The {@link Location} to check for
     * 
     * @return Whether the given {@link Location} is part of this {@link Network}
     */
    public boolean connectsTo(@Nonnull Location l) {
        Validate.notNull(l, "The Location cannot be null.");

        if (this.regulator.equals(l)) {
            return true;
        } else if (!l.getWorld().getUID().equals(this.worldId)) {
            return false;
        } else {
            return positions.contains(BlockPosition.getAsLong(l));
        }
    }

    private @Nullable NetworkComponent getCurrentClassification(@Nonnull Location l) {
        if (regulatorNodes.contains(l)) {
            return NetworkComponent.REGULATOR;
        } else if (connectorNodes.contains(l)) {
            return NetworkComponent.CONNECTOR;
        } else if (terminusNodes.contains(l)) {
            return NetworkComponent.TERMINUS;
        }

        return null;
    }

    private void discoverStep() {
        int maxSteps = manager.getMaxSize();
        int steps = 0;

        while (nodeQueue.peek() != null) {
            Location l = nodeQueue.poll();
            NetworkComponent currentAssignment = getCurrentClassification(l);
            NetworkComponent classification = classifyLocation(l);

            if (classification != currentAssignment) {
                if (currentAssignment == NetworkComponent.REGULATOR || currentAssignment == NetworkComponent.CONNECTOR) {
                    // Requires a complete rebuild of the network, so we just throw the current one away.
                    manager.unregisterNetwork(this);
                    return;
                } else if (currentAssignment == NetworkComponent.TERMINUS) {
                    terminusNodes.remove(l);
                }

                if (classification == NetworkComponent.REGULATOR) {
                    regulatorNodes.add(l);
                    discoverNeighbors(l);
                } else if (classification == NetworkComponent.CONNECTOR) {
                    connectorNodes.add(l);
                    discoverNeighbors(l);
                } else if (classification == NetworkComponent.TERMINUS) {
                    terminusNodes.add(l);
                }

                onClassificationChange(l, currentAssignment, classification);
            }

            steps += 1;

            if (steps >= maxSteps) {
                break;
            }
        }
    }

    private void discoverNeighbors(@Nonnull Location l, double xDiff, double yDiff, double zDiff) {
        for (int i = getRange() + 1; i > 0; i--) {
            Location newLocation = l.clone().add(i * xDiff, i * yDiff, i * zDiff);
            addLocationToNetwork(newLocation);
        }
    }

    private void discoverNeighbors(@Nonnull Location l) {
        discoverNeighbors(l, 1.0, 0.0, 0.0);
        discoverNeighbors(l, -1.0, 0.0, 0.0);
        discoverNeighbors(l, 0.0, 1.0, 0.0);
        discoverNeighbors(l, 0.0, -1.0, 0.0);
        discoverNeighbors(l, 0.0, 0.0, 1.0);
        discoverNeighbors(l, 0.0, 0.0, -1.0);
    }

    /**
     * This method runs the network visualizer which displays a {@link Particle} on
     * every {@link Location} that this {@link Network} is connected to.
     */
    public void display() {
        if (manager.isVisualizerEnabled()) {
            // TODO: Make Color configurable / network-dependent
            Slimefun.runSync(new NetworkVisualizer(this, Color.BLUE));
        }
    }

    /**
     * This returns the {@link Location} of the regulator block for this {@link Network}
     * 
     * @return The {@link Location} of our regulator
     */
    public @Nonnull Location getRegulator() {
        return regulator;
    }

    /**
     * This method updates this {@link Network} and serves as the starting point
     * for any running operations.
     */
    public void tick() {
        discoverStep();
    }
}
