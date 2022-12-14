package com.mysticaldream.glutemo.channel.handler;

import com.mysticaldream.glutemo.channel.ChannelHandlerContext;

/**
 * @author MysticalDream
 */
public interface ChannelInHandler extends ChannelHandler {


    void channelRegistered(ChannelHandlerContext context) throws Exception;

    void channelRead(ChannelHandlerContext context, Object msg) throws Exception;

    void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception;

}
