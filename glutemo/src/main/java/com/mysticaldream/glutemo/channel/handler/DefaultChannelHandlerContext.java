package com.mysticaldream.glutemo.channel.handler;

import com.mysticaldream.glutemo.concurrent.AbstractTaskLoopExecutor;

/**
 * @author MysticalDream
 */
public class DefaultChannelHandlerContext extends AbstractChannelHandlerContext {

    private ChannelHandler channelHandler;

    public DefaultChannelHandlerContext(ChannelPipeline channelPipeline, AbstractTaskLoopExecutor taskLoopExecutor, String name, ChannelHandler channelHandler) {
        super(channelPipeline, taskLoopExecutor, name, channelHandler.getClass());
        this.channelHandler = channelHandler;
    }

    @Override
    public ChannelHandler getHandler() {
        return channelHandler;
    }
}
