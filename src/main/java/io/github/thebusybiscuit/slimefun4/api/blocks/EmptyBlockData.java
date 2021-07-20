package io.github.thebusybiscuit.slimefun4.api.blocks;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * This package-private class is supposed to be used as a singleton fallback in places where a
 * {@link NullPointerException} should be avoided, like {@link BlockStorage#getLocationInfo(org.bukkit.Location)}.
 * <p>
 * This object is a read-only variant of {@link SlimefunBlockData} and only serves the purpose of
 * performance and memory optimization.
 * 
 * @author TheBusyBiscuit
 *
 */
final class EmptyBlockData extends SlimefunBlockData {

    EmptyBlockData() {
        super();
    }

    @Override
    public String getValue(String path) {
        return null;
    }

    @Override
    public void setValue(String key, Object value) {
        throw new UnsupportedOperationException("Cannot store values (" + key + ':' + value + ") on a read-only data object!");
    }

    @Override
    public boolean hasValue(String key) {
        return false;
    }

}
