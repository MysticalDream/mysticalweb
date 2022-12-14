package com.mysticaldream.glutemo.channel;

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

    private int defaultAcceptCounter = 16;

    private final int acceptCounter = defaultAcceptCounter;

    private int backlog = 50;

    public NioServerSocketChannel() throws IOException {
        super(ServerSocketChannel.open());
    }

    public NioServerSocketChannel(int backlog) throws IOException {
        this();
        this.backlog = backlog;
    }

    @Override
    public ServerSocketChannel javaChannel() {
        return ((ServerSocketChannel) super.javaChannel());
    }

    @Override
    public void bind(SocketAddress socketAddress) throws IOException {
        ServerSocketChannel serverSocketChannel = javaChannel();
        serverSocketChannel.bind(socketAddress, backlog);
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
    public void read() {
        try {
            //TODO 可以循环一下，看看是否不止一个连接请求
            SocketChannel accept;
            int count = acceptCounter > 0 ? acceptCounter : 1;
            while (count > 0 && ((accept = javaChannel().accept()) != null)) {
                accept.configureBlocking(false);
                ChannelPipeline channelPipeline = pipeline();
                channelPipeline.propagateReadEvent(accept);
                count--;
            }

        } catch (Exception e) {
            log.error("acceptor read", e);
        }
    }
}
