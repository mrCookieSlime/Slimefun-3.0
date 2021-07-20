package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import io.github.bakedlibs.dough.collections.LoopIterator;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.guide.SurvivalSlimefunGuide;

/**
 * A {@link AsyncRecipeChoiceTask} is an asynchronously repeating task that cycles
 * through the different variants of {@link Material} that a {@link MaterialChoice} or {@link Tag} can represent.
 * 
 * It is used in the {@link SurvivalSlimefunGuide} for any {@link ItemStack} from Minecraft
 * that accepts more than one {@link Material} in its {@link Recipe}.
 * 
 * @author TheBusyBiscuit
 *
 */
public class AsyncRecipeChoiceTask implements Runnable {

    private static final int UPDATE_INTERVAL = 14;

    private final Map<Integer, LoopIterator<Material>> iterators = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private Inventory inventory;
    private int id;

    /**
     * This will start this task for the given {@link Inventory}.
     * 
     * @param inv
     *            The {@link Inventory} to start this task for
     */
    public void start(@Nonnull Inventory inv) {
        Validate.notNull(inv, "Inventory must not be null");

        inventory = inv;
        id = Bukkit.getScheduler().runTaskTimerAsynchronously(Slimefun.instance(), this, 0, UPDATE_INTERVAL).getTaskId();
    }

    public void add(int slot, @Nonnull MaterialChoice choice) {
        Validate.notNull(choice, "Cannot add a null RecipeChoice");

        lock.writeLock().lock();

        try {
            iterators.put(slot, new LoopIterator<>(choice.getChoices()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void add(int slot, @Nonnull Tag<Material> tag) {
        Validate.notNull(tag, "Cannot add a null Tag");

        lock.writeLock().lock();

        try {
            iterators.put(slot, new LoopIterator<>(tag.getValues()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * This method checks if there are any slots that need to be updated.
     * 
     * @return Whether this task has nothing to do
     */
    public boolean isEmpty() {
        lock.readLock().lock();

        try {
            return iterators.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * This method clears the {@link AsyncRecipeChoiceTask} and removes all active
     * iterators.
     */
    public void clear() {
        lock.writeLock().lock();

        try {
            iterators.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void run() {
        // Terminate the task when noone is viewing the Inventory
        if (inventory.getViewers().isEmpty()) {
            Bukkit.getScheduler().cancelTask(id);
            return;
        }

        lock.readLock().lock();

        try {
            for (Map.Entry<Integer, LoopIterator<Material>> entry : iterators.entrySet()) {
                inventory.setItem(entry.getKey(), new ItemStack(entry.getValue().next()));
            }
        } finally {
            lock.readLock().unlock();
        }
    }

}
