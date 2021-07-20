package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.AncientAltarCraftEvent;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.AncientAltarListener;

/**
 * The {@link AncientAltarTask} is responsible for the animation that happens when a ritual
 * involving the {@link AncientAltar} is started.
 * 
 * @author dniym
 * @author meiamsome
 * @author TheBusyBiscuit
 * 
 * @see AncientAltar
 * @see AncientAltarListener
 *
 */
public class AncientAltarTask implements Runnable {

    private final AncientAltarListener listener;
    private final AncientPedestal pedestalItem = (AncientPedestal) SlimefunItems.ANCIENT_PEDESTAL.getItem();

    private final Block altar;
    private final int stepDelay;
    private final Location dropLocation;
    private final ItemStack output;
    private final List<Block> pedestals;
    private final List<ItemStack> items;

    private final Collection<Location> particleLocations = new LinkedList<>();
    private final Map<Item, Location> positionLock = new HashMap<>();

    private boolean running;
    private int stage;
    private final Player player;

    @ParametersAreNonnullByDefault
    public AncientAltarTask(AncientAltarListener listener, Block altar, int stepDelay, ItemStack output, List<Block> pedestals, List<ItemStack> items, Player player) {
        this.listener = listener;
        this.dropLocation = altar.getLocation().add(0.5, 1.3, 0.5);
        this.stepDelay = stepDelay;
        this.altar = altar;
        this.output = output;
        this.pedestals = pedestals;
        this.items = items;
        this.player = player;

        this.running = true;
        this.stage = 0;

        for (Block pedestal : pedestals) {
            Optional<Item> item = pedestalItem.getPlacedItem(pedestal);

            if (item.isPresent()) {
                Item entity = item.get();
                positionLock.put(entity, entity.getLocation().clone());
            }
        }
    }

    @Override
    public void run() {
        idle();

        if (!checkLockedItems()) {
            abort();
            return;
        }

        if (this.stage == 36) {
            finish();
            return;
        }

        if (this.stage > 0 && this.stage % 4 == 0) {
            checkPedestal(pedestals.get(this.stage / 4 - 1));
        }

        this.stage += 1;
        Slimefun.runSync(this, stepDelay);
    }

    private boolean checkLockedItems() {
        for (Map.Entry<Item, Location> entry : positionLock.entrySet()) {
            if (entry.getKey().getLocation().distanceSquared(entry.getValue()) > 0.1) {
                return false;
            }
        }

        return true;
    }

    private void idle() {
        dropLocation.getWorld().spawnParticle(Particle.SPELL_WITCH, dropLocation, 16, 1.2F, 0F, 1.2F);
        dropLocation.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, dropLocation, 8, 0.2F, 0F, 0.2F);

        for (Location loc : particleLocations) {
            dropLocation.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 16, 0.3F, 0.2F, 0.3F);
            dropLocation.getWorld().spawnParticle(Particle.CRIT_MAGIC, loc, 8, 0.3F, 0.2F, 0.3F);
        }
    }

    private void checkPedestal(@Nonnull Block pedestal) {
        Optional<Item> item = pedestalItem.getPlacedItem(pedestal);

        if (!item.isPresent() || positionLock.remove(item.get()) == null) {
            abort();
        } else {
            Item entity = item.get();
            particleLocations.add(pedestal.getLocation().add(0.5, 1.5, 0.5));
            items.add(pedestalItem.getOriginalItemStack(entity));
            pedestal.getWorld().playSound(pedestal.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 2F);

            dropLocation.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, pedestal.getLocation().add(0.5, 1.5, 0.5), 16, 0.3F, 0.2F, 0.3F);
            dropLocation.getWorld().spawnParticle(Particle.CRIT_MAGIC, pedestal.getLocation().add(0.5, 1.5, 0.5), 8, 0.3F, 0.2F, 0.3F);

            positionLock.remove(entity);
            entity.remove();
            entity.removeMetadata("no_pickup", Slimefun.instance());
        }
    }

    private void abort() {
        running = false;

        for (Block b : pedestals) {
            listener.getAltarsInUse().remove(b.getLocation());
        }

        // This should re-enable altar blocks on craft failure.
        listener.getAltarsInUse().remove(altar.getLocation());
        dropLocation.getWorld().playSound(dropLocation, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1F, 1F);
        positionLock.clear();
        listener.getAltars().remove(altar);
    }

    private void finish() {
        if (running) {

            AncientAltarCraftEvent event = new AncientAltarCraftEvent(output, altar, player);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                dropLocation.getWorld().playSound(dropLocation, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1F, 1F);
                dropLocation.getWorld().playEffect(dropLocation, Effect.STEP_SOUND, Material.EMERALD_BLOCK);
                dropLocation.getWorld().dropItemNaturally(dropLocation.add(0, -0.5, 0), event.getItem());
            }

            for (Block b : pedestals) {
                listener.getAltarsInUse().remove(b.getLocation());
            }

            // This should re-enable altar blocks on craft completion.
            listener.getAltarsInUse().remove(altar.getLocation());
            listener.getAltars().remove(altar);
        } else {
            dropLocation.getWorld().playSound(dropLocation, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1F, 1F);
        }
    }
}
