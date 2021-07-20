package io.github.thebusybiscuit.slimefun4.implementation.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockInteractEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.test.TestUtilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

class TestMultiblockListener {

    private static Slimefun plugin;
    private static MultiBlockListener listener;
    private static MultiBlock multiblock;
    private static ServerMock server;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Slimefun.class);
        listener = new MultiBlockListener(plugin);
        SlimefunItem item = TestUtilities.mockSlimefunItem(plugin, "MULTIBLOCK_LISTENER_TEST", new CustomItemStack(Material.DIAMOND, "&9Some multiblock item"));
        multiblock = new MultiBlock(item, new Material[] { null, Material.EMERALD_BLOCK, null, null, Material.DIAMOND_BLOCK, null, null, Material.LAPIS_BLOCK, null }, BlockFace.SELF);
        Slimefun.getRegistry().getMultiBlocks().add(multiblock);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Test MultiBlocks not messing up normal interactions")
    void testNoMultiblock() {
        Player player = server.addPlayer();
        World world = server.addSimpleWorld("Multiblock Test World");
        Block b = world.getBlockAt(3456, 90, -100);
        b.setType(Material.STONE);

        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, new ItemStack(Material.AIR), b, BlockFace.NORTH);
        listener.onRightClick(event);

        // No Multiblock, so nothing should happen
        Assertions.assertEquals(Result.ALLOW, event.useInteractedBlock());
    }

    @Test
    @DisplayName("Test MultiBlocks being recognized on right click")
    void testMultiblock() {
        Player player = server.addPlayer();
        World world = server.addSimpleWorld("Multiblock Test World");
        Block top = world.getBlockAt(1234, 92, -60);
        top.setType(multiblock.getStructure()[1]);

        Block self = world.getBlockAt(1234, 91, -60);
        self.setType(multiblock.getStructure()[4]);

        Block bottom = world.getBlockAt(1234, 90, -60);
        bottom.setType(multiblock.getStructure()[7]);

        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, new ItemStack(Material.AIR), self, BlockFace.NORTH);
        listener.onRightClick(event);

        Assertions.assertEquals(Result.DENY, event.useInteractedBlock());

        server.getPluginManager().assertEventFired(MultiBlockInteractEvent.class, e -> {
            Assertions.assertEquals(player, e.getPlayer());
            Assertions.assertEquals(self, e.getClickedBlock());
            Assertions.assertEquals(BlockFace.NORTH, e.getClickedFace());
            Assertions.assertEquals(multiblock, e.getMultiBlock());
            return true;
        });
    }
}
