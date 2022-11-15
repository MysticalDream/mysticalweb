package com.mysticaldream.glutemo.channel.handler;

import com.mysticaldream.glutemo.promise.ChannelPromise;

/**
 * @author MysticalDream
 */
public interface ChannelOutHandler extends ChannelHandler {

    void write(ChannelHandlerContext context, Object msg, ChannelPromise channelPromise) throws Exception;

    void flush(ChannelHandlerContext context) throws Exception;

}
