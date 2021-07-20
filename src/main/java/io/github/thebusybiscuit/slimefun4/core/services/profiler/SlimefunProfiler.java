package io.github.thebusybiscuit.slimefun4.core.services.profiler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.TickerTask;
import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;

/**
 * The {@link SlimefunProfiler} works closely to the {@link TickerTask} and is
 * responsible for monitoring that task.
 * It collects timings data for any ticked {@link Block} and the corresponding {@link SlimefunItem}.
 * This allows developers to identify laggy {@link SlimefunItem SlimefunItems} or {@link SlimefunAddon SlimefunAddons}.
 * But it also enables Server Admins to locate lag-inducing areas on the {@link Server}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see TickerTask
 *
 */
public class SlimefunProfiler {

    /**
     * A minecraft server tick is 50ms and Slimefun ticks are stretched
     * across two ticks (sync and async blocks), so we use 100ms as a reference here
     */
    private static final int MAX_TICK_DURATION = 100;

    /**
     * Our internal instance of {@link SlimefunThreadFactory}, it provides the naming
     * convention for our {@link Thread} pool and also the count of this pool.
     */
    private final SlimefunThreadFactory threadFactory = new SlimefunThreadFactory(2);

    /**
     * This is our {@link Thread} pool to evaluate timings data.
     * We cannot use the {@link BukkitScheduler} here because we need to evaluate
     * this data in split seconds.
     * So we cannot simply wait until the next server tick for this.
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(threadFactory.getThreadCount(), threadFactory);

    /**
     * All possible values of {@link PerformanceRating}.
     * We cache these for fast access since Enum#values() creates
     * an array everytime it is called.
     */
    private final PerformanceRating[] performanceRatings = PerformanceRating.values();

    /**
     * This boolean marks whether we are currently profiling or not.
     */
    private volatile boolean isProfiling = false;

    /**
     * This {@link AtomicInteger} holds the amount of blocks that still need to be
     * profiled.
     */
    private final AtomicInteger queued = new AtomicInteger(0);

    private long totalElapsedTime;

    private final Map<ProfiledBlock, Long> timings = new ConcurrentHashMap<>();
    private final Queue<PerformanceInspector> requests = new ConcurrentLinkedQueue<>();

    private final AtomicLong totalMsTicked = new AtomicLong();
    private final AtomicInteger ticksPassed = new AtomicInteger();

    /**
     * This method terminates the {@link SlimefunProfiler}.
     * We need to call this method when the {@link Server} shuts down to prevent any
     * of our {@link Thread Threads} from being kept alive.
     */
    public void kill() {
        executor.shutdown();
    }

    /**
     * This method starts the profiling, data from previous runs will be cleared.
     */
    public void start() {
        isProfiling = true;
        queued.set(0);
        timings.clear();
    }

    /**
     * This method starts a new profiler entry.
     * 
     * @return A timestamp, best fed back into {@link #closeEntry(Location, SlimefunItem, long)}
     */
    public long newEntry() {
        if (!isProfiling) {
            return 0;
        }

        queued.incrementAndGet();
        return System.nanoTime();
    }

    /**
     * This method schedules a given amount of entries for the future.
     * Be careful to {@link #closeEntry(Location, SlimefunItem, long)} all of them again!
     * No {@link PerformanceSummary} will be sent until all entries were closed.
     * 
     * If the specified amount is negative, scheduled entries will be removed
     * 
     * @param amount
     *            The amount of entries that should be scheduled. Can be negative
     */
    public void scheduleEntries(int amount) {
        if (isProfiling) {
            queued.getAndAdd(amount);
        }
    }

    /**
     * This method closes a previously started entry.
     * Make sure to call {@link #newEntry()} to get the timestamp in advance.
     * 
     * @param l
     *            The {@link Location} of our {@link Block}
     * @param item
     *            The {@link SlimefunItem} at this {@link Location}
     * @param timestamp
     *            The timestamp marking the start of this entry, you can retrieve it using {@link #newEntry()}
     *
     * @return The total timings of this entry
     */
    public long closeEntry(@Nonnull Location l, @Nonnull SlimefunItem item, long timestamp) {
        Validate.notNull(l, "Location must not be null!");
        Validate.notNull(item, "You need to specify a SlimefunItem!");

        if (timestamp == 0) {
            return 0;
        }

        long elapsedTime = System.nanoTime() - timestamp;

        executor.execute(() -> {
            ProfiledBlock block = new ProfiledBlock(l, item);

            // Merge (if we have multiple samples for whatever reason)
            timings.merge(block, elapsedTime, Long::sum);
            queued.decrementAndGet();
        });

        return elapsedTime;
    }

    /**
     * This stops the profiling.
     */
    public void stop() {
        isProfiling = false;

        if (Slimefun.instance() == null || !Slimefun.instance().isEnabled()) {
            // Slimefun has been disabled
            return;
        }

        executor.execute(this::finishReport);
    }

    private void finishReport() {
        // We will only wait for a maximum of this many 1ms sleeps
        int iterations = 4000;

        // Wait for all timing results to come in
        while (!isProfiling && queued.get() > 0) {
            try {
                /*
                 * Since we got more than one Thread in our pool,
                 * blocking this one is (hopefully) completely fine
                 */
                Thread.sleep(1);
                iterations--;

                // If we waited for too long, then we should just abort
                if (iterations <= 0) {
                    Iterator<PerformanceInspector> iterator = requests.iterator();

                    while (iterator.hasNext()) {
                        iterator.next().sendMessage("Your timings report has timed out, we were still waiting for " + queued.get() + " samples to be collected :/");
                        iterator.remove();
                    }

                    return;
                }
            } catch (InterruptedException e) {
                Slimefun.logger().log(Level.SEVERE, "A Profiler Thread was interrupted", e);
                Thread.currentThread().interrupt();
            }
        }

        if (isProfiling && queued.get() > 0) {
            // Looks like the next profiling has already started, abort!
            return;
        }

        totalElapsedTime = timings.values().stream().mapToLong(Long::longValue).sum();

        /*
         * We log how many milliseconds have been ticked, and how many ticks have passed
         * This is so when bStats requests the average timings, they're super quick to figure out
         */
        totalMsTicked.addAndGet(TimeUnit.NANOSECONDS.toMillis(totalElapsedTime));
        ticksPassed.incrementAndGet();

        if (!requests.isEmpty()) {
            PerformanceSummary summary = new PerformanceSummary(this, totalElapsedTime, timings.size());
            Iterator<PerformanceInspector> iterator = requests.iterator();

            while (iterator.hasNext()) {
                summary.send(iterator.next());
                iterator.remove();
            }
        }
    }

    /**
     * This method requests a summary for the given {@link PerformanceInspector}.
     * The summary will be sent upon the next available moment in time.
     * 
     * @param inspector
     *            The {@link PerformanceInspector} who shall receive this summary.
     */
    public void requestSummary(@Nonnull PerformanceInspector inspector) {
        Validate.notNull(inspector, "Cannot request a summary for null");

        requests.add(inspector);
    }

    @Nonnull
    protected Map<String, Long> getByItem() {
        Map<String, Long> map = new HashMap<>();

        for (Map.Entry<ProfiledBlock, Long> entry : timings.entrySet()) {
            map.merge(entry.getKey().getId(), entry.getValue(), Long::sum);
        }

        return map;
    }

    @Nonnull
    protected Map<String, Long> getByPlugin() {
        Map<String, Long> map = new HashMap<>();

        for (Map.Entry<ProfiledBlock, Long> entry : timings.entrySet()) {
            map.merge(entry.getKey().getAddon().getName(), entry.getValue(), Long::sum);
        }

        return map;
    }

    @Nonnull
    protected Map<String, Long> getByChunk() {
        Map<String, Long> map = new HashMap<>();

        for (Map.Entry<ProfiledBlock, Long> entry : timings.entrySet()) {
            ProfiledBlock block = entry.getKey();
            String world = block.getWorld().getName();
            int x = block.getChunkX();
            int z = block.getChunkZ();

            map.merge(world + " (" + x + ',' + z + ')', entry.getValue(), Long::sum);
        }

        return map;
    }

    protected int getBlocksInChunk(@Nonnull String chunk) {
        Validate.notNull(chunk, "The chunk cannot be null!");
        int blocks = 0;

        for (ProfiledBlock block : timings.keySet()) {
            String world = block.getWorld().getName();
            int x = block.getChunkX();
            int z = block.getChunkZ();

            if (chunk.equals(world + " (" + x + ',' + z + ')')) {
                blocks++;
            }
        }

        return blocks;
    }

    protected int getBlocksOfId(@Nonnull String id) {
        Validate.notNull(id, "The id cannot be null!");
        int blocks = 0;

        for (ProfiledBlock block : timings.keySet()) {
            if (block.getId().equals(id)) {
                blocks++;
            }
        }

        return blocks;
    }

    protected int getBlocksFromPlugin(@Nonnull String pluginName) {
        Validate.notNull(pluginName, "The Plugin name cannot be null!");
        int blocks = 0;

        for (ProfiledBlock block : timings.keySet()) {
            if (block.getAddon().getName().equals(pluginName)) {
                blocks++;
            }
        }

        return blocks;
    }

    protected float getPercentageOfTick() {
        float millis = totalElapsedTime / 1000000.0F;
        float fraction = (millis * 100.0F) / MAX_TICK_DURATION;

        return Math.round((fraction * 100.0F) / 100.0F);
    }

    /**
     * This method returns the current {@link PerformanceRating}.
     * 
     * @return The current performance grade
     */
    @Nonnull
    public PerformanceRating getPerformance() {
        float percentage = getPercentageOfTick();

        for (PerformanceRating rating : performanceRatings) {
            if (rating.test(percentage)) {
                return rating;
            }
        }

        return PerformanceRating.UNKNOWN;
    }

    @Nonnull
    public String getTime() {
        return NumberUtils.getAsMillis(totalElapsedTime);
    }

    public int getTickRate() {
        return Slimefun.getTickerTask().getTickRate();
    }

    /**
     * This method checks whether the {@link SlimefunProfiler} has collected timings on
     * the given {@link Block}
     * 
     * @param b
     *            The {@link Block}
     * 
     * @return Whether timings of this {@link Block} have been collected
     */
    public boolean hasTimings(@Nonnull Block b) {
        Validate.notNull(b, "Cannot get timings for a null Block");

        return timings.containsKey(new ProfiledBlock(b));
    }

    public String getTime(@Nonnull Block b) {
        Validate.notNull(b, "Cannot get timings for a null Block");

        long time = timings.getOrDefault(new ProfiledBlock(b), 0L);
        return NumberUtils.getAsMillis(time);
    }

    public String getTime(@Nonnull Chunk chunk) {
        Validate.notNull(chunk, "Cannot get timings for a null Chunk");

        long time = getByChunk().getOrDefault(chunk.getWorld().getName() + " (" + chunk.getX() + ',' + chunk.getZ() + ')', 0L);
        return NumberUtils.getAsMillis(time);
    }

    public String getTime(@Nonnull SlimefunItem item) {
        Validate.notNull(item, "Cannot get timings for a null SlimefunItem");

        long time = getByItem().getOrDefault(item.getId(), 0L);
        return NumberUtils.getAsMillis(time);
    }

    /**
     * Get and reset the average millisecond timing for this {@link SlimefunProfiler}.
     *
     * @return The average millisecond timing for this {@link SlimefunProfiler}.
     */
    public long getAndResetAverageTimings() {
        long l = totalMsTicked.get() / ticksPassed.get();
        totalMsTicked.set(0);
        ticksPassed.set(0);

        return l;
    }
}
