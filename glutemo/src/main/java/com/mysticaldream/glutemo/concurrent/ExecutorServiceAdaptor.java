package com.mysticaldream.glutemo.concurrent;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author MysticalDream
 */
public abstract class ExecutorServiceAdaptor extends AbstractExecutorService {

    private final AtomicReference<StateFlag> flag = new AtomicReference<>(StateFlag.NOT_START);


    enum StateFlag {
        NOT_START,
        STARTED,
        SHUTDOWN,
        TERMINATED
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return flag.get() == StateFlag.SHUTDOWN;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    protected void setStartFlag() {
        flag.set(StateFlag.STARTED);
    }

    protected boolean isStart() {
        return flag.get() == StateFlag.STARTED;
    }
}
