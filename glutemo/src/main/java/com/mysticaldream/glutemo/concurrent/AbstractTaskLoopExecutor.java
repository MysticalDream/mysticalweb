package com.mysticaldream.glutemo.concurrent;

import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author MysticalDream
 */
public abstract class AbstractTaskLoopExecutor extends ExecutorServiceAdaptor implements TaskLoopExecutor {

    private Deque<Runnable> tasks = new LinkedBlockingDeque<>();

    private Executor executor;

    private volatile Thread thread;

    public AbstractTaskLoopExecutor(Executor executor) {
        this.executor = executor;
    }

    public void startTaskLoop() {
        if (!isStart()) {
            setStartFlag();
            executor.execute(() -> {
                thread = Thread.currentThread();
                this.run();
            });
        }
    }


    abstract protected void run();

    abstract protected void wakeup(boolean inLoop);


    @Override
    public void execute(Runnable command) {
        tasks.offer(command);
        boolean inLoop = inLoop();
        if (!inLoop) {
            startTaskLoop();
        }
        wakeup(inLoop);
    }

    protected void processTasks() {
        Runnable r;
        while ((r = tasks.poll()) != null) {
            r.run();
        }
    }

    @Override
    public boolean inLoop() {
        return thread == Thread.currentThread();
    }
}
