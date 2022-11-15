package com.mysticaldream.glutemo.bootstrap;

import com.mysticaldream.glutemo.channel.*;
import com.mysticaldream.glutemo.channel.handler.ChannelHandler;
import com.mysticaldream.glutemo.channel.handler.ChannelHandlerContext;
import com.mysticaldream.glutemo.channel.handler.ChannelInitializeHandler;
import com.mysticaldream.glutemo.channel.handler.ChannelInHandlerAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

/**
 * 服务器引导类
 *
 * @author MysticalDream
 */
public class ServerBootStrap {

    ReactorGroup mainReactorGroup;

    ReactorGroup subReactorGroup;

    private final List<ServerChannel> channels = new LinkedList<>();

    public ServerBootStrap(ReactorGroup mainReactorGroup, ReactorGroup subReactorGroup) {
        this.mainReactorGroup = mainReactorGroup;
        this.subReactorGroup = subReactorGroup;
    }

    public ServerBootStrap(ReactorGroup mainReactorGroup) {
        this.mainReactorGroup = mainReactorGroup;
        this.subReactorGroup = mainReactorGroup;
    }

    public ServerBootStrap addServerChannel(AbstractNioChannel abstractNioChannel, int port, ChannelHandler childChannelHandler) {
        channels.add(new ServerChannel(abstractNioChannel, port, childChannelHandler));
        return this;
    }


    static class ServerChannel {
        AbstractNioChannel channel;
        int port;

        ChannelHandler childChannelHandler;

        public ServerChannel(AbstractNioChannel channel, int port, ChannelHandler childChannelHandler) {
            this.channel = channel;
            this.port = port;
            this.childChannelHandler = childChannelHandler;
        }
    }

    /**
     * 绑定所有注册的端口并进行初始化
     *
     * @throws IOException
     */
    public void bind() throws IOException {

        for (ServerChannel serverChannel : channels) {

            AbstractNioChannel channel = serverChannel.channel;

            channel.bind(new InetSocketAddress(serverChannel.port));

            channel.pipeline().addLast(new ChannelInitializeHandler<AbstractNioChannel>() {
                @Override
                public void initChannel(AbstractNioChannel channel) {
                    channel.reactor().execute(() -> {
                        channel.pipeline().addLast(new AcceptorRegister(serverChannel.childChannelHandler, subReactorGroup));
                    });

                }
            });

            mainReactorGroup.register(channel, channel.getInterestedOps());

        }
    }

    /**
     * 连接请求入站处理
     * <p>
     * 通过注册{@link NioSocketChannel socketChannel}到{@link ReactorGroup subReactorGroup}中的一个{@link Reactor reactor}
     */
    static class AcceptorRegister extends ChannelInHandlerAdapter {

        private final ChannelHandler childHandler;

        private final ReactorGroup subReactorGroup;

        public AcceptorRegister(ChannelHandler childHandler, ReactorGroup subReactorGroup) {
            this.childHandler = childHandler;
            this.subReactorGroup = subReactorGroup;
        }

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
            AbstractNioChannel channel = null;

            if (msg instanceof DatagramChannel) {
                channel = new NioDatagramChannel((SelectableChannel) msg);
            } else if (msg instanceof SocketChannel) {
                channel = new NioSocketChannel((SelectableChannel) msg);
            }

            if (channel == null) {
                throw new InvalidParameterException("不支持的类型:" + msg.getClass());
            }

            channel.pipeline().addLast(childHandler);
            subReactorGroup.register(channel, channel.getInterestedOps());
        }
    }


}
