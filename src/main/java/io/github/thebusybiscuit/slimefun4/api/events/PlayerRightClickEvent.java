package io.github.thebusybiscuit.slimefun4.api.events;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.bakedlibs.dough.data.TriStateOptional;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

import me.mrCookieSlime.Slimefun.api.BlockStorage;

/**
 * The {@link PlayerRightClickEvent} is our custom version of the {@link PlayerInteractEvent}.
 * But it is only triggered on right click.
 * The main and (almost) sole purpose of this {@link Event} is to cache the {@link SlimefunItem}
 * of the {@link ItemStack} and/or {@link Block} involved.
 * This allows us (and addons) to efficiently check the used {@link SlimefunItem} without the need
 * to do a heavy lookup or item comparison.
 * 
 * @author TheBusyBiscuit
 *
 */
public class PlayerRightClickEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    /**
     * The original {@link PlayerInteractEvent}.
     */
    private final PlayerInteractEvent event;

    private final Optional<ItemStack> itemStack;
    private final Optional<Block> clickedBlock;

    private final EquipmentSlot hand;
    private final BlockFace face;

    private TriStateOptional<SlimefunItem> slimefunItem = TriStateOptional.createNew();
    private TriStateOptional<SlimefunItem> slimefunBlock = TriStateOptional.createNew();

    private Result itemResult;
    private Result blockResult;

    /**
     * This constructs a new {@link PlayerRightClickEvent} based on the original {@link PlayerInteractEvent}.
     * The {@link Result} of the original {@link PlayerInteractEvent} will be copied.
     * 
     * @param originalEvent
     *            The original {@link PlayerInteractEvent}
     */
    public PlayerRightClickEvent(@Nonnull PlayerInteractEvent originalEvent) {
        super(originalEvent.getPlayer());

        event = originalEvent;
        clickedBlock = Optional.ofNullable(originalEvent.getClickedBlock());
        face = originalEvent.getBlockFace();
        hand = originalEvent.getHand();

        itemResult = originalEvent.useItemInHand();
        blockResult = originalEvent.useInteractedBlock();

        if (originalEvent.getItem() == null || originalEvent.getItem().getType() == Material.AIR || originalEvent.getItem().getAmount() == 0) {
            itemStack = Optional.empty();
        } else {
            itemStack = Optional.of(originalEvent.getItem());
        }
    }

    /**
     * This returns the original {@link PlayerInteractEvent} that triggered this
     * {@link PlayerRightClickEvent}.
     * 
     * @return The original {@link PlayerInteractEvent}
     */
    @Nonnull
    public PlayerInteractEvent getInteractEvent() {
        return event;
    }

    /**
     * This method returns the {@link ItemStack} that was held in the hand of the {@link Player}.
     * It will never return null, should there be no {@link ItemStack} then it will return
     * {@code new ItemStack(Material.AIR)}.
     * 
     * @return The {@link ItemStack} that the {@link Player} right clicked with
     */
    @Nonnull
    public ItemStack getItem() {
        return itemStack.orElse(new ItemStack(Material.AIR));
    }

    /**
     * This returns the hand that was used in this interaction.
     * Can either be {@code EquipmentSlot.HAND} or {@code EquipmentSlot.OFF_HAND}.
     * 
     * @return The hand used in this {@link Event}
     */
    @Nonnull
    public EquipmentSlot getHand() {
        return hand;
    }

    @Nonnull
    public Optional<Block> getClickedBlock() {
        return clickedBlock;
    }

    @Nonnull
    public BlockFace getClickedFace() {
        return face;
    }

    @Nonnull
    public Optional<SlimefunItem> getSlimefunItem() {
        if (!slimefunItem.isComputed()) {
            if (itemStack.isPresent()) {
                slimefunItem.compute(SlimefunItem.getByItem(itemStack.get()));
            } else {
                slimefunItem = TriStateOptional.empty();
            }
        }

        return slimefunItem.getAsOptional();
    }

    @Nonnull
    public Optional<SlimefunItem> getSlimefunBlock() {
        if (!slimefunBlock.isComputed()) {
            if (clickedBlock.isPresent()) {
                slimefunBlock.compute(BlockStorage.check(clickedBlock.get()));
            } else {
                slimefunBlock = TriStateOptional.empty();
            }
        }

        return slimefunBlock.getAsOptional();
    }

    /**
     * This method cancels the {@link PlayerRightClickEvent}.
     * This will deny the item and block usage.
     */
    public void cancel() {
        itemResult = Result.DENY;
        blockResult = Result.DENY;
    }

    @Nonnull
    public Result useItem() {
        return itemResult;
    }

    @Nonnull
    public Result useBlock() {
        return blockResult;
    }

    public void setUseItem(@Nonnull Result result) {
        Validate.notNull(result, "Result cannot be null");
        itemResult = result;
    }

    public void setUseBlock(@Nonnull Result result) {
        Validate.notNull(result, "Result cannot be null");
        blockResult = result;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

}
