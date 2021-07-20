package io.github.thebusybiscuit.slimefun4.implementation.tasks;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

/**
 * This is our {@link ThreadFactory} for the {@link TickerTask}.
 * It holds the amount of {@link Thread Threads} we dedicate towards our {@link TickerTask}
 * and provides a naming convention for our {@link Thread Threads}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see TickerTask
 *
 */
final class TickerThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNumber = new AtomicInteger();

    private final int threadCount;

    /**
     * This constructs a new {@link TickerThreadFactory} with the given {@link Thread} count.
     * 
     * @param threadCount
     *            The amount of {@link Thread Threads} to provide to the {@link TickerTask}
     */
    TickerThreadFactory(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * This returns the amount of {@link Thread Threads} we dedicate towards
     * the {@link TickerTask}.
     * 
     * @return The {@link Thread} count
     */
    int getThreadCount() {
        return threadCount;
    }

    /**
     * This creates a new {@link Thread} for the {@link TickerTask}.
     */
    @Override
    public Thread newThread(@Nonnull Runnable runnable) {
        return new Thread(runnable, nextThreadName());
    }

    private @Nonnull String nextThreadName() {
        return "Slimefun Ticker Thread-" + threadNumber.getAndIncrement();
    }

}
