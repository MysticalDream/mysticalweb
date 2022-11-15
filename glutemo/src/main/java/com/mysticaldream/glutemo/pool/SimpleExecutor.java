package com.mysticaldream.glutemo.pool;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * @author MysticalDream
 */
public class SimpleExecutor implements Executor {

    private final ThreadFactory threadFactory;

    public SimpleExecutor(ThreadFactory threadFactory) {
        this.threadFactory = Objects.requireNonNull(threadFactory, "threadFactory");
    }

    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }
}
