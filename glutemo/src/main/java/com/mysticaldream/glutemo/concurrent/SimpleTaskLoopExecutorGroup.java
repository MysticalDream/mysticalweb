package com.mysticaldream.glutemo.concurrent;

import com.mysticaldream.glutemo.pool.DefaultThreadFactory;
import com.mysticaldream.glutemo.pool.SimpleExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author MysticalDream
 */
public class SimpleTaskLoopExecutorGroup extends ExecutorServiceAdaptor {

    private int num;

    private SimpleTaskLoopExecutor[] taskLoopExecutors;

    private final String POOLNAME = "simpleTaskLoopExecutorGroup";

    private AtomicInteger pollCounter = new AtomicInteger(0);

    public SimpleTaskLoopExecutorGroup(int num) {
        this.num = num;
        init();
    }

    private void init() {

        Executor executor = new SimpleExecutor(new DefaultThreadFactory(POOLNAME));

        taskLoopExecutors = new SimpleTaskLoopExecutor[num];

        for (int i = 0; i < num; i++) {
            taskLoopExecutors[i] = new SimpleTaskLoopExecutor(executor);
        }
    }

    public SimpleTaskLoopExecutorGroup() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public SimpleTaskLoopExecutor next() {
        return taskLoopExecutors[pollCounter.getAndAccumulate(num - 1, (prev, x) -> {
            if (prev == x) {
                return 0;
            } else {
                return ++prev;
            }
        })];
    }

    @Override
    public void execute(Runnable command) {
        next().execute(command);
    }
}
