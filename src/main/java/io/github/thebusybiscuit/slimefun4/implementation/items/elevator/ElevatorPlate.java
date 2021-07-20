package io.github.thebusybiscuit.slimefun4.implementation.items.elevator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.common.ChatColors;
import io.github.bakedlibs.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.github.thebusybiscuit.slimefun4.utils.WorldUtils;
import io.papermc.lib.PaperLib;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link ElevatorPlate} is a quick way of teleportation.
 * You can place multiple {@link ElevatorPlate ElevatorPlates} along the y axis
 * to teleport between them.
 *
 * @author TheBusyBiscuit
 * @author Walshy
 */
public class ElevatorPlate extends SimpleSlimefunItem<BlockUseHandler> {

    /**
     * This is our key for storing the floor name.
     */
    private static final String DATA_KEY = "floor";

    /**
     * This is the size of our {@link Inventory}.
     */
    private static final int GUI_SIZE = 27;

    /**
     * This is our {@link Set} of currently teleporting {@link Player Players}.
     * It is used to prevent them from triggering the {@link ElevatorPlate} they land on.
     */
    private final Set<UUID> users = new HashSet<>();

    @ParametersAreNonnullByDefault
    public ElevatorPlate(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, ItemStack recipeOutput) {
        super(category, item, recipeType, recipe, recipeOutput);

        addItemHandler(onPlace());
    }

    @Nonnull
    private BlockPlaceHandler onPlace() {
        return new BlockPlaceHandler(false) {

            @Override
            public void onPlayerPlace(BlockPlaceEvent e) {
                Block b = e.getBlock();
                BlockStorage.addBlockInfo(b, DATA_KEY, ChatColor.WHITE + "Floor #0");
                BlockStorage.addBlockInfo(b, "owner", e.getPlayer().getUniqueId().toString());
            }
        };
    }

    @Nonnull
    @Override
    public BlockUseHandler getItemHandler() {
        return e -> {
            Block b = e.getClickedBlock().get();

            if (BlockStorage.getLocationInfo(b.getLocation(), "owner").equals(e.getPlayer().getUniqueId().toString())) {
                openEditor(e.getPlayer(), b);
            }
        };
    }

    @Nonnull
    public List<ElevatorFloor> getFloors(@Nonnull Block b) {
        LinkedList<ElevatorFloor> floors = new LinkedList<>();
        int index = 0;

        for (int y = WorldUtils.getMinHeight(b.getWorld()); y < b.getWorld().getMaxHeight(); y++) {
            if (y == b.getY()) {
                String name = ChatColors.color(BlockStorage.getLocationInfo(b.getLocation(), DATA_KEY));
                floors.addFirst(new ElevatorFloor(name, index, b));
                index++;
                continue;
            }

            Block block = b.getWorld().getBlockAt(b.getX(), y, b.getZ());

            if (block.getType() == getItem().getType() && BlockStorage.check(block, getId())) {
                String name = ChatColors.color(BlockStorage.getLocationInfo(block.getLocation(), DATA_KEY));
                floors.addFirst(new ElevatorFloor(name, index, block));
                index++;
            }
        }

        return floors;
    }

    @ParametersAreNonnullByDefault
    public void openInterface(Player p, Block b) {
        if (users.remove(p.getUniqueId())) {
            return;
        }

        List<ElevatorFloor> floors = getFloors(b);

        if (floors.size() < 2) {
            Slimefun.getLocalization().sendMessage(p, "machines.ELEVATOR.no-destinations", true);
        } else {
            openFloorSelector(b, floors, p, 1);
        }
    }

    @ParametersAreNonnullByDefault
    private void openFloorSelector(Block b, List<ElevatorFloor> floors, Player p, int page) {
        ChestMenu menu = new ChestMenu(Slimefun.getLocalization().getMessage(p, "machines.ELEVATOR.pick-a-floor"));
        menu.setEmptySlotsClickable(false);

        int index = GUI_SIZE * (page - 1);

        for (int i = 0; i < Math.min(GUI_SIZE, floors.size() - index); i++) {
            ElevatorFloor floor = floors.get(index + i);

            // @formatter:off
            if (floor.getAltitude() == b.getY()) {
                menu.addItem(i, new CustomItemStack(
                    Material.COMPASS,
                    ChatColor.GRAY.toString() + floor.getNumber() + ". " + ChatColor.BLACK + floor.getName(),
                    Slimefun.getLocalization().getMessage(p, "machines.ELEVATOR.current-floor") + ' ' + ChatColor.WHITE + floor.getName()
                ), ChestMenuUtils.getEmptyClickHandler());
            } else {
                menu.addItem(i, new CustomItemStack(
                    Material.PAPER,
                    ChatColor.GRAY.toString() + floor.getNumber() + ". " + ChatColor.BLACK + floor.getName(),
                    Slimefun.getLocalization().getMessage(p, "machines.ELEVATOR.click-to-teleport") + ' ' + ChatColor.WHITE + floor.getName()
                ), (player, slot, itemStack, clickAction) -> {
                    teleport(player, floor);
                    return false;
                });
            }
            // @formatter:on
        }

        int pages = 1 + (floors.size() / GUI_SIZE);

        // 0 index so size is the first slot of the last row.
        for (int i = GUI_SIZE; i < GUI_SIZE + 9; i++) {
            if (i == GUI_SIZE + 2 && pages > 1 && page != 1) {
                menu.addItem(i, ChestMenuUtils.getPreviousButton(p, page, pages), (player, i1, itemStack, clickAction) -> {
                    openFloorSelector(b, floors, p, page - 1);
                    return false;
                });
            } else if (i == GUI_SIZE + 6 && pages > 1 && page != pages) {
                menu.addItem(i, ChestMenuUtils.getNextButton(p, page, pages), (player, i1, itemStack, clickAction) -> {
                    openFloorSelector(b, floors, p, page + 1);
                    return false;
                });
            } else {
                menu.addItem(i, ChestMenuUtils.getBackground(), (player, i1, itemStack, clickAction) -> false);
            }
        }

        menu.open(p);
    }

    @ParametersAreNonnullByDefault
    private void teleport(Player player, ElevatorFloor floor) {
        Slimefun.runSync(() -> {
            users.add(player.getUniqueId());

            float yaw = player.getEyeLocation().getYaw() + 180;

            if (yaw > 180) {
                yaw = -180 + (yaw - 180);
            }

            Location loc = floor.getLocation();
            Location destination = new Location(player.getWorld(), loc.getX() + 0.5, loc.getY() + 0.4, loc.getZ() + 0.5, yaw, player.getEyeLocation().getPitch());

            PaperLib.teleportAsync(player, destination).thenAccept(teleported -> {
                if (teleported.booleanValue()) {
                    player.sendTitle(ChatColor.WHITE + ChatColors.color(floor.getName()), null, 20, 60, 20);
                }
            });
        });
    }

    @ParametersAreNonnullByDefault
    public void openEditor(Player p, Block b) {
        ChestMenu menu = new ChestMenu("Elevator Settings");

        menu.addItem(4, new CustomItemStack(Material.NAME_TAG, "&7Floor Name &e(Click to edit)", "", ChatColor.WHITE + ChatColors.color(BlockStorage.getLocationInfo(b.getLocation(), DATA_KEY))));
        menu.addMenuClickHandler(4, (pl, slot, item, action) -> {
            pl.closeInventory();
            pl.sendMessage("");
            Slimefun.getLocalization().sendMessage(p, "machines.ELEVATOR.enter-name");
            pl.sendMessage("");

            ChatUtils.awaitInput(pl, message -> {
                BlockStorage.addBlockInfo(b, DATA_KEY, message.replace(ChatColor.COLOR_CHAR, '&'));

                pl.sendMessage("");
                Slimefun.getLocalization().sendMessage(p, "machines.ELEVATOR.named", msg -> msg.replace("%floor%", message));
                pl.sendMessage("");

                openEditor(pl, b);
            });

            return false;
        });

        menu.open(p);
    }

}
