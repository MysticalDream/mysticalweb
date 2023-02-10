package com.mysticaldream.glutemo.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author MysticalDream
 */
@Slf4j
public class SimpleTaskLoopExecutor extends AbstractTaskLoopExecutor {


    public SimpleTaskLoopExecutor(Executor executor) {
        super(new LinkedBlockingQueue<>(), executor);
    }

    @Override
    protected void run() {
        while (true) {
            try {
                processTasks();
                if (isShutdown()) {
                    break;
                }
            } catch (Exception e) {
                log.error("exception", e);
            }
        }
    }

    @Override
    protected void processTasks() throws InterruptedException {
        ((LinkedBlockingQueue<Runnable>) tasks()).take().run();
    }

    @Override
    protected void wakeup(boolean inLoop) {
        //nothing
    }
}
