package com.mysticaldream.glutemo.concurrent;

import java.util.concurrent.Executor;

/**
 * @author MysticalDream
 */
public class SimpleTaskLoopExecutor extends AbstractTaskLoopExecutor {


    public SimpleTaskLoopExecutor(Executor executor) {
        super(executor);
    }

    @Override
    protected void run() {
        while (true) {
            processTasks();
            if (isShutdown()) {
                break;
            }
        }
    }

    @Override
    protected void wakeup(boolean inLoop) {
        //nothing
    }
}
