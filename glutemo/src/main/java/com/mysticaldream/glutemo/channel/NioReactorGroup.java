package com.mysticaldream.glutemo.channel;

import com.mysticaldream.glutemo.concurrent.ExecutorServiceAdaptor;
import com.mysticaldream.glutemo.pool.DefaultThreadFactory;
import com.mysticaldream.glutemo.pool.SimpleExecutor;
import com.mysticaldream.glutemo.promise.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author MysticalDream
 */
@Slf4j
public class NioReactorGroup extends ExecutorServiceAdaptor implements ReactorGroup {

    private Reactor[] reactor;

    private int num;

    private AtomicInteger pollCounter = new AtomicInteger(0);


    public NioReactorGroup(int num) {
        this.num = num;
        init();
    }

    public NioReactorGroup() {
        this(Runtime.getRuntime().availableProcessors() * 2);
    }

    private void init() {

        Executor executor = new SimpleExecutor(new DefaultThreadFactory());

        reactor = new Reactor[num];

        for (int i = 0; i < num; i++) {
            reactor[i] = new NioReactor(this, executor);
        }

    }


    @Override
    public Reactor next() {

        if (pollCounter.get() == num) {
            pollCounter.set(0);
        }

        return reactor[pollCounter.getAndIncrement()];
    }

    @Override
    public ChannelPromise register(AbstractNioChannel channel, int interestOps) {
        return next().register(channel, interestOps);
    }

    @Override
    public void execute(Runnable command) {
        next().execute(command);
    }
}
