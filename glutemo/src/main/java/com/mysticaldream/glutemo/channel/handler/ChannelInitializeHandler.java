package com.mysticaldream.glutemo.channel.handler;

import com.mysticaldream.glutemo.channel.AbstractNioChannel;
import com.mysticaldream.glutemo.channel.ChannelHandlerContext;

/**
 * @author MysticalDream
 */
public abstract class ChannelInitializeHandler<T extends AbstractNioChannel> extends ChannelInHandlerAdapter {
    @Override
    public void channelRegistered(ChannelHandlerContext context) throws Exception {
        initChannel((T) context.channel());
        context.propagateRegisteredEvent();
    }

    public abstract void initChannel(T channel);
}