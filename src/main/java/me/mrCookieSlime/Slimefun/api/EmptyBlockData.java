package me.mrCookieSlime.Slimefun.api;

import io.github.thebusybiscuit.slimefun4.api.blocks.SlimefunBlockData;

/**
 * This package-private class is supposed to be used as a singleton fallback in places where a
 * {@link NullPointerException} should be avoided, like {@link BlockStorage#getLocationInfo(org.bukkit.Location)}.
 * 
 * This object is a read-only variant of {@link SlimefunBlockData} and only serves the purpose of
 * performance and memory optimization.
 * 
 * @author TheBusyBiscuit
 *
 */
@Deprecated
class EmptyBlockData extends BlockInfoConfig {

    EmptyBlockData() {
        super(null);
    }

    @Override
    public void setValue(String path, Object value) {
        throw new UnsupportedOperationException("Cannot store values (" + path + ':' + value + " on a read-only data object!");
    }

    @Override
    public String getString(String path) {
        return null;
    }

}
