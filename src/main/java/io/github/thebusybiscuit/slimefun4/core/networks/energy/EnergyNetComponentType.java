package io.github.thebusybiscuit.slimefun4.core.networks.energy;

import org.bukkit.block.Block;

import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.Capacitor;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.EnergyConnector;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors.Reactor;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;

/**
 * This enum contains the different types of components an {@link EnergyNet}
 * can have.
 * 
 * @author TheBusyBiscuit
 * @author Linox
 * 
 * @see EnergyNetComponent
 * @see EnergyNet
 *
 */
public enum EnergyNetComponentType {

    /**
     * A Generator generates Energy and feeds it into the network.
     * Also see: {@link AGenerator} or {@link Reactor}
     */
    GENERATOR,

    /**
     * A {@link Capacitor} stores energy from the network and provides it to any consumers.
     * It can be used as a buffer.
     */
    CAPACITOR,

    /**
     * A Consumer consumes energy from the network, most often linked to {@link AContainer}
     * or other types of machinery.
     */
    CONSUMER,

    /**
     * A Connector transmits energy through the network.
     * Also see: {@link EnergyConnector}
     */
    CONNECTOR,

    /**
     * A fallback value to use when a {@link Block} cannot be classified as any of the
     * other options.
     */
    NONE;

}