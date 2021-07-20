package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import io.github.thebusybiscuit.cscorelib2.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.api.ErrorReport;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * The {@link TickerTask} is responsible for ticking every {@link BlockTicker}, synchronous
 * or not.
 * 
 * @author TheBusyBiscuit
 * @author md5sha256
 * @author Linox
 * @author Qalle
 * 
 * @see BlockTicker
 *
 */
public final class TickerTask {

    /**
     * This {@link Runnable} represents the end of our {@link Queue}.
     * When this element is reached, we consider the task finished.
     */
    private static final Runnable END_ELEMENT = () -> {};

    /**
     * This is the maximum time we will wait for tasks to be queued.
     * See {@link BlockingQueue#poll(long, TimeUnit)}
     * The applicable {@link TimeUnit} is {@link TimeUnit#NANOSECONDS}.
     */
    private static final long MAX_POLL_TIME = 250_000L;

    /**
     * This is the maximum time we allow tasks to be run.
     * Should a tick take longer than this, we will abort it.
     * The applicable {@link TimeUnit} is {@link TimeUnit#NANOSECONDS}.
     */
    private static final long MAX_WAIT_TIME = 1_500_000L;

    /**
     * This Map holds all currently actively ticking locations.
     */
    private final TickerCollection tickerCollection = new TickerCollection();

    /**
     * This Map tracks how many bugs have occurred in a given Location.
     * If too many bugs happen, we delete that Location.
     */
    private final Map<BlockPosition, AtomicInteger> bugs = new ConcurrentHashMap<>();

    /**
     * Don't overload the Bukkit scheduler with a thousand runnables.
     * It doesn't like that. This way, we can spread the
     * time cost over several game ticks and adapt ticker rate to server performance.
     */
    private final BlockingQueue<Runnable> syncTasks = new ArrayBlockingQueue<>(32);

    /**
     * Our internal instance of {@link TickerThreadFactory}, it provides the naming
     * convention for our {@link Thread} pool and also the count of this pool.
     */
    private final TickerThreadFactory threadFactory = new TickerThreadFactory(1);

    /**
     * This is our {@link Thread} pool.
     * Using an {@link ExecutorService} here instead of continuously spawning a new {@link Thread}
     * will help performance.
     */
    private final ExecutorService asyncExecutor = Executors.newSingleThreadExecutor(threadFactory);


    /**
     * These are "Queues" of blocks that need to be removed or moved.
     * <p>
     * Whilst the {@link ConcurrentHashMap} implementation is thread-safe,
     * the {@link Location Locations} are not. Therefore, the internal copy of the locations
     * should be treated as <strong>read only</strong> and never exposed externally.
     */
    private final Map<Location, Location> movingQueue = new ConcurrentHashMap<>();
    private final Map<Location, Boolean> deletionQueue = new ConcurrentHashMap<>();

    /**
     * Our {@link SlimefunPlugin} instance.
     */
    private SlimefunPlugin plugin;

    /**
     * This is our tick rate, aka the delay inbetween ticks.
     */
    private int tickRate;

    /**
     * This boolean marks whether the ticker is supposed to be halted.
     * This is true when the {@link Server} is shutting down.
     */
    private boolean halted = false;

    /**
     * This value marks whether the ticker is currently in the middle of an execution or run.
     * This needs to be volatile.
     * Visibility is not guaranteed for primitives across multiple threads
     * and since this is a {@link Runnable}, we cannot control which {@link Thread} calls {@link Runnable#run()}.
     */
    private volatile boolean running = false;

    /**
     * This method starts the {@link TickerTask} on an asynchronous schedule.
     * 
     * @param plugin
     *            The instance of our {@link SlimefunPlugin}
     */
    public void start(@Nonnull SlimefunPlugin plugin) {
        if (this.plugin != null) {
            throw new UnsupportedOperationException("The Slimefun TickerTask has already been started!");
        }

        this.plugin = plugin;
        this.tickRate = SlimefunPlugin.getCfg().getInt("URID.custom-ticker-delay");

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskLater(plugin, this::tick, 100L);
    }

    public void tick() {
        /*
         * This should only happen when the plugin gets disabled and the
         * task is already running.
         * Process all sync tasks before returning, blocking the main thread until
         * the task is complete.
         */
        if (running) {
            processSyncTasks();
            return;
        }

        if (!Bukkit.isPrimaryThread()) {
            return;
        }

        /*
         * Process all asynchronous tasks using our Executor.
         */
        asyncExecutor.submit(this::processAsyncTasks);

        /*
         * Process the remaining synchronous tasks on the main Thread.
         */
        processSyncTasks();
    }

    private void processSyncTasks() {
        /*
         * Synchronous tasks should only ever be processed on the
         * main Thread. Never on any other one.
         */
        if (!Bukkit.isPrimaryThread()) {
            return;
        }

        /*
         * Let's not lag the main thread.
         * Spikes suck.
         * Steady time usage is much better.
         */
        long deadline = System.nanoTime() + MAX_WAIT_TIME;

        try {
            /*
             * Only continue running this if the Plugin is shutting down or if we
             * haven't reached the deadline yet.
             */
            while (!plugin.isEnabled() || deadline > System.nanoTime()) {
                /*
                 * Poll the next task.
                 * But don't wait on an empty queue for too long.
                 */
                Runnable task = syncTasks.poll(MAX_POLL_TIME, TimeUnit.NANOSECONDS);

                if (task == null) {
                    /*
                     * If the queue is empty, let the server do its thing.
                     * We'll check back later.
                     * 
                     * If the plugin is not enabled, then the Server will
                     * expect the task to shutdown.
                     * We will continue blocking until we reach our end element.
                     */
                    if (!plugin.isEnabled()) {
                        break;
                    }
                } else if (task == END_ELEMENT) {
                    /*
                     * The end element is a singleton runnable and represents the end of the ticker task list.
                     * Finish up.
                     */
                    finish();
                } else {
                    /*
                     * The task is neither null nor our end element.
                     * Run it synchronously.
                     */
                    task.run();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        /*
         * We're not done with the synchronous tickers yet.
         * Give the server a breather and let it progress one tick.
         */
        if (!halted) {
            Bukkit.getScheduler().runTaskLater(plugin, this::processSyncTasks, 2L);
        }
    }

    private void finish() {
        running = false;

        /*
         * If the plugin has been disabled,
         * do not schedule the next execution.
         */
        if (!plugin.isEnabled()) {
            return;
        }

        /*
         * Schedule the ticker task to run again once the tick interval has elapsed.
         * Use a synchronous task instead of an asynchronous one to bind the
         * interval to the server clock, not the wall clock.
         */
        Bukkit.getScheduler().runTaskLater(plugin, this::tick, tickRate);
    }

    private void processAsyncTasks() {
        try {
            // Start our profiler
            SlimefunPlugin.getProfiler().start();

            /*
             * We don't care about the equality of elements here.
             * Use a list, it has lower add/remove/iterate time complexity and
             * smaller memory footprint.
             */
            List<BlockTicker> tickers = new LinkedList<>();

            /*
             * Copy the queue, this way we don't have to update the queue after each deletion,
             * reducing update contention.
             */
            Map<Location, Boolean> deletionQueueCopy = new HashMap<>(deletionQueue);
            deletionQueue.clear();

            for (Map.Entry<Location, Boolean> entry : deletionQueueCopy.entrySet()) {
                BlockStorage.deleteLocationInfoUnsafely(entry.getKey(), entry.getValue());
            }

            if (!halted) {
                Collection<Location> toTick = tickerCollection.getBlocksAsLocations();
                tickLocations(tickers, toTick);
            }

            /*
             * Copy the queue, this way we don't have to update the queue after each deletion,
             * reducing update contention.
             */
            Map<Location, Location> moveCopy = new HashMap<>(movingQueue);
            movingQueue.clear();

            for (Map.Entry<Location, Location> entry : moveCopy.entrySet()) {
                BlockStorage.moveLocationInfoUnsafely(entry.getKey(), entry.getValue());
            }

            // Start a new tick cycle for every BlockTicker
            for (BlockTicker ticker : tickers) {
                ticker.reset();
            }

            SlimefunPlugin.getProfiler().stop();
        } catch (Exception | LinkageError x) {
            SlimefunPlugin.logger().log(Level.SEVERE, x, () -> "An Exception was caught while ticking the Block Tickers Task for Slimefun v" + SlimefunPlugin.getVersion());
        } finally {
            try {
                // Notify the sync task processor that the end of the ticker list has been reached.
                syncTasks.put(END_ELEMENT);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Executes all tasks in a given {@link Collection} of {@link Location locations}
     */
    private void tickLocations(Collection<BlockTicker> tickers, @Nonnull Collection<Location> locations) {
        for (Location l : locations) {
            Config data = BlockStorage.getLocationInfo(l);
            SlimefunItem item = SlimefunItem.getByID(data.getString("id"));

            if (item != null && item.getBlockTicker() != null) {
                try {
                    runTask(l, data, item);
                    tickers.add(item.getBlockTicker());
                } catch (Exception x) {
                    // Catch and report any errors caused by this ticker.
                    reportErrors(l, item, x);
                }
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void runTask(Location l, Config data, SlimefunItem item) {
        BlockTicker ticker = item.getBlockTicker();

        // Check on which Thread to run this.
        if (ticker.isSynchronized()) {
            // Inform our profiler that we will be scheduling a task.
            SlimefunPlugin.getProfiler().scheduleEntries(1);

            // Start a new iteration for our block ticker
            ticker.start();

            /*
             * We are inserting a new timestamp because synchronized
             * actions are always ran with a 50ms delay (1 game tick).
             */
            try {
                Runnable syncTask = () -> {
                    Block b = l.getBlock();
                    tickBlock(l, b, item, data, System.nanoTime());
                };

                syncTasks.put(syncTask);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            long timestamp = SlimefunPlugin.getProfiler().newEntry();
            ticker.start();
            Block b = l.getBlock();
            tickBlock(l, b, item, data, timestamp);
        }
    }

    @ParametersAreNonnullByDefault
    private void tickBlock(Location l, Block b, SlimefunItem item, Config data, long timestamp) {
        try {
            item.getBlockTicker().tick(b, item, data);
        } catch (Exception | LinkageError x) {
            reportErrors(l, item, x);
        } finally {
            SlimefunPlugin.getProfiler().closeEntry(l, item, timestamp);
        }
    }

    @ParametersAreNonnullByDefault
    private void reportErrors(Location l, SlimefunItem item, Throwable x) {
        BlockPosition position = new BlockPosition(l);
        int errors = bugs.computeIfAbsent(position, pos -> new AtomicInteger()).incrementAndGet();

        if (errors == 1) {
            // Generate a new Error-Report
            new ErrorReport<>(x, l, item);
        } else if (errors >= 4) {
            SlimefunPlugin.logger().log(Level.SEVERE, "X: {0} Y: {1} Z: {2} ({3})", new Object[] { l.getBlockX(), l.getBlockY(), l.getBlockZ(), item.getId() });
            SlimefunPlugin.logger().log(Level.SEVERE, "has thrown 4 error messages in the last 4 Ticks, the Block has been terminated.");
            SlimefunPlugin.logger().log(Level.SEVERE, "Check your /plugins/Slimefun/error-reports/ folder for details.");
            SlimefunPlugin.logger().log(Level.SEVERE, " ");
            bugs.remove(position);

            BlockStorage.deleteLocationInfoUnsafely(l, true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> l.getBlock().setType(Material.AIR));
        }
    }

    /**
     * This returns whether the {@link TickerTask} has been ordered to halt.
     * 
     * @return Whether the {@link TickerTask} should halt.
     */
    public boolean isHalted() {
        return halted;
    }

    /**
     * This method orders the {@link TickerTask} to be halted.
     * Once halted the task <strong>cannot</strong> be un-halted.
     * This method will block until all sync tasks are processed; however,
     * it will not block for the async tasks
     */
    public synchronized void halt() {
        SlimefunPlugin.logger().info("Stopping TickerTask, this may take a few seconds or up to a minute...");
        /*
         * Mark the ticker as halted so that #tick won't attempt to schedule another task
         */
        halted = true;
        tick();
        /*
         * Mark running after #tick so #processAsyncTasks gets scheduled
         */
        running = false;
        // Process async tasks and shutdown the asyncExecutor
        asyncExecutor.shutdown();
        try {
            asyncExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            SlimefunPlugin.logger().log(Level.SEVERE, ex, () -> "Failed to shutdown the async executor");
        }
        // Process remaining sync tasks
        while (!syncTasks.isEmpty()) {
            syncTasks.poll().run();
        }
        SlimefunPlugin.logger().info("Stopped TickerTask!");
    }

    @ParametersAreNonnullByDefault
    public void queueMove(Location from, Location to) {
        Validate.notNull(from, "Source Location cannot be null!");
        Validate.notNull(to, "Target Location cannot be null!");

        movingQueue.put(from, to);
    }

    @ParametersAreNonnullByDefault
    public void queueDelete(Location l, boolean destroy) {
        Validate.notNull(l, "Location must not be null!");

        deletionQueue.put(l, destroy);
    }

    /**
     * This method checks if the given {@link Location} has been reserved
     * by this {@link TickerTask}.
     * A reserved {@link Location} does not currently hold any data but will
     * be occupied upon the next tick.
     * Checking this ensures that our {@link Location} does not get treated like a normal
     * {@link Location} as it is theoretically "moving".
     * 
     * @param l
     *            The {@link Location} to check
     * 
     * @return Whether this {@link Location} has been reserved and will be filled upon the next tick
     */
    public boolean isOccupiedSoon(@Nonnull Location l) {
        Validate.notNull(l, "Null is not a valid Location!");

        return movingQueue.containsValue(l);
    }

    /**
     * This method checks if a given {@link Location} will be deleted on the next tick.
     *
     * @param l
     *            The {@link Location} to check
     *
     * @return Whether this {@link Location} will be deleted on the next tick
     */
    public boolean isDeletedSoon(@Nonnull Location l) {
        Validate.notNull(l, "Null is not a valid Location!");

        return deletionQueue.containsKey(l);
    }

    /**
     * This returns the delay between ticks
     * 
     * @return The tick delay
     */
    public int getTickRate() {
        return tickRate;
    }

    /**
     * This enables the ticker at the given {@link Location} and adds it to the our queue.
     *
     * @param l The {@link Location} to activate
     * @see #disableTicker(Location)
     */
    public void enableTicker(@Nonnull Location l) {
        Validate.notNull(l, "Location cannot be null!");
        Validate.notNull(l.getWorld(), "Location must have a world!");

        tickerCollection.addBlock(l);
    }

    /**
     * This method disables the ticker at the given {@link Location} and removes it from our internal
     * "queue".
     *
     * @param l The {@link Location} to remove
     * @see #enableTicker(Location)
     */
    public void disableTicker(@Nonnull Location l) {
        Validate.notNull(l, "Location cannot be null!");
        Validate.notNull(l.getWorld(), "Location must have a world!");

        tickerCollection.removeBlock(l);
    }
}
