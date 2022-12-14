package com.mysticaldream.glutemo.channel;

import com.mysticaldream.glutemo.channel.handler.ChannelHandler;
import com.mysticaldream.glutemo.concurrent.SimpleTaskLoopExecutorGroup;
import com.mysticaldream.glutemo.channel.event.InEventTrigger;
import com.mysticaldream.glutemo.channel.event.OutEventTrigger;

/**
 * @author MysticalDream
 */
public interface ChannelPipeline extends InEventTrigger, OutEventTrigger {

    ChannelPipeline addFirst(String name, ChannelHandler channelHandler);

    ChannelPipeline addFirst(SimpleTaskLoopExecutorGroup executor, ChannelHandler channelHandler);

    ChannelPipeline addLast(String name, ChannelHandler channelHandler);

    ChannelPipeline addLast(SimpleTaskLoopExecutorGroup executor, ChannelHandler channelHandler);

    ChannelPipeline addFirst(ChannelHandler channelHandler);

    ChannelPipeline addLast(ChannelHandler channelHandler);

    AbstractNioChannel channel();


    boolean isEmpty();

}
