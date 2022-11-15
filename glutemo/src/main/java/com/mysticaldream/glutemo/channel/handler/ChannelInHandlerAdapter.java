package com.mysticaldream.glutemo.channel.handler;

/**
 * @author MysticalDream
 */
public class ChannelInHandlerAdapter implements ChannelInHandler {
    @Override
    public void channelRegistered(ChannelHandlerContext context) throws Exception {
        context.propagateRegisteredEvent();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        context.propagateReadEvent(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {
        context.propagateExceptionCaughtEvent(throwable);
    }
}
