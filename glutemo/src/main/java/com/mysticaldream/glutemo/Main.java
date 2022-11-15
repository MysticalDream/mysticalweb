package com.mysticaldream.glutemo;

import com.mysticaldream.glutemo.bootstrap.ServerBootStrap;
import com.mysticaldream.glutemo.channel.AbstractNioChannel;
import com.mysticaldream.glutemo.channel.NioReactorGroup;
import com.mysticaldream.glutemo.channel.NioServerSocketChannel;
import com.mysticaldream.glutemo.channel.handler.ChannelHandlerContext;
import com.mysticaldream.glutemo.channel.handler.ChannelInHandlerAdapter;
import com.mysticaldream.glutemo.channel.handler.ChannelInitializeHandler;
import com.mysticaldream.glutemo.promise.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @author MysticalDream
 */
@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {

        NioReactorGroup mainReactorGroup = new NioReactorGroup();

        NioReactorGroup subReactorGroup = new NioReactorGroup();

        ServerBootStrap serverBootStrap = new ServerBootStrap(mainReactorGroup, subReactorGroup);

        serverBootStrap.addServerChannel(new NioServerSocketChannel(), 8666, new ChannelInitializeHandler<AbstractNioChannel>() {
            @Override
            public void initChannel(AbstractNioChannel channel) {

                channel.pipeline().addLast("test", new ChannelInHandlerAdapter() {
                    @Override
                    public void channelRegistered(ChannelHandlerContext context) throws Exception {
                        SocketChannel socketChannel = (SocketChannel) context.channel().javaChannel();
                        log.info("register: [{},{}]", socketChannel.getLocalAddress(), socketChannel.getRemoteAddress());
                        super.channelRegistered(context);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {

                        log.info(Arrays.toString((byte[]) msg));

                        log.info("读取1:\n{}", new String((byte[]) msg));

                        String body = "<h1>Hello World</h1>";

                        int contentLength = body.getBytes().length;

                        ChannelPromise channelPromise = context.writeAndFlush(("HTTP/1.1 200 OK\r\nServer: gluttony\r\nContent-Length: " + contentLength + "\r\nContent-Type: text/html\r\n\r\n" + body).getBytes());
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) throws Exception {
                        log.error(throwable.getMessage());
                    }
                });
            }
        });

        serverBootStrap.bind();

    }
}
