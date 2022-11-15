package com.mysticaldream.glutemo.channel;

import com.mysticaldream.glutemo.promise.ChannelPromise;

import java.util.concurrent.ExecutorService;

/**
 * @author MysticalDream
 */
public interface ReactorGroup extends ExecutorService {


    /**
     * 获取reactor
     *
     * @return
     */
    Reactor next();


    ChannelPromise register(AbstractNioChannel channel, final int interestOps);


}
