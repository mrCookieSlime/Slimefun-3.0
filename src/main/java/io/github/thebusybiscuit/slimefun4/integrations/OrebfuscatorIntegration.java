package io.github.thebusybiscuit.slimefun4.integrations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun4.api.events.AndroidBlockBreakEvent;
import io.github.thebusybiscuit.slimefun4.api.events.BlockPlacerPlaceEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ExplosiveToolBreakBlocksEvent;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerBlockBreakEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ReactorExplodeEvent;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import net.imprex.orebfuscator.api.OrebfuscatorService;

/**
 * 
 * This handles block breaks with orebfuscator
 * 
 * @author NgLoader
 *
 */
public class OrebfuscatorIntegration implements Listener {

	private final SlimefunPlugin plugin;
	private OrebfuscatorService service;

	public OrebfuscatorIntegration(@Nonnull SlimefunPlugin plugin) {
        this.plugin = plugin;
    }

	/**
	 * Init orebfuscation service and register listener
	 */
    public void register() {
        this.service = Bukkit.getServer().getServicesManager().getRegistration(OrebfuscatorService.class).getProvider();

        Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlacerPlace(BlockPlacerPlaceEvent event) {
        this.service.deobfuscate(Arrays.asList(event.getBlock(), event.getBlockPlacer()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExplosiveToolBreakBlocks(ExplosiveToolBreakBlocksEvent event) {
        Set<Block> blocks = new HashSet<>();
        blocks.addAll(event.getAdditionalBlocks());
        blocks.add(event.getPrimaryBlock());
        this.service.deobfuscate(blocks);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onReactorExplode(ReactorExplodeEvent event) {
        this.service.deobfuscate(Arrays.asList(event.getLocation().getBlock()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMultiBlockBreak(PlayerBlockBreakEvent event) {
        this.service.deobfuscate(event.getBlocks());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMultiBlockBreak(AndroidBlockBreakEvent event) {
        this.service.deobfuscate(event.getBlocks());
    }
}
