package com.mysticaldream.glutemo.channel;

import com.mysticaldream.glutemo.channel.handler.DefaultChannelPipeline;
import com.mysticaldream.glutemo.channel.handler.ChannelPipeline;
import com.mysticaldream.glutemo.promise.ChannelPromise;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

/**
 * 对通道的属性和行为的抽象，所有的{@link SelectableChannel channel}都继承自该抽象类
 *
 * @author MysticalDream
 */
public abstract class AbstractNioChannel {

    private SelectableChannel sc;

    private ChannelPipeline channelPipeline;

    private Reactor reactor;

    private SelectionKey selectionKey;


    public AbstractNioChannel(SelectableChannel sc) {
        this.sc = sc;
        this.channelPipeline = new DefaultChannelPipeline(this);
    }


    /**
     * 设置{@link Reactor reactor}
     *
     * @param reactor reactor实例
     */
    public void setReactor(Reactor reactor) {
        this.reactor = reactor;
    }

    /**
     * 获取java原生的通道channel
     *
     * @return
     */
    public SelectableChannel javaChannel() {
        return sc;
    }

    /**
     * 获取 {@link Reactor}
     *
     * @return 返回该通道注册的reactor
     */
    public Reactor reactor() {
        return reactor;
    }

    /**
     * 绑定端口
     *
     * @throws IOException
     */
    public abstract void bind(SocketAddress socketAddress) throws IOException;

    /**
     * 获取感兴趣事件
     *
     * @return
     */
    public abstract int getInterestedOps();


    /**
     * 处理读事件
     */
    public abstract void read() throws IOException;

    /**
     * 往通道写入数据
     *
     * @param data           如果是{@link java.nio.ByteBuffer}记得切换成读取模式
     * @param channelPromise
     */
    public abstract void write(Object data, ChannelPromise channelPromise) throws Exception;

    /**
     * 通知要将通道的消息刷新
     */
    public abstract void notifyFlush();

    /**
     * 刷新所有在此通道被暂存的输出消息
     *
     * @throws Exception
     */
    public abstract void flush() throws Exception;


    /**
     * 判断{@link AbstractNioChannel} 是否处于活动且连接的状态
     *
     * @return 如果 {@link AbstractNioChannel} 处于活动状态且已连接，则返回 {@code true}
     */
    public abstract boolean isActive();

    /**
     * 关闭该通道
     *
     * @throws IOException
     */
    public abstract void close() throws IOException;

    public ChannelPipeline pipeline() {
        return channelPipeline;
    }

    /**
     * 设置{@link SelectionKey}
     *
     * @param selectionKey
     */
    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    /**
     * 获取 {@link SelectionKey}
     *
     * @return
     */
    public SelectionKey getSelectionKey() {
        return selectionKey;
    }
}
