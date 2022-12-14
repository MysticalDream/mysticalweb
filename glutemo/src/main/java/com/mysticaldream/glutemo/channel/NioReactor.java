package com.mysticaldream.glutemo.channel;

import com.mysticaldream.glutemo.concurrent.AbstractTaskLoopExecutor;
import com.mysticaldream.glutemo.promise.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.jctools.queues.MpscUnboundedArrayQueue;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author MysticalDream
 */
@Slf4j
public class NioReactor extends AbstractTaskLoopExecutor implements Reactor {


    private Selector selector;

    private ReactorGroup parent;


    private AtomicLong wakeupCounter = new AtomicLong(0);


    public NioReactor(ReactorGroup parent, Executor executor) {
//        super(new ConcurrentLinkedQueue<>(), executor);
        super(new MpscUnboundedArrayQueue<>(1024), executor);
        this.parent = parent;
    }

    {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        while (!isShutdown()) {
            log.trace("selecting");
            try {

                int selectedCount;

                if (wakeupCounter.getAndSet(-1) > 0) {
                    //在消费事件时又新增事件，这时需要selector立即返回
                    selectedCount = selector.selectNow();
                } else {
                    selectedCount = selector.select();
                }

                log.trace("select count {}", selectedCount);

                wakeupCounter.set(0);

                if (selectedCount > 0) {
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey selectionKey = keyIterator.next();
                        processSelectionKey(selectionKey);
                        keyIterator.remove();
                    }
                }

                processTasks();

            } catch (IOException e) {
                log.error("an IO error occurred", e);
            } catch (Exception e) {
                log.error("exception", e);
            }
        }
    }


    private void processSelectionKey(SelectionKey selectionKey) throws Exception {
        if (selectionKey.isValid()) {

            int readyOps = selectionKey.readyOps();

            if ((readyOps & SelectionKey.OP_WRITE) != 0) {
                ((AbstractNioChannel) selectionKey.attachment()).flush();
            }

            if (((readyOps & (SelectionKey.OP_ACCEPT | SelectionKey.OP_READ)) != 0) || readyOps == 0) {
                ((AbstractNioChannel) selectionKey.attachment()).read();
            }

        }

    }


    @Override
    public ReactorGroup reactorGroup() {
        return parent;
    }

    @Override
    public boolean inReactor() {
        return inLoop();
    }

    @Override
    public Reactor next() {
        return this;
    }

    @Override
    public ChannelPromise register(AbstractNioChannel channel, int interestOps) {
        ChannelPromise promise = ChannelPromise.newChannelPromise(channel, this);
        if (inReactor()) {
            register0(channel, interestOps, promise);
        } else {
            try {
                execute(() -> register0(channel, interestOps, promise));
            } catch (Exception e) {
                log.error("register", e);
            }
        }
        return promise;
    }


    private void register0(AbstractNioChannel channel, int interestOps, ChannelPromise channelPromise) {
        try {
            SelectionKey selectionKey = channel.javaChannel().register(selector, interestOps);
            channel.setSelectionKey(selectionKey);
            channel.setReactor(this);
            selectionKey.attach(channel);
            channel.pipeline().propagateRegisteredEvent();
            channelPromise.resolve(channelPromise);
        } catch (ClosedChannelException e) {
            log.error("register0", e);
        }
    }


    @Override
    protected void wakeup(boolean inLoop) {
        if (!inLoop && wakeupCounter.incrementAndGet() == 0) {
            selector.wakeup();
        }
    }
}
