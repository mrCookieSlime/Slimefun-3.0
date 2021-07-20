package io.github.thebusybiscuit.slimefun4.core.services.profiler;

import java.util.concurrent.ThreadFactory;

import javax.annotation.Nonnull;

/**
 * This is our {@link ThreadFactory} for the {@link SlimefunProfiler}.
 * It holds the amount of {@link Thread Threads} we dedicate towards our {@link SlimefunProfiler}
 * and provides a naming convention for our {@link Thread Threads}.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SlimefunProfiler
 *
 */
final class ProfilerThreadFactory implements ThreadFactory {

    private final int threadCount;

    /**
     * This constructs a new {@link ProfilerThreadFactory} with the given {@link Thread} count.
     * 
     * @param threadCount
     *            The amount of {@link Thread Threads} to provide to the {@link SlimefunProfiler}
     */
    ProfilerThreadFactory(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * This returns the amount of {@link Thread Threads} we dedicate towards
     * the {@link SlimefunProfiler}.
     * 
     * @return The {@link Thread} count
     */
    int getThreadCount() {
        return threadCount;
    }

    /**
     * This creates a new {@link Thread} for the {@link SlimefunProfiler}.
     */
    @Override
    public Thread newThread(@Nonnull Runnable runnable) {
        return new Thread(runnable, "Slimefun Profiler");
    }

}
