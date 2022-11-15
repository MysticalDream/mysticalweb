package mytest;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author MysticalDream
 */
public class SimpleThreadPool implements Executor {

    Deque<Runnable> deque = new ConcurrentLinkedDeque<>();
    volatile Thread thread;

    ThreadFactory threadFactory = new ThreadFactory() {

        AtomicInteger integer = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("mystcaildream-pool-1-thread-" + integer.getAndIncrement());
            return thread;
        }
    };


    private Executor executor = command -> threadFactory.newThread(command).start();

    public SimpleThreadPool() {

    }

    public void doStartThread() {
        executor.execute(() -> {
            thread = Thread.currentThread();
            SimpleThreadPool.this.run();
        });
    }

    public void run() {
        while (!Thread.interrupted()) {
            while (!deque.isEmpty()) {
                Runnable poll = deque.poll();
                poll.run();
            }
        }
    }


    @Override
    public void execute(Runnable command) {
        deque.offer(command);
    }

}
