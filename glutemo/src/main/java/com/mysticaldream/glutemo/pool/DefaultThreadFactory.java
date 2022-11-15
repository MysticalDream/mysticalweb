package com.mysticaldream.glutemo.pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author MysticalDream
 */
public class DefaultThreadFactory implements ThreadFactory {

    private static AtomicInteger poolCount = new AtomicInteger(1);

    private AtomicInteger threadCount = new AtomicInteger(1);

    private final String namePrefix;

    public DefaultThreadFactory(String poolPrefix) {
        this.namePrefix = poolPrefix + "-" +
                poolCount.getAndIncrement() +
                "-thread-";
        ;
    }

    public DefaultThreadFactory() {
        namePrefix = "pool-" +
                poolCount.getAndIncrement() +
                "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, namePrefix + threadCount.getAndIncrement());
        return thread;
    }

}
