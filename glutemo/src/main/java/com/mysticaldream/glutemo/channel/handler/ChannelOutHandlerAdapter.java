package com.mysticaldream.glutemo.channel.handler;

import com.mysticaldream.glutemo.channel.AbstractChannelHandlerContext;
import com.mysticaldream.glutemo.channel.ChannelHandlerContext;
import com.mysticaldream.glutemo.promise.ChannelPromise;

/**
 * @author MysticalDream
 */
public class ChannelOutHandlerAdapter implements ChannelOutHandler {
    @Override
    public void write(ChannelHandlerContext context, Object msg, ChannelPromise channelPromise) throws Exception {
        context.write(msg, channelPromise);
    }

    @Override
    public void flush(ChannelHandlerContext context) throws Exception {
        context.flush();
    }

    @Override
    public void close(AbstractChannelHandlerContext context, ChannelPromise channelPromise) {
        context.close(channelPromise);
    }
}
