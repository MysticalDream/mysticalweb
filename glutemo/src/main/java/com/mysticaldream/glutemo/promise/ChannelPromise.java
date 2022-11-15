package com.mysticaldream.glutemo.promise;

import com.mysticaldream.glutemo.channel.AbstractNioChannel;
import com.mysticaldream.glutemo.concurrent.AbstractTaskLoopExecutor;

/**
 * 带有{@link AbstractNioChannel}
 *
 * @author MysticalDream
 */
public class ChannelPromise extends Promise<ChannelPromise> {
    private AbstractNioChannel channel;

    private ChannelPromise(AbstractNioChannel channel, AbstractTaskLoopExecutor executor) {
        super(executor);
        this.channel = channel;
    }

    public static ChannelPromise newChannelPromise(AbstractNioChannel channel, AbstractTaskLoopExecutor executor) {
        return new ChannelPromise(channel, executor);
    }
}
