package com.mysticaldream.glutemo.channel;

import com.mysticaldream.glutemo.promise.ChannelPromise;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * @author MysticalDream
 */
public class NioDatagramChannel extends AbstractNioChannel {


    public NioDatagramChannel(SelectableChannel sc) {
        super(sc);
    }

    @Override
    public void bind(SocketAddress socketAddress) throws IOException {

    }

    @Override
    public int getInterestedOps() {
        return SelectionKey.OP_READ;
    }

    @Override
    public void read() throws IOException {

    }

    @Override
    public void write(Object data, ChannelPromise channelPromise) throws Exception {

    }

    @Override
    public void notifyFlush() {

    }

    @Override
    public void flush() throws Exception {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}
