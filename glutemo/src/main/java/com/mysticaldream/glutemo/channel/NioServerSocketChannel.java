package com.mysticaldream.glutemo.channel;

import com.mysticaldream.glutemo.channel.handler.ChannelPipeline;
import com.mysticaldream.glutemo.promise.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author MysticalDream
 */
@Slf4j
public class NioServerSocketChannel extends AbstractNioChannel {


    public NioServerSocketChannel() throws IOException {
        super(ServerSocketChannel.open());
    }

    @Override
    public ServerSocketChannel javaChannel() {
        return ((ServerSocketChannel) super.javaChannel());
    }

    @Override
    public void bind(SocketAddress socketAddress) throws IOException {
        ServerSocketChannel serverSocketChannel = javaChannel();
        serverSocketChannel.bind(socketAddress);
        serverSocketChannel.configureBlocking(false);
    }

    @Override
    public int getInterestedOps() {
        return SelectionKey.OP_ACCEPT;
    }


    @Override
    public void write(Object data, ChannelPromise channelPromise) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyFlush() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isActive() {
        return javaChannel().isOpen() && javaChannel().socket().isBound();
    }

    @Override
    public void close() throws IOException {
        javaChannel().close();
    }

    @Override
    public void read() {
        try {
            //TODO 可以循环一下，看看是否不止一个连接请求
            SocketChannel accept = javaChannel().accept();
            accept.configureBlocking(false);
            ChannelPipeline channelPipeline = pipeline();
            channelPipeline.propagateReadEvent(accept);
        } catch (Exception e) {
            log.error("acceptor read", e);
        }

    }
}
