package io.github.thebusybiscuit.slimefun4.api.blocks;

import java.io.File;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.gson.GsonBuilder;

import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;

/**
 * 
 * @deprecated This should not be used, it only serves the purpose of supporting legacy code
 *
 */
@Deprecated
class LegacyAdapter extends Config {

    private final SlimefunBlockData data;

    LegacyAdapter(@Nonnull SlimefunBlockData data) {
        super(null, null);
        this.data = data;
    }

    @Override
    public void setValue(String path, Object value) {
        data.setValue(path, value);
    }

    @Override
    public boolean contains(String path) {
        return data.hasValue(path);
    }

    @Override
    public Object getValue(String path) {
        return getString(path);
    }

    @Override
    public String getString(String path) {
        return data.getValue(path);
    }

    @Override
    public Set<String> getKeys() {
        throw new UnsupportedOperationException("Cannot get keys for BlockInfoConfig");
    }

    @Override
    public Set<String> getKeys(String path) {
        throw new UnsupportedOperationException("Cannot get keys for BlockInfoConfig");
    }

    @Override
    public File getFile() {
        throw new UnsupportedOperationException("BlockInfoConfigs do not have a File");
    }

    @Override
    public FileConfiguration getConfiguration() {
        throw new UnsupportedOperationException("BlockInfoConfigs do not have a FileConfiguration");
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("BlockInfoConfigs cannot be saved to a File");
    }

    @Override
    public void save(File file) {
        throw new UnsupportedOperationException("BlockInfoConfigs cannot be saved to a File");
    }

    @Override
    public void createFile() {
        throw new UnsupportedOperationException("BlockInfoConfigs cannot be created from a File");
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException("BlockInfoConfigs cannot be reloaded");
    }

    @Nonnull
    public String toJSON() {
        return new GsonBuilder().create().toJson(data);
    }

}
