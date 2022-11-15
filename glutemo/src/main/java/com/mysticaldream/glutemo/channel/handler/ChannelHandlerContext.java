package com.mysticaldream.glutemo.channel.handler;

import com.mysticaldream.glutemo.channel.AbstractNioChannel;
import com.mysticaldream.glutemo.channel.event.InEventTrigger;
import com.mysticaldream.glutemo.channel.event.OutEventTrigger;
import com.mysticaldream.glutemo.concurrent.AbstractTaskLoopExecutor;
import com.mysticaldream.glutemo.promise.ChannelPromise;

/**
 * @author MysticalDream
 */
public interface ChannelHandlerContext extends InEventTrigger, OutEventTrigger {

    AbstractTaskLoopExecutor getTaskLoopExecutor();

    ChannelHandler getHandler();

    String name();

    @Override
    ChannelHandlerContext propagateReadEvent(Object msg);

    @Override
    ChannelHandlerContext propagateRegisteredEvent();

    @Override
    ChannelHandlerContext propagateExceptionCaughtEvent(Throwable throwable);

    @Override
    ChannelPromise write(Object msg, boolean flush);

    @Override
    ChannelPromise write(Object msg);

    @Override
    ChannelPromise write(Object msg, ChannelPromise promise);

    @Override
    ChannelPromise writeAndFlush(Object msg);

    @Override
    ChannelPromise writeAndFlush(Object msg, ChannelPromise promise);

    @Override
    ChannelHandlerContext flush();

    AbstractNioChannel channel();
}
